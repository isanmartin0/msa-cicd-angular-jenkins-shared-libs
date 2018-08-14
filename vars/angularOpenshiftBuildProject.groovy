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


    def packageJSON = readJSON file: 'package.json'
    def project = utils.getProject(packageJSON.name)
    def projectName = utils.getProjectName(packageJSON.name, config.branch_type, config.branchHY)

    echo "project: ${project}"
    echo "projectName: ${projectName}"

    def version = packageJSON.version


    //def artifactUrl = "${config.repoUrl}/${packageJSON.name}/${version}/${packageJSON.name}-${version}.tgz"
    def buildEnvVars = []


    echo "buildEnvVars.size: ${buildEnvVars.size()}"


    openshiftBuild buildConfig: project, namespace: projectName, verbose: 'true', showBuildLogs: 'true', env: buildEnvVars

    def destTag = "${project}-${version}"

    openshiftTag(namespace: projectName, sourceStream: project, sourceTag: 'latest', destinationStream: project, destinationTag: destTag)
}
