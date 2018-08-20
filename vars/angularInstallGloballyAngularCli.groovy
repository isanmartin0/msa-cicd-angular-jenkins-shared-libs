#!/usr/bin/groovy
import com.evobanco.AngularConstants
import com.evobanco.AngularUtils

def call(body) {
    def utils = new com.evobanco.AngularUtils()
    def config = [:]
    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = config
    body()


    echo "angularInstallGloballyAngularCli global variable parameters\n" +
            "-----------------------------------------------------------\n" +
            "config.installAngularCliSpecificVersion: ${config.installAngularCliSpecificVersion} \n" +
            "config.angularCliDefaultVersion: ${config.angularCliDefaultVersion} \n" +
            "config.angularCliSpecificVersion: ${config.angularCliSpecificVersion} \n" +
            "config.theNodeJS_pipeline_installation: ${config.theNodeJS_pipeline_installation} \n"

    Boolean installAngularCliSpecificVersion = false
    def angularCliVersion = config.angularCliDefaultVersion

    if (config.installAngularCliSpecificVersion) {
        installAngularCliSpecificVersion = config.installAngularCliSpecificVersion.toBoolean()
    }

    if (installAngularCliSpecificVersion) {

        echo "Installing a specific @angular/cli version"
        echo "config.angularCliSpecificVersion: ${config.angularCliSpecificVersion}"
        String angularCliVersionParam = config.angularCliSpecificVersion

        if (angularCliVersionParam != null) {
            angularCliVersion = angularCliVersionParam
        }

    } else {
        echo "Installing default @angular/cli version"
        echo "NodeJS version: ${config.theNodeJS_pipeline_installation}"
        echo "@angular/cli default version: ${angularCliVersion}"
    }

    echo "Installing globally @angular/cli version ${angularCliVersion}"
    sh "npm install -g @angular/cli@${angularCliVersion}"

}
