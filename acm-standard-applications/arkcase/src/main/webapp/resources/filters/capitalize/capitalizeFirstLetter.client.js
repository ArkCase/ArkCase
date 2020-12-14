'use strict';

angular.module('filters').filter('capitalizeFirst', [ 'UtilService', function(Util) {

    // split the string into words by the given regex and capitalize every word
    function capitalizeAndClean(str) {
        var words = str.split(/[.,_\/ -]/);
        for (var i = 0; i < words.length; i++) {
            words[i] = words[i].charAt(0).toUpperCase() + words[i].slice(1);
        }
        return words.join(' ');
    }

    return function(input) {
        if (input != null)
            input = Util.goodValue(input).toLowerCase();
        return capitalizeAndClean(input);
    };
} ]);
