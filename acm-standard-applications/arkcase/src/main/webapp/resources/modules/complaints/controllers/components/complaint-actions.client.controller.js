'use strict';

angular.module('complaints').controller(
        'Complaints.ActionsController',
        [ '$scope', '$state', '$stateParams', '$q', '$modal', 'UtilService', 'ConfigService', 'ObjectService', 'Authentication', 'Object.LookupService', 'Complaint.LookupService', 'Object.SubscriptionService', 'Complaint.InfoService', 'Helper.ObjectBrowserService', 'Object.ModelService',
                'Profile.UserInfoService', 'FormsType.Service', 'Ecm.EmailService', 'Admin.FormWorkflowsLinkService',
                function($scope, $state, $stateParams, $q, $modal, Util, ConfigService, ObjectService, Authentication, ObjectLookupService, ComplaintLookupService, ObjectSubscriptionService, ComplaintInfoService, HelperObjectBrowserService, ObjectModelService, UserInfoService, FormsTypeService, EcmEmailService, AdminFormWorkflowsLinkService) {

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

                    AdminFormWorkflowsLinkService.getFormWorkflowsData().then(function(payload) {
                        //$scope.data = payload.data;

                        // Add 50 empty rows at the end of file
                        var data = angular.copy(payload.data);
                        for (var i = 0; i < data.cells.length; i++) {
                            if(data.cells[i][2].type === "fileType" && data.cells[i][2].value === "close_complaint"){
                                $scope.showApprover = data.cells[i][3].value;
                            }
                        }
                    });

                    FormsTypeService.isAngularFormType().then(function(isAngularFormType) {
                        $scope.isAngularFormType = isAngularFormType;
                    });

                    FormsTypeService.isFrevvoFormType().then(function(isFrevvoFormType) {
                        $scope.isFrevvoFormType = isFrevvoFormType;
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
                            complaintNumber: objectInfo.complaintNumber,
                            complaintTitle: objectInfo.complaintTitle
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
                            "info": complaintInfo,
                            "showApprover": $scope.showApprover
                        };
                        var modalInstance = $modal.open({
                            animation: true,
                            templateUrl: 'modules/complaints/views/components/complaint-close-complaint-modal.client.view.html',
                            controller: 'Complaints.CloseComplaintController',
                            size: 'lg',
                            backdrop: 'static',
                            resolve: {
                                modalParams: function() {
                                    return params;
                                }
                            }
                        });
                    };

                    $scope.newComplaint = function() {
                        var params = {};
                        var modalInstance = $modal.open({
                            animation: true,
                            templateUrl: 'modules/complaints/views/components/complaint-new-complaint-modal.client.view.html',
                            controller: 'Complaints.NewComplaintController',
                            size: 'lg',
                            backdrop: 'static',
                            resolve: {
                                modalParams: function() {
                                    return params;
                                }
                            }
                        });

                        modalInstance.result.then(function(data) {
                            //Do nothing
                        });
                    };

                    $scope.sendEmail = function () {
                        var params = {
                            objectId: $scope.objectInfo.complaintId,
                            objectType: ObjectService.ObjectTypes.COMPLAINT,
                            objectNumber: $scope.objectInfo.complaintNumber,
                            emailSubject: 'Complaint ' + $scope.objectInfo.complaintNumber
                        };
                        var modalInstance = $modal.open({
                            templateUrl: 'modules/common/views/send-email-modal.client.view.html',
                            controller: 'Common.SendEmailModalController',
                            animation: true,
                            size: 'lg',
                            backdrop: 'static',
                            resolve: {
                                params: function() {
                                    return params;
                                }
                            }
                        });

                        modalInstance.result.then(function(res) {
                            var emailData = {};
                            emailData.subject = res.subject;
                            emailData.body = res.body;
                            emailData.footer = '\n\n' + res.footer;
                            emailData.emailAddresses = res.recipients;
                            emailData.ccEmailAddresses = res.ccRecipients;
                            emailData.bccEmailAddresses = res.bccRecipients;
                            emailData.objectId = $scope.objectInfo.complaintId;
                            emailData.objectType = ObjectService.ObjectTypes.COMPLAINT;
                            emailData.objectNumber = $scope.objectInfo.complaintNumber;
                            emailData.modelReferenceName = res.template;

                            if(emailData.modelReferenceName != 'plainEmail') {
                                EcmEmailService.sendManualEmail(emailData);
                            } else {
                                EcmEmailService.sendPlainEmail(emailData, ObjectService.ObjectTypes.COMPLAINT);
                            }

                        });
                    };

                } ]);
