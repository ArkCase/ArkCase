'use strict';

/**
 * @ngdoc controller
 * @name analytics.controller:AnalyticsController
 *
 * @description
 * {@link https://github.com/Armedia/ACM3/tree/develop/acm-user-interface/ark-web/src/main/webapp/resources/modules/analytics/controllers/analytics.client.controller.js modules/analytics/controllers/analytics.client.controller.js}
 *
 * The Analytics module main controller
 */

angular.module('analytics').controller('AnalyticsController', ['$scope', 'LookupService',
    'Analytics.BuildUrl'
    , function ($scope, LookupService, BuildUrl) {

        $scope.data = {};
        // Retrieves the properties from the acm-analytics-config.properties file
        var promiseServerConfig = LookupService.getConfig("acm-analytics-config");
    
        promiseServerConfig.then(function (data) {
            var elkConfig = data;

            $scope.data.slkHost = elkConfig['slk.server.internal.url'];
            $scope.data.slkPort = elkConfig['slk.server.internal.port'];
            $scope.data.slkExternalUrl = elkConfig['slk.server.external.url'];
            $scope.data.slkDashboard = elkConfig['slk.server.dashboard.url'];
            $scope.bananaUrl = BuildUrl.getUrlBanana($scope.data);

        });
    
    }
]);