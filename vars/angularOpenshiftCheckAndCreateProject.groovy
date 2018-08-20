#!/usr/bin/groovy
import com.evobanco.AngularConstants
import com.evobanco.AngularUtils

def call(body) {

    def utils = new com.evobanco.AngularUtils()
    def config = [:]
    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = config
    body()

    echo "angularOpenshiftCheckAndCreateProject global variable parameters\n" +
            "----------------------------------------------------------------\n" +
            "config.template: ${config.template} \n" +
            "config.environment: ${config.environment} \n" +
            "config.branch_type: ${config.branch_type} \n" +
            "config.branchHY: ${config.branchHY} \n" +
            "config.dockerRegistry: ${config.dockerRegistry} \n" +
            "config.sourceRepositoryURL: ${config.sourceRepositoryURL} \n" +
            "config.sourceRepositoryBranch: ${config.sourceRepositoryBranch} \n" +
            "config.environment: ${config.environment} \n" +
            "config.package_name: ${config.package_name} \n" +
            "config.package_tarball: ${config.package_tarball} \n" +
            "config.artifactoryRepo: ${config.artifactoryRepo} \n" +
            "config.contextDir: ${config.contextDir} \n" +
            "config.nginxVersion: ${config.nginxVersion} \n" +
            "config.artCredential: ${config.artCredential}"


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

    def openshiftProjectCreated = false
    try {
        sh "oc project ${projectName}"

        echo "oc version"
        sh "oc version"

        echo "Get project ${projectName} Openshift created objects"
        sh "oc get all -n ${projectName}"

    } catch (err) {

        println err

        echo "As the project does not exist on openshift, we will create it"

        sh "oc new-project ${projectName}"
        sh "oc label namespace ${projectName} \"environment=${config.environment}\""
        sh "oc policy add-role-to-user view system:serviceaccount:${config.jenkinsNS}:jenkins -n ${projectName}"
        sh "oc policy add-role-to-user edit system:serviceaccount:${config.jenkinsNS}:jenkins -n ${projectName}"

        sh "oc project ${projectName}"

        withCredentials([string(credentialsId: "${config.artCredential}", variable: 'ARTIFACTORY_TOKEN')]) {
            sh "oc process -n ${projectName} -f ${config.template} BRANCH_NAME=${env.BRANCH_NAME} BRANCH_NAME_HY=${config.branchHY} BRANCH_NAME_HY_CONTAINER_IMAGE=${branchNameContainerImage} PROJECT=${project} DOCKER_REGISTRY=${config.dockerRegistry} SOURCE_REPOSITORY_URL=${config.sourceRepositoryURL} SOURCE_REPOSITORY_BRANCH=${config.sourceRepositoryBranch} envLabel=${config.environment} HOST_NAME=${hostname} MIN_POD_REPLICAS=${minimumPodReplicas} MAX_POD_REPLICAS=${maximumPodReplicas} ANGULAR_PACKAGE_NAME=${config.package_name} ANGULAR_PACKAGE_TARBALL=${config.package_tarball} ARTIFACTORY_REPO=${config.artifactoryRepo} CONTEXT_DIR=${config.contextDir} NGINX_VERSION=${config.nginxVersion} ARTIFACTORY_TOKEN=${ARTIFACTORY_TOKEN} | oc create -n ${projectName} -f -"
        }

        echo "Resources (is,bc,dc,svc,route) created under OCP namespace ${projectName}"

        echo "oc version"
        sh "oc version"

        echo "Get project ${projectName} Openshift created objects"
        sh "oc get all -n ${projectName}"

        openshiftProjectCreated = true

    }

    if (!openshiftProjectCreated) {
        echo "The Openshift project ${projectName} exists."
        //Set environment
    }

    utils = null
}
