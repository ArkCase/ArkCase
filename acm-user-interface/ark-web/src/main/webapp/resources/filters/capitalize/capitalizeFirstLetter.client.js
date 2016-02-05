'use strict';

angular.module('filters').filter('capitalizeFirst', function () {
    return function(input) {
        if (input!=null)
            input = input.toLowerCase();
        return input.substring(0,1).toUpperCase()+input.substring(1);
    };
})
;