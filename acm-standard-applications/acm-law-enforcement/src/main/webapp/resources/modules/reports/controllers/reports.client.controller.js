'use strict';

/**
 * @ngdoc controller
 * @name reports.controller:ReportsController
 *
 * @description
 * {@link https://github.com/Armedia/ACM3/tree/develop/acm-user-interface/ark-web/src/main/webapp/resources/modules/reports/controllers/reports.client.controller.js modules/reports/controllers/reports.client.controller.js}
 *
 * The Reports module main controller
 */

angular.module('reports').controller('ReportsController', ['$scope', 'UtilService', 'Util.DateService', 'ConfigService', 'LookupService',
    'Reports.BuildUrl', '$q', 'Reports.Data'
    , function ($scope, Util, UtilDateService, ConfigService, LookupService, BuildUrl, $q, Data) {

        $scope.$on('req-component-config', function (e, componentId) {
            promiseModuleConfig.then(function (config) {
                var componentConfig = _.find(config.components, {id: componentId});
                $scope.$broadcast('component-config', componentId, componentConfig);
                return config;
            });
        });

        $scope.data = Data.getData();

        var promiseModuleConfig = ConfigService.getModuleConfig("reports");

        // Retrieves the properties from the acm-reports-server-config.properties file
        var promiseServerConfig = LookupService.getConfig("acm-reports-server-config");

        // Retrieves the properties from the acm-reports.properties file
        var promiseReportConfig = BuildUrl.getAuthorizedReports();

        $q.all([promiseServerConfig, promiseReportConfig, promiseModuleConfig])
            .then(function (data) {
                var reportsConfig = data[0];
                $scope.data.reports = data[1].data;
                $scope.config = data[2];

                // On some reason reports list contains URL and PORT info
                delete $scope.data.reports.PENTAHO_SERVER_URL;
                delete $scope.data.reports.PENTAHO_SERVER_PORT;
                $scope.data.reportsHost = reportsConfig['PENTAHO_SERVER_URL'];
                $scope.data.reportsPort = reportsConfig['PENTAHO_SERVER_PORT'];
                $scope.data.reportsUser = reportsConfig['PENTAHO_SERVER_USER'];
                $scope.data.reportsPassword = reportsConfig['PENTAHO_SERVER_PASSWORD'];
                $scope.data.reportSelected = null;
            });

        $scope.generateReport = function () {
            $scope.reportUrl = BuildUrl.getUrl($scope.data);
        };
    }
]);
