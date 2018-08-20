#!/usr/bin/groovy

def call(body) {
    def config = [:]
    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = config
    body()


    echo "angularNPMRegistryPublish global variable parameters\n" +
            "----------------------------------------------------\n" +
            "config.thePackageTarball: ${config.thePackageTarball} \n" +
            "config.theAngularNPMLocalRepositoryURL: ${config.theAngularNPMLocalRepositoryURL} \n"

    def packageTarball = config.thePackageTarball
    def angularNPMLocalRepositoryURL = config.theAngularNPMLocalRepositoryURL

    echo "Publishing artifact to a NPM registry"

    echo 'Get NPM config registry'
    sh 'npm config get registry'

    echo 'Test NPM repository authentication'
    sh 'npm whoami'

    try {
        echo 'Publish package on Artifactory NPM registry'

        sh "npm publish ${packageTarball} --registry ${angularNPMLocalRepositoryURL}"

    } catch (exc) {
        echo 'There is an error on publish package'
        def exc_message = exc.message
        echo "${exc_message}"

        currentBuild.result = "FAILED"
        throw new hudson.AbortException("Error publishing package on NPM registry") as Throwable
    }
}
