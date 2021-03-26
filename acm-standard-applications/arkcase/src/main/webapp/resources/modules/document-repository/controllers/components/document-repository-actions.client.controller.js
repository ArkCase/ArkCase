'use strict';

angular.module('document-repository').controller(
        'DocumentRepository.ActionsController',
        [ '$scope', '$state', '$stateParams', '$q', '$modal', 'UtilService', 'ConfigService', 'ObjectService', 'Authentication', 'Object.SubscriptionService', 'Object.ModelService', 'DocumentRepository.InfoService', 'Helper.ObjectBrowserService', 'Ecm.EmailService',
                function($scope, $state, $stateParams, $q, $modal, Util, ConfigService, ObjectService, Authentication, ObjectSubscriptionService, ObjectModelService, DocumentRepositoryInfoService, HelperObjectBrowserService, EcmEmailService) {

                    new HelperObjectBrowserService.Component({
                        scope: $scope,
                        stateParams: $stateParams,
                        moduleId: "document-repository",
                        componentId: "actions",
                        retrieveObjectInfo: DocumentRepositoryInfoService.getDocumentRepositoryInfo,
                        validateObjectInfo: DocumentRepositoryInfoService.validateDocumentRepositoryInfo,
                        onObjectInfoRetrieved: function(objectInfo) {
                            onObjectInfoRetrieved(objectInfo);
                        }
                    });

                    var onObjectInfoRetrieved = function(objectInfo) {
                        $scope.restricted = objectInfo.restricted;

                        Authentication.queryUserInfo().then(function(userInfo) {
                            $scope.userId = userInfo.userId;
                            ObjectSubscriptionService.getSubscriptions(userInfo.userId, ObjectService.ObjectTypes.DOC_REPO, $scope.objectInfo.id).then(function(subscriptions) {
                                var found = _.find(subscriptions, {
                                    userId: userInfo.userId,
                                    subscriptionObjectType: ObjectService.ObjectTypes.DOC_REPO,
                                    objectId: $scope.objectInfo.id
                                });
                                $scope.showBtnSubscribe = Util.isEmpty(found);
                                $scope.showBtnUnsubscribe = !$scope.showBtnSubscribe;
                            });
                        });
                    };

                    $scope.onClickRestrict = function() {
                        if ($scope.restricted != $scope.objectInfo.restricted) {
                            $scope.objectInfo.restricted = $scope.restricted;

                            var docRepoInfo = Util.omitNg($scope.objectInfo);
                            DocumentRepositoryInfoService.saveDocumentRepository(docRepoInfo).then(function() {
                            }, function() {
                                $scope.restricted = !$scope.restricted;
                            });
                        }
                    };

                    $scope.subscribe = function(docRepoInfo) {
                        ObjectSubscriptionService.subscribe($scope.userId, ObjectService.ObjectTypes.DOC_REPO, docRepoInfo.id).then(function(data) {
                            $scope.showBtnSubscribe = false;
                            $scope.showBtnUnsubscribe = !$scope.showBtnSubscribe;
                            return data;
                        });
                    };

                    $scope.unsubscribe = function(docRepoInfo) {
                        ObjectSubscriptionService.unsubscribe($scope.userId, ObjectService.ObjectTypes.DOC_REPO, docRepoInfo.id).then(function(data) {
                            $scope.showBtnSubscribe = true;
                            $scope.showBtnUnsubscribe = !$scope.showBtnSubscribe;
                            return data;
                        });
                    };

                    $scope.deleteDR = function(docRepoId) {
                        DocumentRepositoryInfoService.deleteDocumentRepository(docRepoId);
                    };

                    $scope.refresh = function() {
                        $scope.$emit('report-object-refreshed', $stateParams.id);
                    };

                    $scope.sendEmail = function () {
                        var params = {
                            objectId: $scope.objectInfo.id,
                            objectType: ObjectService.ObjectTypes.DOC_REPO,
                            objectNumber: "",
                            emailSubject: 'Repository ' + $scope.objectInfo.id
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
                            emailData.objectId = $scope.objectInfo.id;
                            emailData.objectType = ObjectService.ObjectTypes.DOC_REPO;
                            emailData.objectNumber = "";
                            emailData.modelReferenceName = res.template;

                            if(emailData.modelReferenceName != 'plainEmail') {
                                EcmEmailService.sendManualEmail(emailData);
                            } else {
                                EcmEmailService.sendPlainEmail(emailData);
                            }

                        });
                    };

                } ]);
