'use strict';

angular.module('consultations').run([ 'Menus', 'ConfigService', function(Menus, ConfigService) {
    var config = ConfigService.getModule({
        moduleId: 'consultations'
    });
    config.$promise.then(function(config) {
        if (config.menus) {
            Menus.addMenuItems(config.menus);
        }
    });
} ]);
