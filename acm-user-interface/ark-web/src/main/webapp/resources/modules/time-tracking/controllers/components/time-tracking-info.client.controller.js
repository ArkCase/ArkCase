'use strict';

angular.module('time-tracking').controller('TimeTracking.InfoController', ['$scope', 'UtilService', 'ConfigService'
    , 'TimeTracking.InfoService', 'Helper.ObjectBrowserService'
    , function ($scope, Util, ConfigService, TimeTrackingInfoService, HelperObjectBrowserService) {

        ConfigService.getComponentConfig("time-tracking", "info").then(function (componentConfig) {
            $scope.config = componentConfig;
            return componentConfig;
        });

        //$scope.timesheetSolr = null;
        //$scope.timesheetInfo = null;
        //$scope.$on('timesheet-selected', function onSelectedCase(e, selectedTimesheet) {
        //    $scope.timesheetSolr = selectedTimesheet;
        //});

        $scope.$on('object-updated', function (e, data) {
                $scope.timesheetInfo = data;

        });
        //var currentObjectId = HelperObjectBrowserService.getCurrentObjectId();
        //if (Util.goodPositive(currentObjectId, false)) {
        //    TimeTrackingInfoService.getTimesheetInfo(currentObjectId).then(function (timesheetInfo) {
        //        $scope.timesheetInfo = timesheetInfo;
        //        return timesheetInfo;
        //    });
        //}
    }
]);