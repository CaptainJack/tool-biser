plugins {
	kotlin("multiplatform") version "1.3.71" apply false
	id("nebula.release") version "14.1.0"
	id("ru.capjack.bintray") version "1.0.0"
}

subprojects {
	group = "ru.capjack.tool"
	
	repositories {
		jcenter()
		maven("https://dl.bintray.com/capjack/public")
	}
}