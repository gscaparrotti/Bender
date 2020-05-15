plugins {
	id("org.springframework.boot") version "2.2.6.RELEASE"
	id("io.spring.dependency-management") version "1.0.9.RELEASE"
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
	implementation("com.miglayout:miglayout-swing:5.2")
	implementation("com.github.gscaparrotti:BenderModel:d3ecf50ee8")
	implementation("com.google.guava:guava:20.0")
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.springframework.boot:spring-boot-starter-web")
	runtimeOnly("com.h2database:h2")
	testImplementation("junit:junit:4.12")
	testImplementation("com.vmlens:concurrent-junit:1.0.2")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
}
