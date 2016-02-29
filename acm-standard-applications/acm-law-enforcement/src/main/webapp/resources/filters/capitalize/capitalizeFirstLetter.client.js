'use strict';

angular.module('filters').filter('capitalizeFirst',['UtilService', function (Util) {
    return function(input) {
        if (input!=null)
            input = Util.goodValue(input).toLowerCase();
        return input.substring(0,1).toUpperCase()+input.substring(1);
    };
}])
;