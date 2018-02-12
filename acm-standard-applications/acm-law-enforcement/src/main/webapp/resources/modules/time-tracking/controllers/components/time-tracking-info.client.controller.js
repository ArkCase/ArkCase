'use strict';

angular.module('time-tracking').controller(
        'TimeTracking.InfoController',
        [ '$scope', '$stateParams', 'UtilService', 'ConfigService', 'TimeTracking.InfoService', 'Helper.ObjectBrowserService',
                function($scope, $stateParams, Util, ConfigService, TimeTrackingInfoService, HelperObjectBrowserService) {

                    new HelperObjectBrowserService.Component({
                        scope : $scope,
                        stateParams : $stateParams,
                        moduleId : "time-tracking",
                        componentId : "info",
                        retrieveObjectInfo : TimeTrackingInfoService.getTimesheetInfo,
                        validateObjectInfo : TimeTrackingInfoService.validateTimesheet
                    });
                } ]);