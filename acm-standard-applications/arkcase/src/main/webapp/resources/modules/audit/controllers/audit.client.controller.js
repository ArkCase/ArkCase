'use strict';

//Comments are welcome. But do not use @ngdoc format in controllers.

angular.module('audit').controller(
        'AuditController',
    ['$scope', '$sce', '$q', '$modal', 'ConfigService', 'LookupService', 'AuditController.BuildUrl', 'UtilService', 'Util.DateService', '$window', 'Helper.LocaleService', 'Object.LookupService',
        function ($scope, $sce, $q, $modal, ConfigService, LookupService, BuildUrl, Util, UtilDateService, $window, LocaleHelper, ObjectLookupService) {
                    new LocaleHelper.Locale({
                        scope: $scope
                    });

                    var promiseModuleConfig = ConfigService.getModuleConfig("audit").then(function(config) {
                        $scope.config = config;
                        return config;
                    });

                    $scope.showXmlReport = false;

                    $scope.$on('send-type-id', getObjectValues);
                    $scope.$on('send-date', getDateValues);

                    $scope.objectType = null;
                    $scope.objectId = null;
                    $scope.dateFrom = new Date();
                    $scope.dateTo = new Date();
                    $scope.isDateValid = false;
                    $scope.startDate = null;
                    $scope.dueDate = null;

                    /**
                     * This function is callback function which gets called when "send-type-id" event is emitted.
                     * In this function values are being assigned for $scope.objectType and $scope.objectId from selected dropdown and input text
                     *
                     * @param {Object} e This is event object which have several useful properties and functions
                     * @param {String} selectedObjectType String that represents value that is selected from dropdown
                     * @param {String} inputObjectId String that represents value from text input(default is empty string "")
                     */
                    function getObjectValues(e, selectedObjectType, inputObjectId) {
                        $scope.objectType = selectedObjectType;
                        $scope.objectId = Util.goodValue(inputObjectId);
                    }

                    /**
                     * This function is callback function which gets called when "send-date" event is emitted.
                     * In this function values are being assigned to $scope.dateFrom and $scope.dateTo from selected datepickers
                     * as string. Also if value for dateFrom is bigger than value for dateTo event is emitted.
                     *
                     * @param {Object} e This is event object which have several useful properties and functions
                     * @param {Object} dateFrom Object of type date that represents value for date chosen from dateFrom input
                     * @param {Object} dateTo Object of type date that represents value for date chosen from dateTo input
                     */
                    function getDateValues(e, dateFrom, dateTo) {
                        $scope.dateFrom = dateFrom;
                        $scope.dateTo = dateTo;

                    }

                    // Retrieves the properties from the acm-reports-server-config.properties file
                    var promiseServerConfig = LookupService.getConfig("acm-reports-server-config");

                    // Retrieves the properties from the auditPlugin.properties file
                    var promiseAuditConfig = LookupService.getConfig("audit");
                    var promiseLookupAuditDropdown = ObjectLookupService.getAuditReportNames();

                    $q.all([ promiseServerConfig, promiseAuditConfig, promiseLookupAuditDropdown ]).then(function(data) {
                        $scope.acmReportsProperties = data[0];
                        $scope.auditPluginProperties = data[1];

                        $scope.pentahoHost = $scope.acmReportsProperties['report.plugin.PENTAHO_SERVER_URL'];
                        $scope.pentahoPort = $scope.acmReportsProperties['report.plugin.PENTAHO_SERVER_PORT'];
                        $scope.auditReportUri = $scope.auditPluginProperties['audit.plugin.AUDIT_REPORT'];
                        $scope.pentahoUser = $scope.acmReportsProperties['report.plugin.PENTAHO_SERVER_USER'];
                        $scope.pentahoPassword = $scope.acmReportsProperties['report.plugin.PENTAHO_SERVER_PASSWORD'];
                        $scope.auditDropdown = data[2];
                    });

                    $scope.showIframe = function() {

                        var modalInstance = $modal.open({
                            animation: true,
                            templateUrl: 'modules/common/views/object.modal.loading-spinner.html',
                            size: 'sm',
                            backdrop: 'static'
                        });
                        $scope.modalInstance = modalInstance;

                        $scope.iframeLoadedCallBack = function () {
                            $scope.modalInstance.close();
                        };

                        var reportUri = $scope.auditReportUri;
                        if ($scope.showXmlReport) {
                            reportUri = reportUri.substring(0, reportUri.indexOf('viewer')) + 'report';
                            $window.open(BuildUrl.getUrl($scope.pentahoHost, $scope.pentahoPort, reportUri, $scope.dateFrom, $scope.dateTo, $scope.objectType, $scope.objectId, true, $scope.pentahoUser, $scope.pentahoPassword, $scope.showXmlReport));
                        } else {
                            $scope.auditReportUrl = BuildUrl.getUrl($scope.pentahoHost, $scope.pentahoPort, $scope.auditReportUri, $scope.dateFrom, $scope.dateTo, $scope.objectType, $scope.objectId, true, $scope.pentahoUser, $scope.pentahoPassword, $scope.showXmlReport);
                        }
                    }
        }]);