'use strict';

angular.module('organizations').run(['Menus', 'ConfigService',
    function (Menus, ConfigService) {
        ConfigService.getModuleConfig("organizations").then(function (config) {
            if (config.menus) {
                Menus.addMenuItems(config.menus);
            }
        });
    }
]);


