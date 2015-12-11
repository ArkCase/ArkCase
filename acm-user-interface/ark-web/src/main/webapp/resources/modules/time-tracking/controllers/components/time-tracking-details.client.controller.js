'use strict';

angular.module('time-tracking').controller('TimeTracking.DetailsController', ['$scope', '$stateParams', '$translate', 'UtilService', 'TimeTracking.InfoService', 'MessageService',
    function ($scope, $stateParams, $translate, Util, TimeTrackingInfoService, MessageService) {
        $scope.$emit('req-component-config', 'details');
        $scope.$on('component-config', function (e, componentId, config) {
            if ('details' == componentId) {
                $scope.config = config;
            }
        });

        $scope.$on('timesheet-updated', function (e, data) {
            $scope.timesheetInfo = data;
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