'use strict';

angular
        .module('document-repository')
        .controller(
                'DocumentRepository.NewDocumentRepositoryController',
                [
                        '$scope',
                        '$modal',
                        '$translate',
                        '$window',
                        'ConfigService',
                        'UtilService',
                        'Authentication',
                        'Profile.UserInfoService',
                        'DocumentRepository.InfoService',
                        'MessageService',
                        'ObjectService',
                        'modalParams',
                        function($scope, $modal, $translate, $window, ConfigService, Util, Authentication, UserInfoService,
                                DocumentRepositoryInfoService, MessageService, ObjectService, modalParams) {

                            $scope.modalParams = modalParams;
                            $scope.docRepo = {};
                            $scope.loading = false;
                            var user = {};
                            Authentication.queryUserInfo().then(function(userInfo) {
                                user = userInfo;
                                $scope.assignee = {};
                                $scope.assignee.name = userInfo.fullName;
                                $scope.assignee.object_id_s = userInfo.userId;
                                $scope.owningGroup = {};
                                UserInfoService.getUserInfo().then(function(data) {
                                    $scope.userGroups = data.groups;
                                });
                                return userInfo;
                            });

                            ConfigService.getModuleConfig("document-repository").then(function(moduleConfig) {
                                $scope.userSearchConfig = _.find(moduleConfig.components, {
                                    id : "userSearch"
                                });
                                return moduleConfig;
                            });

                            $scope.searchAssignee = function() {
                                var params = {};
                                params.header = $translate.instant("document-repository.newRepository.searchUser");
                                params.config = $scope.userSearchConfig;

                                var modalInstance = $modal
                                        .open({
                                            templateUrl : "modules/document-repository/views/components/document-repository-assignee-picker-search-modal.client.view.html",
                                            controller : [ '$scope', '$modalInstance', 'params', function($scope, $modalInstance, params) {
                                                $scope.modalInstance = $modalInstance;
                                                $scope.header = params.header;
                                                $scope.filter = params.config.searchFilter;
                                                $scope.config = params.config;
                                            } ],
                                            animation : true,
                                            size : 'lg',
                                            backdrop : 'static',
                                            resolve : {
                                                params : function() {
                                                    return params;
                                                }
                                            }
                                        });
                                modalInstance.result.then(function(selected) {
                                    if (!Util.isEmpty(selected)) {
                                        $scope.assignee = selected;
                                        $scope.userGroups = selected.groups_id_ss;
                                    }
                                });
                            };

                            $scope.changeType = function() {
                                if ($scope.isPersonalDocRepo()) {
                                    $scope.owningGroup.participantLdapId = '';
                                    $scope.assignee.name = user.fullName;
                                    $scope.assignee.object_id_s = user.userId;
                                }
                            };

                            $scope.saveNewDocumentRepository = function() {
                                setParticipants();
                                $scope.loading = true;
                                DocumentRepositoryInfoService.saveDocumentRepository($scope.docRepo).then(function(data) {
                                    if ($scope.isPersonalDocRepo()) {
                                        ObjectService.showObject(ObjectService.ObjectTypes.MY_DOC_REPO, data.id);
                                    } else {
                                        ObjectService.showObject(ObjectService.ObjectTypes.DOC_REPO, data.id);
                                    }
                                    $scope.onModalClose();
                                    $scope.loading = false;
                                }, function(error) {
                                    $scope.loading = false;
                                    if (error.data && error.data.message) {
                                        $scope.error = error.data.message;
                                    } else {
                                        MessageService.error(error);
                                    }
                                });
                            };

                            function setParticipants() {
                                $scope.docRepo.participants = [];
                                var assignee = {};
                                assignee.participantLdapId = $scope.assignee.object_id_s;
                                assignee.participantType = "assignee";
                                $scope.docRepo.participants.push(assignee);
                                if (!$scope.isPersonalDocRepo()) {
                                    var owningGroup = {};
                                    owningGroup.participantType = "owning group";
                                    owningGroup.participantLdapId = $scope.owningGroup.participantLdapId;
                                    $scope.docRepo.participants.push(owningGroup);
                                }
                            }

                            $scope.isPersonalDocRepo = function() {
                                return $scope.docRepo.repositoryType === 'PERSONAL';
                            };

                            $scope.cancelModal = function() {
                                $scope.onModalDismiss();
                            };
                        } ]);
