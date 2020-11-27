package example

import ru.capjack.tool.biser.Encoder
import ru.capjack.tool.biser.UnknownEntityEncoderException
import ru.capjack.tool.biser.Encoders

object ExampleEncoders{
	val ENUM_EnumStub: Encoder<EnumStub> = {
		writeInt(when (it) {
			EnumStub.A -> 1
			EnumStub.B -> 2
		})
	}
	
	val ENTITY_EntityAStub: Encoder<EntityAStub> = {
		when (it) {
			is EntityA1Stub -> {
				writeInt(2)
				ENTITY_EntityA1Stub(it)
			}
			is EntityA2Stub -> {
				ENTITY_EntityA2Stub(it)
			}
			else -> throw UnknownEntityEncoderException(it)
		}
	}
	
	val ENTITY_EntityA1Stub: Encoder<EntityA1Stub> = {
		writeInt(it.v)
		writeLong(it.s)
	}
	
	val ENTITY_EntityA2Stub: Encoder<EntityA2Stub> = {
		when (it) {
			is EntityA3Stub -> {
				writeInt(4)
				ENTITY_EntityA3Stub(it)
			}
			else -> throw UnknownEntityEncoderException(it)
		}
	}
	
	val ENTITY_EntityA3Stub: Encoder<EntityA3Stub> = {
		writeInt(it.v)
		writeString(it.s)
	}
	
	val ENTITY_EntityBStub: Encoder<EntityBStub> = {
		when (it) {
			is EntityB1Stub -> {
				writeInt(6)
				ENTITY_EntityB1Stub(it)
			}
			is EntityB2Stub -> {
				ENTITY_EntityB2Stub(it)
			}
			else -> {
				writeInt(5)
				writeString(it.v)
			}
		}
	}
	
	val ENTITY_EntityB1Stub: Encoder<EntityB1Stub> = {
		writeString(it.v)
		writeBoolean(it.s)
	}
	
	val ENTITY_EntityB2Stub: Encoder<EntityB2Stub> = {
		when (it) {
			is EntityB3Stub -> {
				writeInt(8)
				ENTITY_EntityB3Stub(it)
			}
			else -> {
				writeInt(7)
				writeString(it.v)
				writeByte(it.s)
			}
		}
	}
	
	val ENTITY_EntityB3Stub: Encoder<EntityB3Stub> = {
		writeString(it.v)
		writeByte(it.s)
	}
	
	val ENTITY_EntityCStub: Encoder<EntityCStub> = {
		writeBoolean(it.vBoolean)
		writeByte(it.vByte)
		writeInt(it.vInt)
		writeLong(it.vLong)
		writeDouble(it.vDouble)
		writeBooleanArray(it.vBooleanArray)
		writeByteArray(it.vByteArray)
		writeIntArray(it.vIntArray)
		writeLongArray(it.vLongArray)
		writeDoubleArray(it.vDoubleArray)
		writeString(it.vString)
		write(it.vEntityA, ENTITY_EntityAStub)
		write(it.vEntityA1, ENTITY_EntityA1Stub)
		write(it.vEntityA2, ENTITY_EntityA2Stub)
		write(it.vEntityANullable, NULLABLE_ENTITY_EntityAStub)
		write(it.vEntityA1Nullable, NULLABLE_ENTITY_EntityA1Stub)
		write(it.vEntityA2Nullable, NULLABLE_ENTITY_EntityA2Stub)
		write(it.vEntityB, ENTITY_EntityBStub)
		write(it.vEntityB1, ENTITY_EntityB1Stub)
		write(it.vEntityB2, ENTITY_EntityB2Stub)
		write(it.vEntityBNullable, NULLABLE_ENTITY_EntityBStub)
		write(it.vEntityB1Nullable, NULLABLE_ENTITY_EntityB1Stub)
		write(it.vEntityB2Nullable, NULLABLE_ENTITY_EntityB2Stub)
		write(it.vEnum, ENUM_EnumStub)
		writeList(it.lBoolean, Encoders.BOOLEAN)
		writeList(it.lByte, Encoders.BYTE)
		writeList(it.lInt, Encoders.INT)
		writeList(it.lLong, Encoders.LONG)
		writeList(it.lDouble, Encoders.DOUBLE)
		writeList(it.lBooleanArray, Encoders.BOOLEAN_ARRAY)
		writeList(it.lByteArray, Encoders.BYTE_ARRAY)
		writeList(it.lIntArray, Encoders.INT_ARRAY)
		writeList(it.lLongArray, Encoders.LONG_ARRAY)
		writeList(it.lDoubleArray, Encoders.DOUBLE_ARRAY)
		writeList(it.lString, Encoders.STRING)
		writeList(it.lEntityA, ENTITY_EntityAStub)
		writeList(it.lEntityB, ENTITY_EntityBStub)
		writeList(it.lEnum, ENUM_EnumStub)
		writeList(it.llBoolean, LIST_BOOLEAN)
		writeList(it.llByte, LIST_BYTE)
		writeList(it.llInt, LIST_INT)
		writeList(it.llLong, LIST_LONG)
		writeList(it.llDouble, LIST_DOUBLE)
		writeList(it.llBooleanArray, LIST_BOOLEAN_ARRAY)
		writeList(it.llByteArray, LIST_BYTE_ARRAY)
		writeList(it.llIntArray, LIST_INT_ARRAY)
		writeList(it.llLongArray, LIST_LONG_ARRAY)
		writeList(it.llDoubleArray, LIST_DOUBLE_ARRAY)
		writeList(it.llString, LIST_STRING)
		writeList(it.llEntityA, LIST_ENTITY_EntityAStub)
		writeList(it.llEntityB, LIST_ENTITY_EntityBStub)
		writeList(it.llEnum, LIST_ENUM_EnumStub)
	}
	
	val NULLABLE_ENTITY_EntityAStub: Encoder<EntityAStub?> = {
		if (it == null) writeInt(0) else {
			writeInt(1)
			ENTITY_EntityAStub(it)
		}
	}
	
	val NULLABLE_ENTITY_EntityA1Stub: Encoder<EntityA1Stub?> = {
		if (it == null) writeInt(0) else {
			writeInt(1)
			ENTITY_EntityA1Stub(it)
		}
	}
	
	val NULLABLE_ENTITY_EntityA2Stub: Encoder<EntityA2Stub?> = {
		if (it == null) writeInt(0) else {
			writeInt(1)
			ENTITY_EntityA2Stub(it)
		}
	}
	
	val NULLABLE_ENTITY_EntityBStub: Encoder<EntityBStub?> = {
		if (it == null) writeInt(0) else {
			writeInt(1)
			ENTITY_EntityBStub(it)
		}
	}
	
	val NULLABLE_ENTITY_EntityB1Stub: Encoder<EntityB1Stub?> = {
		if (it == null) writeInt(0) else {
			writeInt(1)
			ENTITY_EntityB1Stub(it)
		}
	}
	
	val NULLABLE_ENTITY_EntityB2Stub: Encoder<EntityB2Stub?> = {
		if (it == null) writeInt(0) else {
			writeInt(1)
			ENTITY_EntityB2Stub(it)
		}
	}
	
	val LIST_BOOLEAN: Encoder<List<Boolean>> = {
		writeList(it, Encoders.BOOLEAN)
	}
	
	val LIST_BYTE: Encoder<List<Byte>> = {
		writeList(it, Encoders.BYTE)
	}
	
	val LIST_INT: Encoder<List<Int>> = {
		writeList(it, Encoders.INT)
	}
	
	val LIST_LONG: Encoder<List<Long>> = {
		writeList(it, Encoders.LONG)
	}
	
	val LIST_DOUBLE: Encoder<List<Double>> = {
		writeList(it, Encoders.DOUBLE)
	}
	
	val LIST_BOOLEAN_ARRAY: Encoder<List<BooleanArray>> = {
		writeList(it, Encoders.BOOLEAN_ARRAY)
	}
	
	val LIST_BYTE_ARRAY: Encoder<List<ByteArray>> = {
		writeList(it, Encoders.BYTE_ARRAY)
	}
	
	val LIST_INT_ARRAY: Encoder<List<IntArray>> = {
		writeList(it, Encoders.INT_ARRAY)
	}
	
	val LIST_LONG_ARRAY: Encoder<List<LongArray>> = {
		writeList(it, Encoders.LONG_ARRAY)
	}
	
	val LIST_DOUBLE_ARRAY: Encoder<List<DoubleArray>> = {
		writeList(it, Encoders.DOUBLE_ARRAY)
	}
	
	val LIST_STRING: Encoder<List<String>> = {
		writeList(it, Encoders.STRING)
	}
	
	val LIST_ENTITY_EntityAStub: Encoder<List<EntityAStub>> = {
		writeList(it, ENTITY_EntityAStub)
	}
	
	val LIST_ENTITY_EntityBStub: Encoder<List<EntityBStub>> = {
		writeList(it, ENTITY_EntityBStub)
	}
	
	val LIST_ENUM_EnumStub: Encoder<List<EnumStub>> = {
		writeList(it, ENUM_EnumStub)
	}
	
}
