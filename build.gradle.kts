plugins {
    java
    application
}

group = "org.chat"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")

    implementation("com.rabbitmq:amqp-client:5.21.0")
}

application {
    mainClass.set("org.chat.Main")
}
tasks.test {
    useJUnitPlatform()
}

tasks.register<JavaExec>("runGUI") {
    group = "Execution"
    description = "Run the GUI application"
    classpath = sourceSets["main"].runtimeClasspath
    mainClass.set("org.chat.gui.ChatGUI")

    // ./gradlew runGui --args="localhost main"
}

tasks.register<JavaExec>("runCLI") {
    group = "Execution"
    description = "Run the CLI in interactive mode"
    classpath = sourceSets["main"].runtimeClasspath
    mainClass.set("org.chat.cli.CliClient")

    standardInput = System.`in`

    jvmArgs = listOf(
        "-Djava.awt.headless=true",
        "-Djline.terminal=jline.UnixTerminal"
    )

    // ./gradlew runCLI --args="localhost main"
}