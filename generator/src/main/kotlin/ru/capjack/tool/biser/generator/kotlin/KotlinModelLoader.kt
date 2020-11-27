package ru.capjack.tool.biser.generator.kotlin

import org.jetbrains.kotlin.builtins.KotlinBuiltIns
import org.jetbrains.kotlin.descriptors.ClassDescriptor
import org.jetbrains.kotlin.descriptors.ClassKind
import org.jetbrains.kotlin.descriptors.DeclarationDescriptor
import org.jetbrains.kotlin.descriptors.Modality
import org.jetbrains.kotlin.descriptors.PropertyDescriptor
import org.jetbrains.kotlin.resolve.descriptorUtil.fqNameUnsafe
import org.jetbrains.kotlin.resolve.descriptorUtil.getSuperClassNotAny
import org.jetbrains.kotlin.types.KotlinType
import org.jetbrains.kotlin.types.typeUtil.isBoolean
import org.jetbrains.kotlin.types.typeUtil.isByte
import org.jetbrains.kotlin.types.typeUtil.isDouble
import org.jetbrains.kotlin.types.typeUtil.isEnum
import org.jetbrains.kotlin.types.typeUtil.isInt
import org.jetbrains.kotlin.types.typeUtil.isInterface
import org.jetbrains.kotlin.types.typeUtil.isLong
import ru.capjack.tool.biser.generator.GeneratorException
import ru.capjack.tool.biser.generator.model.EntityField
import ru.capjack.tool.biser.generator.model.Model
import ru.capjack.tool.biser.generator.model.PrimitiveType
import ru.capjack.tool.biser.generator.model.Type
import org.jetbrains.kotlin.builtins.PrimitiveType as KotlinPrimitiveType

open class KotlinModelLoader<M : Model>(
	protected val model: M,
	protected val source: KotlinSource,
	sourcePackage: String
) {
	private val sourcePackagePrefix: String = "$sourcePackage."
	
	open fun load() {
		model.beginUpdate()
		
		for (descriptor in source.classDescriptors) {
			val name = extractName(descriptor)
			if (name != null) {
				processClassDescriptor(descriptor, name)
			}
		}
		
		model.completeUpdate()
	}
	
	protected fun extractName(descriptor: DeclarationDescriptor): String? {
		return descriptor.fqNameUnsafe.toString()
			.takeIf { it.startsWith(sourcePackagePrefix) }
			?.substring(sourcePackagePrefix.length)
	}
	
	protected open fun processClassDescriptor(descriptor: ClassDescriptor, name: String) {
		when (descriptor.kind) {
			ClassKind.CLASS      -> loadEntity(name, descriptor)
			ClassKind.OBJECT     -> loadObject(name, descriptor)
			ClassKind.ENUM_CLASS -> loadEnum(name, descriptor)
			ClassKind.ENUM_ENTRY -> Unit
			else                 -> throw GeneratorException(descriptor.kind.name)
		}
	}
	
	protected open fun loadEnum(name: String, descriptor: ClassDescriptor) {
		val values = descriptor.unsubstitutedMemberScope.getContributedDescriptors()
			.asSequence()
			.filterIsInstance<ClassDescriptor>()
			.filter { it.kind == ClassKind.ENUM_ENTRY }
			.map { it.name.toString() }
			.toList()
		model.provideEnumStructure(name, values)
	}
	
	protected open fun loadEntity(name: String, descriptor: ClassDescriptor) {
		val properties = descriptor.unsubstitutedMemberScope
			.getContributedDescriptors()
			.asSequence()
			.filterIsInstance<PropertyDescriptor>()
			.associate { it.name to it.type }
		
		val parent = descriptor.getSuperClassNotAny()?.let {
			extractName(it) ?: throw GeneratorException(it.toString())
		}
		
		val fields = descriptor.unsubstitutedPrimaryConstructor!!.valueParameters.map {
			if (properties[it.name] != it.type) {
				throw GeneratorException(it.toString())
			}
			EntityField(it.name.toString(), defineType(it.type))
		}
		
		model.provideEntityStructure(name, descriptor.modality == Modality.ABSTRACT || descriptor.modality == Modality.SEALED, parent, fields)
	}
	
	protected open fun loadObject(name: String, descriptor: ClassDescriptor) {
		
		val parent = descriptor.getSuperClassNotAny()?.let {
			extractName(it) ?: throw GeneratorException(it.toString())
		}
		
		model.provideObjectStructure(name, parent)
	}
	
	protected fun defineType(kType: KotlinType): Type {
		return when {
			kType.isBoolean()                              -> PrimitiveType.BOOLEAN
			kType.isByte()                                 -> PrimitiveType.BYTE
			kType.isInt()                                  -> PrimitiveType.INT
			kType.isLong()                                 -> PrimitiveType.LONG
			kType.isDouble()                               -> PrimitiveType.DOUBLE
			KotlinBuiltIns.isString(kType)                 -> PrimitiveType.STRING
			KotlinBuiltIns.isStringOrNullableString(kType) -> model.provideNullableType(PrimitiveType.STRING)
			
			KotlinBuiltIns.isPrimitiveArray(kType)         -> when (KotlinBuiltIns.getPrimitiveArrayType(kType.constructor.declarationDescriptor!!)) {
				KotlinPrimitiveType.BOOLEAN -> PrimitiveType.BOOLEAN_ARRAY
				KotlinPrimitiveType.BYTE    -> PrimitiveType.BYTE_ARRAY
				KotlinPrimitiveType.INT     -> PrimitiveType.INT_ARRAY
				KotlinPrimitiveType.LONG    -> PrimitiveType.LONG_ARRAY
				KotlinPrimitiveType.DOUBLE  -> PrimitiveType.DOUBLE_ARRAY
				else                        -> throw GeneratorException(kType.toString())
			}
			
			KotlinBuiltIns.isListOrNullableList(kType)     -> {
				if (kType.isMarkedNullable) {
					throw GeneratorException(kType.toString())
				}
				val kElementType = kType.arguments.first().type
				
				if (kElementType.isMarkedNullable) {
					throw GeneratorException(kType.toString())
				}
				
				model.provideListType(defineType(kElementType))
			}
			
			else                                           -> {
				val name = extractName(kType.constructor.declarationDescriptor!!)
					?: throw GeneratorException(kType.toString())
				
				if (kType.isEnum()) {
					if (kType.isMarkedNullable) {
						throw GeneratorException(kType.toString())
					}
					model.provideStructureType(name)
				}
				else if (!kType.isInterface()) {
					val type = model.provideStructureType(name)
					if (kType.isMarkedNullable) model.provideNullableType(type)
					else type
				}
				else {
					throw GeneratorException(kType.toString())
				}
			}
		}
		
	}
}