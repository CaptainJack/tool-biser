plugins {
	kotlin("multiplatform")
	id("ru.capjack.bintray")
}

kotlin {
	jvm {
		compilations.all { kotlinOptions.jvmTarget = "1.8" }
	}
	js(IR) {
		browser()
	}
	
	sourceSets {
		get("commonMain").dependencies {
			implementation("ru.capjack.tool:tool-lang:1.6.1")
			implementation("ru.capjack.tool:tool-utils:1.1.0")
			implementation("ru.capjack.tool:tool-io:0.8.0")
		}
		get("commonTest").dependencies {
			implementation(kotlin("test-common"))
			implementation(kotlin("test-annotations-common"))
		}
		
		get("jvmTest").dependencies {
			implementation(kotlin("test-junit"))
		}
		
		get("jsTest").dependencies {
			implementation(kotlin("test-js"))
		}
	}
}
