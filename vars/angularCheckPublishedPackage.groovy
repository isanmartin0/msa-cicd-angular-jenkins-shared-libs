#!/usr/bin/groovy

def call(body) {
    def config = [:]
    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = config
    body()


    echo "angularCheckPublishedPackage global variable parameters\n" +
            "-------------------------------------------------------\n" +
            "config.thePackageTarball: ${config.thePackageTarball} \n"

    def packageTarball = config.thePackageTarball

    try {
        echo 'Get tarball location of package ...'
        tarball_script = $/eval "npm view  ${
            packageTag
        } dist.tarball | grep '${
            packageViewTarball
        }'"/$
        echo "${tarball_script}"
        def tarball_view = sh(script: "${tarball_script}", returnStdout: true).toString().trim()
        echo "${tarball_view}"
    } catch (exc) {
        echo 'There is an error on retrieving the tarball location'
        def exc_message = exc.message
        echo "${exc_message}"
        currentBuild.result = "FAILED"
        throw new hudson.AbortException("Error checking existence of package on NPM registry") as Throwable
    }
}
