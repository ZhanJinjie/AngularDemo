import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.jetbrains.kotlin.plugin.allopen") version "1.3.72"
    id("org.springframework.boot") version "2.3.1.RELEASE"
    id("io.spring.dependency-management") version "1.0.9.RELEASE"
    id("org.jetbrains.kotlin.plugin.noarg") version "1.3.72"
    kotlin("jvm") version "1.3.72"
    kotlin("plugin.spring") version "1.3.72"
    id("org.asciidoctor.convert") version "1.5.9.2"
//    kotlin("kapt") version ("1.3.72")
//    id("org.jetbrains.kotlin.kapt") version ("1.3.72")

}

allOpen {
    annotation("org.springframework.data.mongodb.core.mapping.Document")
}

noArg {
    annotation("org.springframework.data.mongodb.core.mapping.Document")
}

group = "qx"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_1_8

repositories {
    mavenCentral()
}


dependencies {
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    implementation("org.springframework.boot:spring-boot-starter-data-mongodb")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("io.projectreactor.kotlin:reactor-kotlin-extensions")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactive")
    implementation("com.alibaba:fastjson:1.2.72")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")
//    implementation("org.mongodb.morphia:morphia:1.3.2")
//	implementation("io.reactivex.rxjava3:rxjava:3.0.4")
    testImplementation("org.springframework.boot:spring-boot-starter-test") {
        exclude(group = "org.junit.vintage", module = "junit-vintage-engine")
    }
    testImplementation("io.projectreactor:reactor-test")
    testImplementation("org.springframework.restdocs:spring-restdocs-webtestclient")
//    kapt("com.querydsl:querydsl-apt:4.3.1:morphia")
//    implementation("com.querydsl:querydsl-mongodb:4.3.1")
    asciidoctor("org.springframework.restdocs:spring-restdocs-asciidoctor:2.0.4.RELEASE")
    testImplementation("org.springframework.restdocs:spring-restdocs-mockmvc:2.0.4.RELEASE")
}

val snippetsDir by extra { file("build/generated-snippets") }

tasks {
    test {
        outputs.dir(snippetsDir)
    }
    asciidoctor {
        inputs.dir(snippetsDir)
        dependsOn(test)
    }
    bootJar {
        dependsOn(asciidoctor)
        from("${snippetsDir}/html5").into("static/docs")
    }
}
//kapt {
//    arguments {
//
//    }
//}

dependencyManagement {

}
tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "1.8"
    }
}
