plugins {
	kotlin("multiplatform") version "1.8.21"
	id("ru.capjack.publisher") version "1.1.0"
}

group = "ru.capjack.tool"

repositories {
	mavenCentral()
	mavenCapjack()
}

kotlin {
	jvm {
		jvmToolchain(17)
	}
	js(IR) {
		browser()
	}
	
	sourceSets {
		get("commonMain").dependencies {
			implementation("ru.capjack.tool:tool-lang:1.14.0")
			implementation("ru.capjack.tool:tool-utils:1.11.0")
			implementation("ru.capjack.tool:tool-io:1.3.0")
		}
		get("commonTest").dependencies {
			implementation(kotlin("test"))
		}
	}
}
