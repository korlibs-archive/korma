val hasAndroid: Boolean by rootProject.extra

val pname = "korma"

File(projectDir, "$pname/src/commonMain/kotlin/com/soywiz/$pname/internal/${pname.capitalize()}Version.kt").apply {
	parentFile.mkdirs()
	val newText = "package com.soywiz.$pname.internal\n\ninternal const val ${pname.toUpperCase()}_VERSION = \"${project.version}\""
	if (!exists() || (readText() != newText)) writeText(newText)
}

val projDeps = Deps().run { LinkedHashMap<String, List<Dep>>().apply {
    val base = listOf(kds)
    this["korma"] = base
    this["korma-shape-ops"] = base + Dep(project = ":korma")
    this["korma-triangulate-pathfind"] = base + Dep(project = ":korma")
    this["korma-integration-tests"] = base + Dep(project = ":korma") + Dep(project = ":korma-shape-ops") + Dep(project = ":korma-triangulate-pathfind")
} }

/////////////////////////////////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////////////////////////////////

class Deps {
    val kds = DepKorlib("kds")
}

/////////////////////////////////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////////////////////////////////

fun DepKorlib(name: String) = Dep("com.soywiz:$name:${project.property("${name}Version")}")
class Dep(val commonName: String? = null, val project: String ? = null, val register: (DependencyHandlerScope.() -> Unit)? = null)

val ALL_TARGETS = listOf("android", "iosArm64", "iosArm32", "iosX64", "js", "jvm", "linuxX64", "macosX64", "mingwX64", "metadata")

fun DependencyHandler.addCommon(group: String, name: String, version: String, targets: List<String> = ALL_TARGETS) {
    for (target in targets) {
        val suffix = "-${target.toLowerCase()}"
        val base = when (target) {
            "metadata" -> "common"
            else -> target
        }

        val packed = "$group:$name$suffix:$version"
        add("${base}MainApi", packed)
        add("${base}TestImplementation", packed)
    }
}

fun DependencyHandler.addCommon(dependency: String, targets: List<String> = ALL_TARGETS) {
    val (group, name, version) = dependency.split(":", limit = 3)
    return addCommon(group, name, version, targets)
}

subprojects {
	val deps = projDeps[project.name]
	if (deps != null) {
		dependencies {
			for (dep in deps) {
				if (dep.commonName != null) {
                    addCommon(dep.commonName)
				}
				if (dep.project != null) {
					add("commonMainApi", rootProject.project(dep.project))
					add("commonTestImplementation", rootProject.project(dep.project))
				}
				dep.register?.invoke(this)
			}
		}
	}
}
