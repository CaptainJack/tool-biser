plugins {
	kotlin("jvm")
	id("ru.capjack.bintray")
}

kotlin.target {
	compilations.all { kotlinOptions.jvmTarget = "1.8" }
}

dependencies {
	implementation(kotlin("compiler-embeddable"))
	implementation("org.yaml:snakeyaml:1.25")
	implementation("org.slf4j:slf4j-api:1.7.26")
	
	testImplementation(kotlin("test-junit"))
	testImplementation("ch.qos.logback:logback-classic:1.2.3")
	testImplementation(project(":tool-io-biser"))
}