plugins {
	java
	id("org.springframework.boot") version "3.4.1"
	id("io.spring.dependency-management") version "1.1.7"
}

fun getGitHash(): String {
	return providers.exec {
		commandLine("git", "rev-parse", "--short", "HEAD")
	}.standardOutput.asText.get().trim()
}

group = "kr.hhplus.be"
version = getGitHash()

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(17)
	}
}

repositories {
	mavenCentral()
}

dependencyManagement {
	imports {
		mavenBom("org.springframework.cloud:spring-cloud-dependencies:2024.0.0")
	}
}

dependencies {
    // Spring
	implementation("org.springframework.boot:spring-boot-starter-actuator")
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.springframework.boot:spring-boot-starter-web")

    // DB
	runtimeOnly("com.mysql:mysql-connector-j")

	//Lombok
	compileOnly("org.projectlombok:lombok")
	annotationProcessor("org.projectlombok:lombok")

	// Swagger
	implementation ("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.1.0")

	//QueryDSL
	implementation("com.querydsl:querydsl-jpa:5.0.0:jakarta")
	implementation("com.querydsl:querydsl-apt:5.0.0:jakarta")
	annotationProcessor("com.querydsl:querydsl-apt:5.0.0:jakarta")
	annotationProcessor("jakarta.persistence:jakarta.persistence-api")

	// Redis
	implementation("org.springframework.boot:spring-boot-starter-data-redis")

	// Redisson
	implementation("org.redisson:redisson-spring-boot-starter:3.25.2")

    // Test
    testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("org.springframework.boot:spring-boot-testcontainers")
	testImplementation("org.testcontainers:junit-jupiter")
	testImplementation("org.testcontainers:mysql")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.withType<Test> {
	useJUnitPlatform()
	systemProperty("user.timezone", "UTC")
}

// 생성된 Q클래스 경로 지정
val querydslSrcDir = layout.buildDirectory.dir("generated/querydsl").get().asFile

// 소스 세트 설정
sourceSets {
	main {
		java.srcDir(querydslSrcDir)
	}
}

// JavaCompile 태스크 설정
tasks.withType<JavaCompile>().configureEach {
	options.annotationProcessorPath = configurations.annotationProcessor.get()

	if (name == "compileJava") {
		options.annotationProcessorGeneratedSourcesDirectory = querydslSrcDir
	}
}

// clean 태스크 확장
tasks.named<Delete>("clean") {
	delete(querydslSrcDir)
}