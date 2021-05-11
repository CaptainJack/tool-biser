plugins {
	kotlin("multiplatform") version "1.5.0"
	id("ru.capjack.publisher") version "0.1.0"
}

group = "ru.capjack.tool"

repositories {
	mavenCentral()
	mavenCapjack()
}

kotlin {
	jvm {
		compilations.all { kotlinOptions.jvmTarget = "11" }
	}
	js(IR) {
		browser()
	}
	
	sourceSets {
		get("commonMain").dependencies {
			implementation("ru.capjack.tool:tool-lang:1.11.1")
			implementation("ru.capjack.tool:tool-utils:1.6.1")
			implementation("ru.capjack.tool:tool-io:0.13.0")
		}
		get("commonTest").dependencies {
			implementation(kotlin("test"))
		}
	}
}
