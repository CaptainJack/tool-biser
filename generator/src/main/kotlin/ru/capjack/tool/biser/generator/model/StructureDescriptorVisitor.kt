package ru.capjack.tool.biser.generator.model

interface StructureDescriptorVisitor<R, D> {
	fun visitEnumStructureDescriptor(descriptor: EnumDescriptor, data: D): R
	
	fun visitEntityStructureDescriptor(descriptor: EntityDescriptor, data: D): R
	
	fun visitObjectStructureDescriptor(descriptor: ObjectDescriptor, data: D): R
}