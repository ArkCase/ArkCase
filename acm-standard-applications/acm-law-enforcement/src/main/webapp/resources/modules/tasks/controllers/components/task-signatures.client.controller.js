'use strict';

angular.module('tasks').controller(
    'Tasks.SignaturesController',
    ['$rootScope', '$scope', '$stateParams', '$q', 'UtilService', 'ConfigService', 'ObjectService', 'Object.SignatureService', 'Task.InfoService', 'Helper.UiGridService', 'Helper.ObjectBrowserService',
        function ($rootScope, $scope, $stateParams, $q, Util, ConfigService, ObjectService, ObjectSignatureService, TaskInfoService, HelperUiGridService, HelperObjectBrowserService) {

            var componentHelper = new HelperObjectBrowserService.Component({
                moduleId: "tasks",
                componentId: "signatures",
                scope: $scope,
                stateParams: $stateParams,
                retrieveObjectInfo: TaskInfoService.getTaskInfo,
                validateObjectInfo: TaskInfoService.validateTaskInfo,
                onConfigRetrieved: function (componentConfig) {
                    return onConfigRetrieved(componentConfig);
                }
            });

            $rootScope.$on('task-signed', function () {
                $scope.retrieveGridData();
            });

            var gridHelper = new HelperUiGridService.Grid({
                scope: $scope
            });

            var onConfigRetrieved = function (config) {
                $scope.config = config;
                gridHelper.setColumnDefs(config);
                gridHelper.setBasicOptions(config);
                gridHelper.disableGridScrolling(config);
                gridHelper.showUserFullNames();

                $scope.retrieveGridData();
            };

            $scope.retrieveGridData = function () {
                if (Util.goodPositive(componentHelper.currentObjectId, false)) {
                    var promiseQueryAudit = ObjectSignatureService.findSignatures(ObjectService.ObjectTypes.TASK, componentHelper.currentObjectId);

                    $q.all([promiseQueryAudit]).then(function (data) {
                        var signatures = data[0];
                        $scope.gridOptions.data = signatures;
                        $scope.gridOptions.totalItems = signatures.length;
                    });
                }
            };
        }]);
