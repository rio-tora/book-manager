plugins {
	kotlin("jvm") version "2.2.21"
	kotlin("plugin.spring") version "2.2.21"
	id("org.springframework.boot") version "4.0.2"
	id("io.spring.dependency-management") version "1.1.7"
	id("nu.studer.jooq") version "9.0"
}

group = "com.example"
version = "0.0.1-SNAPSHOT"
description = "Book Management API for the coding test. (Kotlin, Spring Boot, jOOQ)"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(21)
	}
}

repositories {
	mavenCentral()
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("org.springframework.boot:spring-boot-starter-jooq")
	implementation("org.springframework.boot:spring-boot-starter-jdbc")
	implementation("org.springframework.boot:spring-boot-starter-validation")

	implementation("org.flywaydb:flyway-core")
	runtimeOnly("org.flywaydb:flyway-database-postgresql")
	runtimeOnly("org.postgresql:postgresql")

	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin")

	developmentOnly("org.springframework.boot:spring-boot-docker-compose")

	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")

	// jOOQ codegen 用
	jooqGenerator("org.postgresql:postgresql:42.7.7")
}

kotlin {
	compilerOptions {
		freeCompilerArgs.addAll("-Xjsr305=strict", "-Xannotation-default-target=param-property")
	}
}

tasks.withType<Test> {
	useJUnitPlatform()
}

// jOOQ code generation
jooq {
	version.set("3.19.29")
	configurations {
		create("main") {
			// いったん自動生成をコンパイル時に走らせない（手動で generateJooq する）
			generateSchemaSourceOnCompilation.set(false)

			jooqConfiguration.apply {
				jdbc.apply {
					driver = "org.postgresql.Driver"
					url = "jdbc:postgresql://localhost:5432/mydatabase"
					user = "myuser"
					password = "secret"
				}
				generator.apply {
					// Kotlinプロジェクトでも、まずは安定のJava生成でOK
					name = "org.jooq.codegen.DefaultGenerator"
					database.apply {
						name = "org.jooq.meta.postgres.PostgresDatabase"
						inputSchema = "public"
						includes = ".*"
						excludes = "flyway_schema_history"
					}
					target.apply {
						packageName = "com.example.bookmanager.jooq"
						directory = "build/generated-src/jooq/main"
					}
				}
			}
		}
	}
}

sourceSets {
	main {
		java {
			srcDir("build/generated-src/jooq/main")
		}
	}
}

tasks.named("compileKotlin") {
	dependsOn("generateJooq")
}

tasks.named("compileJava") {
	dependsOn("generateJooq")
}
