#!/usr/bin/groovy

def call(body) {
    def config = [:]
    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = config
    body()


    echo "angularBuildAngularApplication global variable parameters\n" +
            "---------------------------------------------------------\n" +
            "config.useBuildProdFlags: ${config.useBuildProdFlags} \n" +
            "config.theBuildProdDefaultFlags: ${config.theBuildProdDefaultFlags} \n" +
            "config.theBuildProdFlags: ${config.theBuildProdFlags} \n" +
            "config.theAngularCliLocalPath: ${config.theAngularCliLocalPath} \n" +
            "config.theInstallGloballyAngularCli: ${config.theInstallGloballyAngularCli} \n"


    echo "Building angular application"

    /***********************************************************
     ************* BUILD PRODUCTION PARAMETERS *****************
     ***********************************************************/

    Boolean useBuildProdFlags = false
    def buildProdFlags = config.theBuildProdDefaultFlags
    Boolean installGloballyAngularCli = config.theInstallGloballyAngularCli
    def angularCliLocalPath = config.theAngularCliLocalPath

    if (config.useBuildProdFlags) {
        useBuildProdFlags = config.useBuildProdFlags.toBoolean()
    }

    if (useBuildProdFlags) {
        buildProdFlags = config.theBuildProdFlags
    } else {
        echo "Build prod parameters default: ${buildProdFlags}"
    }

    echo "useBuildProdFlags: ${useBuildProdFlags}"
    echo "buildProdFlags: ${buildProdFlags}"

    if (installGloballyAngularCli) {
        sh "ng build --prod ${buildProdFlags}"
    } else {
        sh "${angularCliLocalPath}ng build --prod ${buildProdFlags}"
    }
}
