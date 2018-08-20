#!/usr/bin/groovy

def call(body) {
    def config = [:]
    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = config
    body()


    echo "angularGetNGVersion global variable parameters\n" +
            "-----------------------------------------------\n" +
            "config.theAngularCliLocalPath: ${config.theAngularCliLocalPath} \n" +
            "config.theInstallGloballyAngularCli: ${config.theInstallGloballyAngularCli} \n" +

    Boolean installGloballyAngularCli = config.theInstallGloballyAngularCli
    def angularCliLocalPath = config.theAngularCliLocalPath

    echo 'ng version:'

    if (installGloballyAngularCli) {
        sh "ng version "
    } else {
        sh "${angularCliLocalPath}ng version"
    }

}
