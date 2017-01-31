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
        $scope.gridOptions = $scope.gridOptions || {};
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

        var gridHelperPastApprovers = new HelperUiGridService.Grid({scope: $scope});


        ConfigService.getModuleConfig("tasks").then(function (moduleConfig) {
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
                $scope.gridOptions.data = angular.fromJson(objectInfo.buckslipPastApprovers);
                $scope.gridOptions.noData = false;
            } else {
                $scope.gridOptions.data = [];
                $scope.gridOptions.noData = true;
                $scope.noDataMessage = $translate.instant('tasks.comp.approvalRouting.noBuckslipMessage');
            }
            $scope.oldData = _.cloneDeep($scope.gridOptions.data);
        };
    }
])
;