'use strict';

angular.module('time-tracking').controller('TimeTrackingListController', ['$scope', '$state', '$stateParams', '$q', '$translate'
    , 'ConfigService', 'UtilService', 'ObjectService', 'TimeTrackingService', 'CallTimeTrackingService'
    , 'Profile.UserInfoService', 'Authentication', 'Helper.ObjectTreeService'
    , function ($scope, $state, $stateParams, $q, $translate
        , ConfigService, Util, ObjectService, TimeTrackingService, CallTimeTrackingService
        , UserInfoService, Authentication, HelperObjectTreeService) {

        ConfigService.getModuleConfig("time-tracking").then(function (config) {
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
                        CallTimeTrackingService.queryTimeTrackingTreeData(userId, start, n, sort, filters).then(
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
            , getNodeData: function (timesheetId) {
                return CallTimeTrackingService.getTimeTrackingInfo(timesheetId);
            }
            , makeTreeNode: function (timesheetInfo) {
                return {
                    nodeId: Util.goodValue(timesheetInfo.id, 0)
                    , nodeType: ObjectService.ObjectTypes.TIMESHEET
                    , nodeTitle: Util.goodValue(timesheetInfo.title)
                    , nodeToolTip: Util.goodValue(timesheetInfo.title)
                };
            }
        });
        $scope.onLoad = function (start, n, sort, filters) {
            treeHelper.onLoad(start, n, sort, filters);
        };

        $scope.onSelect = function (selectedTimesheet) {
            $scope.$emit('req-select-timesheet', selectedTimesheet);
            var components = Util.goodArray(selectedTimesheet.components);
            var componentType = (1 == components.length) ? components[0] : "main";
            $state.go('time-tracking.' + componentType, {
                id: selectedTimesheet.nodeId
            });
        };
    }
]);