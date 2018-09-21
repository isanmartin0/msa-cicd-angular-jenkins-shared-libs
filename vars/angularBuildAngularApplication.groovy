#!/usr/bin/groovy

def call(body) {
    def config = [:]
    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = config
    body()


    echo "angularBuildAngularApplication global variable parameters\n" +
            "---------------------------------------------------------\n" +
            "config.theIsProdFlag: ${config.theIsProdFlag} \n" +
            "config.theBuildEnvirnomentFlag: ${config.theBuildEnvirnomentFlag} \n" +
            "config.theBuildEnvironment: ${config.theBuildEnvironment} \n" +
            "config.useBuildFlags: ${config.useBuildFlags} \n" +
            "config.theBuildDefaultFlags: ${config.theBuildDefaultFlags} \n" +
            "config.theBuildFlags: ${config.theBuildFlags} \n" +
            "config.theAngularCliLocalPath: ${config.theAngularCliLocalPath} \n" +
            "config.theInstallGloballyAngularCli: ${config.theInstallGloballyAngularCli} \n"


    echo "Building angular application"

    /************************************************
     ************* BUILD PARAMETERS *****************
     ************************************************/

    Boolean withProdFlag = false
    def withProdFlagStr = ''
    def buildEnvironmentFlag = config.theBuildEnvirnomentFlag
    def buildEnvironment = config.theBuildEnvironment
    def buildEnvironmentStr = ''
    Boolean useBuildFlags = false
    def buildFlags = config.theBuildDefaultFlags
    Boolean installGloballyAngularCli = config.theInstallGloballyAngularCli
    def angularCliLocalPath = config.theAngularCliLocalPath

    if (config.theIsProdFlag) {
        withProdFlag = config.theIsProdFlag.toBoolean()
    }

    if (withProdFlag) {
        withProdFlagStr = '--prod'
    }

    buildEnvironmentStr = "${buildEnvironmentFlag}${buildEnvironment}"

    if (config.useBuildFlags) {
        useBuildFlags = config.useBuildFlags.toBoolean()
    }

    if (useBuildFlags) {
        buildFlags = config.theBuildFlags
    } else {
        echo "Build parameters default: ${buildFlags}"
    }

    echo "withProdFlagStr: ${withProdFlagStr}"
    echo "buildEnvironmentStr: ${buildEnvironmentStr}"
    echo "useBuildFlags: ${useBuildFlags}"
    echo "buildFlags: ${buildFlags}"

    if (installGloballyAngularCli) {
        sh "ng build ${withProdFlagStr} ${buildEnvironmentStr} ${buildFlags}"
    } else {
        sh "${angularCliLocalPath}ng build ${withProdFlagStr} ${buildEnvironmentStr} ${buildFlags}"
    }
}
