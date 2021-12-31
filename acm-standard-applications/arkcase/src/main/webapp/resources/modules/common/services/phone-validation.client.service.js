angular.module('services').factory('PhoneValidationService', ['$http', function ($http) {
    return {
        getPhoneRegex: getPhoneRegex,
        validateInput: validateInput
    }

    function getPhoneRegex() {
        return $http({
            method: 'GET',
            url: 'api/latest/plugin/phone/regex',
            headers: {
                'Accept': 'text/plain'
            }
        });
    }

    function validateInput(inputValue, regEx) {
        var value = inputValue;
        var showPhoneError = false;
        if (regEx.test(value)) {
            inputValue = value;
            showPhoneError = false;
        } else {
            inputValue = null;
            showPhoneError = true;
        }
        return {
            inputValue: inputValue,
            showPhoneError: showPhoneError
        };
    }
}]);
