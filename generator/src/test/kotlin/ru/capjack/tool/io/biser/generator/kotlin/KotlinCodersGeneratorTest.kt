package ru.capjack.tool.io.biser.generator.kotlin

import ru.capjack.tool.io.biser.generator.CodePath
import ru.capjack.tool.io.biser.generator.model.Model
import java.nio.file.Files
import java.nio.file.Paths
import kotlin.test.Test
import kotlin.test.assertEquals

class KotlinCodersGeneratorTest {
	@Test
	fun `Load model and generate`() {
		val model = Model()
		val path = Paths.get(javaClass.getResource("/example/stubs.kt").toURI())
		val resourcesDir = path.parent.parent
		
		KotlinModelLoader(model, KotlinSource(path), "example").load()
		
		val generator = KotlinCodersGenerator(CodePath("example"))
		
		val tmpDir = Files.createTempDirectory("biserTest")
//		val tmpDir = Path.of("src/test/resources").toAbsolutePath()
		
		for (structure in model.structures) {
			generator.registerEncoder(structure.type)
			generator.registerDecoder(structure.type)
		}
		
		generator.generate(tmpDir)
		
		val actualEncodersContent = tmpDir.resolve("example/ExampleEncoders.kt").toFile().readText()
		val actualDecodersContent = tmpDir.resolve("example/ExampleDecoders.kt").toFile().readText()
		
		val expectedEncodersContent = resourcesDir.resolve("example/ExampleEncoders.kt").toFile().readText()
		val expectedDecodersContent = resourcesDir.resolve("example/ExampleDecoders.kt").toFile().readText()
		
		assertEquals(expectedEncodersContent, actualEncodersContent)
		assertEquals(expectedDecodersContent, actualDecodersContent)
	}
}