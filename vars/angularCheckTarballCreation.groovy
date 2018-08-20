#!/usr/bin/groovy

def call(body) {
    def config = [:]
    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = config
    body()


    echo "angularCheckTarballCreation global variable parameters\n" +
            "------------------------------------------------------\n" +
            "config.thePackageTarball: ${config.thePackageTarball} \n"

    def packageTarball = config.thePackageTarball

    try {
        echo 'Check tarball creation ...'
        tarball_creation_script = $/eval "ls ${packageTarball}"/$
        echo "${tarball_creation_script}"
        def tarball_creation_view = sh(script: "${tarball_creation_script}", returnStdout: true).toString().trim()
        echo "${tarball_creation_view}"
    } catch (exc) {
        echo 'There is an error on tarball creation'
        def exc_message = exc.message
        echo "${exc_message}"
        currentBuild.result = "FAILED"
        throw new hudson.AbortException("Error checking existence of tarball") as Throwable
    }
}
