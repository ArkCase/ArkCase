'use strict';

angular.module('analytics-audit').run(['Menus', 'ConfigService',
    function(Menus, ConfigService){
        var config = ConfigService.getModule({moduleId: 'analytics-audit'});
        config.$promise.then(function(config){
            if (config.menus) {
                Menus.addMenuItems(config.menus);
            }
        });
    }
]);