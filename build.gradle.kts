plugins {
  id("uk.gov.justice.hmpps.gradle-spring-boot") version "5.1.4"
  kotlin("plugin.spring") version "1.8.21"
}

configurations {
  testImplementation { exclude(group = "org.junit.vintage") }
}

dependencies {
  implementation("org.springframework.boot:spring-boot-starter-webflux")
  implementation("org.seleniumhq.selenium:selenium-java:4.9.0")
  implementation("org.seleniumhq.selenium:selenium-http-jdk-client:4.9.0")
  implementation("io.github.bonigarcia:webdrivermanager:5.2.3")

  testImplementation("com.natpryce:hamkrest:1.8.0.1")
}

java {
  toolchain.languageVersion.set(JavaLanguageVersion.of(19))
}

tasks {
  withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions {
      jvmTarget = "19"
    }
  }
}
