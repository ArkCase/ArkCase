'use strict';

angular.module('filters').filter('mapIdValue', function() {
    return function(input, idValues, idField, valueField) {
        if (!idField) {
            idField = "id";
        }
        if (!valueField) {
            valueField = "value";
        }
        var find = _(idValues).filter(function(idValue) {
            return idValue[idField] == input;
        })
        .map(function(obj) {
            return obj[valueField];
        })
        .value()
        ;

        return (0 < find.length)? find[0] : input;
    };
})
;