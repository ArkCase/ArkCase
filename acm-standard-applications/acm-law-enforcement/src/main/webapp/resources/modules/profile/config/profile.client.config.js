'use strict';

angular.module('profile').run(['Menus', 'ConfigService',
	function(Menus, ConfigService){
		var config = ConfigService.getModule({moduleId: 'profile'});
		config.$promise.then(function(config){
			if (config.menus) {
				Menus.addMenuItems(config.menus);
			}
		});
	}
]);