'use strict';

//Please do not document controller as ngdoc format

///**
// * @ngdoc controller
// * @name audit.controller:AuditController
// *
// * @description
// * {@link https://github.com/Armedia/ACM3/tree/develop/acm-user-interface/ark-web/src/main/webapp/resources/modules/audit/controllers/audit.client.controller.js modules/audit/controllers/audit.client.controller.js}
// *
// * The Audit module main controller
// */
angular.module('audit').controller('AuditController', ['$scope', '$sce', '$q', 'ConfigService', 'LookupService',
    'AuditController.BuildUrl', 'UtilService', 'Util.DateService', '$window'
    , function ($scope, $sce, $q, ConfigService, LookupService, BuildUrl, Util, UtilDateService, $window) {
        var promiseModuleConfig = ConfigService.getModuleConfig("audit").then(function (config) {
            $scope.config = config;
            return config;
        });

        $scope.showXmlReport = false;
        
        $scope.$on('req-component-config', function (e, componentId) {
            promiseModuleConfig.then(function (config) {
                var componentConfig = _.find(config.components, {id: componentId});
                $scope.$broadcast('component-config', componentId, componentConfig);
                return config;
            });
        });

        $scope.$on('send-type-id', getObjectValues);
        $scope.$on('send-date', getDateValues);

        $scope.objectType = null;
        $scope.objectId = null;
        $scope.dateFrom = null;
        $scope.dateTo = null;

        //$scope.showIframe = showIframe;


        /**
         * @ngdoc method
         * @name getObjectValues
         * @methodOf audit.controller:AuditController
         *
         * @description
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
         * @ngdoc method
         * @name getDateValues
         * @methodOf audit.controller:AuditController
         *
         * @description
         * This function is callback function which gets called when "send-date" event is emitted.
         * In this function values are being assigned to $scope.dateFrom and $scope.dateTo from selected datepickers
         * as string. Also if value for dateFrom is bigger than value for dateTo event is emitted.
         *
         * @param {Object} e This is event object which have several useful properties and functions
         * @param {Object} dateFrom Object of type date that represents value for date chosen from dateFrom input
         * @param {Object} dateTo Object of type date that represents value for date chosen from dateTo input
         */
        function getDateValues(e, dateFrom, dateTo) {
            $scope.dateFrom = UtilDateService.goodIsoDate(dateFrom);
            $scope.dateTo = UtilDateService.goodIsoDate(dateTo);

            if (moment($scope.dateFrom).isAfter($scope.dateTo)) {
                $scope.$broadcast('fix-date-values', $scope.dateFrom, $scope.dateFrom);
            }
        }

        // Retrieves the properties from the acm-reports-server-config.properties file
        var promiseServerConfig = LookupService.getConfig("acm-reports-server-config");

        // Retrieves the properties from the auditPlugin.properties file
        var promiseAuditConfig = LookupService.getConfig("audit");

        $q.all([promiseServerConfig, promiseAuditConfig])
            .then(function (data) {
                $scope.acmReportsProperties = data[0];
                $scope.auditPluginProperties = data[1];

                $scope.pentahoHost = $scope.acmReportsProperties['PENTAHO_SERVER_URL'];
                $scope.pentahoPort = $scope.acmReportsProperties['PENTAHO_SERVER_PORT'];
                $scope.auditReportUri = $scope.auditPluginProperties['AUDIT_REPORT'];
                $scope.pentahoUser = $scope.acmReportsProperties['PENTAHO_SERVER_USER'];
                $scope.pentahoPassword = $scope.acmReportsProperties['PENTAHO_SERVER_PASSWORD'];
            });

        /**
         * @ngdoc method
         * @name showIframe
         * @methodOf audit.controller:AuditController
         *
         * @description
         * This function is called when Generate Audit Report button is clicked.
         * In $scope.auditReportUrl is setting builder url from BuildUrl service.
         *
         */
        $scope.showIframe = function () {
            var reportUri = $scope.auditReportUri;
            if ($scope.showXmlReport) {
                reportUri = reportUri.substring(0, reportUri.indexOf('viewer')) + 'report';
                $window.open(BuildUrl.getUrl($scope.pentahoHost, $scope.pentahoPort, reportUri,
                    $scope.dateFrom, $scope.dateTo, $scope.objectType, $scope.objectId, UtilDateService.defaultDateFormat,
                    true, $scope.pentahoUser, $scope.pentahoPassword, $scope.showXmlReport));
            } else {
                $scope.auditReportUrl = BuildUrl.getUrl($scope.pentahoHost, $scope.pentahoPort, $scope.auditReportUri,
                    $scope.dateFrom, $scope.dateTo, $scope.objectType, $scope.objectId, UtilDateService.defaultDateFormat,
                    true, $scope.pentahoUser, $scope.pentahoPassword, $scope.showXmlReport);
            }
        }
    }
]);