#!/usr/bin/groovy

def call(body) {
    def config = [:]
    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = config
    body()


    echo "angularConfigureNPMRepository global variable parameters\n" +
            "--------------------------------------------------------\n" +
            "config.theAngularNPMRepositoryURL: ${config.theAngularNPMRepositoryURL} \n"


    def angularNPMRepositoryURL = config.theAngularNPMRepositoryURL

    echo 'Setting Artifactory NPM registry'
    //sh "npm config set registry ${angularNPMRepositoryURL} "

    sh "npm config get registry"

}
