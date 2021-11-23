plugins {
  id(ScriptPlugins.infrastructure)
}

buildscript {
  repositories {
    google()
    jcenter()
  }

  dependencies {
    classpath (BuildPlugins.androidGradlePlugin)
    classpath (BuildPlugins.kotlinGradlePlugin)
    classpath (BuildPlugins.hiltGradlePlugin)
    classpath("com.android.tools.build:gradle:4.2.2")
  }
}

allprojects {
  repositories {
    google()
    jcenter()
  }
}
