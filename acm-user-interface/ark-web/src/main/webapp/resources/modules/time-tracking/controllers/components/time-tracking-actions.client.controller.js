'use strict';

angular.module('time-tracking').controller('TimeTracking.ActionsController', ['$scope', '$state', '$stateParams', '$translate'
    , 'UtilService', 'ConfigService', 'TimeTracking.InfoService', 'Helper.ObjectBrowserService'
    , function ($scope, $state, $stateParams, $translate
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

        $scope.$on('object-refreshed', function (e, data) {
            if (TimeTrackingInfoService.validateTimesheet(data)) {
                $scope.timesheetInfo = data;
            }
        });

        $scope.createNew = function () {
            $state.go("frevvo", {
                name: "new-timesheet"
            });
        };

        $scope.edit = function (timesheetInfo) {
            var frevvoDateFormat = Util.goodValue($scope.config.frevvoDateFormat, $translate.instant("common.frevvo.defaultDateFormat"));
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