pluginManagement {

    val jacksonVersion = "2.14.1"
    val jgitVersion = "6.4.0.202211300538-r"
    val commonsIoVersion = "2.11.0"
    val slf4jVersion = "1.7.36"
    val xzVersion = "1.9"

    buildscript {
        repositories {
            gradlePluginPortal()
            mavenCentral()
        }
        dependencies {
            classpath("org.slf4j:slf4j-simple:$slf4jVersion")
            classpath("commons-io:commons-io:$commonsIoVersion")
            classpath("com.fasterxml.jackson.module:jackson-module-kotlin:$jacksonVersion")
            classpath( "com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:$jacksonVersion")
            classpath( "com.fasterxml.jackson.datatype:jackson-datatype-jsr310:$jacksonVersion")
            classpath( "org.eclipse.jgit:org.eclipse.jgit:$jgitVersion")
            classpath( "org.eclipse.jgit:org.eclipse.jgit.archive:$jgitVersion")
            classpath( "org.eclipse.jgit:org.eclipse.jgit.ssh.jsch:$jgitVersion")
            classpath( "org.tukaani:xz:$xzVersion")
        }
    }
    plugins { id("org.jbake.site").version(extra["jbake_gradle_plugin_version"].toString()) }
}

dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
    }
}
rootProject.name = "cheroliv.com"
include("codes")
