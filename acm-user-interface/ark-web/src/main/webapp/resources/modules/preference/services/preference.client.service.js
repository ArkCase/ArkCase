'use strict';

angular.module('preference').factory('Preference.PreferenceService', ['ConfigService',
    function (ConfigService) {
        var Service = {};

        Service.filterModules = function(){
            var allModules = ConfigService.queryModules();
            //Modules with widgets enabled: Currently [Complaints, Cases, Tasks, Cost Tracking, Time Tracking]
            var modulesWithWidgets = ['Complaints', 'Cases', 'Tasks', 'Cost Tracking', 'Time Tracking'];

            var modules = []; //empty array to store kept variables
            //Filter modules against the modulesWithWidgets array
            for (var i = 0; i < allModules.length; i++){
                for (var j = 0; j < modulesWithWidgets.length; j++){
                    if (allModules[i] = modulesWithWidgets[j]){
                        modules.add(allModules[i]);
                    }
                }
            }

            return modules;
        }

        return Service;
    }
]);