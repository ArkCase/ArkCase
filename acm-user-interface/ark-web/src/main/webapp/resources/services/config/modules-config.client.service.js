'use strict';

angular.module('services').factory('ConfigService', ['$resource',
	function($resource) {
		return $resource('api/config/',{
		},{
			getModule: {
				method: 'GET',
				cache: true,
				url: 'modules_config/config/modules/:moduleId/config.json',
				isArray: false
			},

			queryModules: {
				method: 'GET',
				cache: true,
				url: 'modules_config/config/modules.json',
				isArray: true
			},

			updateModule: {
				method: 'PUT',
				url: 'modules_config/config/modules/:moduleId/config.json',
				isArray: false
			}
        });
	}
]);