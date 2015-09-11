'use strict';
angular.module('profile').factory('getUserInfo', function($http) {
  var promise;
  var userInfo = {
    async: function() {
      if ( !promise ) {
        promise = $http.get('proxy/arkcase/api/latest/plugin/profile/get/ann-acm').then(function (response) {
          return response.data;
        });
      }
      return promise;
    }
  };
  return userInfo;
});