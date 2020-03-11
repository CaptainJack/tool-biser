rootProject.name = "tool-io-biser"

include(
	"runtime",
	"generator"
)

project(":runtime").name = rootProject.name
project(":generator").name = rootProject.name + "-generator"