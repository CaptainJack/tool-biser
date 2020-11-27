package ru.capjack.tool.biser.generator.model

import kotlin.test.Test
import kotlin.test.assertEquals

class ModelTest {
	@Test
	@Suppress("GrazieInspection")
	fun `Load and save`() {
		val model = Model()
		
		val source = """
			|lastEntityId: 2
			|structures:
			|- name: EnumStub
			|  lastValueId: 2
			|  values:
			|  - {id: 1, name: A}
			|  - {id: 2, name: B}
		    |- name: EntityStub
			|  id: 1
			|  abstract: false
			|  parent: ~
			|  fields:
			|  - {name: id, type: INT}
			|  - {name: name, type: STRING}
			|  - {name: parent, type: EntityStub!}
			|- name: SubEntityStub
			|  id: 2
			|  abstract: false
			|  parent: EntityStub
			|  fields:
			|  - {name: id, type: INT}
			|  - {name: name, type: STRING}
			|  - {name: parent, type: EntityStub!}
			|  - {name: friends, type: EntityStub+}
			|  - {name: friends, type: EntityStub+}
		""".trimMargin()
		
		model.load(source)
		
		val actual = model.save()
		
		val expected = """
			|lastEntityId: 2
			|structures:
			|- name: EnumStub
			|  lastValueId: 2
			|  values:
			|  - {id: 1, name: A}
			|  - {id: 2, name: B}
		    |- name: EntityStub
			|  id: 1
			|  parent: null
			|  abstract: false
			|  fields:
			|  - {name: id, type: INT}
			|  - {name: name, type: STRING}
			|  - {name: parent, type: EntityStub!}
			|- name: SubEntityStub
			|  id: 2
			|  parent: EntityStub
			|  abstract: false
			|  fields:
			|  - {name: id, type: INT}
			|  - {name: name, type: STRING}
			|  - {name: parent, type: EntityStub!}
			|  - {name: friends, type: EntityStub+}
			|  - {name: friends, type: EntityStub+}
			|
		""".trimMargin()
		
		assertEquals(expected, actual)
	}
}