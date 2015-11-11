'use strict';

angular.module('admin').run(['Menus', 'ConfigService', '$log',
    function (Menus, ConfigService) {
        var config = ConfigService.getModule({moduleId: 'admin'});
        config.$promise.then(function (config) {
            if (config.menus) {
                Menus.addMenuItems(config.menus);
            }
        });
    }
]);