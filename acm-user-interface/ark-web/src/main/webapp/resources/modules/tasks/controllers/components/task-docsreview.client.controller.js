'use strict';

angular.module('tasks').controller('Tasks.DocsReviewController', ['$scope', '$q', '$stateParams'
    , 'UtilService', 'ConfigService', 'Helper.UiGridService', 'Task.InfoService'
    , function ($scope, $q, $stateParams, Util, ConfigService, HelperUiGridService, TaskInfoService) {

        var gridHelper = new HelperUiGridService.Grid({scope: $scope});
        var promiseUsers = gridHelper.getUsers();

        ConfigService.getComponentConfig("tasks", "docsreview").then(function (config) {
            gridHelper.setColumnDefs(config);
            gridHelper.setBasicOptions(config);
            gridHelper.setUserNameFilter(promiseUsers);

            //$scope.gridOptions.enableFiltering = false;
            return config;
        });

        TaskInfoService.getTaskInfo($stateParams.id).then(function (taskInfo) {
            $scope.taskInfo = taskInfo;
            $q.all([promiseUsers]).then(function () {
                var arr = (taskInfo.documentUnderReview) ? [taskInfo.documentUnderReview] : [];
                $scope.gridOptions.data = arr;
                gridHelper.hidePagingControlsIfAllDataShown(1);
            });
            return taskInfo;
        });

        $scope.onClickObjLink = function (event, rowEntity) {
            event.preventDefault();

            var targetType = Util.goodMapValue(rowEntity, "targetType");
            var targetId = Util.goodMapValue(rowEntity, "targetId");
            gridHelper.showObject(targetType, targetId);
        };

    }
]);