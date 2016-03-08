'use strict';

angular.module('cost-tracking').run(['Menus', 'ConfigService',
	function(Menus, ConfigService){
		var config = ConfigService.getModule({moduleId: 'cost-tracking'});
		config.$promise.then(function(config){
			if (config.menus) {
				Menus.addMenuItems(config.menus);
			}
		});
	}
]);