'use strict';

angular.module('time-tracking').controller('TimeTracking.InfoController', ['$scope', 'UtilService', 'ConfigService'
    , 'TimeTracking.InfoService', 'Helper.ObjectBrowserService'
    , function ($scope, Util, ConfigService, TimeTrackingInfoService, HelperObjectBrowserService) {

        ConfigService.getComponentConfig("time-tracking", "info").then(function (componentConfig) {
            $scope.config = componentConfig;
            return componentConfig;
        });

        $scope.$on('object-updated', function (e, data) {
                $scope.timesheetInfo = data;

        });

        $scope.$on('object-refreshed', function (e, timesheetInfo) {
            $scope.timesheetInfo = timesheetInfo;
        });
    }
]);