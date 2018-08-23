#!/usr/bin/groovy

def call(body) {
    def config = [:]
    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = config
    body()


    echo "angularExecuteLinting global variable parameters\n" +
            "------------------------------------------------\n" +
            "config.useLintingFlags: ${config.useLintingFlags} \n" +
            "config.theLintingDefaultFlags: ${config.theLintingDefaultFlags} \n" +
            "config.theLintingFlags: ${config.theLintingFlags} \n"
            "config.theAngularCliLocalPath: ${config.theAngularCliLocalPath} \n" +
            "config.theInstallGloballyAngularCli: ${config.theInstallGloballyAngularCli} \n"




    Boolean useLintingFlags = false
    def lintingFlags = config.theLintingDefaultFlags
    Boolean installGloballyAngularCli = config.theInstallGloballyAngularCli
    def angularCliLocalPath = config.theAngularCliLocalPath

    echo "Building linting"


    if (config.useLintingFlags) {
        useLintingFlags = config.useLintingFlags.toBoolean()
    }


    if (useLintingFlags) {
        lintingFlags = config.theLintingFlags
    } else {
        echo "Linting flags default: ${lintingFlags}"
    }

    echo "useLintingFlags: ${useLintingFlags}"
    echo "lintingFlags: ${lintingFlags}"



    if (installGloballyAngularCli) {
        sh "ng lint ${lintingFlags}"
    } else {
        sh "${angularCliLocalPath}ng int ${lintingFlags}"
    }
}
