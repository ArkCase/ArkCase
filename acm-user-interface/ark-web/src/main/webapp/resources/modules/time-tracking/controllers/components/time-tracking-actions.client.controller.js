'use strict';

angular.module('time-tracking').controller('TimeTracking.ActionsController', ['$scope', '$state', 'UtilService', 'TimeTracking.InfoService',
    function ($scope, $state, Util, TimeTrackingInfoService) {
        $scope.$emit('req-component-config', 'actions');
        $scope.$on('component-config', function (e, componentId, config) {
            if ('actions' == componentId) {
                $scope.config = config;
            }
        });

        $scope.$on('timesheet-updated', function (e, data) {
            if (TimeTrackingInfoService.validateTimesheet(data)) {
                $scope.timesheetInfo = data;
            }
        });

        $scope.createNew = function () {
            $state.go("frevvo", {
                name: "new-timesheet"
            });
            //$state.go('newTimesheet');
        };

        $scope.edit = function (timesheetInfo) {
            var frevvoDateFormat = Util.goodValue($scope.config.frevvoDateFormat, "YYYY-MM-DD");
            var starDate = moment(timesheetInfo.startDate).format(frevvoDateFormat);
            $state.go("frevvo", {
                name: "edit-timesheet",
                arg: {
                    period: starDate
                }
            });
            //$state.go('editTimesheet', { period : $scope.timesheetInfo.starDate});
        };

    }
]);