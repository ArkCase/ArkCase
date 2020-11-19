angular.module('directives').filter('orderObjectBy', function() {
    return function(input) {
        if (!angular.isObject(input))
            return input;

        var array = [];
        for ( var objectKey in input) {
            array.push(input[objectKey]);
        }

        array.sort(function(a, b) {
            a = a["name"];
            b = b["name"];
            return a > b ? 1 : a < b ? -1 : 0;
        });
        return array;
    }
});