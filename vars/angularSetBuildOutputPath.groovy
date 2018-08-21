#!/usr/bin/groovy

def call(body) {
    def config = [:]
    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = config
    body()


    echo "angularSetBuildOutputPath global variable parameters\n" +
            "----------------------------------------------------\n" +
            "config.theBuildDefaultOutputPath: ${config.theBuildDefaultOutputPath} \n" +
            "config.useSpecificOutputPath: ${config.useSpecificOutputPath} \n" +
            "config.theBuildSpecificOutputPath: ${config.theBuildSpecificOutputPath} \n"

    def buildOutputPath = config.theBuildDefaultOutputPath
    Boolean useSpecificOutputPath = false

    if (config.useSpecificOutputPath) {
        useSpecificOutputPath = config.useSpecificOutputPath.toBoolean()
    }

    if (useSpecificOutputPath) {
        if (config.theBuildSpecificOutputPath) {
            buildOutputPath = [config.theBuildSpecificOutputPath]
        }
    }

    echo "The build output path: ${buildOutputPath}"

    return buildOutputPath
}
