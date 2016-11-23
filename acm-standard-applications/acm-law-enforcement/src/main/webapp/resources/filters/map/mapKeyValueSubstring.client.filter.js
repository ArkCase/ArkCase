'use strict';

angular.module('filters').filter('mapKeyValueSubstring', function () {
    return function (input, keyValues, keyField, valueField) {
        if (!keyField) {
            keyField = "key";
        }
        if (!valueField) {
            valueField = "value";
        }
        var find = _(keyValues).filter(function (idValue) {
            return idValue[keyField] == input;
        }).map(function (obj) {
            return obj[valueField];
        }).value();

        return (0 < find.length) ? find[0] : input;
    };
});