'use strict';

angular.module('time-tracking').controller('TimeTracking.ActionsController', ['$scope', '$state', '$translate'
    , 'UtilService', 'ConfigService', 'TimeTracking.InfoService'
    , function ($scope, $state, $translate, Util, ConfigService, TimeTrackingInfoService) {

        ConfigService.getComponentConfig("time-tracking", "actions").then(function (componentConfig) {
            $scope.config = componentConfig;
            return componentConfig;
        });

        $scope.$on('timesheet-updated', function (e, data) {
            if (TimeTrackingInfoService.validateTimesheet(data)) {
                $scope.timesheetInfo = data;
            }
        });

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