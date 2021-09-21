angular.module('services').factory('EmailValidationService', ['$http', function ($http) {
    return {
        validateInput: validateInput,
        setEmailRegex: setEmailRegex()
    }

    function getEmailRegex() {
        return $http({
            method: 'GET',
            url: 'api/latest/plugin/email/regex',
            headers: {
                'Accept': 'text/plain'
            }
        });
    }

    function validateInput(inputValue) {
        var value = inputValue;
        var showEmailError = false;
        if (emailRegEx.test(value)) {
            inputValue = value;
            showEmailError = false;
        } else {
            inputValue = null;
            showEmailError = true;
        }
        return {
            inputValue: inputValue,
            showEmailError: showEmailError
        };
    }

    function setEmailRegex() {
        getEmailRegex().then((data) => {
        emailRegEx = new RegExp(data.data);
        })
    }
}]);
