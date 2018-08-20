#!/usr/bin/groovy

def call(body) {

    def config = [:]
    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = config
    body()


    echo "angularRemoveOldBuilds global variable parameters\n" +
            "-------------------------------------------------\n" +
            "config.maxOldBuildsToKeep: ${config.maxOldBuildsToKeep} \n" +
            "config.daysOldBuildsToKeep: ${config.daysOldBuildsToKeep} \n"


    int maxOldBuildsToKeep = 0
    int daysOldBuildsToKeep = 0

    String maxOldBuildsToKeepParam = config.maxOldBuildsToKeep
    String daysOldBuildsToKeepParam = config.daysOldBuildsToKeep

    if (maxOldBuildsToKeepParam != null && maxOldBuildsToKeepParam.isInteger()) {
        maxOldBuildsToKeep = maxOldBuildsToKeepParam as Integer
    }

    if (daysOldBuildsToKeepParam != null && daysOldBuildsToKeepParam.isInteger()) {
        daysOldBuildsToKeep = daysOldBuildsToKeepParam as Integer
    }

    echo "maxOldBuildsToKeep: ${maxOldBuildsToKeep}"
    echo "daysOldBuildsToKeep: ${daysOldBuildsToKeep}"

    if (maxOldBuildsToKeep > 0 && daysOldBuildsToKeep > 0) {

        echo "Keeping last ${maxOldBuildsToKeep} builds"
        echo "Keeping builds for  ${daysOldBuildsToKeep} last days"

        properties([[$class: 'BuildDiscarderProperty', strategy: [$class: 'LogRotator', artifactDaysToKeepStr: '', artifactNumToKeepStr: '', daysToKeepStr: "${daysOldBuildsToKeep}", numToKeepStr: "${maxOldBuildsToKeep}"]]])

    } else if (maxOldBuildsToKeep > 0) {

        echo "Keeping last ${maxOldBuildsToKeep} builds"

        properties([[$class: 'BuildDiscarderProperty', strategy: [$class: 'LogRotator', artifactDaysToKeepStr: '', artifactNumToKeepStr: '', daysToKeepStr: '', numToKeepStr: "${maxOldBuildsToKeep}"]]])

    } else if (daysOldBuildsToKeep > 0) {

        echo "Keeping builds for  ${daysOldBuildsToKeep} last days"

        properties([[$class: 'BuildDiscarderProperty', strategy: [$class: 'LogRotator', artifactDaysToKeepStr: '', artifactNumToKeepStr: '', daysToKeepStr: "${daysOldBuildsToKeep}", numToKeepStr: '']]])

    } else {

        echo "Not removing old builds."

        properties([[$class: 'BuildDiscarderProperty', strategy: [$class: 'LogRotator', artifactDaysToKeepStr: '', artifactNumToKeepStr: '', daysToKeepStr: '', numToKeepStr: '']]])

    }

}
