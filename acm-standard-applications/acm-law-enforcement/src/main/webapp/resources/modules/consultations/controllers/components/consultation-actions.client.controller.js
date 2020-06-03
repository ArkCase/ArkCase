'use strict';

angular.module('consultations').controller(
    'Consultations.ActionsController',
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
        'Object.SubscriptionService',
        'Object.ModelService',
        'Consultation.InfoService',
        'Helper.ObjectBrowserService',
        'Profile.UserInfoService',
        '$timeout',
        'FormsType.Service',
        'Admin.FormWorkflowsLinkService',
        function($scope, $state, $stateParams, $translate, $q, $modal, Util, ConfigService, ObjectService, Authentication, ObjectSubscriptionService, ObjectModelService, ConsultationInfoService, HelperObjectBrowserService, UserInfoService, $timeout,
                 FormsTypeService, AdminFormWorkflowsLinkService) {

            new HelperObjectBrowserService.Component({
                scope: $scope,
                stateParams: $stateParams,
                moduleId: "consultations",
                componentId: "actions",
                retrieveObjectInfo: ConsultationInfoService.getConsultationInfo,
                validateObjectInfo: ConsultationInfoService.validateConsultationInfo,
                onObjectInfoRetrieved: function(objectInfo) {
                    onObjectInfoRetrieved(objectInfo);
                }
            });
            AdminFormWorkflowsLinkService.getFormWorkflowsData().then(function(payload) {
                //$scope.data = payload.data;

                // Add 50 empty rows at the end of file
                var data = angular.copy(payload.data);
                for (var i = 0; i < data.cells.length; i++) {
                    if(data.cells[i][2].type === "fileType" && data.cells[i][2].value === "change_consultation_status"){
                        $scope.showApprover = data.cells[i][3].value;
                    }
                }
            });
            $scope.showBtnChildOutcomes = false;
            $scope.availableChildOutcomes = [];
            $scope.merging = false;
            $scope.splitting = false;

            ConfigService.getModuleConfig("consultations").then(function(moduleConfig) {
                $scope.consultationFileSearchConfig = _.find(moduleConfig.components, {
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
                    ObjectSubscriptionService.getSubscriptions(userInfo.userId, ObjectService.ObjectTypes.CONSULTATION, $scope.objectInfo.id).then(function(subscriptions) {
                        var found = _.find(subscriptions, {
                            userId: userInfo.userId,
                            subscriptionObjectType: ObjectService.ObjectTypes.CONSULTATION,
                            objectId: $scope.objectInfo.id
                        });
                        $scope.showBtnSubscribe = Util.isEmpty(found);
                        $scope.showBtnUnsubscribe = !$scope.showBtnSubscribe;
                    });
                });

                $scope.editParams = {
                    consultationId: objectInfo.id,
                    consultationNumber: objectInfo.consultationNumber,
                    containerId: objectInfo.container.id,
                    folderId: objectInfo.container.folder.id
                };

                $scope.reinvestigateParams = {
                    consultationId: objectInfo.id,
                    consultationNumber: objectInfo.consultationNumber,
                    containerId: objectInfo.container.id,
                    folderId: objectInfo.container.folder.id
                };

                $scope.changeConsultationStatusParams = {
                    consultationId: objectInfo.id,
                    consultationNumber: objectInfo.consultationNumber,
                    status: objectInfo.status
                };

                $scope.editConsultationParams = {
                    isEdit: true,
                    consultation: objectInfo
                };

                $scope.showChangeConsultationStatus = objectInfo.status !== 'IN APPROVAL';
            };

            $scope.newConsultation = function() {
                var params = {
                    isEdit: false
                };
                showModal(params);
            };

            $scope.editConsultation = function() {
                showModal($scope.editConsultationParams);
            };

            function showModal(params) {
                var modalInstance = $modal.open({
                    animation: true,
                    templateUrl: 'modules/consultations/views/components/consultation-new-consultation-modal.client.view.html',
                    controller: 'Consultations.NewConsultationController',
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

                    var consultationInfo = Util.omitNg($scope.objectInfo);
                    ConsultationInfoService.saveConsultationInfo(consultationInfo).then(function() {

                    }, function() {
                        $scope.restricted = !$scope.restricted;
                    });
                }
            };


            $scope.changeConsultationStatus = function(consultationInfo) {
                var params = {
                    "info": consultationInfo,
                    "showApprover": $scope.showApprover
                };
                var modalInstance = $modal.open({
                    animation: true,
                    templateUrl: 'modules/consultations/views/components/consultation-change-status-modal.client.view.html',
                    controller: 'Consultations.ChangeStatusController',
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
                    $scope.refresh();
                }, function() {
                    console.log("error");
                });
            };

            $scope.subscribe = function(consultationInfo) {
                ObjectSubscriptionService.subscribe($scope.userId, ObjectService.ObjectTypes.CONSULTATION, consultationInfo.id).then(function(data) {
                    $scope.showBtnSubscribe = false;
                    $scope.showBtnUnsubscribe = !$scope.showBtnSubscribe;
                    return data;
                });
            };
            $scope.unsubscribe = function(consultationInfo) {
                ObjectSubscriptionService.unsubscribe($scope.userId, ObjectService.ObjectTypes.CONSULTATION, consultationInfo.id).then(function(data) {
                    $scope.showBtnSubscribe = true;
                    $scope.showBtnUnsubscribe = !$scope.showBtnSubscribe;
                    return data;
                });
            };

            $scope.merge = function(consultationInfo) {

                var params = {};
                params.header = $translate.instant("consultations.comp.merge.objectPicker.title");
                params.config = $scope.newObjectPicker;
                params.filter = 'fq="object_type_s": CONSULTATION';

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
                modalInstance.result.then(function(consultationSummary) {
                    if (consultationSummary) {
                        $scope.merging = true;
                        MergeSplitService.mergeConsultationFile(consultationInfo.id, consultationSummary.object_id_s).then(function(data) {
                            $timeout(function() {
                                ObjectService.showObject(ObjectService.ObjectTypes.CONSULTATION, data.id);
                                $scope.merging = false;
                                //4 seconds delay so solr can index the new consultation file
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
                    templateUrl: 'modules/consultations/views/components/consultation-split.client.view.html',
                    controller: 'Consultations.SplitController',
                    size: 'lg',
                    backdrop: 'static'
                });
                modalInstance.result.then(function(consultationSummary) {
                    if (consultationSummary) {
                        $scope.splitting = true;
                        if (consultationSummary != null) {
                            MergeSplitService.splitConsultationFile(consultationSummary).then(function(data) {
                                $timeout(function() {
                                    ObjectService.showObject(ObjectService.ObjectTypes.CONSULTATION, data.id);
                                    $scope.splitting = false;
                                    //4 seconds delay so solr can index the new consultation file
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
                var consultationInfo = Util.omitNg(objectInfo);
                ConsultationInfoService.saveConsultationInfo(consultationInfo).then(function(response) {
                    //success
                    $scope.refresh();
                });
            };

            $scope.unclaim = function(objectInfo) {
                ObjectModelService.setAssignee(objectInfo, "");
                var consultationInfo = Util.omitNg(objectInfo);
                ConsultationInfoService.saveConsultationInfo(consultationInfo).then(function(response) {
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
