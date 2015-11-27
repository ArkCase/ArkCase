'use strict';

angular.module('complaints').controller('ComplaintsListController', ['$scope', '$state', '$stateParams', '$translate', 'UtilService', 'ConstantService', 'Complaint.ListService', 'Complaint.InfoService', 'ConfigService', 'Helper.ObjectTreeService',
    function ($scope, $state, $stateParams, $translate, Util, Constant, ComplaintListService, ComplaintInfoService, ConfigService, HelperObjectTreeService) {
        ConfigService.getModuleConfig("complaints").then(function (config) {
            $scope.treeConfig = config.tree;
            $scope.componentsConfig = config.components;
            return config;
        });

        var treeHelper = new HelperObjectTreeService.Tree({
            scope: $scope
            , nodeId: $stateParams.id
            , getTreeData: function (start, n, sort, filters) {
                return ComplaintListService.queryComplaintsTreeData(start, n, sort, filters);
            }
            , getNodeData: function (complaintId) {
                return ComplaintInfoService.getComplaintInfo(complaintId);
            }
            , makeTreeNode: function (complaintInfo) {
                return {
                    nodeId: Util.goodValue(complaintInfo.id, 0)
                    , nodeType: Constant.ObjectTypes.TASK
                    , nodeTitle: Util.goodValue(complaintInfo.title)
                    , nodeToolTip: Util.goodValue(complaintInfo.title)
                };
            }
        });
        $scope.onLoad = function (start, n, sort, filters) {
            treeHelper.onLoad(start, n, sort, filters);
        };

        $scope.onSelect = function (selectedComplaint) {
            $scope.$emit('req-select-complaint', selectedComplaint);
            var components = Util.goodArray(selectedComplaint.components);
            var componentType = (1 == components.length) ? components[0] : "main";
            $state.go('complaints.' + componentType, {
                id: selectedComplaint.nodeId
            });
        };
    }
]);