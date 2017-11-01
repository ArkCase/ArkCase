'use strict';

angular.module('reports').controller('ReportsController', ['$scope', '$q', '$window', 'UtilService', 'Util.DateService'
    , 'ConfigService', 'LookupService','Reports.BuildUrl', 'Reports.Data', 'Object.LookupService', 'Helper.LocaleService'
    , function ($scope, $q, $window, Util, UtilDateService
        , ConfigService, LookupService, BuildUrl, Data, ObjectLookupService, LocaleHelper
    ) {
        new LocaleHelper.Locale({scope: $scope});

        ObjectLookupService.getLookupByLookupName("reportStates").then(function (reportStates) {
            $scope.reportStates = reportStates;
            return reportStates;
        });

        $scope.showXmlReport = false;

        $scope.data = Data.getData();
        $scope.startDatePickerOpened = false;
        $scope.endDatePickerOpened = false;


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

                updateAvailableReports();
            });

        function updateAvailableReports() {
            $scope.availableReports = [];
            _.forEach($scope.data.reports, function (value, key) {
                $scope.availableReports.push({"name": key.split('_').join(' '), "id": key});
            });
        }

        $scope.reportSelectionChange = function() {
            if($scope.config.resetCaseStateValues.indexOf($scope.data.reportSelected) > -1){
                $scope.data.caseStateSelected = '';
            }
        };

        $scope.generateReport = function () {
            if ($scope.showXmlReport) {
                $window.open(BuildUrl.getUrl($scope.data, $scope.showXmlReport));
            } else {
                $scope.reportUrl = BuildUrl.getUrl($scope.data, $scope.showXmlReport);
            }
        };
    }
]);
