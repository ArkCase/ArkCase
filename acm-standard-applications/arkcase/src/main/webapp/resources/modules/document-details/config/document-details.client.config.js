'use strict';

angular.module('document-details').run([ 'Menus', 'ConfigService', function(Menus, ConfigService) {
    var config = ConfigService.getModule({
        moduleId: 'document-details'
    });
    config.$promise.then(function(config) {
        if (config.menus) {
            Menus.addMenuItems(config.menus);
        }
    });
} ]);