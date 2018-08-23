#!/usr/bin/groovy

def call(body) {
    def config = [:]
    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = config
    body()


    echo "angularInstallKarmaSonarQubeReporter global variable parameters\n" +
            "---------------------------------------------------------------\n" +
            "config.useKarmaSonarQubeReporterSpecificVersion: ${config.useKarmaSonarQubeReporterSpecificVersion} \n" +
            "config.theKarmaSonarQubeReporterDefaultVersion: ${config.theKarmaSonarQubeReporterDefaultVersion} \n" +
            "config.theKarmaSonarQubeReporterSpecificVersion: ${config.theKarmaSonarQubeReporterSpecificVersion} \n"


    Boolean useKarmaSonarQubeReporterSpecificVersion = false
    def karmaSonarQubeReporterSpecificVersion = config.theKarmaSonarQubeReporterDefaultVersion

    if (config.useKarmaSonarQubeReporterSpecificVersion) {
        useKarmaSonarQubeReporterSpecificVersion = config.useKarmaSonarQubeReporterSpecificVersion.toBoolean()
    }

    if (useKarmaSonarQubeReporterSpecificVersion) {
        karmaSonarQubeReporterSpecificVersion = config.theKarmaSonarQubeReporterSpecificVersion
    }

    //Check that karma-spnarqube-reporter is already installed
    try {
        echo 'Check karma-spnarqube-reporter package installation...'
        check_karma_sonarqube_reporter_installation_script = $/eval "npm list | grep 'karma_sonarqube_reporter'"/$
        echo "${check_karma_sonarqube_reporter_installation_script}"
        def check_karma_sonarqube_reporter_installation_script_output = sh(script: "${check_karma_sonarqube_reporter_installation_script}", returnStdout: true).toString().trim()
        echo "${check_karma_sonarqube_reporter_installation_script_output}"
        echo "The karma_sonarqube_reporter package is already installed"
    } catch (exc) {
        echo "The karma_sonarqube_reporter package is not installed"
        def exc_message = exc.message
        echo "${exc_message}"

        if (karmaSonarQubeReporterSpecificVersion) {
            echo "Installing karma-sonarqube-reporter package version ${karmaSonarQubeReporterSpecificVersion}"
            sh "npm i karma-sonarqube-reporter@${karmaSonarQubeReporterSpecificVersion} --save-dev"
        } else {
            echo "Installing karma-sonarqube-reporter package version latest"
            sh "npm i karma-sonarqube-reporter --save-dev"
        }
    }

    return true
}
