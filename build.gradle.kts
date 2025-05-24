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
    mainClass.set("org.chat.gui.ChatGUI")
}
tasks.test {
    useJUnitPlatform()
}