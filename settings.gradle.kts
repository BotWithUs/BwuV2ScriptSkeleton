rootProject.name = "BWU2VScriptsExample"

fun includeModulesFrom(rootDir: File, parent: String = "") {
    fileTree(rootDir).matching {
        include("**/build.gradle.kts")
    }.forEach { gradleFile ->
        val moduleDir = gradleFile.parentFile
        val moduleName = ":$parent${moduleDir.relativeTo(rootDir).path}".replace(File.separator, ":")
        include(moduleName)
    }
}

includeModulesFrom(rootDir)