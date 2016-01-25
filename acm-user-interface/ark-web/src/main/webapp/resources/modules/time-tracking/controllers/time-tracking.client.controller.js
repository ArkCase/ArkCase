'use strict';

angular.module('time-tracking').controller('TimeTrackingController', ['$scope', '$stateParams', '$state', '$translate'
    , 'UtilService', 'ConfigService', 'TimeTracking.InfoService', 'ObjectService', 'Helper.ObjectBrowserService'
    , function ($scope, $stateParams, $state, $translate
        , Util, ConfigService, TimeTrackingInfoService, ObjectService, HelperObjectBrowserService) {

        var contentHelper = new HelperObjectBrowserService.Content({
            scope: $scope
            , state: $state
            , stateParams: $stateParams
            , moduleId: "time-tracking"
            , resetObjectInfo: TimeTrackingInfoService.resetTimesheetInfo
            , getObjectInfo: TimeTrackingInfoService.getTimesheetInfo
            , updateObjectInfo: TimeTrackingInfoService.saveTimesheetInfo
            , initComponentLinks: function (config) {
                return HelperObjectBrowserService.createComponentLinks(config, ObjectService.ObjectTypes.TIMESHEET);
            }
        });

        $scope.$on("collapsed", function(event, collapsed) {
            $scope.linksShown = !collapsed;
        });
	}
]);