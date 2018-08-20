#!/usr/bin/groovy

def call(body) {
    def config = [:]
    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = config
    body()


    echo "angularCreateTarball global variable parameters\n" +
            "-----------------------------------------------\n" +
            "config.thePackageJSON: ${config.thePackageJSON} \n" +
            "config.useSpecificOutputPath: ${config.useSpecificOutputPath} \n" +
            "config.buildSpecificOutputPath: ${config.buildSpecificOutputPath} \n"

    def packageJSON = config.thePackageJSON

    def packageJSONFilesNodeDistributionFolder = ["dist/"]

    echo "Original package.json:"
    echo "${packageJSON}"

    def packageJSONFilesNode = packageJSON.files
    echo "packageJSONFilesNode: ${packageJSONFilesNode}"

    //Redefining packageJSON.files
    Boolean useSpecificOutputPath = false

    if (config.useSpecificOutputPath) {
        useSpecificOutputPath = config.useSpecificOutputPath.toBoolean()
    }

    if (useSpecificOutputPath) {
        if (config.buildSpecificOutputPath) {
            packageJSONFilesNodeDistributionFolder = [config.buildSpecificOutputPath]
        }
    }

    echo "packageJSONFilesNodeDistributionFolder: ${packageJSONFilesNodeDistributionFolder}"

    packageJSON.files = packageJSONFilesNodeDistributionFolder

    def packageJSONPrivateNode = packageJSON.private
    echo "packageJSONPrivateNode: ${packageJSONPrivateNode}"

    //Redefining packageJSON.private
    if (packageJSONPrivateNode) {
        packageJSON.private = false
    }

    echo "Updated package.json:"
    echo "${packageJSON}"

    writeJSON file: 'package.json', json: packageJSON, pretty: 4

    sh "npm pack"


}
