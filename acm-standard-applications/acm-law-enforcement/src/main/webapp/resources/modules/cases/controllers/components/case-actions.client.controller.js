'use strict';

angular.module('cases').controller(
        'Cases.ActionsController',
        [
                '$scope',
                '$state',
                '$stateParams',
                '$translate',
                '$q',
                '$modal',
                'UtilService',
                'ConfigService',
                'ObjectService',
                'Authentication',
                'Case.LookupService',
                'Object.SubscriptionService',
                'Object.ModelService',
                'Case.InfoService',
                'Case.MergeSplitService',
                'Helper.ObjectBrowserService',
                'Profile.UserInfoService',
                '$timeout',
                'FormsType.Service',
                'Admin.FormWorkflowsLinkService',
                function($scope, $state, $stateParams, $translate, $q, $modal, Util, ConfigService, ObjectService, Authentication, CaseLookupService, ObjectSubscriptionService, ObjectModelService, CaseInfoService, MergeSplitService, HelperObjectBrowserService, UserInfoService, $timeout,
                        FormsTypeService, AdminFormWorkflowsLinkService) {

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
                    AdminFormWorkflowsLinkService.getFormWorkflowsData().then(function(payload) {
                        //$scope.data = payload.data;

                        // Add 50 empty rows at the end of file
                        var data = angular.copy(payload.data);
                        for (var i = 0; i < data.cells.length; i++) {
                            if(data.cells[i][2].type === "fileType" && data.cells[i][2].value === "change_case_status"){
                                $scope.showApprover = data.cells[i][3].value;
                            }
                        }
                    });
                    $scope.showBtnChildOutcomes = false;
                    $scope.availableChildOutcomes = [];
                    $scope.merging = false;
                    $scope.splitting = false;

                    ConfigService.getModuleConfig("cases").then(function(moduleConfig) {
                        $scope.caseFileSearchConfig = _.find(moduleConfig.components, {
                            id: "merge"
                        });
                        $scope.newObjectPicker = _.find(moduleConfig.components, {
                            id: "newObjectPicker"
                        });
                    });

                    FormsTypeService.isAngularFormType().then(function(isAngularFormType) {
                        $scope.isAngularFormType = isAngularFormType;
                    });

                    FormsTypeService.isFrevvoFormType().then(function(isFrevvoFormType) {
                        $scope.isFrevvoFormType = isFrevvoFormType;
                    });

                    var onObjectInfoRetrieved = function(objectInfo) {
                        $scope.restricted = objectInfo.restricted;
                        $scope.showBtnChildOutcomes = false;

                        var group = ObjectModelService.getGroup(objectInfo);
                        $scope.owningGroup = group;
                        var assignee = ObjectModelService.getAssignee(objectInfo);
                        $scope.assignee = assignee;

                        Authentication.queryUserInfo().then(function(userInfo) {
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

                        $scope.editParams = {
                            caseId: objectInfo.id,
                            caseNumber: objectInfo.caseNumber,
                            containerId: objectInfo.container.id,
                            folderId: objectInfo.container.folder.id
                        };

                        $scope.reinvestigateParams = {
                            caseId: objectInfo.id,
                            caseNumber: objectInfo.caseNumber,
                            containerId: objectInfo.container.id,
                            folderId: objectInfo.container.folder.id
                        };

                        $scope.changeCaseStatusParams = {
                            caseId: objectInfo.id,
                            caseNumber: objectInfo.caseNumber,
                            status: objectInfo.status
                        };

                        $scope.editCaseParams = {
                            isEdit: true,
                            casefile: objectInfo
                        };

                        if (objectInfo.status == 'IN APPROVAL') {
                            $scope.showChangeCaseStatus = false;
                        } else {
                            $scope.showChangeCaseStatus = true;
                        }
                    };

                    $scope.newCaseFile = function() {
                        var params = {
                            isEdit: false
                        };
                        showModal(params);
                    };

                    $scope.editCaseFile = function() {
                        showModal($scope.editCaseParams);
                    };

                    function showModal(params) {
                        var modalInstance = $modal.open({
                            animation: true,
                            templateUrl: 'modules/cases/views/components/case-new-case-modal.client.view.html',
                            controller: 'Cases.NewCaseController',
                            size: 'lg',
                            backdrop: 'static',
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
                    }

                    $scope.onClickRestrict = function($event) {
                        if ($scope.restricted != $scope.objectInfo.restricted) {
                            $scope.objectInfo.restricted = $scope.restricted;

                            var caseInfo = Util.omitNg($scope.objectInfo);
                            CaseInfoService.saveCaseInfo(caseInfo).then(function() {

                            }, function() {
                                $scope.restricted = !$scope.restricted;
                            });
                        }
                    };


                    $scope.changeCaseStatus = function(caseInfo) {
                        var params = {
                            "info": caseInfo,
                            "showApprover": $scope.showApprover
                        };
                        var modalInstance = $modal.open({
                            animation: true,
                            templateUrl: 'modules/cases/views/components/case-change-status-modal.client.view.html',
                            controller: 'Cases.ChangeStatusController',
                            size: 'lg',
                            backdrop: 'static',
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

                    $scope.merge = function(caseInfo) {

                        var params = {};
                        params.header = $translate.instant("cases.comp.merge.objectPicker.title");
                        params.config = $scope.newObjectPicker;
                        params.filter = 'fq="object_type_s": CASE_FILE';

                        var modalInstance = $modal.open({
                            templateUrl: 'directives/core-participants/core-participants-picker-modal.client.view.html',
                            controller: [ '$scope', '$modalInstance', 'params', function($scope, $modalInstance, params) {
                                $scope.modalInstance = $modalInstance;
                                $scope.header = params.header;
                                $scope.filter = params.filter;
                                $scope.extraFilter = params.extraFilter;
                                $scope.config = params.config;
                            } ],
                            size: 'lg',
                            backdrop: 'static',
                            resolve: {
                                params: function() {
                                    return params;
                                }
                            }
                        });
                        modalInstance.result.then(function(caseSummary) {
                            if (caseSummary) {
                                $scope.merging = true;
                                MergeSplitService.mergeCaseFile(caseInfo.id, caseSummary.object_id_s).then(function(data) {
                                    $timeout(function() {
                                        ObjectService.showObject(ObjectService.ObjectTypes.CASE_FILE, data.id);
                                        $scope.merging = false;
                                        //4 seconds delay so solr can index the new case file
                                    }, 4000);
                                }, function() {
                                    $scope.merging = false;
                                });
                            }
                        });
                    };

                    $scope.split = function() {
                        var modalInstance = $modal.open({
                            animation: $scope.animationsEnabled,
                            templateUrl: 'modules/cases/views/components/case-split.client.view.html',
                            controller: 'Cases.SplitController',
                            size: 'lg',
                            backdrop: 'static'
                        });
                        modalInstance.result.then(function(caseSummary) {
                            if (caseSummary) {
                                $scope.splitting = true;
                                if (caseSummary != null) {
                                    MergeSplitService.splitCaseFile(caseSummary).then(function(data) {
                                        $timeout(function() {
                                            ObjectService.showObject(ObjectService.ObjectTypes.CASE_FILE, data.id);
                                            $scope.splitting = false;
                                            //4 seconds delay so solr can index the new case file
                                        }, 4000);
                                    }, function() {
                                        $scope.merging = false;
                                    });
                                }
                            }
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
                        var caseInfo = Util.omitNg(objectInfo);
                        CaseInfoService.saveCaseInfo(caseInfo).then(function(response) {
                            //success
                            $scope.refresh();
                        });
                    };

                    $scope.unclaim = function(objectInfo) {
                        ObjectModelService.setAssignee(objectInfo, "");
                        var caseInfo = Util.omitNg(objectInfo);
                        CaseInfoService.saveCaseInfo(caseInfo).then(function(response) {
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
                } ]);
