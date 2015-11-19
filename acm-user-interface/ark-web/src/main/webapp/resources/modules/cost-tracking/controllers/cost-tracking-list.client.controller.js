'use strict';

angular.module('cost-tracking').controller('CostTrackingListController',['$scope', '$state', '$stateParams', '$q', '$translate', 'UtilService', 'ConstantService', 'CallCostTrackingService', 'ConfigService', 'Authentication', 'Helper.ObjectTreeService',
    function($scope, $state, $stateParams, $q, $translate, Util, Constant, CallCostTrackingService, ConfigService, Authentication, HelperObjectTreeService){
        ConfigService.getModuleConfig("cost-tracking").then(function (config) {
            $scope.treeConfig = config.tree;
            $scope.componentsConfig = config.components;
            return config;
        });

        var treeHelper = new HelperObjectTreeService.Tree({
            scope: $scope
            , nodeId: $stateParams.id
            , getTreeData: function (start, n, sort, filters) {
                var dfd = $q.defer();
                Authentication.queryUserInfoNew().then(
                    function (userInfo) {
                        var userId = userInfo.userId;
                        CallCostTrackingService.queryCostTrackingTreeData(userId, start, n, sort, filters).then(
                            function (treeData) {
                                dfd.resolve(treeData);
                                return treeData;
                            }
                            , function (error) {
                                dfd.reject(error);
                                return error;
                            }
                        );
                        return userInfo;
                    }
                    , function (error) {
                        dfd.reject(error);
                        return error;
                    }
                );
                return dfd.promise;
            }
            , getNodeData: function (costsheetId) {
                return CallCostTrackingService.getCostTrackingInfo(costsheetId);
            }
            , makeTreeNode: function (costsheetId) {
                return {
                    nodeId: Util.goodValue(costsheetId.id, 0)
                    , nodeType: Constant.ObjectTypes.COSTSHEET
                    , nodeTitle: Util.goodValue(costsheetId.title)
                    , nodeToolTip: Util.goodValue(costsheetId.title)
                };
            }
        });
        $scope.onLoad = function (start, n, sort, filters) {
            treeHelper.onLoad(start, n, sort, filters);
        };


        // var firstLoad = true;
        /*$scope.onLoad = function (start, n, sort, filters) {
            if (firstLoad && $stateParams.id) {
                $scope.treeData = null;
            }
            Authentication.queryUserInfoNew().then(function (userInfo) {
                var userId = userInfo.userId;
                CallCostTrackingService.queryCostTrackingTreeData(userId, start, n, sort).then(
                    function (treeData) {
                        if (firstLoad) {
                            if ($stateParams.id) {
                                if ($scope.treeData) {

                                    var found = _.find(treeData.docs, {nodeId: $scope.treeData.docs[0].nodeId});
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
                return userInfo;
            });
            if (firstLoad && $stateParams.id) {
                CallCostTrackingService.getCostTrackingInfo($stateParams.id).then(
                    function (costsheetInfo) {
                        $scope.treeControl.select({
                            pageStart: start
                            , nodeType: Constant.ObjectTypes.COSTSHEET
                            , nodeId: costsheetInfo.id
                        });

                        var treeData = {docs: [], total: 0};
                        if ($scope.treeData) {
                            var found = _.find($scope.treeData.docs, {nodeId: costsheetInfo.id});
                            if (!found) {
                                treeData.docs = _.clone($scope.treeData.docs);
                                treeData.total = $scope.treeData.total;
                                treeData.docs.unshift({
                                    nodeId: Util.goodValue(costsheetInfo.id, 0)
                                    , nodeType: Constant.ObjectTypes.COSTSHEET
                                    , nodeTitle: Util.goodValue(costsheetInfo.title)
                                    , nodeToolTip: Util.goodValue(costsheetInfo.title)
                                });
                            }
                            firstLoad = false;

                        } else {
                            treeData.total = 1;
                            treeData.docs.unshift({
                                nodeId: Util.goodValue(costsheetInfo.id, 0)
                                , nodeType: Constant.ObjectTypes.COSTSHEET
                                , nodeTitle: Util.goodValue(costsheetInfo.title)
                                , nodeToolTip: Util.goodValue(costsheetInfo.title)
                            });
                        }

                        $scope.treeData = treeData;
                        return costsheetInfo;
                    }
                    , function (errorData) {
                        $scope.treeControl.select({
                            pageStart: start
                            , nodeType: Constant.ObjectTypes.COSTSHEET
                            , nodeId: $stateParams.id
                        });


                        var treeData = {docs: [], total: 0};
                        if ($scope.treeData) {
                            var found = _.find($scope.treeData.docs, {nodeId: $stateParams.id});
                            if (!found) {
                                treeData.docs = _.clone($scope.treeData.docs);
                                treeData.total = $scope.treeData.total;
                                treeData.docs.unshift({
                                    nodeId: $stateParams.id
                                    , nodeType: Constant.ObjectTypes.COSTSHEET
                                    , nodeTitle: $translate.instant("common.directive.objectTree.errorNode.title")
                                    , nodeToolTip: $translate.instant("common.directive.objectTree.errorNode.toolTip")
                                });
                            }
                            firstLoad = false;

                        } else {
                            treeData.total = 1;
                            treeData.docs.unshift({
                                nodeId: $stateParams.id
                                , nodeType: Constant.ObjectTypes.COSTSHEET
                                , nodeTitle: $translate.instant("common.directive.objectTree.errorNode.title")
                                , nodeToolTip: $translate.instant("common.directive.objectTree.errorNode.toolTip")
                            });
                        }

                        $scope.treeData = treeData;
                        return errorData;
                    }
                );
            }

        };*/

        $scope.onSelect = function (selectedCostsheet) {
            $scope.$emit('req-select-costsheet', selectedCostsheet);
            var components = Util.goodArray(selectedCostsheet.components);
            var componentType = (1 == components.length) ? components[0] : "main";
            $state.go('cost-tracking.' + componentType, {
                id: selectedCostsheet.nodeId
            });
        };
    }
]);