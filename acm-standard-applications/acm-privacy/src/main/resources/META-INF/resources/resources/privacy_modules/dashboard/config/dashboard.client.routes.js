'use strict';

//Setting up route
angular.module('dashboard').config([ '$stateProvider', 'ArkCaseDashboardProvider', function($stateProvider, ArkCaseDashboardProvider) {
    $stateProvider.state('dashboard', {
        url: '/dashboard',
        templateUrl: 'modules/dashboard/views/dashboard.client.view.html',
        controller: 'DashboardController',
        resolve: {
            translatePartialLoader: [ '$translate', '$translatePartialLoader', function($translate, $translatePartialLoader) {
                $translatePartialLoader.addPart('common');
                $translatePartialLoader.addPart('dashboard');
                $translatePartialLoader.addPart('request-info');
                return $translate.refresh();
            } ]
        }
    });
} ]).run([ 'ArkCaseDashboard', 'ConfigService', function(ArkCaseDashboard, ConfigService) {
    ConfigService.getModuleConfig("dashboard").then(function(moduleConfig) {
        moduleConfig.structures.forEach(function(structure) {
            ArkCaseDashboard.structure(structure.name, structure.def);
        });

        moduleConfig.locals.forEach(function(local) {
            ArkCaseDashboard.addLocale(local.iso, local.translations);
        });
        return moduleConfig;
    });
} ]);
