'use strict';
// Authentication service for user variables

angular.module('services').factory('Authentication', ['$resource', 'ValidationService',
	function($resource, Validator) {
		return $resource('proxy/arkcase/api/v1/users/info',{},{
			queryUserInfo: {
				method: 'GET',
				cache: true,
				url: 'proxy/arkcase/api/v1/users/info',
				isArray: false,
				transformResponse: function(data, headers){
					var userInfo = {};
					if(Validator.validateUserInfo(JSON.parse(data))){
						userInfo = JSON.parse(data);
					}
					return userInfo;
				}
			}
		});
	}
]);