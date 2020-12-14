'use strict';

angular.module('reports').run([ 'Menus', 'ConfigService', function(Menus, ConfigService) {
    var config = ConfigService.getModule({
        moduleId: 'reports'
    });
    config.$promise.then(function(config) {
        if (config.menus) {
            Menus.addMenuItems(config.menus);
        }
    });
} ]);