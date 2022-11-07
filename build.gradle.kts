plugins {
	kotlin("multiplatform") version "1.7.20"
	id("ru.capjack.publisher") version "1.0.0"
}

group = "ru.capjack.tool"

repositories {
	mavenCentral()
	mavenCapjack()
}

kotlin {
	jvm {
		compilations.all { kotlinOptions.jvmTarget = "17" }
	}
	js(IR) {
		browser()
	}
	
	sourceSets {
		get("commonMain").dependencies {
			implementation("ru.capjack.tool:tool-lang:1.13.1")
			implementation("ru.capjack.tool:tool-utils:1.9.0")
			implementation("ru.capjack.tool:tool-io:1.2.1")
		}
		get("commonTest").dependencies {
			implementation(kotlin("test"))
		}
	}
}
