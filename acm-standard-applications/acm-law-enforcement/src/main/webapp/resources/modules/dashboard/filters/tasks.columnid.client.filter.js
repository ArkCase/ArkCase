'use strict'

angular.module("dashboard.my-tasks").filter('filterColumn', function ($filter) {
    return function (input, char) {
        if(input == null) {
            return;
        }
        if(input.indexOf(char) > -1){
            return input;
        }
        return "";
    };
})
