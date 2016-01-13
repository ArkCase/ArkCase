'use strict';

angular.module('complaints').controller('ComplaintsListController', ['$scope', '$state', '$stateParams', '$translate'
    , 'UtilService', 'ObjectService', 'Complaint.ListService', 'Complaint.InfoService', 'Helper.ObjectBrowserService'
    , function ($scope, $state, $stateParams, $translate
        , Util, ObjectService, ComplaintListService, ComplaintInfoService, HelperObjectBrowserService) {

        //"treeConfig", "treeData", "onLoad", and "onSelect" will be set by Tree Helper
        new HelperObjectBrowserService.Tree({
            scope: $scope
            , state: $state
            , stateParams: $stateParams
            , moduleId: "complaints"
            , resetTreeData: function () {
                return ComplaintListService.resetComplaintsTreeData();
            }
            , getTreeData: function (start, n, sort, filters) {
                return ComplaintListService.queryComplaintsTreeData(start, n, sort, filters);
            }
            , getNodeData: function (complaintId) {
                return ComplaintInfoService.getComplaintInfo(complaintId);
            }
            , makeTreeNode: function (complaintInfo) {
                return {
                    nodeId: Util.goodValue(complaintInfo.complaintId, 0)
                    , nodeType: ObjectService.ObjectTypes.COMPLAINT
                    , nodeTitle: Util.goodValue(complaintInfo.complaintTitle)
                    , nodeToolTip: Util.goodValue(complaintInfo.complaintTitle)
                };
            }
        });

    }
]);