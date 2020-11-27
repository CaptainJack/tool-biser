package example

import ru.capjack.tool.biser.Decoder
import ru.capjack.tool.biser.UnknownIdDecoderException
import ru.capjack.tool.biser.Decoders

object ExampleDecoders{
	val ENUM_EnumStub: Decoder<EnumStub> = {
		when (val id = readInt()) {
			1 -> EnumStub.A
			2 -> EnumStub.B
			else -> throw UnknownIdDecoderException(id, EnumStub::class)
		}
	}
	
	val ENTITY_EntityAStub: Decoder<EntityAStub> = {
		when (val id = readInt()) {
			2 -> ENTITY_EntityA1Stub()
			4 -> ENTITY_EntityA3Stub()
			else -> throw UnknownIdDecoderException(id, EntityAStub::class)
		}
	}
	
	val ENTITY_EntityA1Stub: Decoder<EntityA1Stub> = {
		EntityA1Stub(
			readInt(),
			readLong()
		)
	}
	
	val ENTITY_EntityA2Stub: Decoder<EntityA2Stub> = {
		when (val id = readInt()) {
			4 -> ENTITY_EntityA3Stub()
			else -> throw UnknownIdDecoderException(id, EntityA2Stub::class)
		}
	}
	
	val ENTITY_EntityA3Stub: Decoder<EntityA3Stub> = {
		EntityA3Stub(
			readInt(),
			readString()
		)
	}
	
	private val ENTITY_EntityBStub_RAW: Decoder<EntityBStub> = {
		EntityBStub(
			readString()
		)
	}
	
	val ENTITY_EntityBStub: Decoder<EntityBStub> = {
		when (val id = readInt()) {
			6 -> ENTITY_EntityB1Stub()
			7 -> ENTITY_EntityB2Stub_RAW()
			8 -> ENTITY_EntityB3Stub()
			5 -> ENTITY_EntityBStub_RAW()
			else -> throw UnknownIdDecoderException(id, EntityBStub::class)
		}
	}
	
	val ENTITY_EntityB1Stub: Decoder<EntityB1Stub> = {
		EntityB1Stub(
			readString(),
			readBoolean()
		)
	}
	
	private val ENTITY_EntityB2Stub_RAW: Decoder<EntityB2Stub> = {
		EntityB2Stub(
			readString(),
			readByte()
		)
	}
	
	val ENTITY_EntityB2Stub: Decoder<EntityB2Stub> = {
		when (val id = readInt()) {
			8 -> ENTITY_EntityB3Stub()
			7 -> ENTITY_EntityB2Stub_RAW()
			else -> throw UnknownIdDecoderException(id, EntityB2Stub::class)
		}
	}
	
	val ENTITY_EntityB3Stub: Decoder<EntityB3Stub> = {
		EntityB3Stub(
			readString(),
			readByte()
		)
	}
	
	val ENTITY_EntityCStub: Decoder<EntityCStub> = {
		EntityCStub(
			readBoolean(),
			readByte(),
			readInt(),
			readLong(),
			readDouble(),
			readBooleanArray(),
			readByteArray(),
			readIntArray(),
			readLongArray(),
			readDoubleArray(),
			readString(),
			read(ENTITY_EntityAStub),
			read(ENTITY_EntityA1Stub),
			read(ENTITY_EntityA2Stub),
			read(NULLABLE_ENTITY_EntityAStub),
			read(NULLABLE_ENTITY_EntityA1Stub),
			read(NULLABLE_ENTITY_EntityA2Stub),
			read(ENTITY_EntityBStub),
			read(ENTITY_EntityB1Stub),
			read(ENTITY_EntityB2Stub),
			read(NULLABLE_ENTITY_EntityBStub),
			read(NULLABLE_ENTITY_EntityB1Stub),
			read(NULLABLE_ENTITY_EntityB2Stub),
			read(ENUM_EnumStub),
			readList(Decoders.BOOLEAN),
			readList(Decoders.BYTE),
			readList(Decoders.INT),
			readList(Decoders.LONG),
			readList(Decoders.DOUBLE),
			readList(Decoders.BOOLEAN_ARRAY),
			readList(Decoders.BYTE_ARRAY),
			readList(Decoders.INT_ARRAY),
			readList(Decoders.LONG_ARRAY),
			readList(Decoders.DOUBLE_ARRAY),
			readList(Decoders.STRING),
			readList(ENTITY_EntityAStub),
			readList(ENTITY_EntityBStub),
			readList(ENUM_EnumStub),
			readList(LIST_BOOLEAN),
			readList(LIST_BYTE),
			readList(LIST_INT),
			readList(LIST_LONG),
			readList(LIST_DOUBLE),
			readList(LIST_BOOLEAN_ARRAY),
			readList(LIST_BYTE_ARRAY),
			readList(LIST_INT_ARRAY),
			readList(LIST_LONG_ARRAY),
			readList(LIST_DOUBLE_ARRAY),
			readList(LIST_STRING),
			readList(LIST_ENTITY_EntityAStub),
			readList(LIST_ENTITY_EntityBStub),
			readList(LIST_ENUM_EnumStub)
		)
	}
	
	val NULLABLE_ENTITY_EntityAStub: Decoder<EntityAStub?> = {
		if (readInt() == 0) null else ENTITY_EntityAStub()
	}
	
	val NULLABLE_ENTITY_EntityA1Stub: Decoder<EntityA1Stub?> = {
		if (readInt() == 0) null else ENTITY_EntityA1Stub()
	}
	
	val NULLABLE_ENTITY_EntityA2Stub: Decoder<EntityA2Stub?> = {
		if (readInt() == 0) null else ENTITY_EntityA2Stub()
	}
	
	val NULLABLE_ENTITY_EntityBStub: Decoder<EntityBStub?> = {
		if (readInt() == 0) null else ENTITY_EntityBStub()
	}
	
	val NULLABLE_ENTITY_EntityB1Stub: Decoder<EntityB1Stub?> = {
		if (readInt() == 0) null else ENTITY_EntityB1Stub()
	}
	
	val NULLABLE_ENTITY_EntityB2Stub: Decoder<EntityB2Stub?> = {
		if (readInt() == 0) null else ENTITY_EntityB2Stub()
	}
	
	val LIST_BOOLEAN: Decoder<List<Boolean>> = {
		readList(Decoders.BOOLEAN)
	}
	
	val LIST_BYTE: Decoder<List<Byte>> = {
		readList(Decoders.BYTE)
	}
	
	val LIST_INT: Decoder<List<Int>> = {
		readList(Decoders.INT)
	}
	
	val LIST_LONG: Decoder<List<Long>> = {
		readList(Decoders.LONG)
	}
	
	val LIST_DOUBLE: Decoder<List<Double>> = {
		readList(Decoders.DOUBLE)
	}
	
	val LIST_BOOLEAN_ARRAY: Decoder<List<BooleanArray>> = {
		readList(Decoders.BOOLEAN_ARRAY)
	}
	
	val LIST_BYTE_ARRAY: Decoder<List<ByteArray>> = {
		readList(Decoders.BYTE_ARRAY)
	}
	
	val LIST_INT_ARRAY: Decoder<List<IntArray>> = {
		readList(Decoders.INT_ARRAY)
	}
	
	val LIST_LONG_ARRAY: Decoder<List<LongArray>> = {
		readList(Decoders.LONG_ARRAY)
	}
	
	val LIST_DOUBLE_ARRAY: Decoder<List<DoubleArray>> = {
		readList(Decoders.DOUBLE_ARRAY)
	}
	
	val LIST_STRING: Decoder<List<String>> = {
		readList(Decoders.STRING)
	}
	
	val LIST_ENTITY_EntityAStub: Decoder<List<EntityAStub>> = {
		readList(ENTITY_EntityAStub)
	}
	
	val LIST_ENTITY_EntityBStub: Decoder<List<EntityBStub>> = {
		readList(ENTITY_EntityBStub)
	}
	
	val LIST_ENUM_EnumStub: Decoder<List<EnumStub>> = {
		readList(ENUM_EnumStub)
	}
	
}
