#!/usr/bin/groovy

def call(body) {
    def config = [:]
    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = config
    body()


    echo "angularExecuteUnitTesting global variable parameters\n" +
            "----------------------------------------------------\n" +
            "config.useUnitTestingFlags: ${config.useUnitTestingFlags} \n" +
            "config.theUnitTestingDefaultFlags: ${config.theUnitTestingDefaultFlags} \n" +
            "config.theUnitTestingFlags: ${config.theUnitTestingFlags} \n" +
            "config.useUnitTestingKarmaConfigurationFileSpecificPath: ${config.useUnitTestingKarmaConfigurationFileSpecificPath} \n" +
            "config.theUnitTestingKarmaConfigurationFileSpecificPath: ${config.theUnitTestingKarmaConfigurationFileSpecificPath} \n" +
            "config.theAngularCliLocalPath: ${config.theAngularCliLocalPath} \n" +
            "config.theInstallGloballyAngularCli: ${config.theInstallGloballyAngularCli} \n"




    Boolean useUnitTestingFlags = false
    Boolean useUnitTestingKarmaConfigurationFileSpecificPath = false
    def unitTestingFlags = config.theUnitTestingDefaultFlags
    Boolean installGloballyAngularCli = config.theInstallGloballyAngularCli
    def angularCliLocalPath = config.theAngularCliLocalPath
    def filesKarmaConfJs
    def fileKarmaConfPath = ""

    echo "Building unit test"

    //show file karma.conf.js content
    if (config.useUnitTestingKarmaConfigurationFileSpecificPath) {
        useUnitTestingKarmaConfigurationFileSpecificPath = config.useUnitTestingKarmaConfigurationFileSpecificPath.toBoolean()
    }

    if (useUnitTestingKarmaConfigurationFileSpecificPath) {

        if (config.theUnitTestingKarmaConfigurationFileSpecificPath) {
            fileKarmaConfPath = config.theUnitTestingKarmaConfigurationFileSpecificPath + "/karma.conf.js"
        } else {
            fileKarmaConfPath = "karma.conf.js"
        }

        echo "fileKarmaConfPath: ${fileKarmaConfPath}"

        filesKarmaConfJs = findFiles(glob: "${fileKarmaConfPath}")

        if (filesKarmaConfJs.length == 0) {
            currentBuild.result = "FAILED"
            throw new hudson.AbortException("karma.conf.js is not found on path ${fileKarmaConfPath} of the project") as Throwable
        }

    } else {

        filesKarmaConfJs = findFiles(glob: 'karma.conf.js')

        if (filesKarmaConfJs.length == 0) {
            filesKarmaConfJs = findFiles(glob: 'src/karma.conf.js')
        }

        if (filesKarmaConfJs.length == 0) {
            currentBuild.result = "FAILED"
            throw new hudson.AbortException("Error. karma.conf.js is not found on root directory or src directory of the project") as Throwable
        }
    }

    echo """Karma configuration file path:  ${filesKarmaConfJs[0].path} """
    def karmaConfJSFile = readFile file: "${filesKarmaConfJs[0].path}"

    echo "karma.conf.js content:\n" +
            "${karmaConfJSFile}"




    if (config.useUnitTestingFlags) {
        useUnitTestingFlags = config.useUnitTestingFlags.toBoolean()
    }

    if (useUnitTestingFlags) {
        unitTestingFlags = config.theUnitTestingFlags
    } else {
        echo "Unit testing flags default: ${unitTestingFlags}"
    }

    echo "useUnitTestingFlags: ${useUnitTestingFlags}"
    echo "unitTestingFlags: ${unitTestingFlags}"



    if (installGloballyAngularCli) {
        sh "ng test ${unitTestingFlags}"
    } else {
        sh "${angularCliLocalPath}ng test ${unitTestingFlags}"
    }
}
