import org.gradle.api.internal.file.pattern.PatternMatcherFactory.compile





plugins {
    id("com.android.application")
    id("com.google.android.libraries.mapsplatform.secrets-gradle-plugin")
}


android {
    namespace = "com.example.kcubewirelesscontroller"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.kcubewirelesscontroller"
        minSdk = 29
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }


    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {

    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("com.google.android.material:material:1.12.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("com.google.android.gms:play-services-location:21.3.0")
    implementation("com.google.android.gms:play-services-maps:18.2.0")
    implementation("com.google.android.gms:play-services-location:21.3.0")
    implementation(files("D:\\KcubeV2\\KcubeWirelessController\\app\\libs\\osmbonuspack_6.9.0.aar"))
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    implementation ("org.osmdroid:osmdroid-android:6.1.11")
    //implementation ("com.github.MKergall:osmbonuspack:6.9.0")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

   //implementation ("com.github.MKergall:osmbonuspack:6.9.0")

    implementation ("org.osmdroid:osmdroid-android:6.1.13")
    implementation ("org.apache.commons:commons-lang3:3.8.1")
    implementation ("com.google.code.gson:gson:2.8.6")
    implementation ("com.squareup.okhttp3:okhttp:4.7.2")
    //implementation files("libs/osmbonuspack_6.9.0.aar")
    implementation ("org.osmdroid:osmdroid-android:6.1.13")
    implementation ("org.apache.commons:commons-lang3:3.8.1")
    implementation ("com.google.code.gson:gson:2.8.6")
    implementation ("com.squareup.okhttp3:okhttp:4.7.2")
}