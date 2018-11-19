'use strict';

angular.module('document-repository').controller(
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
                'Mentions.Service',
                function($scope, $modal, $translate, $window, ConfigService, Util, Authentication, UserInfoService,
                        DocumentRepositoryInfoService, MessageService, ObjectService, modalParams, MentionsService) {

                    $scope.modalParams = modalParams;
                    $scope.docRepo = {};
                    $scope.loading = false;
                    var user = {};

                    // ---------------------   mention   ---------------------------------
                    $scope.emailAddressesName = [];
                    $scope.usersMentionedName = [];

                    // Obtains a list of all users in ArkCase
                    MentionsService.getUsers().then(function (users) {
                        $scope.people = users;
                    });

                    $scope.getMentionedUsers = function (item) {
                        $scope.emailAddressesName.push(item.email_lcs);
                        $scope.usersMentionedName.push('@' + item.name);
                        return '@' + item.name;
                    };

                    $scope.emailAddressesDescription = [];
                    $scope.usersMentionedDescription = [];

                    $scope.getMentionedUsersDescription = function (item) {
                        $scope.emailAddressesDescription.push(item.email_lcs);
                        $scope.usersMentionedDescription.push('@' + item.name);
                        return '@' + item.name;
                    };
                    // -----------------------  end mention   ----------------------------

                    Authentication.queryUserInfo().then(function(userInfo) {
                        user = userInfo;
                        $scope.assignee = {};
                        $scope.assignee.name = '';
                        $scope.owningGroup = {};
                        return userInfo;
                    });

                    ConfigService.getModuleConfig("document-repository").then(function(moduleConfig) {
                        $scope.userSearchConfig = _.find(moduleConfig.components, {
                            id : "userSearch"
                        });
                        return moduleConfig;
                    });

                    $scope.userOrGroupSearch = function() {
                        var params = {};
                        params.header = $translate.instant("document-repository.newRepository.searchUser");
                        params.config = $scope.userSearchConfig;

                        var modalInstance = $modal
                                .open({
                                    templateUrl : "modules/document-repository/views/components/document-repository-assignee-picker-search-modal.client.view.html",
                                    controller : [ '$scope', '$modalInstance', 'params', function($scope, $modalInstance, params) {
                                        $scope.modalInstance = $modalInstance;
                                        $scope.header = params.header;
                                        $scope.filter = params.config.userSearchFilter;
                                        $scope.extraFilter = params.config.userSearchFacetExtraFilter;
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
                            if (selected) {
                                var selectedObjectType = selected.masterSelectedItem.object_type_s;
                                if (selectedObjectType === 'USER') { // Selected user
                                    var selectedUser = selected.masterSelectedItem;
                                    var selectedGroup = selected.detailSelectedItems;

                                    $scope.assignee.object_id_s = selectedUser.object_id_s;
                                    $scope.assignee.name = selectedUser.name;
                                    if (selectedGroup) {
                                        $scope.owningGroup.participantLdapId = selectedGroup.object_id_s;
                                        $scope.owningGroup.name = selectedGroup.name;
                                    }

                                    return;
                                } else if (selectedObjectType === 'GROUP') { // Selected group
                                    var selectedUser = selected.detailSelectedItems;
                                    var selectedGroup = selected.masterSelectedItem;
                                    if (selectedUser) {
                                        $scope.assignee.object_id_s = selectedUser.object_id_s;
                                        $scope.assignee.name = selectedUser.name;
                                    }

                                    $scope.owningGroup.participantLdapId = selectedGroup.object_id_s;
                                    $scope.owningGroup.name = selectedGroup.name;

                                    return;
                                }
                            }
                        });
                    };

                    $scope.changeType = function() {
                        if ($scope.isPersonalDocRepo()) {
                            $scope.owningGroup.participantLdapId = '';
                            $scope.owningGroup.name = '';
                            $scope.assignee.name = user.fullName;
                            $scope.assignee.object_id_s = user.userId;
                        } else {
                            $scope.assignee.name = '';
                            $scope.assignee.object_id_s = '';
                            $scope.owningGroup.participantLdapId = '';
                            $scope.owningGroup.name = '';
                        }
                    };

                    $scope.saveNewDocumentRepository = function() {
                        $scope.docRepo.participants = [];
                        $scope.loading = true;
                        DocumentRepositoryInfoService.saveDocumentRepository($scope.docRepo).then(function(data) {
                            if ($scope.isPersonalDocRepo()) {
                                ObjectService.showObject(ObjectService.ObjectTypes.MY_DOC_REPO, data.id);
                            } else {
                                ObjectService.showObject(ObjectService.ObjectTypes.DOC_REPO, data.id);
                            }
                            MentionsService.sendEmailToMentionedUsers($scope.emailAddressesName, $scope.usersMentionedName,
                                ObjectService.ObjectTypes.DOC_REPO, ObjectService.ObjectTypes.DOC_REPO, data.id, data.name);
                            MentionsService.sendEmailToMentionedUsers($scope.emailAddressesDescription, $scope.usersMentionedDescription,
                                ObjectService.ObjectTypes.DOC_REPO, ObjectService.ObjectTypes.DOC_REPO, data.id, data.description);
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

                    $scope.isPersonalDocRepo = function() {
                        return $scope.docRepo.repositoryType === 'PERSONAL';
                    };

                    $scope.cancelModal = function() {
                        $scope.onModalDismiss();
                    };
                } ]);
