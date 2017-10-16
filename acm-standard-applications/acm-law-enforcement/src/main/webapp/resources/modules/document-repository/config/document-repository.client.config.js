'use strict';

angular.module('document-repository').run(['Menus', 'ConfigService',
    function (Menus, ConfigService) {
        var config = ConfigService.getModule({moduleId: 'document-repository'});
        config.$promise.then(function (config) {
            if (config.menus) {
                Menus.addMenuItems(config.menus);
            }
        });
	}
]);


