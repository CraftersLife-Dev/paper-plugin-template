import xyz.jpenilla.resourcefactory.bukkit.Permission
import xyz.jpenilla.resourcefactory.paper.PaperPluginYaml.Load

val projectVersion: String by project
version = projectVersion

plugins {
    id("java")
    id("checkstyle")
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
    compileOnly(libs.paper.api)

    // Integrations
    compileOnly(libs.mini.placeholders)

    // Libraries
    compileOnly(libs.configurate.yaml) // Paperに組み込んである
    runtimeDownload(libs.adventure.serializer.configurate)
    implementation(libs.kotonoha.translatable.message)
    implementation(libs.kotonoha.translatable.message.extra.miniplaceholders)

    // Annotation processor
    annotationProcessor(libs.kotonoha.resourcebundle.generator.processor)
}

val mainPackage = "io.github.crafterslife.dev.papertemplate" // TODO: パッケージ名を変更 (実際のパッケージ名も変更を忘れないように！)
paperPluginYaml {
    name = "PaperTemplate" // TODO: プラグイン名を変更
    author = "Namiu (うにたろう)" // TODO: 自分の名前に変更
    website = "https://github.com/CraftersLife-Dev"
    apiVersion = "1.21.10"

    main = "$mainPackage.core.JavaPluginImpl"
    bootstrapper = "$mainPackage.core.PluginBootstrapImpl"
    loader = "$mainPackage.core.PluginLoaderImpl"

    permissions {
        register("${paperPluginYaml.name.get().lowercase()}.command.admin") {
            description = "${paperPluginYaml.name}の管理者系コマンド"
            default = Permission.Default.OP
        }
    }

    dependencies {
        bootstrap("MiniPlaceholders", Load.BEFORE, false)
        server("LuckPerms", Load.BEFORE, false)
    }
}

indraSpotlessLicenser {
    licenseHeaderFile(rootProject.file("LICENSE_HEADER"))
    property("name", paperPluginYaml.name)
    property("author", paperPluginYaml.author)
    property("contributors", paperPluginYaml.contributors)
}

configurations {
    compileOnly {
        extendsFrom(configurations.runtimeDownload.get())
    }
}

tasks {
    compileJava {
        options.compilerArgs.add("-parameters")
    }

    shadowJar {
        archiveBaseName = paperPluginYaml.name
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
        minecraftVersion("1.21.10")
        downloadPlugins {
            modrinth("luckperms", "v5.5.0-bukkit")
            modrinth("miniplaceholders", "4zOT6txC")
            hangar("PlaceholderAPI", "2.11.6")
        }
    }

    writeDependencies {
        repos.add("https://repo.papermc.io/repository/maven-public/")
        repos.add("https://repo.maven.apache.org/maven2/")
    }

    checkstyle {
        toolVersion = libs.versions.check.style.get()
        configDirectory = rootDir.resolve(".checkstyle")
    }
}
