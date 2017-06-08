'use strict';

angular.module('analytics').run(['Menus', 'ConfigService',
    function(Menus, ConfigService){
        var config = ConfigService.getModule({moduleId: 'analytics'});
        config.$promise.then(function(config){
            if (config.menus) {
                Menus.addMenuItems(config.menus);
            }
        });
    }
]);