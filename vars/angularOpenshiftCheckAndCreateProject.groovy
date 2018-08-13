#!/usr/bin/groovy
import com.evobanco.AngularConstants
import com.evobanco.AngularUtils

def call(body) {
    def utils = new com.evobanco.AngularUtils()
    def config = [:]
    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = config
    body()

    echo "angularOpenshiftCheckAndCreateProject parameters"
    echo "config.template: ${config.template}"
    echo "config.environment: ${config.environment}"
    echo "config.branch_type: ${config.branch_type}"
    echo "config.branchHY: ${config.branchHY}"
    echo "config.dockerRegistry: ${config.dockerRegistry}"
    echo "config.sourceRepositoryURL: ${config.sourceRepositoryURL}"
    echo "config.sourceRepositoryBranch: ${config.sourceRepositoryBranch}"
    echo "config.environment: ${config.environment}"
    echo "config.package_tag: ${config.package_tag}"
    echo "config.package_tarball: ${config.package_tarball}"
    echo "config.artifactoryNPMRepo: ${config.artifactoryNPMRepo}"
    echo "config.artifactoryNPMAuth: ${config.artifactoryNPMAuth}"
    echo "config.artifactoryNPMEmailAuth: ${config.artifactoryNPMEmailAuth}"




    def packageJSON = readJSON file: 'package.json'
    def project = utils.getProject(packageJSON.name)
    def projectName = utils.getProjectName(packageJSON.name, config.branch_type, config.branchHY)
    def branchNameContainerImage = ""
    int minimumPodReplicas = AngularConstants.MINIMUM_POD_REPLICAS
    int maximumPodReplicas = AngularConstants.MAXIMUM_POD_REPLICAS
    def hostname = ""


    echo "project: ${project}"
    echo "projectName: ${projectName}"

    if (config.branch_type == 'master') {
        //Host name for the route element
        hostname = ".svcs" + AngularConstants.HOSTNAME_DOMAIN

        //Set minimum number of replicas of pod
        minimumPodReplicas = AngularConstants.MINIMUM_POD_REPLICAS_PRO_ENVIRONMENT
        maximumPodReplicas = AngularConstants.MAXIMUM_POD_REPLICAS_PRO_ENVIRONMENT

    } else {
        //Branch name for image container
        branchNameContainerImage = "-${config.branchHY}"
        //Host name for the route element
        hostname = "-${config.branchHY}.svcs${config.environment}" + AngularConstants.HOSTNAME_DOMAIN
    }

    withCredentials([usernamePassword(credentialsId: "${config.oseCredential}", passwordVariable: 'PASSWORD', usernameVariable: 'USERNAME')]) {
        sh "oc login ${config.cloudURL} -u ${USERNAME} -p ${PASSWORD} --insecure-skip-tls-verify=true"
    }

    try {
        sh "oc project ${projectName}"
    } catch (err) {

        println err

        echo "As the project does not exist on openshift, we will create it"

        sh "oc new-project ${projectName}"
        sh "oc label namespace ${projectName} \"environment=${config.environment}\""
        sh "oc policy add-role-to-user view system:serviceaccount:${config.jenkinsNS}:jenkins -n ${projectName}"
        sh "oc policy add-role-to-user edit system:serviceaccount:${config.jenkinsNS}:jenkins -n ${projectName}"

        sh "oc project ${projectName}"

        withCredentials([string(credentialsId: "${config.artifactoryNPMAuth}", variable: 'ARTIFACTORY_NPM_AUTH'), string(credentialsId: "${config.artifactoryNPMEmailAuth}", variable: 'ARTIFACTORY_NPM_EMAIL_AUTH')]) {
            sh "oc process -n ${projectName} -f ${config.template} BRANCH_NAME=${env.BRANCH_NAME} BRANCH_NAME_HY=${config.branchHY} BRANCH_NAME_HY_CONTAINER_IMAGE=${branchNameContainerImage} PROJECT=${project} DOCKER_REGISTRY=${config.dockerRegistry} SOURCE_REPOSITORY_URL=${config.sourceRepositoryURL} SOURCE_REPOSITORY_BRANCH=${config.sourceRepositoryBranch} envLabel=${config.environment} HOST_NAME=${hostname} MIN_POD_REPLICAS=${minimumPodReplicas} MAX_POD_REPLICAS=${maximumPodReplicas} NODEJS_PACKAGE_TAG=${config.package_tag} NODEJS_PACKAGE_TARBALL=${config.package_tarball} ARTIFACTORY_NPM_REPO=${config.artifactoryNPMRepo} ARTIFACTORY_NPM_AUTH=${ARTIFACTORY_NPM_AUTH} ARTIFACTORY_NPM_EMAIL_AUTH=${ARTIFACTORY_NPM_EMAIL_AUTH}| oc create -n ${projectName} -f -"
        }

        echo "Resources (is,bc,dc,svc,route) created under OCP namespace ${projectName}"
    }
}