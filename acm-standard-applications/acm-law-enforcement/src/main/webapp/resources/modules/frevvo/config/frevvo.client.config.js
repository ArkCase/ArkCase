'use strict';

angular.module('frevvo').run([ 'Menus', 'ConfigService', function(Menus, ConfigService) {
    var config = ConfigService.getModule({
        moduleId: 'frevvo'
    });
    config.$promise.then(function(config) {
        if (config.menus) {
            Menus.addMenuItems(config.menus);
        }
    });
} ]);
