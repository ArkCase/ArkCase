angular.module('services').factory('PhoneValidationService', ['$http', function ($http) {
    return {
        validateInput: validateInput,
        getPhoneRegex: getPhoneRegex
    }

    function getPhoneRegex() {
        return $http({
            method: 'GET',
            url: 'api/latest/plugin/regex',
        });
    }

    function validateInput(inputValue, response) {
        var regExp = new RegExp(response);
        var value = inputValue;
        var showPhoneError = false;
        if (regExp.test(value)) {
            inputValue = value;
            showPhoneError = false;
        } else {
            inputValue = null;
            showPhoneError = true;
        }
        return  {
            inputValue : inputValue,
            showPhoneError : showPhoneError
        };
    };

}]);
