import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    kotlin("plugin.lombok") version "2.1.20"
    id("io.freefair.lombok") version "8.13"

}

kotlin {
    jvm("desktop")
    
    sourceSets {
        val desktopMain by getting
        
        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material)
            implementation(compose.ui)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)
            implementation(libs.androidx.lifecycle.viewmodel)
            implementation(libs.androidx.lifecycle.runtime.compose)

        }
        desktopMain.dependencies {
            implementation(compose.desktop.currentOs)
            implementation(libs.kotlinx.coroutines.swing)
            implementation("com.lowagie:itext:2.1.7")
            // https://mvnrepository.com/artifact/org.apache.xmlgraphics/batik-dom
            implementation("org.apache.xmlgraphics:batik-dom:1.19")
            // https://mvnrepository.com/artifact/org.apache.xmlgraphics/batik-svggen
            implementation("org.apache.xmlgraphics:batik-svggen:1.19")

        }
    }
}


compose.desktop {
    application {
        mainClass = "com.edu.nwalgo.MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "BioInfoTools"
            packageVersion = "1.1.0"
        }
    }
}
