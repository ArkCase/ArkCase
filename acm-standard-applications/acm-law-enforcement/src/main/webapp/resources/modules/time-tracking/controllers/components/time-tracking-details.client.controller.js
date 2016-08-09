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
        });

		$scope.options = {
            focus: true,
            dialogsInBody:true
            //,height: 120
        };

        $scope.saveDetails = function() {
            var timesheetInfo = Util.omitNg($scope.objectInfo);
           TimeTrackingInfoService.saveTimesheetInfo(timesheetInfo).then(
                function (timesheetInfo) {
                    MessageService.info($translate.instant("timeTracking.comp.details.informSaved"));
                    return timesheetInfo;
                }
            );
        };

    }
]);