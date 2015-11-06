'use strict';

angular.module('welcome').run(['Menus', 'ConfigService',
	function(Menus, ConfigService){
		var config = ConfigService.getModule({moduleId: 'welcome'});
		config.$promise.then(function(config){
			if (config.menus) {
				Menus.addMenuItems(config.menus);
			}
		});
	}
]);