'use strict';

/**
 * @ngdoc controller
 * @name analytics-audit.controller:AnalyticsAuditController
 *
 * @description
 * {@link https://github.com/Armedia/ACM3/tree/develop/acm-user-interface/ark-web/src/main/webapp/resources/modules/analytics-audit/controllers/analytics-audit.client.controller.js modules/analytics-audit/controllers/analytics-audit.client.controller.js}
 *
 * The Audit Analytics module main controller
 */

angular.module('analytics-audit').controller('AnalyticsAuditController', ['$scope', 'LookupService',
    'Analytics.BuildUrl'
    , function ($scope, LookupService, BuildUrl) {

        $scope.data = {};
    
        // Retrieves the properties from the acm-elk-config.properties file
        var promiseServerConfig = LookupService.getConfig("acm-elk-config");
    
        promiseServerConfig.then(function (data) {
            var elkConfig = data;
            $scope.data.elkHost = elkConfig['elk.server.url'];
            $scope.data.elkPort = elkConfig['elk.server.port'];
            $scope.data.elkUser = elkConfig['elk.server.user'];
            $scope.data.elkPassword = elkConfig['elk.server.password'];
            $scope.data.elkDashboard = elkConfig['elk.dashboard.url'];
            $scope.elkUrl = BuildUrl.getUrl($scope.data);
        });
    
    }
]);