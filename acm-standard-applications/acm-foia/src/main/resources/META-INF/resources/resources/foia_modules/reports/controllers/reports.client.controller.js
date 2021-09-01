'use strict';

angular.module('reports').controller('ReportsController',
    ['$scope', '$q', '$window', '$modal', 'UtilService', 'Util.DateService', 'ConfigService', 'LookupService', 'Reports.BuildUrl', 'Reports.Data', 'Helper.LocaleService', 'Admin.ReportsConfigService', 'FileSaver', 'Admin.FoiaConfigService',
        function ($scope, $q, $window, $modal, Util, UtilDateService, ConfigService, LookupService, BuildUrl, Data, LocaleHelper, ReportsConfigService, FileSaver, AdminFoiaConfigService) {

            new LocaleHelper.Locale({
                scope: $scope
            });

            $scope.showReportParameters = false;

            $scope.showXmlReport = false;

            $scope.showGenerateNiemXmlButton = false;

            $scope.data = angular.copy(Data.getData());

            $scope.data.fiscalYears = [];

            var promiseModuleConfig = ConfigService.getModuleConfig("reports");

            // Retrieves the properties from the acm-reports-server-config.properties file
            var promiseServerConfig = LookupService.getConfig("acm-reports-server-config");

            // Retrieves the properties from the acm-reports.properties file
            var promiseReportConfig = BuildUrl.getAuthorizedReports();

            // Retrieves the properties from the fiscalYear.properties file
            var fiscalYearPropertiesPromise = LookupService.getConfig("fiscalYear");

            var adminFoiaConfigPromise = AdminFoiaConfigService.getFoiaConfig();

            function fillFiscalYears(fiscalYearProperties) {
                var now = moment();
                for (var year = fiscalYearProperties['fiscal.year.start.year']; year <= now.year(); year++) {
                    var fiscalYearStartDate = moment([year, fiscalYearProperties['fiscal.year.start.month'] - 1, fiscalYearProperties['fiscal.year.start.day'], 0, 0, 0, 0]);
                    if (fiscalYearStartDate.isBefore(now)) {
                        var fiscalYearEndDate = fiscalYearStartDate.clone().add(1, 'year').subtract(1, 'milliseconds');
                        if (!fiscalYearEndDate.isBefore(now)) {
                            fiscalYearEndDate = moment();
                        }
                        $scope.data.fiscalYears.push({
                            "id": "FY" + (fiscalYearStartDate.year() + 1),
                            "name": "FY" + (fiscalYearStartDate.year() + 1),
                            "startDate": fiscalYearStartDate.toDate(),
                            "endDate": fiscalYearEndDate.toDate()
                        });
                    }
                }
                $scope.data.fiscalYear = $scope.data.fiscalYears[$scope.data.fiscalYears.length - 1].id;
            }

            $q.all([promiseServerConfig, promiseReportConfig, promiseModuleConfig, fiscalYearPropertiesPromise, adminFoiaConfigPromise]).then(function (data) {
                var reportsConfig = data[0];
                $scope.data.reports = data[1].data;
                $scope.config = data[2];
                fillFiscalYears(data[3]);
                $scope.dojYearlyReports = data[4].data.dojYearlyReports;

                // On some reason reports list contains URL and PORT info
                delete $scope.data.reports["report.plugin.PENTAHO_SERVER_URL"];
                delete $scope.data.reports["report.plugin.PENTAHO_SERVER_PORT"];
                $scope.data.reportsHost = reportsConfig['report.plugin.PENTAHO_SERVER_URL'];
                $scope.data.reportsPort = reportsConfig['report.plugin.PENTAHO_SERVER_PORT'];
                $scope.data.reportsUser = reportsConfig['report.plugin.PENTAHO_SERVER_USER'];
                $scope.data.reportsPassword = reportsConfig['report.plugin.PENTAHO_SERVER_PASSWORD'];
                $scope.data.reportSelected = null;
                $scope.data.dateSearchType = null;

                $scope.data.outputType = null

                $scope.showReportParameters = false;
            });

            $scope.generateNiemXmlReport = function () {

                var reportSectionName = $scope.niemXMLReportSection;

                ReportsConfigService.exportReportToNIEMXml(reportSectionName).then(function (result) {
                    var data = new Blob([result.data], {
                        type: 'application/octet-stream'
                    });
                    FileSaver.saveAs(data, 'DOJ-report-' + reportSectionName + '.xml');
                });

            };


            $scope.generateReport = function () {

                var modalInstance = $modal.open({
                    animation: true,
                    templateUrl: 'modules/common/views/object.modal.loading-spinner.html',
                    size: 'sm',
                    backdrop: 'static'
                });
                $scope.modalInstance = modalInstance;

                $scope.showGenerateNiemXmlButton = false;

                $scope.iframeLoadedCallBack = function () {
                    $scope.modalInstance.close();
                    var niemXMLReportSection = _.invert($scope.dojYearlyReports)[$scope.data.reportSelected];
                    if (niemXMLReportSection) {
                        $scope.showGenerateNiemXmlButton = true;
                        $scope.niemXMLReportSection = niemXMLReportSection;
                    }

                };

                if ($scope.data.dateSearchType == 'FISCAL_YEAR') {
                    var fiscalYear = _.find($scope.data.fiscalYears, {
                        "id": $scope.data.fiscalYear
                    });
                    $scope.data.startDate = fiscalYear.startDate;
                    $scope.data.endDate = fiscalYear.endDate;
                }
                if ($scope.showXmlReport) {
                    $window.open(BuildUrl.getUrl($scope.data, $scope.showXmlReport));
                } else {
                    $scope.reportUrl = BuildUrl.getUrl($scope.data, $scope.showXmlReport);
                }
            };

            $scope.$watchCollection('data.reportSelected', onReportSelected);

            function onReportSelected() {
                if (!$scope.data.reports || !$scope.data.reportSelected) {
                    $scope.showReportParameters = false;
                    return;
                }

                // Retrieve outputType
                LookupService.getConfig("reportsParameters").then(function(payload) {
                    $scope.data.outputType = payload['outputType'];
                });

                var reportUrl = $scope.data.reports[$scope.data.reportSelected];
                // show report parameters only on prpt reports
                if (reportUrl.indexOf('prpt/viewer', reportUrl.length - 'prpt/viewer'.length) !== -1) {
                    $scope.showReportParameters = true;
                } else {
                    $scope.showReportParameters = false;
                }
            };

        }
    ]);
