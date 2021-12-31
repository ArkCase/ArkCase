angular.module('services').factory('RegexValidationService', function () {
    return {
    validateInput: validateInput
    }

    function validateInput(inputValue, regEx) {
        var showRegexError = regEx.test(inputValue);
        return {
            inputValue: inputValue,
            showRegexError: showRegexError
        };
    }
});