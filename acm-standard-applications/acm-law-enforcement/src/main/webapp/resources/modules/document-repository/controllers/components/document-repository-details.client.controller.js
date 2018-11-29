'use strict';

angular.module('document-repository').controller(
        'DocumentRepository.DetailsController',
        [ '$scope', '$stateParams', '$translate', 'UtilService', 'ConfigService', 'DocumentRepository.InfoService', 'MessageService', 'Helper.ObjectBrowserService', 'ObjectService', 'Mentions.Service',
                function($scope, $stateParams, $translate, Util, ConfigService, DocumentRepositoryInfoService, MessageService, HelperObjectBrowserService, ObjectService, MentionsService) {

                    new HelperObjectBrowserService.Component({
                        scope: $scope,
                        stateParams: $stateParams,
                        moduleId: "document-repository",
                        componentId: "details",
                        retrieveObjectInfo: DocumentRepositoryInfoService.getDocumentRepositoryInfo,
                        validateObjectInfo: DocumentRepositoryInfoService.validateDocumentRepositoryInfo
                    });

                    // ---------------------   mention   ---------------------------------
                    $scope.emailAddresses = [];
                    $scope.usersMentioned = [];

                    // Obtains a list of all users in ArkCase
                    MentionsService.getUsers().then(function(users) {
                        $scope.people = [];
                        $scope.peopleEmails = [];
                        _.forEach(users, function(user) {
                            $scope.people.push(user.name);
                            $scope.peopleEmails.push(user.email_lcs);
                        });
                    });

                    $scope.options = {
                        focus: true,
                        dialogsInBody: true,
                        hint: {
                            mentions: $scope.people,
                            match: /\B@(\w*)$/,
                            search: function(keyword, callback) {
                                callback($.grep($scope.people, function(item) {
                                    return item.indexOf(keyword) == 0;
                                }));
                            },
                            content: function(item) {
                                var index = $scope.people.indexOf(item);
                                $scope.emailAddresses.push($scope.peopleEmails[index]);
                                $scope.usersMentioned.push('@' + item);
                                return '@' + item;
                            }
                        }
                    };
                    // -----------------------  end mention   ----------------------------

                    $scope.saveDetails = function() {
                        var docRepoInfo = Util.omitNg($scope.objectInfo);
                        DocumentRepositoryInfoService.saveDocumentRepository(docRepoInfo).then(function(docRepoInfo) {
                            MentionsService.sendEmailToMentionedUsers($scope.emailAddresses, $scope.usersMentioned, ObjectService.ObjectTypes.DOC_REPO, "DETAILS", docRepoInfo.id, docRepoInfo.details);
                            MessageService.info($translate.instant("document-repository.comp.details.informSaved"));
                            return docRepoInfo;
                        });
                    };
                } ]);