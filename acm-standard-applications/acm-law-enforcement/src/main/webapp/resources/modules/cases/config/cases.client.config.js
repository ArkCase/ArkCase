'use strict';

angular.module('cases').run(['Menus', 'ConfigService','$translate',
    function (Menus, ConfigService, $translate) {
        var config = ConfigService.getModule({moduleId: 'cases'});
        config.$promise.then(function (config) {
            if (config.menus) {
                Menus.addMenuItems(config.menus);
            }
        });
	}
]);


