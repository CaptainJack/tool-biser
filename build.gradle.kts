plugins {
	kotlin("multiplatform") version "1.4.20" apply false
	id("nebula.release") version "15.3.0"
	id("ru.capjack.bintray") version "1.0.0"
}

subprojects {
	group = "ru.capjack.tool"
	
	repositories {
		jcenter()
		maven("https://dl.bintray.com/capjack/public")
	}
}