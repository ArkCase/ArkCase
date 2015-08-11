'use strict';

angular.module('services').factory('ConfigService', ['$resource',
	function($resource) {
		return $resource('api/config/',{
		},{
			getModule: {
				method: 'GET',
				cache: false,
				url: 'api/config/modules/:moduleId',
				isArray: false
			},

			queryModules: {
				method: 'GET',
				cache: true,
				url: 'api/config/modules',
				isArray: true
			},

			updateModule: {
				method: 'PUT',
				url: 'api/config/modules/:moduleId',
				isArray: false
			}
        });
	}
]);