'use strict';
angular.module('search').factory('ResultService', function () {
    var result = {};
    result.data = '';
    result.queryString = '';
    result.filterParams='';
    result.passData = function (data, queryString,filter) {
        result.data = data;
        result.queryString = queryString;
        result.filterParams=filter;
    };
    return result;
});
