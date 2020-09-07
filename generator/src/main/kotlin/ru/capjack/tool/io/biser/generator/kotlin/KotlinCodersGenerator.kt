package ru.capjack.tool.io.biser.generator.kotlin

import ru.capjack.tool.io.biser.generator.CodePath
import ru.capjack.tool.io.biser.generator.CoderNameScopeVisitor
import ru.capjack.tool.io.biser.generator.CodersGenerator
import ru.capjack.tool.io.biser.generator.ImportsCollection
import ru.capjack.tool.io.biser.generator.TypeAggregator
import ru.capjack.tool.io.biser.generator.model.ListType
import ru.capjack.tool.io.biser.generator.model.NullableType
import ru.capjack.tool.io.biser.generator.model.PrimitiveType
import ru.capjack.tool.io.biser.generator.model.StructureType
import ru.capjack.tool.io.biser.generator.model.Type
import ru.capjack.tool.io.biser.generator.model.TypeVisitor
import java.nio.file.Path

class KotlinCodersGenerator(
	private val targetPackage: CodePath,
	private val internal: Boolean = false,
	encodersName: String? = null,
	decodersName: String? = null
) : CodersGenerator() {
	
	private val encodersPath = targetPackage.resolve(encodersName ?: (targetPackage.name.capitalize() + "Encoders"))
	private val decodersPath = targetPackage.resolve(decodersName ?: (targetPackage.name.capitalize() + "Decoders"))
	
	private val typeNameVisitor = KotlinTypeNameVisitor(targetPackage)
	
	private val innerEncoderNameVisitor: TypeVisitor<String, Unit> = KotlinCoderNameVisitor(object : CoderNameScopeVisitor {
		override fun visitPrimitiveScope(name: String) = "Encoders.$name"
		override fun visitGeneratedScope(name: String) = name
	})
	private val innerDecodeNameVisitor: TypeVisitor<String, Unit> = KotlinCoderNameVisitor(object : CoderNameScopeVisitor {
		override fun visitPrimitiveScope(name: String) = "Decoders.$name"
		override fun visitGeneratedScope(name: String) = name
	})
	
	private val innerWriteCallVisitor: TypeVisitor<String, String> = KotlinWriteCallVisitor(innerEncoderNameVisitor)
	private val innerReadCallVisitor: TypeVisitor<String, Unit> = KotlinReadCallVisitor(innerDecodeNameVisitor)
	
	private val outerWriteCallVisitor: TypeVisitor<String, String> = KotlinWriteCallVisitor(KotlinCoderNameVisitor(object : CoderNameScopeVisitor {
		override fun visitPrimitiveScope(name: String) = "Encoders.$name"
		override fun visitGeneratedScope(name: String) = encodersPath.name + '.' + name
	}))
	
	private val outerReadCallVisitor: TypeVisitor<String, Unit> = KotlinReadCallVisitor(KotlinCoderNameVisitor(object : CoderNameScopeVisitor {
		override fun visitPrimitiveScope(name: String) = "Decoders.$name"
		override fun visitGeneratedScope(name: String) = decodersPath.name + '.' + name
	}))
	
	
	private val outerEncodersRegister = object : TypeVisitor<Unit, ImportsCollection> {
		private var deep = 0
		
		override fun visitPrimitiveType(type: PrimitiveType, data: ImportsCollection) {
			if (deep != 0) {
				data.addImport("ru.capjack.tool.io.biser.Encoders")
			}
		}
		
		override fun visitListType(type: ListType, data: ImportsCollection) {
			data.addImport(encodersPath)
			encoders.add(type.element)
			++deep
			type.element.accept(this, data)
			--deep
		}
		
		override fun visitStructureType(type: StructureType, data: ImportsCollection) {
			if (deep == 0) {
				data.addImport(encodersPath)
				encoders.add(type)
			}
		}
		
		override fun visitNullableType(type: NullableType, data: ImportsCollection) {
			if (type.original == PrimitiveType.STRING) {
				return
			}
			if (deep == 0) {
				data.addImport(encodersPath)
				encoders.add(type)
			}
		}
	}
	
	private val outerDecodersRegister = object : TypeVisitor<Unit, ImportsCollection> {
		private var deep = 0
		
		override fun visitPrimitiveType(type: PrimitiveType, data: ImportsCollection) {
			if (deep != 0) {
				data.addImport("ru.capjack.tool.io.biser.Decoders")
			}
		}
		
		override fun visitListType(type: ListType, data: ImportsCollection) {
			data.addImport(decodersPath)
			decoders.add(type.element)
			++deep
			type.element.accept(this, data)
			--deep
		}
		
		override fun visitStructureType(type: StructureType, data: ImportsCollection) {
			if (deep == 0) {
				data.addImport(decodersPath)
				decoders.add(type)
			}
		}
		
		override fun visitNullableType(type: NullableType, data: ImportsCollection) {
			if (type.original == PrimitiveType.STRING) {
				return
			}
			if (deep == 0) {
				data.addImport(decodersPath)
				decoders.add(type)
			}
		}
	}
	
	fun getTypeName(imports: ImportsCollection, type: Type): String {
		return type.accept(typeNameVisitor, imports)
	}
	
	override fun provideWriteCall(imports: ImportsCollection, type: Type, value: String): String {
		type.accept(outerEncodersRegister, imports)
		return type.accept(outerWriteCallVisitor, value)
	}
	
	override fun provideReadCall(imports: ImportsCollection, type: Type): String {
		type.accept(outerDecodersRegister, imports)
		return type.accept(outerReadCallVisitor)
	}
	
	override fun generate(sourceDir: Path) {
		generate(
			sourceDir,
			encodersPath,
			encoders,
			KotlinEncoderGeneratorVisitor(innerWriteCallVisitor, innerEncoderNameVisitor, typeNameVisitor, targetPackage)
		)
		
		generate(
			sourceDir,
			decodersPath,
			decoders,
			KotlinDecoderGeneratorVisitor(innerReadCallVisitor, innerDecodeNameVisitor, typeNameVisitor, targetPackage)
		)
	}
	
	private fun generate(sourceDir: Path, path: CodePath, types: Set<Type>, generator: TypeVisitor<Unit, KotlinGeneratorContext>) {
		if (types.isEmpty()) {
			return
		}
		
		val file = KotlinFile(path)
		val aggregator = TypeAggregator()
		val code = file.identBracketsCurly((if (internal) "internal " else "") + "object ${path.name}")
		val context = KotlinGeneratorContext(aggregator, code, file)
		
		types.forEach { aggregator.add(it) }
		
		for (type in aggregator) {
			type.accept(generator, context)
		}
		
		file.write(sourceDir)
	}
}


