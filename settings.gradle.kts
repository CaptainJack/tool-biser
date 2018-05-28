rootProject.name = "biser"

include(
	"kotlin:common",
	"kotlin:js",
	"kotlin:jvm"
)

pluginManagement {
	repositories.maven("http://artifactory.capjack.ru/public")
	resolutionStrategy.eachPlugin {
		val id = requested.id.id
		when {
			id.startsWith("kotlin")     ->
				useModule("org.jetbrains.kotlin:kotlin-gradle-plugin:${requested.version}")
			id.startsWith("ru.capjack.degos") ->
				useModule("ru.capjack.degos:degos-${id.substringAfterLast('.')}:${requested.version}")
		}
	}
}