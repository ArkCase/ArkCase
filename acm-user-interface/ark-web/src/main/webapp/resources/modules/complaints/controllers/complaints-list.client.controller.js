'use strict';

angular.module('complaints').controller('ComplaintsListController', ['$scope', '$state', '$stateParams', '$translate', 'UtilService', 'ConstantService', 'CallComplaintsService', 'CallConfigService',
    function ($scope, $state, $stateParams, $translate, Util, Constant, CallComplaintsService, CallConfigService) {
        CallConfigService.getModuleConfig("complaints").then(function (config) {
            $scope.treeConfig = config.tree;
            $scope.componentsConfig = config.components;
            return config;
        });


        var firstLoad = true;
        $scope.onLoad = function (start, n, sort, filters) {
            if (firstLoad && $stateParams.id) {
                $scope.treeData = null;
            }

            CallComplaintsService.queryComplaintsTreeData(start, n, sort, filters).then(
                function (treeData) {
                    if (firstLoad) {
                        if ($stateParams.id) {
                            if ($scope.treeData) {            //It must be set by CallComplaintsService.getComplaintInfo(), only 1 items in docs[] is expected
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

            if (firstLoad && $stateParams.id) {
                CallComplaintsService.getComplaintInfo($stateParams.id).then(
                    function (complaintInfo) {
                        $scope.treeControl.select({
                            pageStart: start
                            , nodeType: Constant.ObjectTypes.CASE_FILE
                            , nodeId: complaintInfo.id
                        });

                        var treeData = {docs: [], total: 0};
                        if ($scope.treeData) {            //It must be set by CallComplaintsService.queryComplaintsTreeData()
                            var found = _.find($scope.treeData.docs, {nodeId: complaintInfo.id});
                            if (!found) {
                                treeData.docs = _.clone($scope.treeData.docs);
                                treeData.total = $scope.treeData.total;
                                treeData.docs.unshift({
                                    nodeId: Util.goodValue(complaintInfo.id, 0)
                                    , nodeType: Constant.ObjectTypes.COMPLAINT
                                    , nodeTitle: Util.goodValue(complaintInfo.title)
                                    , nodeToolTip: Util.goodValue(complaintInfo.title)
                                });
                            } else {
                                treeData = $scope.treeData; //use what is there already
                            }
                            firstLoad = false;

                        } else {
                            treeData.total = 1;
                            treeData.docs.unshift({
                                nodeId: Util.goodValue(complaintInfo.id, 0)
                                , nodeType: Constant.ObjectTypes.COMPLAINT
                                , nodeTitle: Util.goodValue(complaintInfo.title)
                                , nodeToolTip: Util.goodValue(complaintInfo.title)
                            });
                        }

                        $scope.treeData = treeData;
                        return complaintInfo;
                    }
                    , function (errorData) {
                        $scope.treeControl.select({
                            pageStart: start
                            , nodeType: Constant.ObjectTypes.COMPLAINT
                            , nodeId: $stateParams.id
                        });


                        var treeData = {docs: [], total: 0};
                        if ($scope.treeData) {            //It must be set by CallComplaintsService.queryComplaintsTreeData()
                            var found = _.find($scope.treeData.docs, {nodeId: $stateParams.id});
                            if (!found) {
                                treeData.docs = _.clone($scope.treeData.docs);
                                treeData.total = $scope.treeData.total;
                                treeData.docs.unshift({
                                    nodeId: $stateParams.id
                                    , nodeType: Constant.ObjectTypes.COMPLAINT
                                    , nodeTitle: $translate.instant("common.directive.objectTree.errorNode.title")
                                    , nodeToolTip: $translate.instant("common.directive.objectTree.errorNode.toolTip")
                                });
                            } else {
                                treeData = $scope.treeData; //use what is there already
                            }
                            firstLoad = false;

                        } else {
                            treeData.total = 1;
                            treeData.docs.unshift({
                                nodeId: $stateParams.id
                                , nodeType: Constant.ObjectTypes.COMPLAINT
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

        $scope.onSelect = function (selectedComplaint) {
            $scope.$emit('req-select-complaint', selectedComplaint);
            var components = Util.goodArray(selectedComplaint.components);
            var componentType = (1 == components.length) ? components[0] : "main";
            //$state.go('complaints.' + componentType, {
            //    id: selectedComplaint.nodeId
            //});
        };
    }
]);