'use strict';

angular.module('tasks').controller('Tasks.DocsReviewController', ['$scope', '$window', 'UtilService', 'ValidationService', 'HelperService', 'LookupService',
    function ($scope, $window, Util, Validator, Helper, LookupService) {
        return;
        $scope.$emit('req-component-config', 'docsreview');
        $scope.$on('component-config', function (e, componentId, config) {
            if ("docsreview" == componentId) {
                Helper.Grid.setColumnDefs($scope, config);
                Helper.Grid.setBasicOptions($scope, config);
            }
        });

        $scope.$on('task-retrieved', function (e, data) {
            if (Validator.validateTask(data)) {
                $scope.taskInfo = Util.goodValue(data, {references: []});
                $scope.gridOptions.data = $scope.taskInfo.references;
                Helper.Grid.hidePagingControlsIfAllDataShown($scope, $scope.taskInfo.references.length);
            }
        });

        $scope.onClickObjLink = function (event, rowEntity) {
            event.preventDefault();

            var targetType = Util.goodMapValue(rowEntity, "targetType");
            var targetId = Util.goodMapValue(rowEntity, "targetId");
            Helper.Grid.showObject($scope, targetType, targetId);
        };

    }
]);