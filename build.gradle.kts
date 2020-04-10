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
	implementation("org.danilopianini:javalib-java7:[0, 1]")
	implementation("com.miglayout:miglayout:[3, 4]")
	implementation("jgoodies:forms:[1, 2]")
	implementation("com.github.gscaparrotti:BenderModel:8efa72d")
	implementation(files("./res"))
	testImplementation("junit:junit:4.12")
	testImplementation("com.vmlens:concurrent-junit:1.0.2")
}
