#!/usr/bin/groovy
import com.evobanco.AngularConstants

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
            "config.theSonarTypescriptExclusions: ${config.theSonarTypescriptExclusions} \n" +
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
                && config.theSonarTypescriptExclusions
                && config.theSonarTestExecutionReportPath
                && config.theSonarCoverageReportPath) {

            //Pipeline parameters contains properties for SonarQube.
            def sonarSources = config.theSonarSources
            def sonarExclusions = config.theSonarExclusions
            def sonarTests = config.theSonarTests
            def sonarTestsInclusions = config.theSonarTestsInclusions
            def sonarTypescriptExclusions = config.theSonarTypescriptExclusions
            def sonarTestExecutionReportPath = config.theSonarTestExecutionReportPath
            def sonarCoverageReportPath = config.theSonarCoverageReportPath


            echo 'sonarQube parameters extracted from pipeline parameters:'

            echo "sonarSources: ${sonarSources}"
            echo "sonarExclusions: ${sonarExclusions}"
            echo "sonarTests: ${sonarTests}"
            echo "sonarTestsInclusions: ${sonarTestsInclusions}"
            echo "sonarTypescriptExclusions: ${sonarTypescriptExclusions}"
            echo "sonarTestExecutionReportPath: ${sonarTestExecutionReportPath}"
            echo "sonarCoverageReportPath: ${sonarCoverageReportPath}"


            withSonarQubeEnv("${config.theSonarQubeServer}") {
                sh "${scannerHome}/bin/sonar-scanner -X -Dsonar.projectKey=${sonar_project_key} -Dsonar.projectName=${sonar_project_name} -Dsonar.sources=${sonarSources} -Dsonar.exclusions=${sonarExclusions} -Dsonar.tests=${sonarTests} -Dsonar.test.inclusions=${sonarTestsInclusions} -Dsonar.typescript.exclusions=${sonarTypescriptExclusions} -Dsonar.testExecutionReportPaths=${sonarTestExecutionReportPath} -Dsonar.typescript.lcov.reportPaths=${sonarCoverageReportPath} "
            }

        } else {
            //Failed status
            currentBuild.result = AngularConstants.FAILURE_BUILD_RESULT
            throw new hudson.AbortException("A mandatory sonarQube parameter has not found. A sonar-project.properties OR sonarQube pipeline parameters are mandatory. The mandatory properties on sonar-project.properties are sonar.sources, sonar.exclusions, sonar.tests, sonar.test.inclusions, sonar.typescript.exclusions, sonar.typescript.lcov.reportPaths and sonar.testExecutionReportPaths. The mandatory params.testing.predeploy.sonarQubeAnalisis parameters of pipeline are: sonarSources, sonarExclusions, sonarTests, sonarTestInclusions, sonarTypescriptExclusions, sonarTestExecutionReportPath and sonarCoverageReportPath")

        }

    }
}
