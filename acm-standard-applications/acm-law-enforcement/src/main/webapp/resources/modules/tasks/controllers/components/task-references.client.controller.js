'use strict';

angular.module('tasks').controller('Tasks.ReferencesController', ['$scope', '$stateParams'
    , 'UtilService', 'ConfigService', 'Task.InfoService', 'Helper.UiGridService', 'Helper.ObjectBrowserService', '$modal', 'Object.ReferenceService', 'ObjectService'
    , function ($scope, $stateParams
        , Util, ConfigService, TaskInfoService, HelperUiGridService, HelperObjectBrowserService, $modal, referenceService, ObjectService) {

        new HelperObjectBrowserService.Component({
            scope: $scope
            , stateParams: $stateParams
            , moduleId: "tasks"
            , componentId: "references"
            , retrieveObjectInfo: TaskInfoService.getTaskInfo
            , validateObjectInfo: TaskInfoService.validateTaskInfo
            , onConfigRetrieved: function (componentConfig) {
                return onConfigRetrieved(componentConfig);
            }
            , onObjectInfoRetrieved: function (objectInfo) {
                onObjectInfoRetrieved(objectInfo);
            }
        });

        var gridHelper = new HelperUiGridService.Grid({scope: $scope});

        var onConfigRetrieved = function (config) {
            $scope.config = config;

            gridHelper.setColumnDefs(config);
            gridHelper.setBasicOptions(config);
            gridHelper.disableGridScrolling(config);
        };

        var onObjectInfoRetrieved = function (objectInfo) {
            $scope.objectInfo = objectInfo;
            $scope.gridOptions = $scope.gridOptions || {};
            $scope.gridOptions.data = Util.goodArray($scope.objectInfo.references);
        };

        $scope.onClickObjLink = function (event, rowEntity) {
            event.preventDefault();

            var targetType = Util.goodMapValue(rowEntity, "targetType");
            var targetId = Util.goodMapValue(rowEntity, "targetId");
            gridHelper.showObject(targetType, targetId);

            if (ObjectService.ObjectTypes.TASK == targetType || ObjectService.ObjectTypes.ADHOC_TASK == targetType) {
                $scope.$emit('request-show-object', targetId, targetType);
            }
        };

        ConfigService.getModuleConfig("tasks").then(function (moduleConfig) {
        	$scope.modalConfig = _.find(moduleConfig.components, {id: "referenceSearchGrid"});
            return moduleConfig;
        });

        $scope.refresh = function () {
            $scope.$emit('report-object-refreshed', $stateParams.id);
        };

        // open addreference modal
        $scope.addReference = function () {
            var modalInstance = $modal.open({
                animation: $scope.animationsEnabled,
                templateUrl: 'modules/tasks/views/components/task-reference-modal.client.view.html',
                controller: 'Tasks.ReferenceModalController',
                size: 'lg',
                resolve: {
                    $filter: function () {
                    	var filter = $scope.modalConfig.searchFilter + "&-id:" + $scope.currentObjectId + "-TASK";
                        if ($scope.gridOptions.data.length > 0) {
                            for (var i = 0; i < $scope.gridOptions.data.length; i++) {
                                var data = $scope.gridOptions.data[i];
                                filter += "&-id:" + data.targetId + "-" + data.targetType;
                            }
                        }
                        return filter.replace(/&/gi, '%26');
                    },
                    $config: function () {
                        return $scope.modalConfig;
                    }
                }
            });

            modalInstance.result.then(function (chosenReference) {
                if (chosenReference) {
                    var reference = {};
                    reference.referenceId = chosenReference.object_id_s;
                    reference.referenceTitle = chosenReference.title_parseable;
                    reference.referenceType = chosenReference.object_type_s;
                    reference.referenceNumber = chosenReference.name;
                    reference.referenceStatus = chosenReference.status_lcs;
                    reference.parentId = $stateParams.id;
                    reference.parentType = ObjectService.ObjectTypes.TASK;
                    referenceService.addReference(reference).then(
                        function (objectSaved) {
                        	$scope.refresh();
                            return objectSaved;
                        },
                        function (error) {
                        	return error;
                        }
                    );
                    return;
                }
            }, function () {
                // Cancel button was clicked.
                return [];
            });

        };

    }
]);