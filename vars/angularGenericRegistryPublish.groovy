#!/usr/bin/groovy

def call(body) {
    def config = [:]
    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = config
    body()


    echo "angularGenericRegistryPublish global variable parameters\n" +
            "--------------------------------------------------------\n" +
            "config.artCredentialsId: ${config.artCredentialsId} \n" +
            "config.theArtifactoryURL: ${config.theArtifactoryURL} \n" +
            "config.theAngularGenericLocalRepositoryURL: ${config.theAngularGenericLocalRepositoryURL} \n" +
            "config.thePackageName: ${config.thePackageName} \n" +
            "config.thePackageTarball: ${config.thePackageTarball} \n" +
            "config.theUtils: ${config.theUtils} \n"

    def artifactoryURL = config.theArtifactoryURL
    def angularGenericLocalRepositoryURL = config.theAngularGenericLocalRepositoryURL
    def packageName = config.thePackageName
    def packageTarball = config.thePackageTarball

    echo "Publishing artifact to a generic registry"

    try {
        echo 'Publish package on Artifactory generic registry'

        withCredentials([string(credentialsId: "${config.artCredentialsId}", variable: 'ARTIFACTORY_TOKEN')]) {
            echo "Checking credentials on Artifactory"
            sh "curl -H X-JFrog-Art-Api:${ARTIFACTORY_TOKEN} ${artifactoryURL}api/system/ping"

            echo "Deploying artifact on Artifactory gemeric repository"
            sh "curl -H X-JFrog-Art-Api:${ARTIFACTORY_TOKEN} -X PUT ${angularGenericLocalRepositoryURL}${packageName}/${packageTarball} -T ${packageTarball}"

        }

    } catch (exc) {
        echo 'There is an error on publish package'
        def exc_message = exc.message
        echo "${exc_message}"

        currentBuild.result = "FAILED"
        throw new hudson.AbortException("Error publishing package on generic registry") as Throwable
    }
}
