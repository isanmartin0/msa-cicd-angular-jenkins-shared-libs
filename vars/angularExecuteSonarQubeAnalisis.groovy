#!/usr/bin/groovy

def call(body) {
    def config = [:]
    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = config
    body()


    echo "angularExecuteSonarQubeAnalisis global variable parameters\n" +
            "----------------------------------------------------------\n" +
            "config.theSonarProjectPath: ${config.theSonarProjectPath} \n" +
            "config.thePackageName: ${config.thePackageName} \n" +
            "config.theBranchNameHY: ${config.theBranchNameHY} \n" +
            "config.theSonarQubeServer: ${config.theSonarQubeServer} \n" +
            "config.theScannerHome: ${config.theScannerHome} \n" +
            "config.theSonarSources: ${config.theSonarSources} \n" +
            "config.theSonarExclusions: ${config.theSonarExclusions} \n" +
            "config.theSonarTests: ${config.theSonarTests} \n" +
            "config.theSonarTestsInclusions: ${config.theSonarTestsInclusions} \n" +
            "config.theSonarTSLintConfigPath: ${config.theSonarTSLintConfigPath} \n" +
            "config.theSonarTestExecutionReportPath: ${config.theSonarTestExecutionReportPath} \n" +
            "config.theSonarCoverageReportPath: ${config.theSonarCoverageReportPath} \n"



    echo "Running SonarQube..."

    // Jenkinsfile
    isSonarProjectFile = fileExists config.theSonarProjectPath
    echo "isSonarProjectFile : ${isSonarProjectFile}"

    def sonar_project_key = config.thePackageName + "-" + config.theBranchNameHY
    def sonar_project_name = config.thePackageName + "-" + config.theBranchNameHY

    echo "sonar_project_key: ${sonar_project_key}"
    echo "sonar_project_name: ${sonar_project_name}"

    // requires SonarQube Scanner 3.1+
    def scannerHome = tool "${config.theScannerHome}"

    if (isSonarProjectFile) {
        //sonar-project.properties contains properties for SonarQube

        echo 'sonarQube parameters extracted from sonar-project.properties file'

        withSonarQubeEnv("${config.theSonarQubeServer}") {
            sh "${scannerHome}/bin/sonar-scanner -X -Dsonar.projectKey=${sonar_project_key} -Dsonar.projectName=${sonar_project_name}"
        }

    } else {

        if (config.theSonarSources
                && config.theSonarExclusions
                && config.theSonarTests
                && config.theSonarTestsInclusions
                && config.theSonarTSLintConfigPath
                && config.theSonarTestExecutionReportPath
                && config.theSonarCoverageReportPath) {

            //Pipeline parameters contains properties for SonarQube.
            def sonarSources = config.theSonarSources
            def sonarExclusions = config.theSonarExclusions
            def sonarTests = config.theSonarTests
            def sonarTestsInclusions = config.theSonarTestsInclusions
            def sonarTSLintConfigPath = config.theSonarTSLintConfigPath
            def sonarTestExecutionReportPath = config.theSonarTestExecutionReportPath
            def sonarCoverageReportPath = config.theSonarCoverageReportPath


            echo 'sonarQube parameters extracted from pipeline parameters:'

            echo "sonarSources: ${sonarSources}"
            echo "sonarExclusions: ${sonarExclusions}"
            echo "sonarTests: ${sonarTests}"
            echo "sonarTestsInclusions: ${sonarTestsInclusions}"
            echo "sonarTSLintConfigPath: ${sonarTSLintConfigPath}"
            echo "sonarTestExecutionReportPath: ${sonarTestExecutionReportPath}"
            echo "sonarCoverageReportPath: ${sonarCoverageReportPath}"


            withSonarQubeEnv("${config.theSonarQubeServer}") {
                sh "${scannerHome}/bin/sonar-scanner -X -Dsonar.projectKey=${sonar_project_key} -Dsonar.projectName=${sonar_project_name} -Dsonar.sources=${sonarSources} -Dsonar.exclusions=${sonarExclusions} -Dsonar.tests=${sonarTests} -Dsonar.test.inclusions=${sonarTestsInclusions}  -Dsonar.ts.tslintconfigpath={sonarTSLintConfigPath}  -Dsonar.testExecutionReportPaths=${sonarTestExecutionReportPath} -Dsonar.javascript.lcov.reportPaths=${sonarCoverageReportPath} "
            }

        } else {
            //Failed status
            currentBuild.result = NodejsConstants.FAILURE_BUILD_RESULT
            throw new hudson.AbortException("A mandatory sonarQube parameter has not found. A sonar-project.properties OR sonarQube pipeline parameters are mandatory. The mandatory properties on sonar-project.properties are sonar.sources, sonar.exclusions, sonar.tests, sonar.test.inclusions, sonar.ts.tslintconfigpath, sonar.testExecutionReportPaths, sonar.javascript.lcov.reportPaths and . The mandatory params.testing.predeploy.sonarQubeAnalisis parameters of pipeline are: sonarSources, sonarTests, sonarTestExecutionReportPath. sonarCoverageReportPath amd sonarExclusions")

        }

    }
}
