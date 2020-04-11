plugins {
	java
}

configurations {
	all {
		resolutionStrategy.cacheChangingModulesFor(0, "seconds")
	}
}

repositories {
	mavenCentral()
	maven(url = "https://jitpack.io")
}

dependencies {
	implementation(files("$rootDir/res"))
	implementation("org.danilopianini:javalib-java7:[0, 1]")
	implementation("com.miglayout:miglayout:[3, 4]")
	implementation("com.github.gscaparrotti:BenderModel:8efa72d")
	testImplementation("junit:junit:4.12")
	testImplementation("com.vmlens:concurrent-junit:1.0.2")
}

tasks.register<Jar>("fatJar") {
	manifest {
		attributes(mapOf(
			"Implementation-Title" to "Bender",
			"Main-Class" to "application.AppLauncher"
		))
	}
	archiveBaseName.set("${rootProject.name}-redist")
	from(configurations.runtimeClasspath.get().map { if (it.isDirectory) it else zipTree(it) })
	with(tasks.jar.get() as CopySpec)
}
