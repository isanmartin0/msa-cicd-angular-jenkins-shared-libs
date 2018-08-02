#!/usr/bin/groovy

def call(body) {

    def config = [:]
    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = config
    body()

    def taurus_test_base_path = config.pts_taurus_test_base_path
    def acceptance_test_path = config.pts_acceptance_test_path
    def openshift_route_hostname_with_protocol = config.pts_openshift_route_hostname_with_protocol
    def performance_test_type = config.pts_performance_test_type

    echo "angularExecutePerformanceTest global variable parameters"
    echo "taurus_test_base_path: ${taurus_test_base_path}"
    echo "acceptance_test_path: ${acceptance_test_path}"
    echo "openshift_route_hostname_with_protocol: ${openshift_route_hostname_with_protocol}"
    echo "performance_test_type: ${performance_test_type}"

    def isErrorTestStage = false
    checkout scm
    echo "Running ${performance_test_type} tests..."

    def test_files_location = taurus_test_base_path + acceptance_test_path + '**/*.yml'
    echo "Searching ${performance_test_type} tests with pattern: ${test_files_location}"

    def files = findFiles(glob: test_files_location)

    def testFilesNumber = files.length
    echo "${performance_test_type} test files found number: ${testFilesNumber}"

    files.eachWithIndex { file, index ->

        def isDirectory = files[index].directory

        if (!isDirectory) {
            echo "Executing ${performance_test_type} test file number #${index}: ${files[index].path}"

            echo "Setting taurus scenarios.scenario-default.default-address to ${openshift_route_hostname_with_protocol}"
            echo "Setting taurus modules.gatling.java-opts to ${openshift_route_hostname_with_protocol}"

            def bztScript = 'bzt -o scenarios.scenario-default.default-address=' + openshift_route_hostname_with_protocol + ' -o modules.gatling.java-opts=-Ddefault-address=' + openshift_route_hostname_with_protocol + ' ' + files[index].path  + ' -report --option=modules.console.disable=true'

            try {
                echo "Executing script ${bztScript}"
                sh "${bztScript}"
            } catch (exc) {
                isErrorTestStage = true
                echo "There is an error executing ${performance_test_type} test"
                def exc_message = exc.message
                echo "${exc_message}"
            }
        }
    }

    if (isErrorTestStage) {
        echo "${performance_test_type} tests have caused an unstable result to build"
        sh "exit 1"
    }
}
