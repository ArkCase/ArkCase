'use strict';

angular.module('time-tracking').controller('TimeTracking.DetailsController', ['$scope', '$stateParams', '$translate'
    , 'UtilService', 'ConfigService', 'TimeTracking.InfoService', 'MessageService'
    , function ($scope, $stateParams, $translate, Util, ConfigService, TimeTrackingInfoService, MessageService) {

        ConfigService.getComponentConfig("time-tracking", "details").then(function (componentConfig) {
            $scope.config = componentConfig;
            return componentConfig;
        });

        //$scope.$on('timesheet-updated', function (e, data) {
        //    $scope.timesheetInfo = data;
        //});
        TimeTrackingInfoService.getTimeTrackingInfo($stateParams.id).then(function (timesheetInfo) {
            $scope.timesheetInfo = timesheetInfo;
            return timesheetInfo;
        });

        $scope.saveDetails = function() {
            var timesheetInfo = Util.omitNg($scope.timesheetInfo);
           TimeTrackingInfoService.saveTimesheetInfo(timesheetInfo).then(
                function (timesheetInfo) {
                    MessageService.info($translate.instant("timeTracking.comp.details.informSaved"));
                    return timesheetInfo;
                }
            );
        };

    }
]);