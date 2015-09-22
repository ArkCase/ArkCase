//'use strict';
//angular.module('profile').factory('getUserInfo', function($http) {
//  var promise;
//  var userInfo = {
//    async: function() {
//      if ( !promise ) {
//        promise = $http.get('proxy/arkcase/api/latest/plugin/profile/get/ann-acm').then(function (response) {
//          return response.data;
//        });
//      }
//      return promise;
//    }
//  };
//  return userInfo;
//});
'use strict';
angular.module('profile').service('userInfoService', function ($http, $q) {
    return({
        getUserInfo: getUserInfo,
        updateUserInfo:updateUserInfo
    });
    function getUserInfo() {
        var request = $http({
            method: "GET",
            url: 'proxy/arkcase/api/latest/plugin/profile/get/ann-acm'
        });
        return(request.then(handleSuccess, handleError));
    };
    function updateUserInfo(data) {
        var request = $http({
            method: "POST",
            processData: false,
            url: 'proxy/arkcase/api/latest/plugin/profile/userOrgInfo/set',
            data: data
        });
        return(request.then(handleSuccess, handleError));
    };
    function handleError(response) {
        if (
                !angular.isObject(response.data) ||
                !response.data.message
                ) {
            return($q.reject("An unknown error occurred."));
        }
        return($q.reject(response.data.message));
    }
    function handleSuccess(response) {
        return(response.data);
    }
});
