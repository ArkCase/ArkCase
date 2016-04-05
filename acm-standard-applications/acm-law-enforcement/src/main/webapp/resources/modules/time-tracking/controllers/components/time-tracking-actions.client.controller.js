'use strict';

angular.module('time-tracking').controller('TimeTracking.ActionsController', ['$scope', '$state', '$stateParams', '$translate'
    , 'UtilService', 'ConfigService', 'TimeTracking.InfoService', 'Helper.ObjectBrowserService'
    , function ($scope, $state, $stateParams, $translate
        , Util, ConfigService, TimeTrackingInfoService, HelperObjectBrowserService) {

        new HelperObjectBrowserService.Component({
            scope: $scope
            , stateParams: $stateParams
            , moduleId: "time-tracking"
            , componentId: "actions"
            , retrieveObjectInfo: TimeTrackingInfoService.getTimesheetInfo
            , validateObjectInfo: TimeTrackingInfoService.validateTimesheet
        });


        $scope.createNew = function () {
            $state.go("frevvo", {
                name: "new-timesheet"
            });
        };

        $scope.edit = function (timesheetInfo) {
            var frevvoDateFormat = $translate.instant("common.frevvo.defaultDateFormat");
            var starDate = moment(timesheetInfo.startDate).format(frevvoDateFormat);
            $state.go("frevvo", {
                name: "edit-timesheet",
                arg: {
                    period: starDate
                }
            });
        };

        $scope.refresh = function () {
            $scope.$emit('report-object-refreshed', $stateParams.id);
        };

    }
]);