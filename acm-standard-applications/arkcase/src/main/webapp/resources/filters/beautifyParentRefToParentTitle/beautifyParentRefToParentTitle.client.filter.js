'use strict';

angular.module('filters').filter('beautifyParentRefToParentTitle', function() {
    return function(myString) {
        return myString.substring(myString.lastIndexOf("-") + 1);
    };
});