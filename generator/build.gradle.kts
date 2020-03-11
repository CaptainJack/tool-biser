plugins {
	kotlin("jvm")
	id("ru.capjack.bintray")
}

kotlin.target {
	compilations.all { kotlinOptions.jvmTarget = "11" }
}

dependencies {
	implementation(kotlin("stdlib-jdk8"))
	implementation(kotlin("compiler"))
	implementation("org.yaml:snakeyaml:1.25")
	implementation("ru.capjack.tool:tool-logging:1.0.1")
	
	testImplementation(kotlin("test-junit"))
	testImplementation("ch.qos.logback:logback-classic:1.2.3")
	testImplementation(project(":tool-io-biser"))
}