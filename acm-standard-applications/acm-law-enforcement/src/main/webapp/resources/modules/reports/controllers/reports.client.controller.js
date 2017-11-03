'use strict';

angular.module('reports').controller('ReportsController', ['$scope', 'UtilService', 'Util.DateService', 'ConfigService', 'LookupService',
    'Reports.BuildUrl', '$q', 'Reports.Data', '$window'
    , function ($scope, Util, UtilDateService, ConfigService, LookupService, BuildUrl, $q, Data, $window) {

        $scope.showXmlReport = false;
        
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
            if ($scope.showXmlReport) {
                $window.open(BuildUrl.getUrl($scope.data, $scope.showXmlReport));
            } else {
                $scope.reportUrl = BuildUrl.getUrl($scope.data, $scope.showXmlReport);
            }
        };
    }
]);
