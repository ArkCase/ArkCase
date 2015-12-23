'use strict';

angular.module('time-tracking').controller('TimeTracking.ActionsController', ['$scope', '$state', '$translate'
    , 'UtilService', 'ConfigService', 'TimeTracking.InfoService', 'Helper.ObjectBrowserService'
    , function ($scope, $state, $translate
        , Util, ConfigService, TimeTrackingInfoService, HelperObjectBrowserService) {

        ConfigService.getComponentConfig("time-tracking", "actions").then(function (componentConfig) {
            $scope.config = componentConfig;
            return componentConfig;
        });

        $scope.$on('object-updated', function (e, data) {
            if (TimeTrackingInfoService.validateTimesheet(data)) {
                $scope.timesheetInfo = data;
            }
        });
        //var currentObjectId = HelperObjectBrowserService.getCurrentObjectId();
        //if (Util.goodPositive(currentObjectId, false)) {
        //    TimeTrackingInfoService.getTimesheetInfo(currentObjectId).then(function (timesheetInfo) {
        //        $scope.timesheetInfo = timesheetInfo;
        //        return timesheetInfo;
        //    });
        //}

        $scope.createNew = function () {
            $state.go("frevvo-new-timesheet", {
                name: "new-timesheet"
            });
            //$state.go('newTimesheet');
        };

        $scope.edit = function (timesheetInfo) {
            var frevvoDateFormat = Util.goodValue($scope.config.frevvoDateFormat, $translate.instant("common.frevvo.defaultDateFormat"));
            var starDate = moment(timesheetInfo.startDate).format(frevvoDateFormat);
            $state.go("frevvo-edit-timesheet", {
                name: "edit-timesheet",
                arg: {
                    period: starDate
                }
            });
            //$state.go('editTimesheet', { period : $scope.timesheetInfo.starDate});
        };

    }
]);