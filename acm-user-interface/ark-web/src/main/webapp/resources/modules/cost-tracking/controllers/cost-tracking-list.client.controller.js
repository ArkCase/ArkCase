'use strict';

angular.module('cost-tracking').controller('CostTrackingListController', ['$scope', '$state', '$stateParams', '$q', '$translate', 'UtilService', 'ObjectService', 'CallCostTrackingService', 'ConfigService', 'Authentication', 'Helper.ObjectTreeService',
    function ($scope, $state, $stateParams, $q, $translate, Util, ObjectService, CallCostTrackingService, ConfigService, Authentication, HelperObjectTreeService) {
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
                Authentication.queryUserInfo().then(
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
                    , nodeType: ObjectService.ObjectTypes.COSTSHEET
                    , nodeTitle: Util.goodValue(costsheetId.title)
                    , nodeToolTip: Util.goodValue(costsheetId.title)
                };
            }
        });

        $scope.onLoad = function (start, n, sort, filters) {
            treeHelper.onLoad(start, n, sort, filters);
        };

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