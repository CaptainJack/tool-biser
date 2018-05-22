plugins {
	id("kotlin-platform-common")
}

dependencies {
	implementation(kotlin("stdlib-common"))
	testImplementation(kotlin("test-common"))
	testImplementation(kotlin("test-annotations-common"))
}