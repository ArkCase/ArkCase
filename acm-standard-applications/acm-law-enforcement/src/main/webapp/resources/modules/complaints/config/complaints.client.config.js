'use strict';

angular.module('complaints').run([ 'Menus', 'ConfigService', function(Menus, ConfigService) {
    ConfigService.getModuleConfig("complaints").then(function(config) {
        if (config.menus) {
            Menus.addMenuItems(config.menus);
        }
    });
} ]);
