#!/usr/bin/groovy
import com.evobanco.AngularUtils

def call(body) {

    def utils = new com.evobanco.AngularUtils()
    def config = [:]
    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = config
    body()

    echo "angularOpenshiftDeployProject parameters"

    echo "config.branch_type: ${config.branch_type}"
    echo "config.branchHY: ${config.branchHY}"

    def packageJSON = readJSON file: 'package.json'
    def project = utils.getProject(packageJSON.name)
    def projectName = utils.getProjectName(packageJSON.name, config.branch_type, config.branchHY)

    echo "project: ${project}"
    echo "projectName: ${projectName}"

    openshiftDeploy deploymentConfig: project, namespace: projectName

    def hostname = sh(script: "oc get route ${project} -o jsonpath=\"{.spec.host}\" -n ${projectName}", returnStdout: true).toString().trim()

    echo "Deployed app at hostname: ${hostname}"

    return hostname
}
