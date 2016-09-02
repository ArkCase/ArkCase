'use strict';

angular.module('cost-tracking').controller('CostTrackingListController', ['$scope', '$state', '$stateParams', '$q', '$translate'
    , 'Authentication', 'UtilService', 'ObjectService', 'Helper.ObjectBrowserService'
    , 'CostTracking.ListService', 'CostTracking.InfoService', 'ServCommService'
    , function ($scope, $state, $stateParams, $q, $translate
        , Authentication, Util, ObjectService, HelperObjectBrowserService
        , CostTrackingListService, CostTrackingInfoService, ServCommService) {

        /*//
         // Check to see if complaint page is shown as a result returned by Frevvo
         // Reset the tree cache so that new entry created by Frevvo can be shown.
         // This is a temporary solution until UI and backend communication is implemented
         //
         var topics = ["new-costsheet"];
         _.each(topics, function (topic) {
         var data = ServCommService.popRequest("frevvo", topic);
         if (data) {
         CostTrackingListService.resetCostTrackingTreeData();
         }
         });*/

        //subscribe to the bus for the object
        var eventName = "object.inserted";
        $scope.$bus.subscribe(eventName, function (data) {
            if (data.objectType === ObjectService.ObjectTypes.COSTSHEET) {
                var frevvoRequest = ServCommService.popRequest("frevvo", "new-costsheet");
                if (frevvoRequest) {
                    ObjectService.gotoUrl(ObjectService.ObjectTypes.COSTSHEET, data.objectId);
                }
                else {
                    MessageService.info(data.objectType + " with ID " + data.objectId + " was created.");
                }
            }

        });


        //"treeConfig", "treeData", "onLoad", and "onSelect" will be set by Tree Helper
        new HelperObjectBrowserService.Tree({
            scope: $scope
            , state: $state
            , stateParams: $stateParams
            , moduleId: "cost-tracking"
            , resetTreeData: function () {
                return CostTrackingListService.resetCostTrackingTreeData();
            }
            , updateTreeData: function (start, n, sort, filters, query, nodeData) {
                return CostTrackingListService.updateCostTrackingTreeData(start, n, sort, filters, query, nodeData);
            }
            , getTreeData: function (start, n, sort, filters, query) {
                var dfd = $q.defer();
                Authentication.queryUserInfo().then(
                    function (userInfo) {
                        var userId = userInfo.userId;
                        CostTrackingListService.queryCostTrackingTreeData(userId, start, n, sort, filters, query).then(
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