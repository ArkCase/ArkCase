'use strict';
angular.module('search').factory('resultService',function($rootScope){
    var result={};
    result.data='';
    result.queryString='';
    result.passData=function(data,queryString){
       result.data=data;
       result.queryString=queryString;
       $rootScope.$broadcast('queryComplete');
    };
    return result;
});
