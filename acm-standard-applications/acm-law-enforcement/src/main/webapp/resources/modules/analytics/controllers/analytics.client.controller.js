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
            $scope.data.elkHost = elkConfig['ELK_SERVER_URL'];
            $scope.data.elkPort = elkConfig['ELK_SERVER_PORT'];
            $scope.data.elkUser = elkConfig['ELK_SERVER_USER'];
            $scope.data.elkPassword = elkConfig['ELK_SERVER_PASSWORD'];
            $scope.data.elkDashboard = elkConfig['ELK_DASHBOARD_URL'];
            $scope.elkUrl = BuildUrl.getUrl($scope.data);
        });
    
    }
]);