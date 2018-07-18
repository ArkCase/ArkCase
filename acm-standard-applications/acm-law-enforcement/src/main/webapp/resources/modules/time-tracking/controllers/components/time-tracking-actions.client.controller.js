'use strict';

angular.module('time-tracking').controller('TimeTracking.ActionsController',
        [ '$scope', '$state', '$stateParams', '$translate', 'UtilService', 'ConfigService', 'TimeTracking.InfoService', 'Helper.ObjectBrowserService', function($scope, $state, $stateParams, $translate, Util, ConfigService, TimeTrackingInfoService, HelperObjectBrowserService) {

            new HelperObjectBrowserService.Component({
                scope: $scope,
                stateParams: $stateParams,
                moduleId: "time-tracking",
                componentId: "actions",
                retrieveObjectInfo: TimeTrackingInfoService.getTimesheetInfo,
                validateObjectInfo: TimeTrackingInfoService.validateTimesheet,
                onObjectInfoRetrieved: function(objectInfo) {
                    onObjectInfoRetrieved(objectInfo);
                }
            });

            var onObjectInfoRetrieved = function(objectInfo) {
                var frevvoDateFormat = $translate.instant("common.frevvo.defaultDateFormat");
                var startDate = moment(objectInfo.startDate).format(frevvoDateFormat);

                $scope.editTimesheetParams = {
                    period: startDate
                };
            };

            $scope.refresh = function() {
                $scope.$emit('report-object-refreshed', $stateParams.id);
            };

            $scope.isVisible = function() {
                return !Util.isEmpty($scope.objectInfo) && $scope.objectInfo.status === 'DRAFT';
            };

        } ]);