'use strict';

angular.module('time-tracking').controller('TimeTracking.DetailsController', ['$scope', '$stateParams', '$translate'
    , 'UtilService', 'ConfigService', 'TimeTracking.InfoService', 'MessageService', 'Helper.ObjectBrowserService'
    , function ($scope, $stateParams, $translate
        , Util, ConfigService, TimeTrackingInfoService, MessageService, HelperObjectBrowserService) {

        new HelperObjectBrowserService.Component({
            scope: $scope
            , stateParams: $stateParams
            , moduleId: "time-tracking"
            , componentId: "details"
            , retrieveObjectInfo: TimeTrackingInfoService.getTimesheetInfo
            , validateObjectInfo: TimeTrackingInfoService.validateTimesheet
            , onObjectInfoRetrieved: function (timesheetInfo) {
                $scope.timesheetInfo = timesheetInfo;
            }
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