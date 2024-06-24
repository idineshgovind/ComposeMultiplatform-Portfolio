import org.jetbrains.kotlin.gradle.targets.js.dsl.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpackConfig
import java.net.URI

plugins {
	alias(libs.plugins.kotlinMultiplatform)
	alias(libs.plugins.androidLibrary)
	alias(libs.plugins.jetbrainsCompose)
	alias(libs.plugins.compose.compiler)
}

kotlin {
	androidTarget {
		publishLibraryVariants("release")
	}

	jvm {
		compilations.all {
			kotlinOptions.jvmTarget = "17"
		}
	}

	js(IR) {
		browser()
		binaries.executable()
	}

	@OptIn(ExperimentalWasmDsl::class)
	wasmJs {
		moduleName = "mpfilepicker"
		browser {
			commonWebpackConfig {
				outputFileName = "mpfilepicker.js"
				devServer = (devServer ?: KotlinWebpackConfig.DevServer()).apply {
					static = (static ?: mutableListOf()).apply {
						// Serve sources to debug inside browser
						add(project.projectDir.path)
					}
				}
			}
		}
		binaries.executable()
	}

	macosX64()

	listOf(
		iosX64(),
		iosArm64(),
		iosSimulatorArm64(),
	).forEach {
		it.binaries.framework {
			baseName = "MPFilePicker"
		}
	}

	sourceSets {
		commonMain.dependencies {
			api(compose.runtime)
			api(compose.foundation)
		}

		commonTest.dependencies {
			implementation(kotlin("test"))
		}

		androidMain.dependencies {
			api(compose.uiTooling)
			api(compose.preview)
			api(compose.material)
			api(libs.androidx.appcompat)
			api(libs.androidx.core.ktx)
			implementation("androidx.activity:activity-compose:1.8.2")
			implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.8.0")

		}

		jvmMain.dependencies {
			api(compose.uiTooling)
			api(compose.preview)
			api(compose.material)

			val lwjglVersion = "3.3.1"
			listOf("lwjgl", "lwjgl-tinyfd").forEach { lwjglDep ->
				implementation("org.lwjgl:${lwjglDep}:${lwjglVersion}")
				listOf(
					"natives-windows",
					"natives-windows-x86",
					"natives-windows-arm64",
					"natives-macos",
					"natives-macos-arm64",
					"natives-linux",
					"natives-linux-arm64",
					"natives-linux-arm32"
				).forEach { native ->
					runtimeOnly("org.lwjgl:${lwjglDep}:${lwjglVersion}:${native}")
				}
			}
		}
		val jvmTest by getting
		val jsMain by getting
		val wasmJsMain by getting
	}

	@Suppress("OPT_IN_USAGE")
	compilerOptions {
		freeCompilerArgs = listOf("-Xexpect-actual-classes")
	}

	val javadocJar by tasks.registering(Jar::class) {
		archiveClassifier.set("javadoc")
	}

}

android {
	namespace = "com.dinesh.libraries.mpfilepicker"
	compileSdk = 34
	sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
	defaultConfig {
		minSdk = 21
	}
	compileOptions {
		sourceCompatibility = JavaVersion.VERSION_17
		targetCompatibility = JavaVersion.VERSION_17
	}
}

compose.experimental {
	web.application {}
}
