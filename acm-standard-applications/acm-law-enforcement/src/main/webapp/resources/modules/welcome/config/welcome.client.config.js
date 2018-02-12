'use strict';

angular.module('welcome').run([ 'Menus', 'ConfigService', function(Menus, ConfigService) {
    var config = ConfigService.getModuleConfig("welcome").then(function(config) {
        if (config.menus) {
            Menus.addMenuItems(config.menus);
        }
        return config;
    });
} ]);