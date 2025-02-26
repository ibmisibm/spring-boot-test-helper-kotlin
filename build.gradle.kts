import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

buildscript {
    repositories {
        jcenter()
        mavenCentral()
    }
    dependencies {
        classpath("com.karumi.kotlinsnapshot:plugin:2.2.2")
    }
}

plugins {
    id("org.springframework.boot") version "2.4.1"
    id("io.spring.dependency-management") version "1.0.10.RELEASE"
    id("org.jetbrains.dokka") version "0.10.1"
    id("com.jfrog.bintray") version "1.8.5"
    kotlin("jvm") version "1.4.21"
    kotlin("plugin.spring") version "1.4.21"
    `maven-publish`
}

apply(plugin = "com.karumi.kotlin-snapshot")

group = "common.marvel"
version = "0.1.11"

configure<JavaPluginExtension> {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

repositories {
    jcenter()
    mavenCentral()
    maven("https://dl.bintray.com/common-marvel/kotlin")
}

dependencies {
    api("common.marvel:spring-boot-helper-kotlin:0.1.10")
    implementation("org.springframework.boot:spring-boot-starter")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310")
    implementation("io.kotlintest:kotlintest-runner-junit5:3.4.2")
    implementation("io.kotlintest:kotlintest-extensions-spring:3.4.2")
    implementation("com.karumi.kotlinsnapshot:core:2.2.2")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "11"
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.dokka {
    outputFormat = "html"
    outputDirectory = "$buildDir/javadoc"
}

val dokkaJar by tasks.creating(Jar::class) {
    group = JavaBasePlugin.DOCUMENTATION_GROUP
    description = "Assembles Kotlin docs with Dokka"
    archiveClassifier.set("javadoc")
    from(tasks.dokka)
}

val sourceJar by tasks.creating(Jar::class) {
    group = JavaBasePlugin.DOCUMENTATION_GROUP
    description = "Source"
    archiveClassifier.set("sources")
    from(sourceSets.getByName("main").allSource)
}

publishing {
    publications {
        create<MavenPublication>("default") {
            from(components["java"])
            artifact(sourceJar)
            artifact(dokkaJar)
        }
    }
}

bintray {
    user = System.getenv("BINTRAY_USER")
    key = System.getenv("BINTRAY_KEY")
    publish = true
    setPublications("default")
    pkg(
        delegateClosureOf<com.jfrog.bintray.gradle.BintrayExtension.PackageConfig> {
            userOrg = "common-marvel"
            repo = "kotlin"
            name = "spring-boot-test-helper-kotlin"
            websiteUrl = "https://github.com/CommonMarvel/spring-boot-test-helper-kotlin"
            githubRepo = "CommonMarvel/spring-boot-test-helper-kotlin"
            vcsUrl = "https://github.com/CommonMarvel/spring-boot-test-helper-kotlin.git"
            description = ""
            setLabels("kotlin")
            setLicenses("MIT")
            desc = description
        }
    )
}

tasks.getByName("bintrayUpload").enabled = true
tasks.getByName("publish").enabled = false
tasks.getByName("bootJar").enabled = false
tasks.getByName("jar").enabled = true
