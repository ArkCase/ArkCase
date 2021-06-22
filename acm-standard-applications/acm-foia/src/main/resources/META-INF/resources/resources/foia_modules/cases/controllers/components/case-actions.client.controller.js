'use strict';

angular.module('cases').controller(
    'Cases.ActionsController',
    ['$scope', '$translate', '$state', '$timeout', '$stateParams', '$q', '$modal', 'UtilService', 'ConfigService', 'ObjectService', 'Authentication', 'Object.LookupService', 'Case.LookupService', 'Object.SubscriptionService', 'Object.ModelService', 'Case.InfoService', 'Case.MergeSplitService',
        'Helper.ObjectBrowserService', 'Profile.UserInfoService', 'Ecm.EmailService', 'Admin.ZylabIntegrationService', 'Request.ZylabMatterService', 'MessageService',
        function ($scope, $translate, $state, $timeout, $stateParams, $q, $modal, Util, ConfigService, ObjectService, Authentication, ObjectLookupService, CaseLookupService, ObjectSubscriptionService, ObjectModelService, CaseInfoService, MergeSplitService, HelperObjectBrowserService, UserInfoService, EcmEmailService, ZylabIntegrationService, RequestZylabMatterService, MessageService) {

            new HelperObjectBrowserService.Component({
                scope: $scope,
                stateParams: $stateParams,
                moduleId: "cases",
                componentId: "actions",
                retrieveObjectInfo: CaseInfoService.getCaseInfo,
                validateObjectInfo: CaseInfoService.validateCaseInfo,
                onObjectInfoRetrieved: function (objectInfo) {
                    onObjectInfoRetrieved(objectInfo);
                }
            });

            $scope.loadingRequestIcon = "fa fa-save";
            $scope.creatingMatterIcon = "fa fa-gavel";
            $scope.createMatterInProgress = false;

            $scope.showBtnChildOutcomes = false;
            $scope.availableChildOutcomes = [];
            $scope.splitting = false;


            ConfigService.getModuleConfig("cases").then(function (moduleConfig) {
                $scope.caseFileSearchConfig = _.find(moduleConfig.components, {
                    id: "merge"
                });
            });

            ZylabIntegrationService.getConfiguration().then(function (response) {
                $scope.documentReviewEnabled = response.data["zylabIntegration.enabled"];
                $scope.zylabIntegrationConfig = response.data;
            });


            var promiseQueryUser = Authentication.queryUserInfo();
            var promiseGetGroups = ObjectLookupService.getGroups();

            var onObjectInfoRetrieved = function (objectInfo) {

                var group = ObjectModelService.getGroup(objectInfo);
                $scope.owningGroup = group;
                var assignee = ObjectModelService.getAssignee(objectInfo);
                $scope.assignee = assignee;
                $scope.showBtnChildOutcomes = false;
                $scope.createMatterInProgress = false;
                $scope.creatingMatterIcon = "fa fa-gavel";

                var assignee = ObjectModelService.getAssignee(objectInfo);
                var promiseGetApprovers = CaseLookupService.getApprovers(group, assignee);
                $q.all([promiseQueryUser, promiseGetGroups, promiseGetApprovers]).then(function (data) {
                    var userInfo = data[0];
                    var groups = data[1];
                    var assignees = data[2];
                    $scope.restricted = ObjectModelService.checkRestriction(userInfo.userId, assignee, group, assignees, groups);
                });

                promiseQueryUser.then(function (userInfo) {
                    $scope.userId = userInfo.userId;
                    ObjectSubscriptionService.getSubscriptions(userInfo.userId, ObjectService.ObjectTypes.CASE_FILE, $scope.objectInfo.id).then(function (subscriptions) {
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

            $scope.save = function () {
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

            $scope.$bus.subscribe('report-object-updated', function (caseInfo) {
                $scope.loadingRequestIcon = "fa fa-save";
            });

            $scope.$bus.subscribe('report-object-update-failed', function (caseInfo) {
                $scope.loadingRequestIcon = "fa fa-save";
            });

            $scope.subscribe = function (caseInfo) {
                ObjectSubscriptionService.subscribe($scope.userId, ObjectService.ObjectTypes.CASE_FILE, caseInfo.id).then(function (data) {
                    $scope.showBtnSubscribe = false;
                    $scope.showBtnUnsubscribe = !$scope.showBtnSubscribe;
                    return data;
                });
            };
            $scope.unsubscribe = function (caseInfo) {
                ObjectSubscriptionService.unsubscribe($scope.userId, ObjectService.ObjectTypes.CASE_FILE, caseInfo.id).then(function (data) {
                    $scope.showBtnSubscribe = true;
                    $scope.showBtnUnsubscribe = !$scope.showBtnSubscribe;
                    return data;
                });
            };

            $scope.createMatter = function (requestInfo) {
                $scope.creatingMatterIcon = "fa fa-circle-o-notch fa-spin";
                $scope.createMatterInProgress = true;

                RequestZylabMatterService.createMatter(requestInfo.id).then(function (data) {
                    var createMatterResponse = data.data;
                    if (createMatterResponse.status === "CREATED") {
                        MessageService.info($translate.instant("cases.comp.actions.createMatter.sucess"));
                        $scope.createMatterInProgress = false;
                        $scope.openMatter(createMatterResponse.zylabId);
                        if ($scope.objectInfo.caseNumber === createMatterResponse.matterName) {
                            $scope.objectInfo.externalIdentifier = createMatterResponse.zylabId;
                        }
                    } else if (createMatterResponse.status === "IN_PROGRESS") {
                        MessageService.info($translate.instant("cases.comp.actions.createMatter.inProgress"));
                    } else {
                        MessageService.error($translate.instant("cases.comp.actions.createMatter.error"));
                    }

                    $scope.creatingMatterIcon = "fa fa-gavel";
                    $scope.createMatterInProgress = false;
                }).catch(function () {
                    MessageService.error($translate.instant("cases.comp.actions.createMatter.error"));
                    $scope.creatingMatterIcon = "fa fa-gavel";
                    $scope.createMatterInProgress = false;
                });
            };

            $scope.openMatter = function (matterId) {
                var openMatterPath = $scope.zylabIntegrationConfig["zylabIntegration.openMatterPath"].replace("{matterId}", matterId);
                var openMatterURL = $scope.zylabIntegrationConfig["zylabIntegration.url"] + openMatterPath;

                window.open(openMatterURL, '_blank');
            };

            $scope.openMatterReports = function (requestInfo) {
                var openMatterReportsPath = $scope.zylabIntegrationConfig["zylabIntegration.matterReportsPath"].replace("{matterId}", requestInfo.externalIdentifier);
                var openMatterReportsURL = $scope.zylabIntegrationConfig["zylabIntegration.url"] + openMatterReportsPath;

                window.open(openMatterReportsURL, '_blank');
            };

            UserInfoService.getUserInfo().then(function (infoData) {
                $scope.currentUserProfile = infoData;
            });

            $scope.split = function () {
                var modalInstance = $modal.open({
                    animation: $scope.animationsEnabled,
                    templateUrl: 'modules/cases/views/components/case-split.client.view.html',
                    controller: 'Cases.SplitController',
                    size: 'lg',
                    backdrop: 'static'
                });
                modalInstance.result.then(function (caseSummary) {
                    if (caseSummary) {
                        $scope.splitting = true;
                        if (caseSummary != null) {
                            MergeSplitService.splitCaseFile(caseSummary).then(function (data) {
                                $timeout(function () {
                                    ObjectService.showObject(ObjectService.ObjectTypes.CASE_FILE, data.id);
                                    $scope.splitting = false;
                                    //4 seconds delay so solr can index the new case file
                                }, 4000);
                            }, function () {
                                $scope.splitting = false;
                            });
                        }
                    }
                });
            };


            $scope.refresh = function () {
                $scope.$emit('report-object-refreshed', $stateParams.id);
            };

            $scope.claim = function (objectInfo) {
                ObjectModelService.setAssignee(objectInfo, $scope.currentUserProfile.userId);
                var requestInfo = Util.omitNg(objectInfo);
                CaseInfoService.saveCaseInfo(requestInfo).then(function (response) {
                    //success
                    $scope.refresh();
                });
            };

            $scope.unclaim = function (objectInfo) {
                ObjectModelService.setAssignee(objectInfo, "");
                var requestInfo = Util.omitNg(objectInfo);
                CaseInfoService.saveCaseInfo(requestInfo).then(function (response) {
                    //success
                    $scope.refresh();
                });
            };

            $scope.onClickChildOutcome = function (name) {
                $scope.$bus.publish('CHILD_OBJECT_OUTCOME_CLICKED', name);
            };

            $scope.$bus.subscribe('CHILD_OBJECT_OUTCOMES_FOUND', function (outcomes) {
                $scope.availableChildOutcomes = outcomes;
                $scope.showBtnChildOutcomes = true;
            });

            $scope.sendEmail = function () {
                var params = {
                    objectId: $scope.objectInfo.id,
                    objectType: ObjectService.ObjectTypes.CASE_FILE,
                    objectNumber: $scope.objectInfo.caseNumber,
                    emailSubject: 'Request ' + $scope.objectInfo.caseNumber,
                    emailOfOriginator: $scope.objectInfo.acmObjectOriginator.person.defaultEmail ? $scope.objectInfo.acmObjectOriginator.person.defaultEmail.value : ""
                };
                var modalInstance = $modal.open({
                    templateUrl: 'modules/common/views/send-email-modal.client.view.html',
                    controller: 'Common.SendEmailModalController',
                    animation: true,
                    size: 'lg',
                    backdrop: 'static',
                    resolve: {
                        params: function () {
                            return params;
                        }
                    }
                });

                modalInstance.result.then(function (res) {
                    var emailData = {};
                    emailData.subject = res.subject;
                    emailData.body = res.body;
                    emailData.footer = '\n\n' + res.footer;
                    emailData.emailAddresses = res.recipients;
                    emailData.ccEmailAddresses = res.ccRecipients;
                    emailData.bccEmailAddresses = res.bccRecipients;
                    emailData.objectId = $scope.objectInfo.id;
                    emailData.objectType = ObjectService.ObjectTypes.CASE_FILE;
                    emailData.objectNumber = $scope.objectInfo.caseNumber;
                    emailData.modelReferenceName = res.template;

                    if (emailData.modelReferenceName != 'plainEmail') {
                        EcmEmailService.sendManualEmail(emailData);
                    } else {
                        EcmEmailService.sendPlainEmail(emailData, ObjectService.ObjectTypes.CASE_FILE);
                    }

                });
            };


        }

    ]);
