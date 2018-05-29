plugins {
	id("kotlin-platform-common")
	id("ru.capjack.degos.publish")
}

dependencies {
	implementation(kotlin("stdlib-common"))
	testImplementation(kotlin("test-common"))
	testImplementation(kotlin("test-annotations-common"))
}