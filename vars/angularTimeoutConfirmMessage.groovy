#!/usr/bin/groovy

def call(body) {
    def config = [:]
    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = config
    body()


    echo "angularTimeoutConfirmMessage global variable parameters\n" +
            "-------------------------------------------------------\n" +
            "config.theTimeoutConfirm: ${config.theTimeoutConfirm} \n" +
            "config.theTimeoutConfirmTime: ${config.theTimeoutConfirmTime} \n" +
            "config.theTimeoutConfirmUnit: ${config.theTimeoutConfirmUnit} \n" +
            "config.theMessage: ${config.theMessage} \n" +
            "config.theChoiceName: ${config.theChoiceName} \n" +
            "config.theChoices: ${config.theChoices} \n" +
            "config.theChoiceDescription: ${config.theChoiceDescription} \n"

    //Parameters timeout answer

    Boolean timeoutConfirm = false
    int timeoutConfirmTime = 0
    String timeoutConfirmUnit = ''
    boolean isTimeoutConfirmUnitValid = false
    def answer = ''


    if (config.theTimeoutConfirm != null) {
        timeoutConfirm = config.theTimeoutConfirm.toBoolean()
    }

    if (timeoutConfirm) {


        String timeoutConfirmTimeParam = config.theTimeoutConfirmTime
        if (timeoutConfirmTimeParam != null && timeoutConfirmTimeParam.isInteger()) {
            timeoutConfirmTime = timeoutConfirmTimeParam as Integer
        }

        if (config.theTimeoutConfirmUnit != null && ("NANOSECONDS".equals(config.theTimeoutConfirmUnit.toUpperCase())
                || "MICROSECONDS".equals(config.theTimeoutConfirmUnit.toUpperCase())
                || "MILLISECONDS".equals(config.theTimeoutConfirmUnit.toUpperCase())
                || "SECONDS".equals(config.theTimeoutConfirmUnit.toUpperCase())
                || "MINUTES".equals(config.theTimeoutConfirmUnit.toUpperCase())
                || "HOURS".equals(config.theTimeoutConfirmUnit.toUpperCase())
                || "DAYS".equals(config.theTimeoutConfirmUnit.toUpperCase()))) {
            isTimeoutConfirmUnitValid = true
            timeoutConfirmUnit = config.theTimeoutConfirmUnit.toUpperCase()
        }
    }

    echo "timeoutConfirm value: ${timeoutConfirm}"

    if (timeoutConfirm) {
        echo "timeoutConfirmTime value: ${timeoutConfirmTime}"
        echo "timeoutConfirmUnit value: ${timeoutConfirmUnit}"
    }


    if (timeoutConfirm && timeoutConfirmTime > 0 && isTimeoutConfirmyUnitValid) {
        //Wrap input with timeout
        timeout(time:timeoutConfirmTime, unit:"${timeoutConfirmUnit}") {
            answer = input message: "${config.theMessage}",
                    parameters: [choice(name: "${config.theChoiceName}", choices: "${config.theChoices}", description: "${config.theChoiceDescription}")]
        }
    } else {
        //Input without timeout
        answer = input message: "${config.theMessage}",
                parameters: [choice(name: "${config.theChoiceName}", choices: "${config.theChoices}", description: "${config.theChoiceDescription}")]

    }

    return answer
}
