'use strict';

angular.module('dashboard').run(['Menus', 'ConfigService',
    function (Menus, ConfigService) {
        var config = ConfigService.getModule({moduleId: 'dashboard'});
        config.$promise.then(function (config) {
            if (config.menus) {
                Menus.addMenuItems(config.menus);
            }
        });
    }
]);