plugins {
  id("uk.gov.justice.hmpps.gradle-spring-boot") version "5.1.4"
  kotlin("plugin.spring") version "1.8.21"
}

configurations {
  testImplementation { exclude(group = "org.junit.vintage") }
}

dependencies {
  implementation("org.springframework.boot:spring-boot-starter-webflux")
  implementation("org.seleniumhq.selenium:selenium-java:4.9.1")
  implementation("org.seleniumhq.selenium:selenium-http-jdk-client:4.9.1")
  implementation("io.github.bonigarcia:webdrivermanager:5.3.1")
  implementation("commons-io:commons-io:2.7") // Address CVE-2021-29425
  implementation("org.bouncycastle:bcprov-jdk15on:1.67") // Address CVE-2020-15522

  implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core")
  implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")

  testImplementation("com.natpryce:hamkrest:1.8.0.1")
}

java {
  toolchain.languageVersion.set(JavaLanguageVersion.of(18))
}

tasks {
  withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions {
      jvmTarget = "18"
    }
  }
}
