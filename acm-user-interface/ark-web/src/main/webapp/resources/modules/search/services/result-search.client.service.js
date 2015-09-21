'use strict';
angular.module('search').factory('ResultService', function ($rootScope) {
    var result = {};
    result.data = '';
    result.queryString = '';
    result.passData = function (data, queryString) {
        result.data = data;
        result.queryString = queryString;
        $rootScope.$broadcast('query-complete');
    };
    return result;
});
