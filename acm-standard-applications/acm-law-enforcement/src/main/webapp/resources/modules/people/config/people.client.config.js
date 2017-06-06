'use strict';

angular.module('people').run(['Menus', 'ConfigService',
    function (Menus, ConfigService) {
        ConfigService.getModuleConfig("people").then(function (config) {
            if (config.menus) {
                Menus.addMenuItems(config.menus);
            }
        });
    }
]);


