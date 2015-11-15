'use strict';

angular.module('tasks').controller('Tasks.DocsReviewController', ['$scope', '$q', 'UtilService', 'HelperService',
    function ($scope, $q, Util, Helper) {
        $scope.$emit('req-component-config', 'docsreview');
        $scope.$on('component-config', function (e, componentId, config) {
            if ("docsreview" == componentId) {
                Helper.Grid.setColumnDefs($scope, config);
                Helper.Grid.setBasicOptions($scope, config);
                Helper.Grid.setUserNameFilter($scope, promiseUsers);

                //$scope.gridOptions.enableFiltering = false;
            }
        });

        var promiseUsers = Helper.Grid.getUsers($scope);

        $scope.$on('task-updated', function (e, data) {
            $scope.taskInfo = data;
            $q.all([promiseUsers]).then(function (data) {
                var arr = (data.documentUnderReview) ? [data.documentUnderReview] : [];
                $scope.gridOptions.data = arr;
                Helper.Grid.hidePagingControlsIfAllDataShown($scope, 1);
            });
        });

        $scope.onClickObjLink = function (event, rowEntity) {
            event.preventDefault();

            var targetType = Util.goodMapValue(rowEntity, "targetType");
            var targetId = Util.goodMapValue(rowEntity, "targetId");
            Helper.Grid.showObject($scope, targetType, targetId);
        };

    }
]);