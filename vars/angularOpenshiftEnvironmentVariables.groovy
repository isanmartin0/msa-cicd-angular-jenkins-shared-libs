#!/usr/bin/groovy
import com.evobanco.AngularUtils

def call(body) {

    def utils = new com.evobanco.AngularUtils()
    def config = [:]
    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = config
    body()

    def branchNameHY = config.branchHY
    def branchType = config.branch_type


    echo "angularOpenshiftEnvironmentVariables global variable parameters\n" +
            "---------------------------------------------------------------\n" +
            "branchNameHY: ${branchNameHY} \n" +
            "branchType: ${branchType} \n" +
            "config.theEnvironmentVariablesMap: \n" +
            config.theEnvironmentVariablesMap.each { key, value ->
                echo "Environment variable: ${key} = ${value}"
            }


    def packageJSON = readJSON file: 'package.json'
    def project = utils.getProject(packageJSON.name)
    def projectName = utils.getProjectName(packageJSON.name, branchType, branchNameHY)
    def mapEnvironmentVariables = [:]

    echo "project: ${project}"
    echo "projectName: ${projectName}"


    if (config.theEnvironmentVariablesMap) {
        mapEnvironmentVariables = config.theEnvironmentVariablesMap
    }

    echo "mapEnvironmentVariables:"
    mapEnvironmentVariables.each { key, value ->
        echo "Map environment variable: ${key} = ${value}"
    }

    int mapEnvironmentVariablesSize = mapEnvironmentVariables.size()

    echo "mapEnvironmentVariables size: ${mapEnvironmentVariablesSize}"

    if (mapEnvironmentVariablesSize > 0) {

        mapEnvironmentVariables.each { key, value ->
            try {
                echo "Removing $key environment variable"
                sh "oc env dc/${project} $key- -n ${projectName}"
            } catch (err) {
                echo "The $key environment variable on dc/${project} -n ${projectName} cannot be removed"
            }

            echo "Adding $key=$value environment variable"
            sh "oc env dc/${project} $key=$value -n ${projectName}"

        }
    }

    utils = null
}
