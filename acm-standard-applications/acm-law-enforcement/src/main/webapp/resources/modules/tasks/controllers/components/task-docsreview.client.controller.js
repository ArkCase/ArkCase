'use strict';

angular.module('tasks').controller('Tasks.DocsReviewController', ['$scope', '$q', '$stateParams'
    , 'UtilService', 'ConfigService', 'Helper.UiGridService', 'Task.InfoService', 'Helper.ObjectBrowserService', 'ObjectService'
    , function ($scope, $q, $stateParams, Util, ConfigService, HelperUiGridService, TaskInfoService, HelperObjectBrowserService, ObjectService) {

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
                return onConfigRetrieved(componentConfig);
            }
        });

        var gridHelper = new HelperUiGridService.Grid({scope: $scope});
        

        var onConfigRetrieved = function (config) {
            $scope.config = config;
            gridHelper.setColumnDefs(config);
            gridHelper.setBasicOptions(config);
            gridHelper.disableGridScrolling(config);
            gridHelper.showUserFullNames();
            //$scope.gridOptions.enableFiltering = false;
        };

        //if (Util.goodPositive(componentHelper.currentObjectId, false)) {
        //    TaskInfoService.getTaskInfo(componentHelper.currentObjectId).then(function (taskInfo) {
        //        $scope.objectInfo = taskInfo;
        //        $q.all([promiseUsers]).then(function () {
        //            var arr = (taskInfo.documentUnderReview) ? [taskInfo.documentUnderReview] : [];
        //            $scope.gridOptions.data = arr;
        //        });
        //        return taskInfo;
        //    });
        //}
        var onObjectInfoRetrieved = function (objectInfo) {
            $scope.objectInfo = objectInfo;
            $q.all().then(function () {
                var urv = Util.goodMapValue($scope.objectInfo, "documentUnderReview", null);
                $scope.gridOptions.data = (urv)? [urv] : [];
            });
        };

        $scope.onClickObjLink = function (event, rowEntity) {
            event.preventDefault();
            var targetType = Util.goodMapValue(rowEntity, "container.containerObjectType");
            var targetId = Util.goodMapValue(rowEntity, "container.containerObjectId");
            gridHelper.showObject(targetType, targetId);
        };

        $scope.onDownloadFile = function (event, rowEntity) {
            event.preventDefault();
            var targetType = ObjectService.ObjectTypes.FILE;
            var targetId = Util.goodMapValue(rowEntity, "fileId");
            gridHelper.showObject(targetType, targetId);
        }

    }
]);