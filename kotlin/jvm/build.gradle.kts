import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	id("kotlin-platform-jvm")
	id("ru.capjack.degos.publish")
}

dependencies {
	expectedBy(project(":biser-kotlin-common"))
	implementation(kotlin("stdlib-jdk8"))
	implementation(kotlin("reflect"))
	testImplementation(kotlin("test-junit"))
}