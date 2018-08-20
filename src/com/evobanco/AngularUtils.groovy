#!/usr/bin/env groovy
package com.evobanco

def getBranchType(String branchName) {
    //Must be specified according to <flowInitContext> configuration of jgitflow-maven-plugin in pom.xml
    def dev_pattern = "develop"
    def release_pattern = "release/.*"
    def feature_pattern = "feature/.*"
    def hotfix_pattern = "hotfix/.*"
    def master_pattern = "master"

    if (branchName ==~ dev_pattern) {
        return NodejsConstants.BRANCH_TYPE_DEVELOP
    } else if (branchName ==~ release_pattern) {
        return NodejsConstants.BRANCH_TYPE_RELEASE
    } else if (branchName ==~ master_pattern) {
        return NodejsConstants.BRANCH_TYPE_MASTER
    } else if (branchName ==~ feature_pattern) {
        return NodejsConstants.BRANCH_TYPE_FEATURE
    } else if (branchName ==~ hotfix_pattern) {
        return NodejsConstants.BRANCH_TYPE_HOTFIX
    } else {
        return null
    }
}

def getBranch(){
    if (env.BRANCH_NAME){
        return env.BRANCH_NAME
    } else {
        return sh(script: 'git symbolic-ref --short HEAD', returnStdout: true).toString().trim()
    }
}


def String getParallelConfigurationProjectURL(String projectURL) {

    def ppc_extension = "-ppc";
    def parallelProject = ""

    if (projectURL != null && !"".equals(projectURL)) {
        parallelProject = projectURL + ppc_extension
    }

    return parallelProject
}

def boolean isScopedPackage(String packageName) {
    if (packageName == null || "".equals(packageName)) {
        return false
    } else if (packageName.trim().startsWith("@") && packageName.trim().contains("/")) {
        return true
    } else {
        return false
    }
}

def getPackageScope(String packageName) {
    def packageScope = ""
    if (isScopedPackage(packageName)) {
        packageScope = packageName.substring(packageName.indexOf('@') + 1 , packageName.indexOf('/'))
    } else {
        throw new Exception("is not a scoped package")
    }
    return packageScope.toLowerCase().trim()
}

def getUnscopedElement(String scopedElement) {
    def unscopedElement = ""
    if (scopedElement == null || "".equals(scopedElement)) {
        return ""
    } else {
        if (isScopedPackage(scopedElement)) {
            unscopedElement = scopedElement.substring(scopedElement.indexOf('/') + 1)
        } else {
            throw new Exception("is not a scoped package")
        }
    }
    return unscopedElement.toLowerCase().trim()
}

def getProject(String packageName) {
    def project = ""
    if (packageName == null || "".equals(packageName)) {
        return ""
    } else {
        if (isScopedPackage(packageName)) {
            packageScope = getPackageScope(packageName)
            def unscopedPackageName = getUnscopedElement(packageName)
            project = "${packageScope}-${unscopedPackageName}"
        } else {
            project = packageName
        }
    }
    return project.toLowerCase().trim()
}

def getProjectName(String packageName, String branchType, String branchHY) {
    def projectName = ""
    if (packageName == null || "".equals(packageName) || branchType == null || "".equals(branchType)  || branchHY == null || "".equals(branchHY)) {
        return ""
    } else {
        def finalPackageName = ""
        if (isScopedPackage(packageName)) {
            packageScope = getPackageScope(packageName)
            def unscopedPackageName = getUnscopedElement(packageName)
            finalPackageName = "${packageScope}-${unscopedPackageName}"
        } else {
            finalPackageName = packageName
        }

        if (branchType == 'master') {
            projectName = "${finalPackageName}"
        } else {
            projectName = "${finalPackageName}-${branchHY}"
        }
    }
    return projectName.toLowerCase().trim()
}

def getPackageTag(String packageName, String packageVersion) {

    def packageTag = ""
    if (packageName == null || "".equals(packageName) || packageVersion == null || "".equals(packageVersion)) {
        return ""
    } else {
        packageTag = packageName.trim() + "@" + packageVersion.trim()
    }
    return packageTag.toLowerCase().trim()
}

def getPackageTarball(String packageName, String packageVersion) {

    def packageTarball = ""
    def tarballSufix = ".tgz"
    if (packageName == null || "".equals(packageName) || packageVersion == null || "".equals(packageVersion)) {
        return ""
    } else {
        if (isScopedPackage(packageName)) {
            packageScope = getPackageScope(packageName)
            def unscopedPackageName = getUnscopedElement(packageName)
            packageTarball = packageScope + "-" + unscopedPackageName.trim() + "-" + packageVersion.trim() + tarballSufix
        } else {
            packageTarball = packageName.trim() + "-" + packageVersion.trim() + tarballSufix
        }
    }
    return packageTarball.toLowerCase().trim()
}

def getPackageTarball(String packageTag) {

    def packageTarball = ""
    def tarballSufix = ".tgz"
    if (packageTag == null || "".equals(packageTag)) {
        return ""
    } else {
        packageTarball = packageTag.trim().replace("@","-") + tarballSufix
    }
    return packageTarball.toLowerCase().trim()
}

def getPackageViewTarball(String packageName, String packageVersion) {

    def packageViewTarball = ""
    def tarballSufix = ".tgz"
    if (packageName == null || "".equals(packageName) || packageVersion == null || "".equals(packageVersion)) {
        return ""
    } else {
        if (isScopedPackage(packageName)) {
            packageViewTarball = packageName + "-" + packageVersion.trim() + tarballSufix
        } else {
            packageViewTarball = getPackageTarball(packageName, packageVersion)
        }
    }
    return packageViewTarball.toLowerCase().trim()
}

def getRouteHostnameWithProtocol(String routeHostname, boolean isSecuredRoute) {

    def routeHostNameWithProtocol = ''
    if (routeHostname != null && !"".equals(routeHostname)) {
        if (!routeHostname.toLowerCase().startsWith(NodejsConstants.HTTP_PROTOCOL) && !routeHostname.toLowerCase().startsWith(NodejsConstants.HTTPS_PROTOCOL)) {
            if (isSecuredRoute) {
                routeHostNameWithProtocol = NodejsConstants.HTTPS_PROTOCOL + routeHostname
            } else {
                routeHostNameWithProtocol = NodejsConstants.HTTP_PROTOCOL + routeHostname
            }
        } else {
            routeHostNameWithProtocol = routeHostname
        }
    }

    return routeHostNameWithProtocol
}

@NonCPS
def logMessage(String message) {
    echo message
}