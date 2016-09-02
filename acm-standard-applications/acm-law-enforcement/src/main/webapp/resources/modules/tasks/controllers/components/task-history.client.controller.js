'use strict';

angular.module('tasks').controller('Tasks.HistoryController', ['$scope', '$stateParams', '$q'
    , 'UtilService', 'ConfigService', 'Helper.UiGridService', 'ObjectService', 'Object.AuditService'
    , 'Task.InfoService', 'Helper.ObjectBrowserService', 'Acm.StoreService'
    , function ($scope, $stateParams, $q
        , Util, ConfigService, HelperUiGridService, ObjectService, ObjectAuditService, TaskInfoService
        , HelperObjectBrowserService, Store) {

        var componentHelper = new HelperObjectBrowserService.Component({
            moduleId: "tasks"
            , componentId: "history"
            , scope: $scope
            , stateParams: $stateParams
            , retrieveObjectInfo: TaskInfoService.getTaskInfo
            , validateObjectInfo: TaskInfoService.validateTaskInfo
            , onConfigRetrieved: function (componentConfig) {
                return onConfigRetrieved(componentConfig);
            }
        });

        var gridHelper = new HelperUiGridService.Grid({scope: $scope});
        var promiseUsers = gridHelper.getUsers();

        var onConfigRetrieved = function (config) {
            $scope.config = config;
            gridHelper.setColumnDefs(config);
            gridHelper.setBasicOptions(config);
            gridHelper.disableGridScrolling(config);
            gridHelper.setExternalPaging(config, retrieveGridData);
            gridHelper.setUserNameFilter(promiseUsers);
            retrieveGridData();
        };

        function retrieveGridData () {
          gridHelper.retrieveAuditData(ObjectService.ObjectTypes.TASK, $stateParams.id);
        }
    }
]);