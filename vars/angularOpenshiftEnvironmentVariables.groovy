#!/usr/bin/groovy
import com.evobanco.AngularConstants
import com.evobanco.AngularUtils

def call(body) {

    def utils = new com.evobanco.AngularUtils()
    def config = [:]
    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = config
    body()

    Boolean isCreatePortEnvironmentVariable = false
    Boolean isDevMode = false
    Boolean isUseNpmMirror = false
    Boolean isUseAlternateNpmRunScript = false



    def createPortEnvironmentVariable = config.createPortEnvironmentVariableOpenshift
    def portNumber = config.portNumber
    def devMode = config.devModeOpenshift
    def debugPort = config.debugPortOpenshift
    def useNpmMirror = config.useNpmMirrorOpenshift
    def npmMirror = config.npmMirrorOpenshift
    def useAlternateNpmRun = config.useAlternateNpmRunOpenshift
    def alternateNpmRunScript = config.alternateNpmRunScriptOpenshift
    def branchNameHY = config.branchHY
    def branchType = config.branch_type
    def mapEnvironmentVariables = config.map_environment_variables

    echo "angularOpenshiftEnvironmentVariables parameters"
    echo "createPortEnvironmentVariable: ${createPortEnvironmentVariable}"
    echo "portNumber: ${portNumber}"
    echo "devMode: ${devMode}"
    echo "debugPort: ${debugPort}"
    echo "useNpmMirror: ${useNpmMirror}"
    echo "npmMirror: ${npmMirror}"
    echo "useAlternateNpmRun: ${useAlternateNpmRun}"
    echo "alternateNpmRunScript: ${alternateNpmRunScript}"
    echo "branchNameHY: ${branchNameHY}"
    echo "branchType: ${branchType}"

    echo "mapEnvironmentVariables:"
    mapEnvironmentVariables.each { key, value ->
        echo "Environment variable: ${key} = ${value}"
    }


    def packageJSON = readJSON file: 'package.json'
    def project = utils.getProject(packageJSON.name)
    def projectName = utils.getProjectName(packageJSON.name, branchType, branchNameHY)

    echo "project: ${project}"
    echo "projectName: ${projectName}"

    if (createPortEnvironmentVariable != null) {
        isCreatePortEnvironmentVariable = createPortEnvironmentVariable.toBoolean()
    }

    if (devMode != null) {
        isDevMode = devMode.toBoolean()
    }

    if (useNpmMirror != null) {
        isUseNpmMirror = useNpmMirror.toBoolean()
    }

    if (useAlternateNpmRun != null) {
        isUseAlternateNpmRunScript = useAlternateNpmRun.toBoolean()
    }


    if (isCreatePortEnvironmentVariable) {
        try {
            //Remove PORT environment variable created by template
            echo "Removing PORT environment variable"
            sh "oc env dc/${project} ${AngularConstants.PORT_ENVIRONMENT_VARIABLE}- -n ${projectName}"
        } catch (err) {
            echo "The ${AngularConstants.PORT_ENVIRONMENT_VARIABLE} environment variable on dc/${project} -n ${projectName} cannot be removed"
        }

        echo "Adding ${AngularConstants.PORT_ENVIRONMENT_VARIABLE}=${portNumber} environment variable"
        sh "oc env dc/${project} ${AngularConstants.PORT_ENVIRONMENT_VARIABLE}=\"${portNumber}\" -n ${projectName}"

    }

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
