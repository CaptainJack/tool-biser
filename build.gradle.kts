plugins {
	kotlin("multiplatform") version "1.3.61"
	id("nebula.release") version "14.0.4"
	id("ru.capjack.bintray") version "1.0.0"
}

group = "ru.capjack.tool"

repositories {
	jcenter()
	maven("https://dl.bintray.com/capjack/public")
}

kotlin {
	jvm {
		compilations.all { kotlinOptions.jvmTarget = "11" }
	}
	js {
		compilations.all { kotlinOptions.sourceMap = false }
		browser()
	}
	
	sourceSets {
		get("commonMain").dependencies {
			implementation(kotlin("stdlib-common"))
			implementation("ru.capjack.tool:tool-io:0.5.0")
			implementation("ru.capjack.tool:tool-lang:1.1.0")
		}
		get("commonTest").dependencies {
			implementation(kotlin("test-common"))
			implementation(kotlin("test-annotations-common"))
		}
		
		get("jvmMain").dependencies {
			implementation(kotlin("stdlib-jdk8"))
		}
		get("jvmTest").dependencies {
			implementation(kotlin("test-junit"))
		}
		
		get("jsMain").dependencies {
			implementation(kotlin("stdlib-js"))
		}
		get("jsTest").dependencies {
			implementation(kotlin("test-js"))
		}
	}
}