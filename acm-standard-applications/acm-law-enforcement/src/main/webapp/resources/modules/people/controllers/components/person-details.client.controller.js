'use strict';

angular.module('people').controller(
        'People.DetailsController',
        [ '$scope', '$stateParams', '$translate', 'UtilService', 'ConfigService', 'Person.InfoService', 'MessageService', 'Helper.ObjectBrowserService', 'Mentions.Service', 'ObjectService', '$timeout',
                function($scope, $stateParams, $translate, Util, ConfigService, PersonInfoService, MessageService, HelperObjectBrowserService, MentionsService, ObjectService, $timeout) {

                    new HelperObjectBrowserService.Component({
                        scope: $scope,
                        stateParams: $stateParams,
                        moduleId: "people",
                        componentId: "details",
                        retrieveObjectInfo: PersonInfoService.getPersonInfo,
                        validateObjectInfo: PersonInfoService.validatePersonInfo
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

                    $scope.init = function() {
                        PermissionsService.getActionPermission('editPerson', $scope.objectInfo, {
                            objectType: ObjectService.ObjectTypes.PERSON
                        }).then(function(result) {
                            if (!result) {
                                $timeout(function() {
                                    $scope.editor.summernote('disable');
                                }, 0);
                            }
                        });
                    };

                    $scope.saveDetails = function() {
                        var promiseSaveInfo = Util.errorPromise($translate.instant("common.service.error.invalidData"));
                        if (PersonInfoService.validatePersonInfo($scope.objectInfo)) {
                            var objectInfo = Util.omitNg($scope.objectInfo);
                            promiseSaveInfo = PersonInfoService.savePersonInfo(objectInfo);
                            promiseSaveInfo.then(function(objectInfo) {
                                $scope.$emit("report-object-updated", objectInfo);
                                MessageService.info($translate.instant("people.comp.details.informSaved"));
                                MentionsService.sendEmailToMentionedUsers($scope.emailAddresses, $scope.usersMentioned, ObjectService.ObjectTypes.PERSON, "DETAILS", objectInfo.id, objectInfo.details);
                                return objectInfo;
                            }, function(error) {
                                $scope.$emit("report-object-update-failed", error);
                                return error;
                            });
                        }
                        return promiseSaveInfo;
                    }
                } ]);