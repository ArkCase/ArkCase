'use strict';

angular.module('tasks').controller('Tasks.DocsReviewController', ['$scope', '$q'
    , 'UtilService', 'Helper.UiGridService', 'Task.InfoService'
    , function ($scope, $q, Util, HelperUiGridService, TaskInfoService) {

        var gridHelper = new HelperUiGridService.Grid({scope: $scope});
        var promiseUsers = gridHelper.getUsers();

        $scope.$emit('req-component-config', 'docsreview');
        $scope.$on('component-config', function (e, componentId, config) {
            if ("docsreview" == componentId) {
                gridHelper.setColumnDefs(config);
                gridHelper.setBasicOptions(config);
                gridHelper.setUserNameFilter(promiseUsers);

                //$scope.gridOptions.enableFiltering = false;
            }
        });

        $scope.$on('task-updated', function (e, data) {
            if (!TaskInfoService.validateTaskInfo(data)) {
                return;
            }
            $scope.taskInfo = data;
            $q.all([promiseUsers]).then(function (data) {
                var arr = (data.documentUnderReview) ? [data.documentUnderReview] : [];
                $scope.gridOptions.data = arr;
                gridHelper.hidePagingControlsIfAllDataShown(1);
            });
        });

        $scope.onClickObjLink = function (event, rowEntity) {
            event.preventDefault();

            var targetType = Util.goodMapValue(rowEntity, "targetType");
            var targetId = Util.goodMapValue(rowEntity, "targetId");
            gridHelper.showObject(targetType, targetId);
        };

    }
]);