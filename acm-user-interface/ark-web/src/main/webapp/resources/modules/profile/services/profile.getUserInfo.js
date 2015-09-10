'use strict';
angular.module('profile').factory('getUserInfo', function($http) {
  var promise;
  var userInfo = {
    async: function() {
      if ( !promise ) {
        // $http returns a promise, which has a then function, which also returns a promise
        promise = $http.get('proxy/arkcase/api/latest/plugin/profile/get/ann-acm').then(function (response) {
          return response.data;
        });
      }
      // Return the promise to the controller
      return promise;
    }
  };
  return userInfo;
});