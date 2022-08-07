pluginManagement {
    buildscript {
        repositories {
            mavenLocal()
            gradlePluginPortal()
            mavenCentral()
            maven(url = "https://repo.eclipse.org/content/groups/releases/")
        }
        dependencies {
            val jacksonVersion = "2.13.2"
            val jgitVersion = "6.1.0.202203080745-r"
            val commonsIoVersion = "2.11.0"
            val slf4jVersion = "1.7.36"
            val xzVersion = "1.9"
            classpath(dependencyNotation = "com.fasterxml.jackson.module:jackson-module-kotlin:$jacksonVersion")
            classpath(dependencyNotation = "com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:$jacksonVersion")
            classpath(dependencyNotation = "com.fasterxml.jackson.datatype:jackson-datatype-jsr310:$jacksonVersion")

            classpath(dependencyNotation = "org.eclipse.jgit:org.eclipse.jgit:$jgitVersion")
            classpath(dependencyNotation = "org.eclipse.jgit:org.eclipse.jgit.archive:$jgitVersion")
            classpath(dependencyNotation = "org.eclipse.jgit:org.eclipse.jgit.ssh.jsch:$jgitVersion")
            classpath(dependencyNotation = "org.tukaani:xz:$xzVersion")

            classpath(dependencyNotation = "commons-io:commons-io:$commonsIoVersion")
            classpath(dependencyNotation = "org.slf4j:slf4j-simple:$slf4jVersion")
        }
    }
    plugins { id("org.jbake.site").version(extra["jbake_gradle_plugin_version"].toString()) }
}
rootProject.name = "cheroliv.com"
