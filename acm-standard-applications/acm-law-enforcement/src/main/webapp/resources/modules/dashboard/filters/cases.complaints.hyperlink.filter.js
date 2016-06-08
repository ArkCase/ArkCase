'use strict'

angular.module("dashboard.my-tasks").filter('hyperlinkFilter', function ($filter) {
    return function (input, char) {
        if(input == null) {
            return;
        }
        if(input == 'CASE_FILE'){
            return 'cases';
        }
        if(input == 'COMPLAINT')
        return 'complaints';
    };
})
