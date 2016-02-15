'use strict';

angular.module('tasks').controller('Tasks.DocsReviewController', ['$scope', '$q', '$stateParams'
    , 'UtilService', 'ConfigService', 'Helper.UiGridService', 'Task.InfoService', 'Helper.ObjectBrowserService'
    , function ($scope, $q, $stateParams, Util, ConfigService, HelperUiGridService, TaskInfoService, HelperObjectBrowserService) {

        var componentHelper = new HelperObjectBrowserService.Component({
            moduleId: "tasks"
            , componentId: "docsreview"
            , scope: $scope
            , stateParams: $stateParams
            , retrieveObjectInfo: TaskInfoService.getTaskInfo
            , validateObjectInfo: TaskInfoService.validateTaskInfo
            , onObjectInfoRetrieved: function (objectInfo) {
                onObjectInfoRetrieved(objectInfo);
            }
            , onConfigRetrieved: function (componentConfig) {
                onConfigRetrieved(componentConfig);
            }
        });

        var gridHelper = new HelperUiGridService.Grid({scope: $scope});
        var promiseUsers = gridHelper.getUsers();

        var onConfigRetrieved = function (config) {
            $scope.config = config;
            gridHelper.setColumnDefs(config);
            gridHelper.setBasicOptions(config);
            gridHelper.disableGridScrolling(config);
            gridHelper.setUserNameFilter(promiseUsers);
            //$scope.gridOptions.enableFiltering = false;
        };

        //if (Util.goodPositive(componentHelper.currentObjectId, false)) {
        //    TaskInfoService.getTaskInfo(componentHelper.currentObjectId).then(function (taskInfo) {
        //        $scope.objectInfo = taskInfo;
        //        $q.all([promiseUsers]).then(function () {
        //            var arr = (taskInfo.documentUnderReview) ? [taskInfo.documentUnderReview] : [];
        //            $scope.gridOptions.data = arr;
        //            //gridHelper.hidePagingControlsIfAllDataShown(1);
        //        });
        //        return taskInfo;
        //    });
        //}
        var onObjectInfoRetrieved = function (objectInfo) {
            $scope.objectInfo = objectInfo;
            $q.all([promiseUsers]).then(function () {
                var urv = Util.goodMapValue($scope.objectInfo, "documentUnderReview", null);
                $scope.gridOptions.data = (urv)? [urv] : [];
                //gridHelper.hidePagingControlsIfAllDataShown(1);
            });
        };

        $scope.onClickObjLink = function (event, rowEntity) {
            event.preventDefault();

            var targetType = Util.goodMapValue(rowEntity, "targetType");
            var targetId = Util.goodMapValue(rowEntity, "targetId");
            gridHelper.showObject(targetType, targetId);
        };

    }
]);