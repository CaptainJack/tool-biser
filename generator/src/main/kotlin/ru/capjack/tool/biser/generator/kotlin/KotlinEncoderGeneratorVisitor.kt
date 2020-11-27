package ru.capjack.tool.biser.generator.kotlin

import ru.capjack.tool.biser.generator.CodeBlock
import ru.capjack.tool.biser.generator.CodePath
import ru.capjack.tool.biser.generator.ImportsCollection
import ru.capjack.tool.biser.generator.model.*

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
				data.imports.addImport("ru.capjack.tool.biser.Encoders")
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
		if (type.original == PrimitiveType.STRING) {
			return
		}
		data.types.add(type.original)
		writeDeclaration(type, data).apply {
			//TODO Legacy
			//identBracketsCurly("if (it == null) writeInt(0) else ") {
			//  line("writeInt(1)")
			identBracketsCurly("if (it == null) writeInt(-1) else ") {
				line(type.original.accept(writeCalls, "it"))
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
					//TODO Legacy line("$typeName.${it.name} -> ${it.id}")
					line("$typeName.${it.name} -> ${it.id - 1}")
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
						data.types.add(child)
						val childDescriptor = child.descriptor
						val childTypeName = child.accept(typeNames, data.imports)
						if (childDescriptor is EntityDescriptor) {
							identBracketsCurly("is $childTypeName -> ") {
								/*TODO Legacy
								if (children.isEmpty()) {
									line("writeInt(${id})")
								}*/
								line("${child.accept(encoderNames)}(it)")
							}
						}
						else if (childDescriptor is ObjectDescriptor) {
							identBracketsCurly("$childTypeName -> ") {
								line("${child.accept(encoderNames)}(it as $childTypeName)")
							}
						}
					}
					
					if (descriptor.abstract) {
						data.imports.addImport("ru.capjack.tool.biser.UnknownEntityEncoderException")
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
	
	override fun visitObjectStructureDescriptor(descriptor: ObjectDescriptor, data: KotlinGeneratorContext) {
		val type = descriptor.type
		
		data.imports.addImport("ru.capjack.tool.biser.Encoder")
		
		val name = type.accept(encoderNames)
		val typeName = type.accept(typeNames, data.imports)
		
		data.code.identBracketsCurly("val $name: Encoder<$typeName> = ") {
			line("writeInt(${descriptor.id})")
		}
		
		data.code.line()
	}
	
	private fun CodeBlock.writeEntityFields(descriptor: EntityDescriptor, data: KotlinGeneratorContext) {
		/*TODO Legacy
		if (descriptor.children.isNotEmpty()) {
			line("writeInt(${descriptor.id})")
		}*/
		line("writeInt(${descriptor.id})")
		descriptor.fields.forEach { field ->
			field.type.accept(entityFieldTypesVisitor, data)
			line(field.type.accept(writeCalls, "it.${field.name}"))
		}
	}
	
	private fun writeDeclaration(type: Type, context: KotlinGeneratorContext): CodeBlock {
		context.imports.addImport("ru.capjack.tool.biser.Encoder")
		
		val name = type.accept(encoderNames)
		val typeName = type.accept(typeNames, context.imports)
		
		val block = context.code.identBracketsCurly("val $name: Encoder<$typeName> = ")
		
		context.code.line()
		
		return block
	}
}