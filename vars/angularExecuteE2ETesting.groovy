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
            "config.theAngularCliLocalPath: ${config.theAngularCliLocalPath} \n" +
            "config.theInstallGloballyAngularCli: ${config.theInstallGloballyAngularCli} \n"


    Boolean useE2ETestingFlags = false
    def e2eTestingFlags = config.theE2ETestingDefaultFlags
    Boolean installGloballyAngularCli = config.theInstallGloballyAngularCli
    def angularCliLocalPath = config.theAngularCliLocalPath

    echo "Building e2e test"

    //show file karma.conf.js content
    def filesKarmaConfJs = findFiles(glob: 'karma.conf.js')

    if (filesKarmaConfJs.length == 0) {
        filesKarmaConfJs = findFiles(glob: 'src/karma.conf.js')
    }

    if (filesKarmaConfJs.length == 0) {
        currentBuild.result = "FAILED"
        throw new hudson.AbortException("Error. karma.conf.js is not found on root directory or src directory of the project") as Throwable
    }

    echo """Karma configuration file path:  ${filesKarmaConfJs[0].path} """
    def karmaConfJSFile = readFile file: "${filesKarmaConfJs[0].path}"

    echo "karma.conf.js content:\n" +
            "${karmaConfJSFile}"



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
        sh "ng e2e ${unitTestingFlags}"
    } else {
        sh "${angularCliLocalPath}ng e2e ${unitTestingFlags}"
    }
}