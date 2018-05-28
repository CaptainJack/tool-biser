import com.moowork.gradle.node.NodeExtension
import com.moowork.gradle.node.npm.NpmTask
import com.moowork.gradle.node.task.NodeTask
import org.jetbrains.kotlin.gradle.tasks.Kotlin2JsCompile
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	id("kotlin-platform-js")
	id("com.moowork.node") version "1.2.0"
	id("ru.capjack.degos.publish")
}

dependencies {
	expectedBy(project(":biser-common"))
	implementation(kotlin("stdlib-js"))
	testImplementation(kotlin("test-js"))
}

tasks.withType<Kotlin2JsCompile> {
	kotlinOptions {
		moduleKind = "umd"
		sourceMap = true
		sourceMapEmbedSources = "always"
	}
}

configure<NodeExtension> {
	version = "8.11.1"
	download = true
	nodeModulesDir = buildDir.resolve("node_modules")
	npmWorkDir = buildDir.resolve("npm")
}

task<Copy>("populateNodeModules") {
	dependsOn("compileKotlin2Js")
	
	from((tasks.getByName("compileKotlin2Js") as Kotlin2JsCompile).destinationDir)
	
	configurations.testRuntimeClasspath.forEach {
		from(zipTree(it.absolutePath).matching { include("*.js") })
	}
	
	into("$buildDir/node_modules")
}

task<NpmTask>("installQunit") {
	val version = "2.6.0"
	inputs.property("qunitVersion", version)
	outputs.dir(file("$buildDir/node_modules/qunit"))
	setArgs(listOf("install", "qunit@${version}"))
}

task<NodeTask>("runQunit") {
	dependsOn("compileTestKotlin2Js", "populateNodeModules", "installQunit")
	setScript(file("$buildDir/node_modules/qunit/bin/qunit"))
	setArgs(listOf(projectDir.toPath().relativize(file((tasks.getByName("compileTestKotlin2Js") as Kotlin2JsCompile).outputFile).toPath())))
}

tasks.getByName("test").dependsOn("runQunit")