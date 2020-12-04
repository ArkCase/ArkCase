'use strict';

angular.module('goodbye').run([ 'Menus', 'ConfigService', function(Menus, ConfigService) {
    var config = ConfigService.getModuleConfig("goodbye").then(function(config) {
        if (config.menus) {
            Menus.addMenuItems(config.menus);
        }
        return config;
    });
} ]);