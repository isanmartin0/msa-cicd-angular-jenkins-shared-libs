#!/usr/bin/groovy

def call(body) {
    def config = [:]
    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = config
    body()


    echo "angularInstallDependencies global variable parameters\n" +
            "-----------------------------------------------------\n" +
            "config.removeSourcePackageLock: ${config.removeSourcePackageLock} \n"

    Boolean removeSourcePackageLock = false

    if (config.removeSourcePackageLock) {
        removeSourcePackageLock = config.removeSourcePackageLock.toBoolean()
    }

    if (removeSourcePackageLock) {
        boolean isPackageLockJSON = fileExists 'package-lock.json'
        echo "file package-lock.json exists: ${isPackageLockJSON}"

        echo "Removing package-lock.json"

        try {
            sh "rm package-lock.json"
        } catch (exc) {
            echo "package-lock.json doesn't exist"
            def exc_message = exc.message
            echo "${exc_message}"
        }

        isPackageLockJSON = fileExists 'package-lock'
        echo "file package-lock.json exists: ${isPackageLockJSON}"
    }

    echo 'Building dependencies...'
    sh 'npm i'

}
