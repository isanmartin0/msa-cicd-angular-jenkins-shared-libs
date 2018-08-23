#!/usr/bin/groovy

def call(body) {
    def config = [:]
    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = config
    body()


    echo "angularExecuteE2ETesting global variable parameters\n" +
            "---------------------------------------------------\n" +
            "config.useE2ETestingFlags: ${config.useE2ETestingFlags} \n" +
            "config.theE2ETestingDefaultFlags: ${config.theE2ETestingDefaultFlags} \n" +
            "config.theE2ETestingFlags: ${config.theE2ETestingFlags} \n" +
            "config.useE2ETestingProtractorConfigurationFileSpecificPath: ${config.useE2ETestingProtractorConfigurationFileSpecificPath} \n" +
            "config.theE2ETestingProtractorConfigurationFileSpecificPath: ${config.theE2ETestingProtractorConfigurationFileSpecificPath} \n" +
            "config.theAngularCliLocalPath: ${config.theAngularCliLocalPath} \n" +
            "config.theInstallGloballyAngularCli: ${config.theInstallGloballyAngularCli} \n"


    Boolean useE2ETestingFlags = false
    Boolean useE2ETestingProtractorConfigurationFileSpecificPath = false
    def e2eTestingFlags = config.theE2ETestingDefaultFlags
    Boolean installGloballyAngularCli = config.theInstallGloballyAngularCli
    def angularCliLocalPath = config.theAngularCliLocalPath
    def filesProtractorConfJs

    echo "Building e2e test"

    //show file protractor.conf.js content
    if (config.useE2ETestingProtractorConfigurationFileSpecificPath) {
        useE2ETestingProtractorConfigurationFileSpecificPath = config.useE2ETestingProtractorConfigurationFileSpecificPath.toBoolean()
    }


    if (useUnitTestingKarmaConfigurationFileSpecificPath) {

        filesProtractorConfJs = findFiles(glob: "${config.theE2ETestingProtractorConfigurationFileSpecificPath}/protractor.conf.js")

        if (filesProtractorConfJs.length == 0) {
            currentBuild.result = "FAILED"
            throw new hudson.AbortException("Error. protractor.conf.js is not found on path ${config.theE2ETestingProtractorConfigurationFileSpecificPath} of the project") as Throwable
        }

    } else {

        filesProtractorConfJs = findFiles(glob: 'protractor.conf.js')

        if (filesProtractorConfJs.length == 0) {
            filesProtractorConfJs = findFiles(glob: 'e2e/protractor.conf.js')
        }

        if (filesProtractorConfJs.length == 0) {
            currentBuild.result = "FAILED"
            throw new hudson.AbortException("Error. protractor.conf.js is not found on root directory or e2e directory of the project") as Throwable
        }
    }



    echo """Protractor configuration file path:  ${filesProtractorConfJs[0].path} """
    def protractorConfJSFile = readFile file: "${filesProtractorConfJs[0].path}"

    echo "protractor.conf.js content:\n" +
            "${protractorConfJSFile}"



    if (config.useE2ETestingFlags) {
        useE2ETestingFlags = config.useE2ETestingFlags.toBoolean()
    }

    if (useE2ETestingFlags) {
        e2eTestingFlags = config.theE2ETestingFlags
    } else {
        echo "e2e testing flags default: ${e2eTestingFlags}"
    }

    echo "useE2ETestingFlags: ${useE2ETestingFlags}"
    echo "e2eTestingFlags: ${e2eTestingFlags}"



    if (installGloballyAngularCli) {
        sh "ng e2e ${e2eTestingFlags}"
    } else {
        sh "${angularCliLocalPath}ng e2e ${e2eTestingFlags}"
    }
}
