@file:Suppress("PackageDirectoryMismatch", "MemberVisibilityCanBePrivate", "unused")

package example

enum class EnumStub {
	A, B
}

abstract class EntityAStub(val v: Int)

class EntityA1Stub(v: Int, val s: Long) : EntityAStub(v)
abstract class EntityA2Stub(v: Int, val s: String) : EntityAStub(v)
class EntityA3Stub(v: Int, s: String) : EntityA2Stub(v,s )

open class EntityBStub(val v: String)
class EntityB1Stub(v: String, val s: Boolean) : EntityBStub(v)
open class EntityB2Stub(v: String, val s: Byte) : EntityBStub(v)
class EntityB3Stub(v: String, s: Byte) : EntityB2Stub(v, s)


class EntityCStub(
	val vBoolean: Boolean,
	val vByte: Byte,
	val vInt: Int,
	val vLong: Long,
	val vDouble: Double,
	
	val vBooleanArray: BooleanArray,
	val vByteArray: ByteArray,
	val vIntArray: IntArray,
	val vLongArray: LongArray,
	val vDoubleArray: DoubleArray,
	
	val vString: String,
	
	val vEntityA: EntityAStub,
	val vEntityA1: EntityA1Stub,
	val vEntityA2: EntityA2Stub,
	val vEntityANullable: EntityAStub?,
	val vEntityA1Nullable: EntityA1Stub?,
	val vEntityA2Nullable: EntityA2Stub?,
	
	val vEntityB: EntityBStub,
	val vEntityB1: EntityB1Stub,
	val vEntityB2: EntityB2Stub,
	val vEntityBNullable: EntityBStub?,
	val vEntityB1Nullable: EntityB1Stub?,
	val vEntityB2Nullable: EntityB2Stub?,
	
	val vEnum: EnumStub,
	
	val lBoolean: List<Boolean>,
	val lByte: List<Byte>,
	val lInt: List<Int>,
	val lLong: List<Long>,
	val lDouble: List<Double>,
	
	val lBooleanArray: List<BooleanArray>,
	val lByteArray: List<ByteArray>,
	val lIntArray: List<IntArray>,
	val lLongArray: List<LongArray>,
	val lDoubleArray: List<DoubleArray>,
	
	val lString: List<String>,
	
	val lEntityA: List<EntityAStub>,
	val lEntityB: List<EntityBStub>,
	val lEnum: List<EnumStub>,
	
	val llBoolean: List<List<Boolean>>,
	val llByte: List<List<Byte>>,
	val llInt: List<List<Int>>,
	val llLong: List<List<Long>>,
	val llDouble: List<List<Double>>,
	
	val llBooleanArray: List<List<BooleanArray>>,
	val llByteArray: List<List<ByteArray>>,
	val llIntArray: List<List<IntArray>>,
	val llLongArray: List<List<LongArray>>,
	val llDoubleArray: List<List<DoubleArray>>,
	
	val llString: List<List<String>>,
	
	val llEntityA: List<List<EntityAStub>>,
	val llEntityB: List<List<EntityBStub>>,
	val llEnum: List<List<EnumStub>>
)
