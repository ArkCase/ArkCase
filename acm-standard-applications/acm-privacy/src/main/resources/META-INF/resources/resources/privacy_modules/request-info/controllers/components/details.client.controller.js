'use strict';

angular.module('request-info').controller('RequestInfo.DetailsController',
        [ '$scope', '$state', '$stateParams', '$translate', 'Case.InfoService', 'Helper.ObjectBrowserService', 'ConfigService', 'UtilService', 'Util.DateService', function($scope, $state, $stateParams, $translate, CaseInfoService, HelperObjectBrowserService, ConfigService, Util, UtilDateService) {

            // DG : removing duplicated code and requests from parent request-info controller
            //        new HelperObjectBrowserService.Component({
            //            scope: $scope
            //            , stateParams: $stateParams
            //            , moduleId: "request-info"
            //            , componentId: "details"
            //            , retrieveObjectInfo: CaseInfoService.getCaseInfo
            //            , validateObjectInfo: CaseInfoService.validateCaseInfo
            //            , onObjectInfoRetrieved: function (objectInfo) {
            //                onObjectInfoRetrieved(objectInfo);
            //            }
            //        });
            //
            //        var onObjectInfoRetrieved = function (objectInfo) {
            //            $scope.requestInfo = objectInfo;
            //            $scope.requestInfo.scannedDate = UtilDateService.isoToDate(objectInfo.scannedDate);
            //            $scope.requestInfo.receivedDate = UtilDateService.isoToDate(objectInfo.receivedDate);
            //        };

            //        $scope.$bus.subscribe('required-fields-retrieved', function (fields) {
            //            $scope.requiredFields = fields;
            //        });

            //        ConfigService.getModuleConfig("request-info").then(function (moduleConfig) {
            //            var config = _.find(moduleConfig.components, {id: "requests"});
            //            $scope.requestTypes = config.requestTypes;
            //            $scope.requestSubTypes = config.requestSubTypes;
            //            $scope.dispositionTypes = config.dispositionTypes;
            //            $scope.dispositionSubTypes = config.dispositionSubTypes;
            //        });

            //        $scope.opened = {};
            //        $scope.opened.openedStart = false;
            //        $scope.opened.openedEnd = false;
            //
            //        $scope.openedScanned = {};
            //        $scope.opened.openedStart = false;
            //        $scope.opened.openedEnd = false;

            //        $scope.$watch('requestInfo', function (newValue, oldValue) {
            //            if (newValue && (newValue != oldValue)) {
            //                $scope.$emit("object-refreshed", newValue);
            //            }
            //        }, true);
        } ]);