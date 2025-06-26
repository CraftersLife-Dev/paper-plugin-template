import xyz.jpenilla.resourcefactory.bukkit.Permission
import xyz.jpenilla.resourcefactory.paper.PaperPluginYaml.Load

val projectVersion: String by project
version = projectVersion

plugins {
    id("java")
    alias(libs.plugins.shadow)
    alias(libs.plugins.resource.factory)
    alias(libs.plugins.run.paper)
    alias(libs.plugins.gremlin)
    alias(libs.plugins.indra.licenser.spotless)
}

java {
    toolchain.languageVersion = JavaLanguageVersion.of(21)
}

dependencies {
    // Paper
    compileOnly(libs.paper.api) {
        exclude("net.md-5") // すごい邪魔
    }

    // Integrations
    compileOnly(libs.mini.placeholders)

    // Libraries
    compileOnly(libs.configurate.yaml) // Paperに組み込んであるためダウンロードは不要
    runtimeDownload(libs.adventure.serializer.configurate)
}

val mainPackage = "$group.${rootProject.name.lowercase()}"
paperPluginYaml {
    name = rootProject.name
    author = "Namiu/うにたろう" // TODO: 自分の名前に変えてね
    website = "https://github.com/CraftersLife-Dev"
    apiVersion = "1.21"

    main = "$mainPackage.minecraft.TemplatePlugin" // TODO: JavaPluginの具象クラス名に変えてね
    bootstrapper = "$mainPackage.minecraft.TemplateBootstrap" // TODO: PluginBootstrapの具象クラス名に変えてね
    loader = "$mainPackage.minecraft.TemplateLoader" // TODO: PluginLoaderの具象クラス名に変えてね

    permissions {
        register("${rootProject.name.lowercase()}.command.admin") {
            description = "${paperPluginYaml.name}の管理者系コマンド"
            default = Permission.Default.OP
        }
        register("${rootProject.name.lowercase()}.command.admin.reload") {
            description = "${paperPluginYaml.name}の設定を再読み込みするコマンド"
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
        minecraftVersion(libs.versions.minecraft.get())
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
