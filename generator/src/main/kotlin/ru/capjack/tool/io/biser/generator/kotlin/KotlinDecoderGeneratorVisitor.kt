package ru.capjack.tool.io.biser.generator.kotlin

import ru.capjack.tool.io.biser.generator.CodeBlock
import ru.capjack.tool.io.biser.generator.CodePath
import ru.capjack.tool.io.biser.generator.ImportsCollection
import ru.capjack.tool.io.biser.generator.model.*

class KotlinDecoderGeneratorVisitor(
	private val readCalls: TypeVisitor<String, Unit>,
	private val decoderNames: TypeVisitor<String, Unit>,
	private val typeNames: TypeVisitor<String, ImportsCollection>,
	private val targetPackage: CodePath
) : TypeVisitor<Unit, KotlinGeneratorContext>,
	StructureDescriptorVisitor<Unit, KotlinGeneratorContext> {
	
	private val entityFieldTypesVisitor = object : TypeVisitor<Unit, KotlinGeneratorContext> {
		private var deep = 0
		
		override fun visitPrimitiveType(type: PrimitiveType, data: KotlinGeneratorContext) {
			data.imports.addImport("ru.capjack.tool.io.biser.Decoders")
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
			line(readCalls.visitListType(type, Unit))
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
			line(type.accept(readCalls))
		}
	}
	
	override fun visitEnumStructureDescriptor(descriptor: EnumDescriptor, data: KotlinGeneratorContext) {
		val type = descriptor.type
		val typeName = type.accept(typeNames, data.imports)
		writeDeclaration(type, data).apply {
			identBracketsCurly("when (val id = readInt()) ") {
				descriptor.values.forEach {
					//TODO Legacy line("${it.id} -> $typeName.${it.name}")
					line("${it.id - 1} -> $typeName.${it.name}")
				}
				data.imports.addImport("ru.capjack.tool.io.biser.UnknownIdDecoderException")
				line("else -> throw UnknownIdDecoderException(id, $typeName::class)")
			}
		}
	}
	
	
	override fun visitEntityStructureDescriptor(descriptor: EntityDescriptor, data: KotlinGeneratorContext) {
		val type = descriptor.type
		
		if (descriptor.children.isNotEmpty()) {
			
			if (!descriptor.abstract) {
				writeDeclaration(type, data, true).apply {
					writeEntityDecode(descriptor, data)
				}
			}
			
			writeDeclaration(type, data).apply {
				identBracketsCurly("when (val id = readInt()) ") {
					descriptor.allChildren.forEach { child ->
						data.types.add(child)
						val childDescriptor = child.descriptor
						val name = child.accept(decoderNames)
						if (childDescriptor is EntityDescriptor) {
							if (!childDescriptor.abstract) {
								if (childDescriptor.children.isNotEmpty()) {
									line("${childDescriptor.id} -> ${name}_RAW()")
								}
								else {
									line("${childDescriptor.id} -> $name()")
								}
							}
						}
						else if (childDescriptor is ObjectDescriptor) {
							line("${childDescriptor.id} -> $name()")
						}
					}
					
					if (!descriptor.abstract) {
						val name = type.accept(decoderNames)
						line("${descriptor.id} -> ${name}_RAW()")
					}
					
					val typeName = type.accept(typeNames, data.imports)
					data.imports.addImport("ru.capjack.tool.io.biser.UnknownIdDecoderException")
					line("else -> throw UnknownIdDecoderException(id, $typeName::class)")
				}
			}
		}
		else {
			writeDeclaration(type, data).apply {
				writeEntityDecode(descriptor, data)
			}
		}
	}
	
	override fun visitObjectStructureDescriptor(descriptor: ObjectDescriptor, data: KotlinGeneratorContext) {
		writeDeclaration(descriptor.type, data).apply {
			ident {
				line(descriptor.type.path.name)
			}
		}
	}
	
	private fun CodeBlock.writeEntityDecode(descriptor: EntityDescriptor, data: KotlinGeneratorContext) {
		identBracketsRound(descriptor.type.path.name) {
			val last = descriptor.fields.size - 1
			descriptor.fields.forEachIndexed { i, field ->
				field.type.accept(entityFieldTypesVisitor, data)
				line(field.type.accept(readCalls) + (if (i == last) "" else ","))
			}
		}
	}
	
	private fun writeDeclaration(type: Type, context: KotlinGeneratorContext, raw: Boolean = false): CodeBlock {
		context.imports.addImport("ru.capjack.tool.io.biser.Decoder")
		
		var name = type.accept(decoderNames)
		val typeName = type.accept(typeNames, context.imports)
		
		if (raw) {
			name += "_RAW"
		}
		
		val block = context.code.identBracketsCurly((if (raw) "private " else "") + "val $name: Decoder<$typeName> = ")
		
		context.code.line()
		
		return block
	}
}