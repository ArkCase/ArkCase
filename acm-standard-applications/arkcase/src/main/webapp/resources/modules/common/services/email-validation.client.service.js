angular.module('services').factory('EmailValidationService', ['$http', '$q', function ($http, $q) {
    var emailRegEx = null;
    return {
        validateInput: validateInput
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
        var deferred = $q.defer();
        if (!emailRegEx) {
            getEmailRegex().then(function(response) {
                emailRegEx = new RegExp(response.data);
                deferred.resolve(validate(inputValue));
            })
        } else {
            deferred.resolve(validate(inputValue))

        }
        return deferred.promise
    }


    function validate(inputValue) {
        var value = inputValue;
        var showEmailError = false;
        if (emailRegEx.test(value)) {
            inputValue = value;
            showEmailError = false;
        } else {
            inputValue = null;
            showEmailError = true;
        }
        return { inputValue: inputValue,
            showEmailError: showEmailError}
    }

}]);
