'use strict';

angular.module('complaints').controller(
        'Complaints.ActionsController',
        [ '$scope', '$state', '$stateParams', '$q', 'UtilService', 'ConfigService', 'ObjectService', 'Authentication', 'Object.LookupService', 'Complaint.LookupService', 'Object.SubscriptionService', 'Complaint.InfoService', 'Helper.ObjectBrowserService', 'Object.ModelService',
                'Profile.UserInfoService', '$modal',
                function($scope, $state, $stateParams, $q, Util, ConfigService, ObjectService, Authentication, ObjectLookupService, ComplaintLookupService, ObjectSubscriptionService, ComplaintInfoService, HelperObjectBrowserService, ObjectModelService, UserInfoService, $modal) {

                    new HelperObjectBrowserService.Component({
                        scope: $scope,
                        stateParams: $stateParams,
                        moduleId: "complaints",
                        componentId: "actions",
                        retrieveObjectInfo: ComplaintInfoService.getComplaintInfo,
                        validateObjectInfo: ComplaintInfoService.validateComplaintInfo,
                        onObjectInfoRetrieved: function(objectInfo) {
                            onObjectInfoRetrieved(objectInfo);
                        }
                    });

                    $scope.showBtnChildOutcomes = false;
                    $scope.availableChildOutcomes = [];

                    var onObjectInfoRetrieved = function(objectInfo) {
                        $scope.objectInfo = objectInfo;
                        $scope.restricted = objectInfo.restricted;
                        $scope.showCreateAndClose = ($scope.objectInfo.status !== "CLOSED");
                        $scope.showBtnChildOutcomes = false;
                        var group = ObjectModelService.getGroup(objectInfo);
                        $scope.owningGroup = group;
                        var assignee = ObjectModelService.getAssignee(objectInfo);
                        $scope.assignee = assignee;

                        Authentication.queryUserInfo().then(function(userInfo) {
                            $scope.userId = userInfo.userId;
                            ObjectSubscriptionService.getSubscriptions(userInfo.userId, ObjectService.ObjectTypes.COMPLAINT, $scope.objectInfo.complaintId).then(function(subscriptions) {
                                var found = _.find(subscriptions, {
                                    userId: userInfo.userId,
                                    subscriptionObjectType: ObjectService.ObjectTypes.COMPLAINT,
                                    objectId: $scope.objectInfo.complaintId
                                });
                                $scope.showBtnSubscribe = Util.isEmpty(found);
                                $scope.showBtnUnsubscribe = !$scope.showBtnSubscribe;
                            });
                        });

                        $scope.closeParams = {
                            complaintId: objectInfo.complaintId,
                            complaintNumber: objectInfo.complaintNumber
                        };
                    };

                    $scope.onClickRestrict = function($event) {
                        if ($scope.restricted != $scope.objectInfo.restricted) {
                            $scope.objectInfo.restricted = $scope.restricted;

                            var complaintInfo = Util.omitNg($scope.objectInfo);
                            ComplaintInfoService.saveComplaintInfo(complaintInfo).then(function() {

                            }, function() {
                                $scope.restricted = !$scope.restricted;
                            });
                        }
                    };

                    $scope.subscribe = function(complaintInfo) {
                        ObjectSubscriptionService.subscribe($scope.userId, ObjectService.ObjectTypes.COMPLAINT, complaintInfo.complaintId).then(function(data) {
                            $scope.showBtnSubscribe = false;
                            $scope.showBtnUnsubscribe = !$scope.showBtnSubscribe;
                            return data;
                        });
                    };

                    $scope.unsubscribe = function(complaintInfo) {
                        ObjectSubscriptionService.unsubscribe($scope.userId, ObjectService.ObjectTypes.COMPLAINT, complaintInfo.complaintId).then(function(data) {
                            $scope.showBtnSubscribe = true;
                            $scope.showBtnUnsubscribe = !$scope.showBtnSubscribe;
                            return data;
                        });
                    };

                    UserInfoService.getUserInfo().then(function(infoData) {
                        $scope.currentUserProfile = infoData;
                    });

                    $scope.refresh = function() {
                        $scope.$emit('report-object-refreshed', $stateParams.id);
                    };

                    $scope.claim = function(objectInfo) {
                        ObjectModelService.setAssignee(objectInfo, $scope.currentUserProfile.userId);
                        var complaintInfo = Util.omitNg(objectInfo);
                        ComplaintInfoService.saveComplaintInfo(complaintInfo).then(function(response) {
                            //success
                            $scope.refresh();
                        });
                    };

                    $scope.unclaim = function(objectInfo) {
                        ObjectModelService.setAssignee(objectInfo, "");
                        var complaintInfo = Util.omitNg(objectInfo);
                        ComplaintInfoService.saveComplaintInfo(complaintInfo).then(function(response) {
                            //success
                            $scope.refresh();
                        });
                    };

                    $scope.onClickChildOutcome = function(name) {
                        $scope.$bus.publish('CHILD_OBJECT_OUTCOME_CLICKED', name);
                    };

                    $scope.$bus.subscribe('CHILD_OBJECT_OUTCOMES_FOUND', function(outcomes) {
                        $scope.availableChildOutcomes = outcomes;
                        $scope.showBtnChildOutcomes = true;
                    });

                    $scope.closeComplaint = function(complaintInfo) {
                        var params = {
                            "info": complaintInfo
                        };
                        var modalInstance = $modal.open({
                            animation: true,
                            templateUrl: 'modules/complaints/views/components/complaint-close-complaint-modal.client.view.html',
                            controller: 'Complaints.CloseComplaintController',
                            size: 'lg',
                            resolve: {
                                modalParams: function() {
                                    return params;
                                }
                            }
                        });

                        modalInstance.result.then(function(data) {
                            console.log(data);
                        }, function() {
                            console.log("error");
                        });
                    };

                    $scope.newComplaint = function() {
                        var params = {};
                        var modalInstance = $modal.open({
                            animation: true,
                            templateUrl: 'modules/complaints/views/components/complaint-new-complaint-modal.client.view.html',
                            controller: 'Complaints.NewComplaintController',
                            size: 'lg',
                            resolve: {
                                modalParams: function() {
                                    return params;
                                }
                            }
                        });

                        modalInstance.result.then(function(data) {
                            console.log(data);
                        }, function() {
                            console.log("error");
                        });
                    };

                } ]);