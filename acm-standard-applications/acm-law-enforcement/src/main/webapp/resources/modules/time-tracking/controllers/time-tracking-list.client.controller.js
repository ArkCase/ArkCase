'use strict';

angular.module('time-tracking').controller(
        'TimeTrackingListController',
        [ '$scope', '$state', '$stateParams', '$q', '$translate', 'Authentication', 'UtilService', 'MessageService', 'ObjectService', 'Helper.ObjectBrowserService', 'TimeTracking.ListService', 'TimeTracking.InfoService', 'ServCommService',
                function($scope, $state, $stateParams, $q, $translate, Authentication, Util, MessageService, ObjectService, HelperObjectBrowserService, TimeTrackingListService, TimeTrackingInfoService, ServCommService) {

                    /*//
                     // Check to see if complaint page is shown as a result returned by Frevvo
                     // Reset the tree cache so that new entry created by Frevvo can be shown.
                     // This is a temporary solution until UI and backend communication is implemented
                     //
                     var topics = ["new-timesheet"];
                     _.each(topics, function (topic) {
                     var data = ServCommService.popRequest("frevvo", topic);
                     if (data) {
                     TimeTrackingListService.resetTimeTrackingTreeData();
                     }
                     });*/

                    //subscribe to the bus for the object
                    var eventName = "object.inserted";
                    $scope.$bus.subscribe(eventName, function(data) {
                        if (data.objectType === ObjectService.ObjectTypes.TIMESHEET) {
                            var frevvoRequest = ServCommService.popRequest("frevvo", "new-timesheet");
                            if (frevvoRequest) {
                                ObjectService.showObject(ObjectService.ObjectTypes.TIMESHEET, data.objectId);
                            } else {
                                MessageService.info(data.objectType + " with ID " + data.objectId + " was created.");
                            }
                        }

                    });

                    //"treeConfig", "treeData", "onLoad", and "onSelect" will be set by Tree Helper
                    new HelperObjectBrowserService.Tree({
                        scope: $scope,
                        state: $state,
                        stateParams: $stateParams,
                        moduleId: "time-tracking",
                        resetTreeData: function() {
                            return TimeTrackingListService.resetTimeTrackingTreeData();
                        },
                        updateTreeData: function(start, n, sort, filters, query, nodeData) {
                            return TimeTrackingListService.updateTimeTrackingTreeData(start, n, sort, filters, query, nodeData);
                        },
                        getTreeData: function(start, n, sort, filters, query) {
                            var dfd = $q.defer();
                            Authentication.queryUserInfo().then(function(userInfo) {
                                var userId = userInfo.userId;
                                TimeTrackingListService.queryTimeTrackingTreeData(userId, start, n, sort, filters, query).then(function(treeData) {
                                    dfd.resolve(treeData);
                                    return treeData;
                                }, function(error) {
                                    dfd.reject(error);
                                    return error;
                                });
                                return userInfo;
                            }, function(error) {
                                dfd.reject(error);
                                return error;
                            });
                            return dfd.promise;
                        },
                        getNodeData: function(timesheetId) {
                            return TimeTrackingInfoService.getTimesheetInfo(timesheetId);
                        },
                        makeTreeNode: function(timesheetInfo) {
                            return {
                                nodeId: Util.goodValue(timesheetInfo.id, 0),
                                nodeType: ObjectService.ObjectTypes.TIMESHEET,
                                nodeTitle: Util.goodValue(timesheetInfo.title),
                                nodeToolTip: Util.goodValue(timesheetInfo.title)
                            };
                        }
                    });

                } ]);