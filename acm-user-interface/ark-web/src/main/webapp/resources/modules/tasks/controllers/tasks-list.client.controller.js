'use strict';

angular.module('tasks').controller('TasksListController', ['$scope', '$state', '$stateParams', '$translate', 'UtilService', 'ConstantService', 'CallTasksService', 'CallConfigService',
    function ($scope, $state, $stateParams, $translate, Util, Constant, CallTasksService, CallConfigService) {
        CallConfigService.getModuleConfig("tasks").then(function (config) {
            $scope.treeConfig = config.tree;
            $scope.componentsConfig = config.components;
            return config;
        });


        var findByNodeId = function (docs, nodeId) {
            var found = null;
            for (var i = 0; i < docs.length; i++) {
                if (docs[i].nodeId == nodeId) {
                    found = docs[i];
                    break;
                }
            }
            return found;
        };
        var firstLoad = true;
        $scope.onLoad = function (start, n, sort, filters) {
            if (firstLoad && $stateParams.id) {
                $scope.treeData = null;
            }

            CallTasksService.queryTasksTreeData(start, n, sort, filters).then(
                function (treeData) {
                    if (firstLoad) {
                        if ($stateParams.id) {
                            if ($scope.treeData) {            //It must be set by CallTasksService.getTaskInfo(), only 1 items in docs[] is expected
                                var a1 = treeData.docs;
                                var a2 = $scope.treeData.docs[0].nodeId;
                                var a3 = _.find(a1, {nodeId: a2});
                                var a5 = _.find(a1, {'nodeId': 9601});
                                var a4 = _.where(a1, {nodeId: a2});


                                //var found = _.find(treeData.docs, {nodeId: $scope.treeData.docs[0].nodeId});  //what am I doing wrong? It just is not working
                                var found = findByNodeId(treeData.docs, $scope.treeData.docs[0].nodeId);
                                if (!found) {
                                    var clone = _.clone(treeData.docs);
                                    clone.unshift($scope.treeData.docs[0]);
                                    treeData.docs = clone;
                                }
                                firstLoad = false;
                            }


                        } else {
                            if (0 < treeData.docs.length) {
                                var selectNode = treeData.docs[0];
                                $scope.treeControl.select({
                                    pageStart: start
                                    , nodeType: selectNode.nodeType
                                    , nodeId: selectNode.nodeId
                                });
                            }
                            firstLoad = false;
                        }
                    }

                    $scope.treeData = treeData;
                    return treeData;
                }
            );

            if (firstLoad && $stateParams.id) {
                CallTasksService.getTaskInfo($stateParams.id).then(
                    function (taskInfo) {
                        $scope.treeControl.select({
                            pageStart: start
                            , nodeType: Constant.ObjectTypes.TASK
                            , nodeId: taskInfo.taskId
                        });

                        var treeData = {docs: [], total: 0};
                        if ($scope.treeData) {            //It must be set by CallTasksService.queryTasksTreeData()
                            //var found = _.find($scope.treeData.docs, {nodeId: taskInfo.taskId});
                            var found = findByNodeId($scope.treeData.docs, taskInfo.taskId);
                            if (!found) {
                                treeData.docs = _.clone($scope.treeData.docs);
                                treeData.total = $scope.treeData.total;
                                treeData.docs.unshift({
                                    nodeId: Util.goodValue(taskInfo.taskId, 0)
                                    , nodeType: Constant.ObjectTypes.TASK
                                    , nodeTitle: Util.goodValue(taskInfo.title)
                                    , nodeToolTip: Util.goodValue(taskInfo.title)
                                });
                            }
                            firstLoad = false;

                        } else {
                            treeData.total = 1;
                            treeData.docs.unshift({
                                nodeId: Util.goodValue(taskInfo.taskId, 0)
                                , nodeType: Constant.ObjectTypes.TASK
                                , nodeTitle: Util.goodValue(taskInfo.title)
                                , nodeToolTip: Util.goodValue(taskInfo.title)
                            });
                        }

                        $scope.treeData = treeData;
                        return taskInfo;
                    }
                    , function (errorData) {
                        $scope.treeControl.select({
                            pageStart: start
                            , nodeType: Constant.ObjectTypes.TASK
                            , nodeId: $stateParams.id
                        });


                        var treeData = {docs: [], total: 0};
                        if ($scope.treeData) {            //It must be set by CallTasksService.queryTasksTreeData()
                            //var found = _.find($scope.treeData.docs, {nodeId: $stateParams.id});
                            var found = findByNodeId($scope.treeData.docs, $stateParams.id);
                            if (!found) {
                                treeData.docs = _.clone($scope.treeData.docs);
                                treeData.total = $scope.treeData.total;
                                treeData.docs.unshift({
                                    nodeId: $stateParams.id
                                    , nodeType: Constant.ObjectTypes.TASK
                                    , nodeTitle: $translate.instant("common.directive.objectTree.errorNode.title")
                                    , nodeToolTip: $translate.instant("common.directive.objectTree.errorNode.toolTip")
                                });
                            }
                            firstLoad = false;

                        } else {
                            treeData.total = 1;
                            treeData.docs.unshift({
                                nodeId: $stateParams.id
                                , nodeType: Constant.ObjectTypes.TASK
                                , nodeTitle: $translate.instant("common.directive.objectTree.errorNode.title")
                                , nodeToolTip: $translate.instant("common.directive.objectTree.errorNode.toolTip")
                            });
                        }

                        $scope.treeData = treeData;
                        return errorData;
                    }
                );
            }

        };

        $scope.onSelect = function (selectedTask) {
            $scope.$emit('req-select-task', selectedTask);
            var components = Util.goodArray(selectedTask.components);
            var componentType = (1 == components.length) ? components[0] : "main";
            $state.go('tasks.' + componentType, {
                id: selectedTask.nodeId
            });
        };
    }
]);