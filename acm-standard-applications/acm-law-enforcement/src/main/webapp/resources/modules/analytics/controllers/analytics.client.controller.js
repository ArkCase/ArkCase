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
    
        // Retrieves the properties from the acm-elk-config.properties file
        var promiseServerConfig = LookupService.getConfig("acm-elk-config");
    
        promiseServerConfig.then(function (data) {
            var elkConfig = data;
            
            /*
            $scope.data.elkHost = elkConfig['elk.server.url'];
            $scope.data.elkPort = elkConfig['elk.server.port'];
            $scope.data.elkUser = elkConfig['elk.server.user'];
            $scope.data.elkPassword = elkConfig['elk.server.password'];
            $scope.data.elkDashboard = elkConfig['elk.dashboard.url'];
            $scope.elkUrl = BuildUrl.getUrl($scope.data);
             */

            $scope.data.slkHost = elkConfig['slk.server.internal.url'];
            $scope.data.slkPort = elkConfig['slk.server.internal.port'];
            $scope.data.slkDashboard = elkConfig['slk.server.dashboard.url'];
            $scope.bananaUrl = BuildUrl.getUrlBanana($scope.data);

        });
    
    }
]);