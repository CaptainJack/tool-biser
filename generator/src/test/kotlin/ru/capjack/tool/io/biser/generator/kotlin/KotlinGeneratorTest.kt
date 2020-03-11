package ru.capjack.tool.io.biser.generator.kotlin

import ru.capjack.tool.io.biser.generator.model.Model
import java.nio.file.Files
import java.nio.file.Path
import kotlin.test.Test
import kotlin.test.assertEquals

class KotlinGeneratorTest {
	@Test
	fun `Load model and generate`() {
		val model = Model()
		val path = Path.of(javaClass.getResource("/example/stubs.kt").toURI())
		val resourcesDir = path.parent.parent
		
		KotlinModelLoader(model, KotlinSource(path), "example").load()
		
		val generator = KotlinGenerator("example")

		val tmpDir = Files.createTempDirectory("biserTest")
//		val tmpDir = Path.of("src/test/resources").toAbsolutePath()
		
		for (structure in model.structures) {
			generator.registerEncoder(structure.type)
			generator.registerDecoder(structure.type)
		}
		
		generator.generate(tmpDir)
		
		val actualEncodersContent = Files.readString(tmpDir.resolve("example/ExampleEncoders.kt"))
		val actualDecodersContent = Files.readString(tmpDir.resolve("example/ExampleDecoders.kt"))
		
		val expectedEncodersContent = Files.readString(resourcesDir.resolve("example/ExampleEncoders.kt"))
		val expectedDecodersContent = Files.readString(resourcesDir.resolve("example/ExampleDecoders.kt"))
		
		assertEquals(expectedEncodersContent, actualEncodersContent)
		assertEquals(expectedDecodersContent, actualDecodersContent)
	}
}