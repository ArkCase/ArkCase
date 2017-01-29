'use strict';

angular.module('cases').controller('Tasks.PastApprovalRoutingController', ['$scope', '$stateParams', '$q', '$translate', '$modal'
    , 'UtilService', 'Util.DateService', 'ConfigService', 'ObjectService', 'LookupService', 'Object.LookupService'
    , 'Task.InfoService', 'Helper.UiGridService', 'Helper.ObjectBrowserService', 'Authentication'
    , 'PermissionsService', 'Profile.UserInfoService'
    , function ($scope, $stateParams, $q, $translate, $modal
        , Util, UtilDateService, ConfigService, ObjectService, LookupService, ObjectLookupService
        , TaskInfoService, HelperUiGridService, HelperObjectBrowserService, Authentication
        , PermissionsService, UserInfoService) {

        $scope.pastApprovers = {};
        $scope.pastApprovers.GridOptions = $scope.pastApprovers.GridOptions || {};
        $scope.taskInfo = null;

        var currentUser = '';

        new HelperObjectBrowserService.Component({
            scope: $scope
            , stateParams: $stateParams
            , moduleId: "tasks"
            , componentId: "pastapprovals"
            , retrieveObjectInfo: TaskInfoService.getTaskInfo
            , validateObjectInfo: TaskInfoService.validateTaskInfo
            , onConfigRetrieved: function (componentConfig) {
                return onConfigRetrieved(componentConfig);
            }
            , onObjectInfoRetrieved: function (objectInfo) {
                onObjectInfoRetrieved(objectInfo);
            }
        });

        var gridHelperPastApprovers = new HelperUiGridService.Grid({scope: $scope.pastApprovers});


        ConfigService.getModuleConfig("tasks").then(function (moduleConfig) {
            $scope.config = _.find(moduleConfig.components, {id: "approvalrouting"});

            $scope.userSearchConfig = _.find(moduleConfig.components, {id: "userSearch"});
            return moduleConfig;
        });

        Authentication.queryUserInfo().then(function (data) {
            currentUser = data.userId;
        });

        var onConfigRetrieved = function (config) {
            $scope.config = config;
            gridHelperPastApprovers.setColumnDefs(config);
            gridHelperPastApprovers.setBasicOptions(config);
            gridHelperPastApprovers.disableGridScrolling(config);
        };


        var onObjectInfoRetrieved = function (objectInfo) {
            $scope.taskInfo = objectInfo;

            //set past approvers info
            if (objectInfo.buckslipPastApprovers) {
                $scope.pastApprovers.GridOptions.data = objectInfo.buckslipPastApprovers;
                $scope.pastApprovers.GridOptions.noData = false;
            } else {
                $scope.pastApprovers.GridOptions.data = [];
                $scope.pastApprovers.GridOptions.noData = true;
                $scope.noDataMessage = $translate.instant('tasks.comp.approvalRouting.noBuckslipMessage');
            }
            $scope.oldData = _.cloneDeep($scope.gridOptions.data);
        };
    }
])
;