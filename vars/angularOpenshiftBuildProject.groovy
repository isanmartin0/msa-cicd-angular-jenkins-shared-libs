#!/usr/bin/groovy
import com.evobanco.AngularConstants
import com.evobanco.AngularUtils

def call(body) {

    def utils = new com.evobanco.AngularUtils()
    def config = [:]
    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = config
    body()

    echo "angularOpenshiftBuildProject parameters"

    echo "config.branch_type:  ${config.branch_type}"
    echo "config.branchHY:  ${config.branchHY}"
    echo "config.devModeOpenshift:  ${config.devModeOpenshift}"
    echo "config.debugPortOpenshift:  ${config.debugPortOpenshift}"
    echo "config.useNpmMirrorOpenshift:  ${config.useNpmMirrorOpenshift}"
    echo "config.npmMirrorOpenshift:  ${config.npmMirrorOpenshift}"
    echo "config.useAlternateNpmRunOpenshift:  ${config.useAlternateNpmRunOpenshift}"
    echo "config.alternateNpmRunScriptOpenshift:  ${config.alternateNpmRunScriptOpenshift}"


    Boolean isDevMode = false
    Boolean isUseNpmMirror = false
    Boolean isUseAlternateNpmRunScript = false

    def devMode = config.devModeOpenshift
    def debugPort = config.debugPortOpenshift
    def useNpmMirror = config.useNpmMirrorOpenshift
    def npmMirror = config.npmMirrorOpenshift
    def useAlternateNpmRun = config.useAlternateNpmRunOpenshift
    def alternateNpmRunScript = config.alternateNpmRunScriptOpenshift


    def packageJSON = readJSON file: 'package.json'
    def project = utils.getProject(packageJSON.name)
    def projectName = utils.getProjectName(packageJSON.name, config.branch_type, config.branchHY)

    echo "project: ${project}"
    echo "projectName: ${projectName}"

    def version = packageJSON.version


    if (devMode != null) {
        isDevMode = devMode.toBoolean()
    }

    if (useNpmMirror != null) {
        isUseNpmMirror = useNpmMirror.toBoolean()
    }

    if (useAlternateNpmRun != null) {
        isUseAlternateNpmRunScript = useAlternateNpmRun.toBoolean()
    }




    //def artifactUrl = "${config.repoUrl}/${packageJSON.name}/${version}/${packageJSON.name}-${version}.tgz"
    def buildEnvVars = []


    echo "buildEnvVars.size: ${buildEnvVars.size()}"

    if (isDevMode) {
        buildEnvVars <<  [ name : AngularConstants.DEV_MODE_ENVIRONMENT_VARIABLE, value : isDevMode ]
        buildEnvVars <<  [ name : AngularConstants.DEBUG_PORT_ENVIRONMENT_VARIABLE, value : debugPort ]
    }

    if (isUseNpmMirror) {
        buildEnvVars <<  [ name : AngularConstants.NPM_MIRROR_ENVIRONMENT_VARIABLE, value : npmMirror ]
    }

    if (isUseAlternateNpmRunScript) {
        buildEnvVars <<  [ name : AngularConstants.NPM_RUN_ENVIRONMENT_VARIABLE, value : alternateNpmRunScript ]
    }

    echo "buildEnvVars.size: ${buildEnvVars.size()}"

    openshiftBuild buildConfig: project, namespace: projectName, verbose: 'true', showBuildLogs: 'true', env: buildEnvVars

    def destTag = "${project}-${version}"

    openshiftTag(namespace: projectName, sourceStream: project, sourceTag: 'latest', destinationStream: project, destinationTag: destTag)
}
