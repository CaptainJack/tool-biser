@file:Suppress("GrazieInspection")

package ru.capjack.tool.io.biser.generator.kotlin

import ru.capjack.tool.io.biser.generator.model.Change
import ru.capjack.tool.io.biser.generator.model.Model
import java.nio.file.Path
import java.nio.file.Paths
import kotlin.test.Test
import kotlin.test.assertEquals

class KotlinModuleLoaderTest {
	@Test
	fun `Load to empty model`() {
		
		val model = Model()
		val path = Paths.get(javaClass.getResource("/example/stubs.kt").toURI())
		
		KotlinModelLoader(model, KotlinSource(path), "example").load()
		
		val expected = """
			|lastEntityId: 9
			|structures:
			|- name: EnumStub
			|  lastValueId: 2
			|  values:
			|  - {id: 1, name: A}
			|  - {id: 2, name: B}
			|- name: EntityAStub
			|  id: 1
			|  parent: null
			|  abstract: true
			|  fields:
			|  - {name: v, type: INT}
			|- name: EntityA1Stub
			|  id: 2
			|  parent: EntityAStub
			|  abstract: false
			|  fields:
			|  - {name: v, type: INT}
			|  - {name: s, type: LONG}
			|- name: EntityA2Stub
			|  id: 3
			|  parent: EntityAStub
			|  abstract: true
			|  fields:
			|  - {name: v, type: INT}
			|  - {name: s, type: STRING}
			|- name: EntityA3Stub
			|  id: 4
			|  parent: EntityA2Stub
			|  abstract: false
			|  fields:
			|  - {name: v, type: INT}
			|  - {name: s, type: STRING}
			|- name: EntityBStub
			|  id: 5
			|  parent: null
			|  abstract: false
			|  fields:
			|  - {name: v, type: STRING}
			|- name: EntityB1Stub
			|  id: 6
			|  parent: EntityBStub
			|  abstract: false
			|  fields:
			|  - {name: v, type: STRING}
			|  - {name: s, type: BOOLEAN}
			|- name: EntityB2Stub
			|  id: 7
			|  parent: EntityBStub
			|  abstract: false
			|  fields:
			|  - {name: v, type: STRING}
			|  - {name: s, type: BYTE}
			|- name: EntityB3Stub
			|  id: 8
			|  parent: EntityB2Stub
			|  abstract: false
			|  fields:
			|  - {name: v, type: STRING}
			|  - {name: s, type: BYTE}
			|- name: EntityCStub
			|  id: 9
			|  parent: null
			|  abstract: false
			|  fields:
			|  - {name: vBoolean, type: BOOLEAN}
			|  - {name: vByte, type: BYTE}
			|  - {name: vInt, type: INT}
			|  - {name: vLong, type: LONG}
			|  - {name: vDouble, type: DOUBLE}
			|  - {name: vBooleanArray, type: BOOLEAN_ARRAY}
			|  - {name: vByteArray, type: BYTE_ARRAY}
			|  - {name: vIntArray, type: INT_ARRAY}
			|  - {name: vLongArray, type: LONG_ARRAY}
			|  - {name: vDoubleArray, type: DOUBLE_ARRAY}
			|  - {name: vString, type: STRING}
			|  - {name: vEntityA, type: EntityAStub}
			|  - {name: vEntityA1, type: EntityA1Stub}
			|  - {name: vEntityA2, type: EntityA2Stub}
			|  - {name: vEntityANullable, type: EntityAStub!}
			|  - {name: vEntityA1Nullable, type: EntityA1Stub!}
			|  - {name: vEntityA2Nullable, type: EntityA2Stub!}
			|  - {name: vEntityB, type: EntityBStub}
			|  - {name: vEntityB1, type: EntityB1Stub}
			|  - {name: vEntityB2, type: EntityB2Stub}
			|  - {name: vEntityBNullable, type: EntityBStub!}
			|  - {name: vEntityB1Nullable, type: EntityB1Stub!}
			|  - {name: vEntityB2Nullable, type: EntityB2Stub!}
			|  - {name: vEnum, type: EnumStub}
			|  - {name: lBoolean, type: BOOLEAN*}
			|  - {name: lByte, type: BYTE*}
			|  - {name: lInt, type: INT*}
			|  - {name: lLong, type: LONG*}
			|  - {name: lDouble, type: DOUBLE*}
			|  - {name: lBooleanArray, type: BOOLEAN_ARRAY*}
			|  - {name: lByteArray, type: BYTE_ARRAY*}
			|  - {name: lIntArray, type: INT_ARRAY*}
			|  - {name: lLongArray, type: LONG_ARRAY*}
			|  - {name: lDoubleArray, type: DOUBLE_ARRAY*}
			|  - {name: lString, type: STRING*}
			|  - {name: lEntityA, type: EntityAStub*}
			|  - {name: lEntityB, type: EntityBStub*}
			|  - {name: lEnum, type: EnumStub*}
			|  - {name: llBoolean, type: BOOLEAN**}
			|  - {name: llByte, type: BYTE**}
			|  - {name: llInt, type: INT**}
			|  - {name: llLong, type: LONG**}
			|  - {name: llDouble, type: DOUBLE**}
			|  - {name: llBooleanArray, type: BOOLEAN_ARRAY**}
			|  - {name: llByteArray, type: BYTE_ARRAY**}
			|  - {name: llIntArray, type: INT_ARRAY**}
			|  - {name: llLongArray, type: LONG_ARRAY**}
			|  - {name: llDoubleArray, type: DOUBLE_ARRAY**}
			|  - {name: llString, type: STRING**}
			|  - {name: llEntityA, type: EntityAStub**}
			|  - {name: llEntityB, type: EntityBStub**}
			|  - {name: llEnum, type: EnumStub**}
			|
		""".trimMargin()
		
		val actual = model.save()
		
		assertEquals(expected, actual)
		assertEquals(Change.COMPATIBLY, model.change)
	}
	
	@Test
	fun `Change model as ABSENT`() {
		
		val data = """
			|lastEntityId: 9
			|structures:
			|- name: EnumStub
			|  lastValueId: 2
			|  values:
			|  - {id: 1, name: A}
			|  - {id: 2, name: B}
			|- name: EntityAStub
			|  id: 1
			|  parent: null
			|  abstract: true
			|  fields:
			|  - {name: v, type: INT}
			|- name: EntityA1Stub
			|  id: 2
			|  parent: EntityAStub
			|  abstract: false
			|  fields:
			|  - {name: v, type: INT}
			|  - {name: s, type: LONG}
			|- name: EntityA2Stub
			|  id: 3
			|  parent: EntityAStub
			|  abstract: true
			|  fields:
			|  - {name: v, type: INT}
			|  - {name: s, type: STRING}
			|- name: EntityA3Stub
			|  id: 4
			|  parent: EntityA2Stub
			|  abstract: false
			|  fields:
			|  - {name: v, type: INT}
			|  - {name: s, type: STRING}
			|- name: EntityBStub
			|  id: 5
			|  parent: null
			|  abstract: false
			|  fields:
			|  - {name: v, type: STRING}
			|- name: EntityB1Stub
			|  id: 6
			|  parent: EntityBStub
			|  abstract: false
			|  fields:
			|  - {name: v, type: STRING}
			|  - {name: s, type: BOOLEAN}
			|- name: EntityB2Stub
			|  id: 7
			|  parent: EntityBStub
			|  abstract: false
			|  fields:
			|  - {name: v, type: STRING}
			|  - {name: s, type: BYTE}
			|- name: EntityB3Stub
			|  id: 8
			|  parent: EntityB2Stub
			|  abstract: false
			|  fields:
			|  - {name: v, type: STRING}
			|  - {name: s, type: BYTE}
			|- name: EntityCStub
			|  id: 9
			|  parent: null
			|  abstract: false
			|  fields:
			|  - {name: vBoolean, type: BOOLEAN}
			|  - {name: vByte, type: BYTE}
			|  - {name: vInt, type: INT}
			|  - {name: vLong, type: LONG}
			|  - {name: vDouble, type: DOUBLE}
			|  - {name: vBooleanArray, type: BOOLEAN_ARRAY}
			|  - {name: vByteArray, type: BYTE_ARRAY}
			|  - {name: vIntArray, type: INT_ARRAY}
			|  - {name: vLongArray, type: LONG_ARRAY}
			|  - {name: vDoubleArray, type: DOUBLE_ARRAY}
			|  - {name: vString, type: STRING}
			|  - {name: vEntityA, type: EntityAStub}
			|  - {name: vEntityA1, type: EntityA1Stub}
			|  - {name: vEntityA2, type: EntityA2Stub}
			|  - {name: vEntityANullable, type: EntityAStub!}
			|  - {name: vEntityA1Nullable, type: EntityA1Stub!}
			|  - {name: vEntityA2Nullable, type: EntityA2Stub!}
			|  - {name: vEntityB, type: EntityBStub}
			|  - {name: vEntityB1, type: EntityB1Stub}
			|  - {name: vEntityB2, type: EntityB2Stub}
			|  - {name: vEntityBNullable, type: EntityBStub!}
			|  - {name: vEntityB1Nullable, type: EntityB1Stub!}
			|  - {name: vEntityB2Nullable, type: EntityB2Stub!}
			|  - {name: vEnum, type: EnumStub}
			|  - {name: lBoolean, type: BOOLEAN*}
			|  - {name: lByte, type: BYTE*}
			|  - {name: lInt, type: INT*}
			|  - {name: lLong, type: LONG*}
			|  - {name: lDouble, type: DOUBLE*}
			|  - {name: lBooleanArray, type: BOOLEAN_ARRAY*}
			|  - {name: lByteArray, type: BYTE_ARRAY*}
			|  - {name: lIntArray, type: INT_ARRAY*}
			|  - {name: lLongArray, type: LONG_ARRAY*}
			|  - {name: lDoubleArray, type: DOUBLE_ARRAY*}
			|  - {name: lString, type: STRING*}
			|  - {name: lEntityA, type: EntityAStub*}
			|  - {name: lEntityB, type: EntityBStub*}
			|  - {name: lEnum, type: EnumStub*}
			|  - {name: llBoolean, type: BOOLEAN**}
			|  - {name: llByte, type: BYTE**}
			|  - {name: llInt, type: INT**}
			|  - {name: llLong, type: LONG**}
			|  - {name: llDouble, type: DOUBLE**}
			|  - {name: llBooleanArray, type: BOOLEAN_ARRAY**}
			|  - {name: llByteArray, type: BYTE_ARRAY**}
			|  - {name: llIntArray, type: INT_ARRAY**}
			|  - {name: llLongArray, type: LONG_ARRAY**}
			|  - {name: llDoubleArray, type: DOUBLE_ARRAY**}
			|  - {name: llString, type: STRING**}
			|  - {name: llEntityA, type: EntityAStub**}
			|  - {name: llEntityB, type: EntityBStub**}
			|  - {name: llEnum, type: EnumStub**}
			|
		""".trimMargin()
		
		val model = Model()
		model.load(data)
		
		val path = Paths.get(javaClass.getResource("/example/stubs.kt").toURI())
		
		KotlinModelLoader(model, KotlinSource(path), "example").load()
		
		assertEquals(Change.ABSENT, model.change)
	}
	
	@Test
	fun `Change model as COMPATIBLY`() {
		
		val data = """
			|lastEntityId: 9
			|structures:
			|- name: EnumStub
			|  lastValueId: 2
			|  values:
			|  - {id: 1, name: A}
			|  - {id: 2, name: B}
			|- name: EntityAStub
			|  id: 1
			|  parent: null
			|  abstract: true
			|  fields:
			|  - {name: v, type: INT}
			|- name: EntityA1Stub
			|  id: 2
			|  parent: EntityAStub
			|  abstract: false
			|  fields:
			|  - {name: v, type: INT}
			|  - {name: s, type: LONG}
			|- name: EntityA2Stub
			|  id: 3
			|  parent: EntityAStub
			|  abstract: true
			|  fields:
			|  - {name: v, type: INT}
			|  - {name: s, type: STRING}
			|- name: EntityA3Stub
			|  id: 4
			|  parent: EntityA2Stub
			|  abstract: false
			|  fields:
			|  - {name: v, type: INT}
			|  - {name: s, type: STRING}
			|- name: EntityBStub
			|  id: 5
			|  parent: null
			|  abstract: false
			|  fields:
			|  - {name: v, type: STRING}
			|- name: EntityB1Stub
			|  id: 6
			|  parent: EntityBStub
			|  abstract: false
			|  fields:
			|  - {name: v, type: STRING}
			|  - {name: s, type: BOOLEAN}
			|- name: EntityB2Stub
			|  id: 7
			|  parent: EntityBStub
			|  abstract: false
			|  fields:
			|  - {name: v, type: STRING}
			|  - {name: s, type: BYTE}
			|
		""".trimMargin()
		
		val model = Model()
		model.load(data)
		
		val path = Paths.get(javaClass.getResource("/example/stubs.kt").toURI())
		
		KotlinModelLoader(model, KotlinSource(path), "example").load()
		
		assertEquals(Change.COMPATIBLY, model.change)
	}
	
	@Test
	fun `Change model as FULL`() {
		
		val data = """
			|lastEntityId: 9
			|structures:
			|- name: EnumStub
			|  lastValueId: 2
			|  values:
			|  - {id: 1, name: A}
			|  - {id: 2, name: B}
			|- name: EntityAStub
			|  id: 1
			|  parent: null
			|  abstract: true
			|  fields:
			|  - {name: v, type: INT}
			|- name: EntityA1Stub
			|  id: 2
			|  parent: EntityAStub
			|  abstract: false
			|  fields:
			|  - {name: v, type: INT}
			|  - {name: s, type: LONG}
			|- name: EntityA2Stub
			|  id: 3
			|  parent: EntityAStub
			|  abstract: true
			|  fields:
			|  - {name: v, type: INT}
			|  - {name: s, type: STRING}
			|- name: EntityA3Stub
			|  id: 4
			|  parent: EntityA2Stub
			|  abstract: false
			|  fields:
			|  - {name: v, type: INT}
			|  - {name: s, type: STRING}
			|- name: EntityBStub
			|  id: 5
			|  parent: null
			|  abstract: false
			|  fields:
			|  - {name: v, type: STRING}
			|- name: EntityB1Stub
			|  id: 6
			|  parent: EntityBStub
			|  abstract: false
			|  fields:
			|  - {name: v, type: STRING}
			|  - {name: s, type: BOOLEAN}
			|- name: EntityB2Stub
			|  id: 7
			|  parent: EntityBStub
			|  abstract: false
			|  fields:
			|  - {name: v, type: STRING}
			|  - {name: s, type: BYTE}
			|- name: EntityB3Stub
			|  id: 8
			|  parent: EntityB2Stub
			|  abstract: false
			|  fields:
			|  - {name: v, type: STRING}
			|  - {name: s, type: BYTE}
			|- name: EntityCStub
			|  id: 9
			|  parent: null
			|  abstract: false
			|  fields:
			|  - {name: vBoolean, type: BOOLEAN}
			|  - {name: vByte, type: BYTE}
			|  - {name: vInt, type: INT}
			|  - {name: vLong, type: LONG}
			|  - {name: vDouble, type: DOUBLE}
			|  - {name: vBooleanArray, type: BOOLEAN_ARRAY}
			|  - {name: vByteArray, type: BYTE_ARRAY}
			|  - {name: vIntArray, type: INT_ARRAY}
			|  - {name: vLongArray, type: LONG_ARRAY}
			|  - {name: vDoubleArray, type: DOUBLE_ARRAY}
			|  - {name: vString, type: STRING}
			|  - {name: vEntityA, type: EntityAStub}
			|  - {name: vEntityA1, type: EntityA1Stub}
			|  - {name: vEntityA2, type: EntityA2Stub}
			|  - {name: vEntityANullable, type: EntityAStub!}
			|  - {name: vEntityA1Nullable, type: EntityA1Stub!}
			|  - {name: vEntityA2Nullable, type: EntityA2Stub!}
			|  - {name: vEntityB, type: EntityBStub}
			|  - {name: vEntityB1, type: EntityB1Stub}
			|  - {name: vEntityB2, type: EntityB2Stub}
			|  - {name: vEntityBNullable, type: EntityBStub!}
			|  - {name: vEntityB1Nullable, type: EntityB1Stub!}
			|  - {name: vEntityB2Nullable, type: EntityB2Stub!}
			|  - {name: vEnum, type: EnumStub}
			|  - {name: lBoolean, type: BOOLEAN*}
			|  - {name: lByte, type: BYTE*}
			|  - {name: lInt, type: INT*}
			|  - {name: lLong, type: LONG*}
			|  - {name: lDouble, type: DOUBLE*}
			|  - {name: lBooleanArray, type: BOOLEAN_ARRAY*}
			|  - {name: lByteArray, type: BYTE_ARRAY*}
			|  - {name: lIntArray, type: INT_ARRAY*}
			|  - {name: lLongArray, type: LONG_ARRAY*}
			|  - {name: lDoubleArray, type: DOUBLE_ARRAY*}
			|  - {name: lString, type: STRING*}
			|  - {name: lEntityA, type: EntityAStub*}
			|  - {name: lEntityB, type: EntityBStub*}
			|  - {name: lEnum, type: EnumStub*}
			|  - {name: llBoolean, type: BOOLEAN**}
			|  - {name: llByte, type: BYTE**}
			|  - {name: llInt, type: INT**}
			|  - {name: llLong, type: LONG**}
			|  - {name: llDouble, type: DOUBLE**}
			|  - {name: llBooleanArray, type: BOOLEAN_ARRAY**}
			|  - {name: llByteArray, type: BYTE_ARRAY**}
			|  - {name: llIntArray, type: INT_ARRAY**}
			|  - {name: llLongArray, type: LONG_ARRAY**}
			|  - {name: llDoubleArray, type: DOUBLE_ARRAY**}
			|  - {name: llString, type: STRING**}
			|  - {name: llEntityA, type: EntityAStub**}
			|  - {name: llEntityB, type: EntityBStub**}
			|
		""".trimMargin()
		
		val model = Model()
		model.load(data)
		
		val path = Paths.get(javaClass.getResource("/example/stubs.kt").toURI())
		
		KotlinModelLoader(model, KotlinSource(path), "example").load()
		
		assertEquals(Change.FULL, model.change)
	}
}