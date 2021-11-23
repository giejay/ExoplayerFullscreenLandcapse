//Include all the existent modules in the project
rootDir
    .walk()
    .maxDepth(3)
    .filter {
        it.name != "buildSrc" && it.isDirectory &&
                (file("${it.absolutePath}/build.gradle.kts").exists())
    }
    .forEach {
        include(":${it.name}")
    }

//include("libs/exoplayer/core")

include(":exoplayer-core")
include(":exoplayer-ui")
include(":exoplayer-rtsp")
include(":exoplayer-rtp")
include(":exoplayer-sdp")

project(":exoplayer-core"    ).projectDir = File(rootDir, "libs/exoplayer/core")
project(":exoplayer-ui"    ).projectDir = File(rootDir, "libs/exoplayer/ui")
project(":exoplayer-rtsp"    ).projectDir = File(rootDir, "libs/exoplayer/rtsp")
project(":exoplayer-rtp"    ).projectDir = File(rootDir, "libs/exoplayer/rtp")
project(":exoplayer-sdp"    ).projectDir = File(rootDir, "libs/exoplayer/sdp")
