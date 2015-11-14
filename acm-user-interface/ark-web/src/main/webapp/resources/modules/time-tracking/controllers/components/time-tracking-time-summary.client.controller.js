'use strict';

angular.module('time-tracking').controller('TimeTracking.TimeSummaryController', ['$scope', 'UtilService', 'HelperService',
    function ($scope, Util, Helper) {
        $scope.$emit('req-component-config', 'time-summary');
        $scope.$on('component-config', function (e, componentId, config) {
            if ('time-summary' == componentId) {
                Helper.Grid.setColumnDefs($scope, config);
                Helper.Grid.setBasicOptions($scope, config);
            }
        });

        $scope.$on('timesheet-retrieved', function(e, data) {
            $scope.timesheetInfo = data;

            $scope.gridOptions = $scope.gridOptions || {};
            $scope.gridOptions.data = $scope.timesheetInfo.times;
        });

        $scope.onClickObjectType = function (event, rowEntity) {
            event.preventDefault();

            var targetType = Util.goodMapValue(rowEntity, "type");
            var targetId = Util.goodMapValue(rowEntity, "objectId");
            Helper.Grid.showObject($scope, targetType, targetId);
        };

    }
]);