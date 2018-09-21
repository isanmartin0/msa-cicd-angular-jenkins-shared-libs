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
            "config.artCredential: ${config.artCredential} \n" +
            "config.build_output_path: ${config.build_output_path}"


    def packageJSON = readJSON file: 'package.json'
    def project = utils.getProject(packageJSON.name)
    def projectName = utils.getProjectName(packageJSON.name, config.branch_type, config.branchHY)
    def branchNameContainerImage = ""
    int minimumPodReplicas = AngularConstants.MINIMUM_POD_REPLICAS
    int maximumPodReplicas = AngularConstants.MAXIMUM_POD_REPLICAS
    def hostname = ""
    def buildOutputPath = config.build_output_path[0]


    echo "project: ${project}"
    echo "projectName: ${projectName}"

    //Remove last /
    if (buildOutputPath.endsWith("/")) {
        buildOutputPath = buildOutputPath[0..-2]
    }

    echo "buildOutputPath: ${buildOutputPath}"

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
            sh "oc process -n ${projectName} -f ${config.template} BRANCH_NAME=${env.BRANCH_NAME} BRANCH_NAME_HY=${config.branchHY} BRANCH_NAME_HY_CONTAINER_IMAGE=${branchNameContainerImage} PROJECT=${project} DOCKER_REGISTRY=${config.dockerRegistry} SOURCE_REPOSITORY_URL=${config.sourceRepositoryURL} SOURCE_REPOSITORY_BRANCH=${config.sourceRepositoryBranch} envLabel=${config.environment} HOST_NAME=${hostname} MIN_POD_REPLICAS=${minimumPodReplicas} MAX_POD_REPLICAS=${maximumPodReplicas} ANGULAR_PACKAGE_NAME=${config.package_name} ANGULAR_PACKAGE_TARBALL=${config.package_tarball} ARTIFACTORY_REPO=${config.artifactoryRepo} CONTEXT_DIR=${config.contextDir} NGINX_VERSION=${config.nginxVersion} ARTIFACTORY_TOKEN=${ARTIFACTORY_TOKEN} BUILD_OUTPUT_PATH=${buildOutputPath} | oc create -n ${projectName} -f -"
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

        //Set environment build config variables (parameters value may overwrite template values

        //ANGULAR_PACKAGE_NAME
        try {
            //Remove ANGULAR_PACKAGE_NAME environment variable created by template
            echo "Removing ANGULAR_PACKAGE_NAME environment variable"
            sh "oc env bc/${project} ${AngularConstants.ANGULAR_PACKAGE_NAME_ENVIRONMENT_VARIABLE}- -n ${projectName}"
        } catch (err) {
            echo "The ${AngularConstants.ANGULAR_PACKAGE_NAME_ENVIRONMENT_VARIABLE} environment variable on bc/${project} -n ${projectName} cannot be removed"
        }

        //ANGULAR_PACKAGE_TARBALL
        try {
            //Remove ANGULAR_PACKAGE_TARBALL environment variable created by template
            echo "Removing ANGULAR_PACKAGE_TARBALL_ENVIRONMENT_VARIABLE environment variable"
            sh "oc env bc/${project} ${AngularConstants.ANGULAR_PACKAGE_TARBALL_ENVIRONMENT_VARIABLE}- -n ${projectName}"
        } catch (err) {
            echo "The ${AngularConstants.ANGULAR_PACKAGE_TARBALL_ENVIRONMENT_VARIABLE} environment variable on bc/${project} -n ${projectName} cannot be removed"
        }

        //BUILD_OUTPUT_PATH
        try {
            //Remove BUILD_OUTPUT_PATH environment variable created by template
            echo "Removing BUILD_OUTPUT_PATH environment variable"
            sh "oc env bc/${project} ${AngularConstants.BUILD_OUTPUT_PATH_ENVIRONMENT_VARIABLE}- -n ${projectName}"
        } catch (err) {
            echo "The ${AngularConstants.BUILD_OUTPUT_PATH_ENVIRONMENT_VARIABLE} environment variable on bc/${project} -n ${projectName} cannot be removed"
        }

        //ARTIFACTORY_REPO
        try {
            //Remove ARTIFACTORY_REPO environment variable created by template
            echo "Removing ARTIFACTORY_REPO environment variable"
            sh "oc env bc/${project} ${AngularConstants.ARTIFACTORY_REPO_ENVIRONMENT_VARIABLE}- -n ${projectName}"
        } catch (err) {
            echo "The ${AngularConstants.ARTIFACTORY_REPO_ENVIRONMENT_VARIABLE} environment variable on bc/${project} -n ${projectName} cannot be removed"
        }

        //Adding environment variables new values
        echo "Adding next environment variables on bc/${project}: \n" +
                "${AngularConstants.ANGULAR_PACKAGE_NAME_ENVIRONMENT_VARIABLE}=${config.package_name} \n" +
                "${AngularConstants.ANGULAR_PACKAGE_TARBALL_ENVIRONMENT_VARIABLE}=${config.package_tarball} \n" +
                "${AngularConstants.BUILD_OUTPUT_PATH_ENVIRONMENT_VARIABLE}=${buildOutputPath} \n" +
                "${AngularConstants.ARTIFACTORY_REPO_ENVIRONMENT_VARIABLE}=${config.artifactoryRepo} \n"

        sh "oc env bc/${project} ${AngularConstants.ANGULAR_PACKAGE_NAME_ENVIRONMENT_VARIABLE}=\"${config.package_name}\" " +
                "${AngularConstants.ANGULAR_PACKAGE_TARBALL_ENVIRONMENT_VARIABLE}=\"${config.package_tarball}\" " +
                "${AngularConstants.BUILD_OUTPUT_PATH_ENVIRONMENT_VARIABLE}=\"${buildOutputPath}\" " +
                "${AngularConstants.ARTIFACTORY_REPO_ENVIRONMENT_VARIABLE}=\"${config.artifactoryRepo}\" --overwrite -n ${projectName}"


        //Check that NGINX version of the build configuration matches with nginx version of the parameter
        try {
            echo 'Get NGINX version of the build configuration...'
            check_nginx_version_script = $/eval "oc describe bc'/'${
                project
            } -n ${
                projectName
            }    | grep 'nginx:${
                config.nginxVersion
            }'"/$
            echo "${check_nginx_version_script}"
            def check_nginx_version_script_output = sh(script: "${check_nginx_version_script}", returnStdout: true).toString().trim()
            echo "${check_nginx_version_script_output}"
            echo "The NGINX version of the build configuration is the same that parameter"
        } catch (exc) {
            echo 'The NGINX version doesn\'t match. Patch the buildconfig with the parameter\'s NGINX version'
            def exc_message = exc.message
            echo "${exc_message}"

            try {
                sh "oc patch buildconfig -p '{\"spec\":{\"strategy\":{\"sourceStrategy\":{\"from\":{\"name\":\"nginx:${config.nginxVersion}\"}}}}}' ${project} -n ${projectName}"
            } catch (innerExc) {
                echo 'There is an error on doing patch of nginx version.'
                def innerExc_message = innerExc.message
                echo "${innerExc_message}"
            }
        }

    }

    utils = null
}
