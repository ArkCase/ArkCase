'use strict';

angular.module('adhoc-reports').run(['Menus', 'ConfigService',
    function (Menus, ConfigService) {
        var config = ConfigService.getModule({
            moduleId: 'adhoc-reports'
        });
        config.$promise.then(function (config) {
            if (config.menus) {
                Menus.addMenuItems(config.menus);
            }
        });
    }
]);