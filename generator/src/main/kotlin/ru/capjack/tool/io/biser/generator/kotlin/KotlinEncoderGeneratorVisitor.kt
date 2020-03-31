package ru.capjack.tool.io.biser.generator.kotlin

import ru.capjack.tool.io.biser.generator.CodeBlock
import ru.capjack.tool.io.biser.generator.CodePath
import ru.capjack.tool.io.biser.generator.ImportsCollection
import ru.capjack.tool.io.biser.generator.model.EntityDescriptor
import ru.capjack.tool.io.biser.generator.model.EnumDescriptor
import ru.capjack.tool.io.biser.generator.model.ListType
import ru.capjack.tool.io.biser.generator.model.NullableType
import ru.capjack.tool.io.biser.generator.model.PrimitiveType
import ru.capjack.tool.io.biser.generator.model.StructureDescriptorVisitor
import ru.capjack.tool.io.biser.generator.model.StructureType
import ru.capjack.tool.io.biser.generator.model.Type
import ru.capjack.tool.io.biser.generator.model.TypeVisitor

class KotlinEncoderGeneratorVisitor(
	private val writeCalls: TypeVisitor<String, String>,
	private val encoderNames: TypeVisitor<String, Unit>,
	private val typeNames: TypeVisitor<String, ImportsCollection>,
	private val targetPackage: CodePath
) : TypeVisitor<Unit, KotlinGeneratorContext>, StructureDescriptorVisitor<Unit, KotlinGeneratorContext> {
	
	private val entityFieldTypesVisitor = object : TypeVisitor<Unit, KotlinGeneratorContext> {
		private var deep = 0
		override fun visitPrimitiveType(type: PrimitiveType, data: KotlinGeneratorContext) {
			if (deep != 0) {
				data.imports.addImport("ru.capjack.tool.io.biser.Encoders")
			}
		}
		
		override fun visitListType(type: ListType, data: KotlinGeneratorContext) {
			data.types.add(type.element)
			++deep
			type.element.accept(this, data)
			--deep
		}
		
		override fun visitStructureType(type: StructureType, data: KotlinGeneratorContext) {
			if (deep == 0) data.types.add(type)
		}
		
		override fun visitNullableType(type: NullableType, data: KotlinGeneratorContext) {
			if (deep == 0) data.types.add(type)
		}
	}
	
	override fun visitPrimitiveType(type: PrimitiveType, data: KotlinGeneratorContext) {
	}
	
	override fun visitListType(type: ListType, data: KotlinGeneratorContext) {
		writeDeclaration(type, data).apply {
			line(writeCalls.visitListType(type, "it"))
		}
	}
	
	override fun visitStructureType(type: StructureType, data: KotlinGeneratorContext) {
		data.imports.addImport(targetPackage.resolve(type.path))
		type.descriptor.accept(this, data)
	}
	
	override fun visitNullableType(type: NullableType, data: KotlinGeneratorContext) {
		writeDeclaration(type, data).apply {
			identBracketsCurly("if (it == null) writeInt(0) else ") {
				line("writeInt(1)")
				line("${type.original.accept(encoderNames)}(it)")
			}
		}
	}
	
	override fun visitEnumStructureDescriptor(descriptor: EnumDescriptor, data: KotlinGeneratorContext) {
		val type = descriptor.type
		val typeName = type.accept(typeNames, data.imports)
		writeDeclaration(type, data).apply {
			line("writeInt(when (it) {")
			ident {
				descriptor.values.forEach {
					line("$typeName.${it.name} -> ${it.id}")
				}
			}
			line("})")
		}
	}
	
	override fun visitEntityStructureDescriptor(descriptor: EntityDescriptor, data: KotlinGeneratorContext) {
		val type = descriptor.type
		
		writeDeclaration(type, data).apply {
			if (descriptor.children.isNotEmpty()) {
				
				identBracketsCurly("when (it) ") {
					descriptor.children.forEach { child ->
						identBracketsCurly("is ${child.accept(typeNames, data.imports)} -> ") {
							with(child.descriptor as EntityDescriptor) {
								if (children.isEmpty()) {
									line("writeInt(${id})")
								}
							}
							line("${child.accept(encoderNames)}(it)")
						}
					}
					
					if (descriptor.abstract) {
						data.imports.addImport("ru.capjack.tool.io.biser.UnknownEntityEncoderException")
						line("else -> throw UnknownEntityEncoderException(it)")
					}
					else {
						identBracketsCurly("else -> ") {
							writeEntityFields(descriptor, data)
						}
					}
				}
			}
			else {
				writeEntityFields(descriptor, data)
			}
		}
	}
	
	private fun CodeBlock.writeEntityFields(descriptor: EntityDescriptor, data: KotlinGeneratorContext) {
		if (descriptor.children.isNotEmpty()) {
			line("writeInt(${descriptor.id})")
		}
		descriptor.fields.forEach { field ->
			field.type.accept(entityFieldTypesVisitor, data)
			line(field.type.accept(writeCalls, "it.${field.name}"))
		}
	}
	
	private fun writeDeclaration(type: Type, context: KotlinGeneratorContext): CodeBlock {
		context.imports.addImport("ru.capjack.tool.io.biser.Encoder")
		
		val name = type.accept(encoderNames)
		val typeName = type.accept(typeNames, context.imports)
		
		val block = context.code.identBracketsCurly("val $name: Encoder<$typeName> = ")
		
		context.code.line()
		
		return block
	}
}