import xyz.jpenilla.resourcefactory.bukkit.Permission
import xyz.jpenilla.resourcefactory.paper.PaperPluginYaml.Load

val projectVersion: String by project
version = projectVersion

plugins {
    id("java")
    id("com.gradleup.shadow") version "9.0.0-beta17"
    id("xyz.jpenilla.resource-factory-paper-convention") version "1.3.0"
    id("xyz.jpenilla.run-paper") version "2.3.1"
    id("xyz.jpenilla.gremlin-gradle") version "0.0.8"
    id("net.kyori.indra.licenser.spotless") version "3.1.3"
}

java {
    toolchain.languageVersion = JavaLanguageVersion.of(21)
}

dependencies {
    // Paper
    compileOnly("io.papermc.paper:paper-api:1.21.4-R0.1-SNAPSHOT") {
        exclude("net.md-5")
    }

    // Integrations
    compileOnly("io.github.miniplaceholders:miniplaceholders-api:2.3.0") // MiniPlaceholders

    // Libraries
    runtimeDownload("org.spongepowered:configurate-yaml:4.2.0") // config
    runtimeDownload("net.kyori:adventure-serializer-configurate4:4.23.0") // config serializer
}

val mainPackage = "$group.${rootProject.name.lowercase()}"
paperPluginYaml {
    author = "Namiu/うにたろう"
    website = "https://github.com/CraftersLife-Dev"
    apiVersion = "1.21"

    main = "$mainPackage.TemplatePlugin" // TODO: JavaPluginの具象クラス名に変えてね
    bootstrapper = "$mainPackage.TemplateBootstrap" // TODO: PluginBootstrapの具象クラス名に変えてね
    loader = "$mainPackage.TemplateLoader" // TODO: PluginLoaderの具象クラス名に変えてね

    permissions {
        register("${rootProject.name.lowercase()}.command.reload") {
            description = "Reloads ${rootProject.name}'s config."
            default = Permission.Default.OP
        }
    }

    dependencies {
        server("MiniPlaceholders", Load.BEFORE, false)
        server("LuckPerms", Load.BEFORE, false)
    }
}

indraSpotlessLicenser {
    licenseHeaderFile(rootProject.file("LICENSE_HEADER"))
    property("name", rootProject.name)
    property("author", paperPluginYaml.author)
    property("contributors", paperPluginYaml.contributors)
}

configurations {
    compileOnly {
        extendsFrom(configurations.runtimeDownload.get())
    }
}

tasks {
    shadowJar {
        archiveClassifier = null as String?
        gremlin {
            listOf("xyz.jpenilla.gremlin")
                .forEach {
                    relocate(it, "$mainPackage.libs.$it")
                }
        }
    }

    runServer {
        // runディレクトリの中にlog4j2.xmlを突っ込むとログの設定を変更可能
        // Paper: https://github.com/PaperMC/Paper/blob/main/paper-server/src/main/resources/log4j2.xml
        systemProperty("log4j.configurationFile", "log4j2.xml")
        minecraftVersion("1.21.4")
        downloadPlugins {
            modrinth("luckperms", "v5.5.0-bukkit")
            modrinth("miniplaceholders", "wck4v0R0")
            modrinth("miniplaceholders-placeholderapi-expansion", "1.2.0")
            hangar("PlaceholderAPI", "2.11.6")
        }
    }

    writeDependencies {
        repos.add("https://repo.papermc.io/repository/maven-public/")
        repos.add("https://repo.maven.apache.org/maven2/")
    }
}
