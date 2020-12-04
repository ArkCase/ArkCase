'use strict';

angular.module('preference').run([ 'Menus', 'ConfigService', function(Menus, ConfigService) {
    var config = ConfigService.getModule({
        moduleId: 'preference'
    });
    config.$promise.then(function(config) {
        if (config.menus) {
            Menus.addMenuItems(config.menus);
        }
    });
} ]);
