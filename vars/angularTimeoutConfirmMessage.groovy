#!/usr/bin/groovy

def call(body) {
    def config = [:]
    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = config
    body()


    echo "angularTimeoutConfirmMessage global variable parameters\n" +
            "-------------------------------------------------------\n" +
            "config.theTimeoutConfirmDeploy: ${config.theTimeoutConfirmDeploy} \n" +
            "config.theTimeoutConfirmDeployTime: ${config.theTimeoutConfirmDeployTime} \n" +
            "config.theTimeoutConfirmDeployUnit: ${config.theTimeoutConfirmDeployUnit} \n" +
            "config.theMessage: ${config.theMessage} \n" +
            "config.theChoiceName: ${config.theChoiceName} \n" +
            "config.theChoices: ${config.theChoices} \n" +
            "config.theChoiceDescription: ${config.theChoiceDescription} \n"

    //Parameters timeout deploy answer

    Boolean timeoutConfirmDeploy = false
    int timeoutConfirmDeployTime = 0
    String timeoutConfirmDeployUnit = ''
    boolean isTimeoutConfirmDeployUnitValid = false


    if (params.timeoutConfirmDeploy != null) {
        timeoutConfirmDeploy = params.timeoutConfirmDeploy.toBoolean()
    }

    if (timeoutConfirmDeploy) {


        String timeoutConfirmDeployTimeParam = params.timeoutConfirmDeployTime
        if (timeoutConfirmDeployTimeParam != null && timeoutConfirmDeployTimeParam.isInteger()) {
            timeoutConfirmDeployTime = timeoutConfirmDeployTimeParam as Integer
        }

        if (params.timeoutConfirmDeployUnit != null && ("NANOSECONDS".equals(params.timeoutConfirmDeployUnit.toUpperCase())
                || "MICROSECONDS".equals(params.timeoutConfirmDeployUnit.toUpperCase())
                || "MILLISECONDS".equals(params.timeoutConfirmDeployUnit.toUpperCase())
                || "SECONDS".equals(params.timeoutConfirmDeployUnit.toUpperCase())
                || "MINUTES".equals(params.timeoutConfirmDeployUnit.toUpperCase())
                || "HOURS".equals(params.timeoutConfirmDeployUnit.toUpperCase())
                || "DAYS".equals(params.timeoutConfirmDeployUnit.toUpperCase()))) {
            isTimeoutConfirmDeployUnitValid = true
            timeoutConfirmDeployUnit = params.timeoutConfirmDeployUnit.toUpperCase()
        }
    }

    echo "timeoutConfirmDeploy value: ${timeoutConfirmDeploy}"

    if (timeoutConfirmDeploy) {
        echo "timeoutConfirmDeployTime value: ${timeoutConfirmDeployTime}"
        echo "timeoutConfirmDeployUnit value: ${timeoutConfirmDeployUnit}"
    }


    if (timeoutConfirmDeploy && timeoutConfirmDeployTime > 0 && isTimeoutConfirmDeployUnitValid) {
        //Wrap input with timeout
        timeout(time:timeoutConfirmDeployTime, unit:"${timeoutConfirmDeployUnit}") {
            deploy = input message: "${config.theMessage}",
                    parameters: [choice(name: "${theChoiceName}", choices: "${theChoices}", description: "${theChoiceDescription}")]
        }
    } else {
        //Input without timeout
        deploy = input message: "${config.theMessage}",
                parameters: [choice(name: "${theChoiceName}", choices: 'No\nYes', description: "${theChoiceDescription}")]

    }

    return deploy
}
