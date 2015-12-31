'use strict';

angular.module('cost-tracking').controller('CostTrackingListController', ['$scope', '$state', '$stateParams', '$q', '$translate'
    , 'Authentication', 'UtilService', 'ObjectService', 'Helper.ObjectBrowserService'
    , 'CostTracking.ListService', 'CostTracking.InfoService'
    , function ($scope, $state, $stateParams, $q, $translate
        , Authentication, Util, ObjectService, HelperObjectBrowserService
        , CostTrackingListService, CostTrackingInfoService) {

        //"treeConfig", "treeData", "onLoad", and "onSelect" will be set by Tree Helper
        new HelperObjectBrowserService.Tree({
            scope: $scope
            , state: $state
            , stateParams: $stateParams
            , moduleId: "cost-tracking"
            , getTreeData: function (start, n, sort, filters) {
                var dfd = $q.defer();
                Authentication.queryUserInfo().then(
                    function (userInfo) {
                        var userId = userInfo.userId;
                        CostTrackingListService.queryCostTrackingTreeData(userId, start, n, sort, filters).then(
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
            , getNodeData: function (costsheetInfo) {
                return CostTrackingInfoService.getCostsheetInfo(costsheetInfo);
            }
            , makeTreeNode: function (costsheetInfo) {
                return {
                    nodeId: Util.goodValue(costsheetInfo.id, 0)
                    , nodeType: ObjectService.ObjectTypes.COSTSHEET
                    , nodeTitle: Util.goodValue(costsheetInfo.title)
                    , nodeToolTip: Util.goodValue(costsheetInfo.title)
                };
            }
        });

    }
]);