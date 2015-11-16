'use strict';

angular.module('tasks').run(['Menus', 'ConfigService',
    function (Menus, ConfigService) {
        var config = ConfigService.getModule({moduleId: 'tasks'});
        config.$promise.then(function (config) {
            if (config.menus) {
                Menus.addMenuItems(config.menus);
            }
        });
    }
]);


