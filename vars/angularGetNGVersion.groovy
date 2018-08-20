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

    Boolean installGloballyAngularCli = false
    def angularCliLocalParh = config.theAngularCliLocalPath

    if (config.theInstallGloballyAngularCli) {
        installGloballyAngularCli = config.theInstallGloballyAngularCli.toBoolean()
    }

    echo 'ng version:'

    if (installGloballyAngularCli) {
        sh "ng version "
    } else {
        sh "${angularCliLocalParh}ng version"
    }

}
