#!/usr/bin/groovy
import com.evobanco.AngularConstants
import com.evobanco.AngularUtils

def call(body) {
    def utils = new com.evobanco.AngularUtils()
    def config = [:]
    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = config
    body()


    echo "angularDisplayInstalledDependencies parameters"
    echo "config.showGlobalInstalledDependencies: ${config.showGlobalInstalledDependencies}"
    echo "config.showGlobalInstalledDependenciesDepthLimit: ${config.showGlobalInstalledDependenciesDepthLimit}"
    echo "config.showGlobalInstalledDependenciesDepth: ${config.showGlobalInstalledDependenciesDepth}"
    echo "config.showLocalInstalledDependencies: ${config.showLocalInstalledDependencies}"
    echo "config.showLocalInstalledDependenciesDepthLimit: ${config.showLocalInstalledDependenciesDepthLimit}"
    echo "config.showLocalInstalledDependenciesDepth: ${config.showLocalInstalledDependenciesDepth}"
    echo "config.showLocalInstalledDependenciesOnlyType: ${config.showLocalInstalledDependenciesOnlyType}"
    echo "config.showLocalInstalledDependenciesType: ${config.showLocalInstalledDependenciesType}"


    Boolean showGlobalInstalledDependencies = false
    Boolean showLocalInstalledDependencies = false


    if (config.showGlobalInstalledDependencies) {
        showGlobalInstalledDependencies = config.showGlobalInstalledDependencies.toBoolean()
    }

    if (config.showLocalInstalledDependencies) {
        showLocalInstalledDependencies = config.showLocalInstalledDependencies.toBoolean()
    }

    if (showGlobalInstalledDependencies) {
        Boolean showGlobalInstalledDependenciesDepthLimit = false
        int showGlobalInstalledDependenciesDepth = -1
        String showGlobalInstalledDependenciesDepthFlags = ""

        if (config.showGlobalInstalledDependenciesDepthLimit) {
            showGlobalInstalledDependenciesDepthLimit = config.showGlobalInstalledDependenciesDepthLimit.toBoolean()
        }

        if (showGlobalInstalledDependenciesDepthLimit) {

            String showGlobalInstalledDependenciesDepthParam = config.showGlobalInstalledDependenciesDepth

            if (showGlobalInstalledDependenciesDepthParam != null && showGlobalInstalledDependenciesDepthParam.isInteger()) {
                showGlobalInstalledDependenciesDepth = showGlobalInstalledDependenciesDepthParam as Integer
            }
        }

        if (showGlobalInstalledDependenciesDepth >=0) {
            showGlobalInstalledDependenciesDepthFlags = " --depth=${showGlobalInstalledDependenciesDepth}"
        }

        try {
            echo "List global dependencies ${showGlobalInstalledDependenciesDepthFlags}"
            sh "npm -g list ${showGlobalInstalledDependenciesDepthFlags}"
        } catch(err) {
            echo 'ERROR. There is an error retrieving NPM global dependencies'
        }

    }

    /*
    if (showLocalInstalledDependencies) {

        Boolean showLocalInstalledDependenciesDepthLimit = false
        int showLocalInstalledDependenciesDepth = -1
        Boolean showLocalInstalledDependenciesOnlyType = false
        String showLocalInstalledDependenciesType = ""
        String showLocalInstalledDependenciesDepthFlags = ""
        String showLocalInstalledDependenciesTypeFlags = ""


        echo "params.installedDependencies.showLocalInstalledDependenciesDepthLimit: ${params.installedDependencies.showLocalInstalledDependenciesDepthLimit}"
        echo "params.installedDependencies.showLocalInstalledDependenciesDepth: ${params.installedDependencies.showLocalInstalledDependenciesDepth}"
        echo "params.installedDependencies.showLocalInstalledDependenciesOnlyType: ${params.installedDependencies.showLocalInstalledDependenciesOnlyType}"
        echo "params.installedDependencies.showLocalInstalledDependenciesType: ${params.installedDependencies.showLocalInstalledDependenciesType}"

        if (params.installedDependencies.showLocalInstalledDependenciesDepthLimit) {
            showLocalInstalledDependenciesDepthLimit = params.installedDependencies.showLocalInstalledDependenciesDepthLimit.toBoolean()
        }

        if (params.installedDependencies.showLocalInstalledDependenciesOnlyType) {
            showLocalInstalledDependenciesOnlyType = params.installedDependencies.showLocalInstalledDependenciesOnlyType.toBoolean()
        }


        if (showLocalInstalledDependenciesDepthLimit) {

            String showLocalInstalledDependenciesDepthParam = params.installedDependencies.showLocalInstalledDependenciesDepth

            if (showLocalInstalledDependenciesDepthParam != null && showLocalInstalledDependenciesDepthParam.isInteger()) {
                showLocalInstalledDependenciesDepth = showLocalInstalledDependenciesDepthParam as Integer
            }

            if (showLocalInstalledDependenciesDepth >= 0) {
                showLocalInstalledDependenciesDepthFlags = " --depth=${showLocalInstalledDependenciesDepth}"
            }
        }

        if (showLocalInstalledDependenciesOnlyType) {
            if (params.installedDependencies.showLocalInstalledDependenciesType) {
                showLocalInstalledDependenciesType = params.installedDependencies.showLocalInstalledDependenciesType
                showLocalInstalledDependenciesType = showLocalInstalledDependenciesType.trim()
            }

            if (!showLocalInstalledDependenciesType.equalsIgnoreCase("dev") && !showLocalInstalledDependenciesType.equalsIgnoreCase("prod")) {
                currentBuild.result = "FAILED"
                throw new hudson.AbortException("The parameter installedDependencies.showLocalInstalledDependenciesType has an incorrect value. Allowed values (dev, prod)") as Throwable
            }

            showLocalInstalledDependenciesTypeFlags = " --only=${showLocalInstalledDependenciesType}"
        }

        try {
            echo "List local dependencies ${showGlobalInstalledDependenciesDepthFlags}"
            sh "npm list ${showLocalInstalledDependenciesDepthFlags} ${showLocalInstalledDependenciesTypeFlags}"
        } catch(err) {
            echo 'ERROR. There is an error retrieving NPM local dependencies'
        }


    }
    */
}
