'use strict';

angular.module('filters').filter('beautifyParentRefToParentId', function() {
    return function(myString) {
        return parseInt(myString.substr(0, myString.indexOf('-')));
    };
});