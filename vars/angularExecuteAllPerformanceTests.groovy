#!/usr/bin/groovy
import com.evobanco.AngularConstants

def call(body) {
    def config = [:]
    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = config
    body()


    echo "angularExecuteAllPerformanceTests global variable parameters\n" +
            "------------------------------------------------------------\n" +
            "config.theBranchType: ${config.theBranchType} \n" +
            "config.theTimeoutConfirmPostDeployTests: ${config.theTimeoutConfirmPostDeployTests} \n" +
            "config.theTimeoutConfirmPostDeployTestsTime: ${config.theTimeoutConfirmPostDeployTestsTime} \n" +
            "config.theTimeoutConfirmPostDeployTestsUnit: ${config.theTimeoutConfirmPostDeployTestsUnit} \n" +
            "config.theSmokeTestingBranches: ${config.theSmokeTestingBranches} \n" +
            "config.theAcceptanceTestingBranches: ${config.theAcceptanceTestingBranches} \n" +
            "config.theSecurityTestingBranches: ${config.theSecurityTestingBranches} \n" +
            "config.thePerformanceTestingBranches: ${config.thePerformanceTestingBranches} \n" +
            "config.errorOnPostDeployTestsUnstableResult: ${config.errorOnPostDeployTestsUnstableResult} \n" +
            "config.theTaurusTestBasePath: ${config.theTaurusTestBasePath} \n" +
            "config.theSmokeTestPath: ${config.theSmokeTestPath} \n" +
            "config.theAcceptanceTestPath: ${config.theAcceptanceTestPath} \n" +
            "config.theSecurityTestPath: ${config.theSecurityTestPath} \n" +
            "config.thePerformanceTestPath: ${config.thePerformanceTestPath} \n" +
            "config.theOpenshiftRouteHostnameWithProtocol: ${config.theOpenshiftRouteHostnameWithProtocol} \n"


    def tasks = [:]
    def executePostDeployTests = 'Yes'
    def doPerformanceTests = false
    Boolean errorOnPostDeployTestsUnstableResult = false
    def branchType = config.theBranchType
    def taurus_test_base_path = config.theTaurusTestBasePath
    def smoke_test_path = config.theSmokeTestPath
    def acceptance_test_path = config.theAcceptanceTestPath
    def security_test_path = config.theSecurityTestPath
    def performance_test_path = config.thePerformanceTestPath
    def openshift_route_hostname_with_protocol = config.theOpenshiftRouteHostnameWithProtocol

    if (config.errorOnPostDeployTestsUnstableResult != null) {
        errorOnPostDeployTestsUnstableResult = config.errorOnPostDeployTestsUnstableResult.toBoolean()
    }

    echo "errorOnPostDeployTestsUnstableResult value: ${errorOnPostDeployTestsUnstableResult}"


    if (branchType in config.theSmokeTestingBranches
            || branchType in config.theSmokeTestingBranches
            || branchType in config.theSmokeTestingBranches
            || branchType in config.theSmokeTestingBranches)   {
        doPerformanceTests = true
    }

    echo "doPerformanceTests value: ${doPerformanceTests}"

    doPerformanceTests = true
    if (doPerformanceTests) {

        node {
            try {
                stage('Decide on execution post deploy tests') {

                    executePostDeployTests = angularTimeoutConfirmMessage {
                        theTimeoutConfirmDeploy = config.theTimeoutConfirmPostDeployTests
                        theTimeoutConfirmDeployTime = config.theTimeoutConfirmPostDeployTestsTime
                        theTimeoutConfirmDeployUnit = config.theTimeoutConfirmPostDeployTestsUnit
                        theMessage = 'Waiting for user approval'
                        theChoiceName = 'Continue and execute post deploy tests? (check that the exposed route is accesible)'
                        theChoices = 'No\nYes'
                        theChoiceDescription = 'Choose "Yes" if you want to execute post deploy tests'
                    }
                }

            } catch (err) {
                def user = err.getCauses()[0].getUser()
                if ('SYSTEM'.equals(user.toString())) { //timeout
                    currentBuild.result = "FAILED"
                    throw new hudson.AbortException("Timeout on confirm execution post deploy tests") as Throwable
                }
            }
        }

        if (executePostDeployTests == 'Yes') {
            //Smoke tests
            if (branchType in config.theSmokeTestingBranches) {
                tasks["${AngularConstants.SMOKE_TEST_TYPE}"] = {
                    node('taurus') { //taurus
                        try {
                            stage("${AngularConstants.SMOKE_TEST_TYPE} Tests") {
                                angularExecutePerformanceTest {
                                    pts_taurus_test_base_path = taurus_test_base_path
                                    pts_acceptance_test_path = smoke_test_path
                                    pts_openshift_route_hostname_with_protocol = openshift_route_hostname_with_protocol
                                    pts_performance_test_type = AngularConstants.SMOKE_TEST_TYPE
                                }
                            }
                        } catch (exc) {
                            def exc_message = exc.message
                            echo "${exc_message}"
                            if (errorOnPostDeployTestsUnstableResult) {
                                currentBuild.result = AngularConstants.UNSTABLE_BUILD_RESULT
                            } else {
                                //Failed status
                                currentBuild.result = AngularConstants.FAILURE_BUILD_RESULT
                                throw new hudson.AbortException("The ${AngularConstants.SMOKE_TEST_TYPE} tests stage has failures") as Throwable
                            }
                        }
                    }
                }
            } else {
                echo "Skipping ${AngularConstants.SMOKE_TEST_TYPE} tests..."
            }

            //Acceptance tests
            if (branchType in config.theAcceptanceTestingBranches) {
                tasks["${AngularConstants.ACCEPTANCE_TEST_TYPE}"] = {
                    node('taurus') { //taurus
                        try {
                            stage("${AngularConstants.ACCEPTANCE_TEST_TYPE} Tests") {
                                angularExecutePerformanceTest {
                                    pts_taurus_test_base_path = taurus_test_base_path
                                    pts_acceptance_test_path = acceptance_test_path
                                    pts_openshift_route_hostname_with_protocol = openshift_route_hostname_with_protocol
                                    pts_performance_test_type = AngularConstants.ACCEPTANCE_TEST_TYPE
                                }
                            }
                        } catch (exc) {
                            def exc_message = exc.message
                            echo "${exc_message}"
                            if (errorOnPostDeployTestsUnstableResult) {
                                currentBuild.result = AngularConstants.UNSTABLE_BUILD_RESULT
                            } else {
                                //Failed status
                                currentBuild.result = AngularConstants.FAILURE_BUILD_RESULT
                                throw new hudson.AbortException("The ${AngularConstants.ACCEPTANCE_TEST_TYPE} tests stage has failures") as Throwable
                            }
                        }
                    }
                }
            } else {
                echo "Skipping ${AngularConstants.ACCEPTANCE_TEST_TYPE} tests..."
            }

            //Security tests
            if (branchType in config.theSecurityTestingBranches) {
                tasks["${AngularConstants.SECURITY_TEST_TYPE}"] = {
                    node('taurus') { //taurus
                        try {
                            stage("${AngularConstants.SECURITY_TEST_TYPE} Tests") {
                                angularExecutePerformanceTest {
                                    pts_taurus_test_base_path = taurus_test_base_path
                                    pts_acceptance_test_path = security_test_path
                                    pts_openshift_route_hostname_with_protocol = openshift_route_hostname_with_protocol
                                    pts_performance_test_type = AngularConstants.SECURITY_TEST_TYPE
                                }
                            }
                        } catch (exc) {
                            def exc_message = exc.message
                            echo "${exc_message}"
                            if (errorOnPostDeployTestsUnstableResult) {
                                currentBuild.result = AngularConstants.UNSTABLE_BUILD_RESULT
                            } else {
                                //Failed status
                                currentBuild.result = AngularConstants.FAILURE_BUILD_RESULT
                                throw new hudson.AbortException("The ${AngularConstants.SECURITY_TEST_TYPE} tests stage has failures") as Throwable
                            }
                        }
                    }
                }
            } else {
                echo "Skipping ${AngularConstants.SECURITY_TEST_TYPE} tests..."
            }

            //Executing smoke, acceptance and security tests in parallel
            parallel tasks

            //Performance tests
            if (branchType in config.thePerformanceTestingBranches) {
                node('taurus') { //taurus
                    try {
                        stage("${AngularConstants.PERFORMANCE_TEST_TYPE} Tests") {
                            angularExecutePerformanceTest {
                                pts_taurus_test_base_path = taurus_test_base_path
                                pts_acceptance_test_path = performance_test_path
                                pts_openshift_route_hostname_with_protocol = openshift_route_hostname_with_protocol
                                pts_performance_test_type = AngularConstants.PERFORMANCE_TEST_TYPE
                            }
                        }
                    } catch (exc) {
                        def exc_message = exc.message
                        echo "${exc_message}"
                        if (errorOnPostDeployTestsUnstableResult) {
                            currentBuild.result = AngularConstants.UNSTABLE_BUILD_RESULT
                        } else {
                            //Failed status
                            currentBuild.result = AngularConstants.FAILURE_BUILD_RESULT
                            throw new hudson.AbortException("The ${AngularConstants.PERFORMANCE_TEST_TYPE} tests stage has failures") as Throwable
                        }
                    }
                }
            } else {
                echo "Skipping ${AngularConstants.PERFORMANCE_TEST_TYPE} tests..."
            }
        } else {
            echo "Skipping all post build types tests..."
        }
    } else {
        echo "Skipping all post build types tests..."
    }
}
