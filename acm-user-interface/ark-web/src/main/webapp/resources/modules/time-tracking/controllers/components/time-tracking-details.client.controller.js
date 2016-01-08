'use strict';

angular.module('time-tracking').controller('TimeTracking.DetailsController', ['$scope', '$stateParams', '$translate'
    , 'UtilService', 'ConfigService', 'TimeTracking.InfoService', 'MessageService', 'Helper.ObjectBrowserService'
    , function ($scope, $stateParams, $translate
        , Util, ConfigService, TimeTrackingInfoService, MessageService, HelperObjectBrowserService) {

        ConfigService.getComponentConfig("time-tracking", "details").then(function (componentConfig) {
            $scope.config = componentConfig;
            return componentConfig;
        });

        var currentObjectId = HelperObjectBrowserService.getCurrentObjectId();
        if (Util.goodPositive(currentObjectId, false)) {
            TimeTrackingInfoService.getTimesheetInfo(currentObjectId).then(function (timesheetInfo) {
                $scope.timesheetInfo = timesheetInfo;
                return timesheetInfo;
            });
        }

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