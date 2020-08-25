'use strict';

angular.module('cases').controller(
        'Cases.ActionsController',
        [ '$scope', '$state', '$stateParams', '$q', '$modal', 'UtilService', 'ConfigService', 'ObjectService', 'Authentication', 'Object.LookupService', 'Case.LookupService', 'Object.SubscriptionService', 'Object.ModelService', 'Case.InfoService', 'Case.MergeSplitService',
                'Helper.ObjectBrowserService', 'Profile.UserInfoService',
                function($scope, $state, $stateParams, $q, $modal, Util, ConfigService, ObjectService, Authentication, ObjectLookupService, CaseLookupService, ObjectSubscriptionService, ObjectModelService, CaseInfoService, MergeSplitService, HelperObjectBrowserService, UserInfoService) {

                    new HelperObjectBrowserService.Component({
                        scope: $scope,
                        stateParams: $stateParams,
                        moduleId: "cases",
                        componentId: "actions",
                        retrieveObjectInfo: CaseInfoService.getCaseInfo,
                        validateObjectInfo: CaseInfoService.validateCaseInfo,
                        onObjectInfoRetrieved: function(objectInfo) {
                            onObjectInfoRetrieved(objectInfo);
                        }
                    });

                    $scope.loadingRequestIcon = "fa fa-save";

                    $scope.showBtnChildOutcomes = false;
                    $scope.availableChildOutcomes = [];

                    ConfigService.getModuleConfig("cases").then(function(moduleConfig) {
                        $scope.caseFileSearchConfig = _.find(moduleConfig.components, {
                            id: "merge"
                        });
                    });

                    var promiseQueryUser = Authentication.queryUserInfo();
                    var promiseGetGroups = ObjectLookupService.getGroups();

                    var onObjectInfoRetrieved = function(objectInfo) {

                        var group = ObjectModelService.getGroup(objectInfo);
                        $scope.owningGroup = group;
                        var assignee = ObjectModelService.getAssignee(objectInfo);
                        $scope.assignee = assignee;
                        $scope.showBtnChildOutcomes = false;

                        var assignee = ObjectModelService.getAssignee(objectInfo);
                        var promiseGetApprovers = CaseLookupService.getApprovers(group, assignee);
                        $q.all([ promiseQueryUser, promiseGetGroups, promiseGetApprovers ]).then(function(data) {
                            var userInfo = data[0];
                            var groups = data[1];
                            var assignees = data[2];
                            $scope.restricted = ObjectModelService.checkRestriction(userInfo.userId, assignee, group, assignees, groups);
                        });

                        promiseQueryUser.then(function(userInfo) {
                            $scope.userId = userInfo.userId;
                            ObjectSubscriptionService.getSubscriptions(userInfo.userId, ObjectService.ObjectTypes.CASE_FILE, $scope.objectInfo.id).then(function(subscriptions) {
                                var found = _.find(subscriptions, {
                                    userId: userInfo.userId,
                                    subscriptionObjectType: ObjectService.ObjectTypes.CASE_FILE,
                                    objectId: $scope.objectInfo.id
                                });
                                $scope.showBtnSubscribe = Util.isEmpty(found);
                                $scope.showBtnUnsubscribe = !$scope.showBtnSubscribe;
                            });
                        });
                    };

                    $scope.save = function() {
                        $scope.loadingRequestIcon = "fa fa-circle-o-notch fa-spin";
                        var deferred = $q.defer();
                        if (($scope.objectInfo.queue.name === 'Fulfill' || $scope.objectInfo.queue.name === 'Intake' || $scope.objectInfo.queue.name === 'Hold') && $scope.objectInfo.requestType === 'New Request' && $scope.objectInfo.disposition == null && $scope.objectInfo.dispositionClosedDate != null) {
                            $scope.$bus.publish('OPEN_REQUEST_DISPOSITION_MODAL', deferred);
                        } else if ($scope.objectInfo.requestType === 'Appeal' && ($scope.objectInfo.queue.name === 'Fulfill' || $scope.objectInfo.queue.name === 'Appeal' || $scope.objectInfo.queue.name === 'Hold') && $scope.objectInfo.disposition == null && $scope.objectInfo.dispositionClosedDate != null) {
                            $scope.$bus.publish('OPEN_APPEAL_DISPOSITION_MODAL', deferred);
                        } else {
                            deferred.resolve();
                        }

                        deferred.promise.then(function () {
                            $scope.$bus.publish('ACTION_SAVE_CASE', {});    
                        });
                    };

                    $scope.$bus.subscribe('report-object-updated', function(caseInfo) {
                        $scope.loadingRequestIcon = "fa fa-save";
                    });

                    $scope.$bus.subscribe('report-object-update-failed', function(caseInfo) {
                        $scope.loadingRequestIcon = "fa fa-save";
                    });

                    $scope.subscribe = function(caseInfo) {
                        ObjectSubscriptionService.subscribe($scope.userId, ObjectService.ObjectTypes.CASE_FILE, caseInfo.id).then(function(data) {
                            $scope.showBtnSubscribe = false;
                            $scope.showBtnUnsubscribe = !$scope.showBtnSubscribe;
                            return data;
                        });
                    };
                    $scope.unsubscribe = function(caseInfo) {
                        ObjectSubscriptionService.unsubscribe($scope.userId, ObjectService.ObjectTypes.CASE_FILE, caseInfo.id).then(function(data) {
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
                        var requestInfo = Util.omitNg(objectInfo);
                        CaseInfoService.saveCaseInfo(requestInfo).then(function(response) {
                            //success
                            $scope.refresh();
                        });
                    };

                    $scope.unclaim = function(objectInfo) {
                        ObjectModelService.setAssignee(objectInfo, "");
                        var requestInfo = Util.omitNg(objectInfo);
                        CaseInfoService.saveCaseInfo(requestInfo).then(function(response) {
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

                }

        ]);
