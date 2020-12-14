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
                        validateObjectInfo: PersonInfoService.validatePersonInfo,
                        onObjectInfoRetrieved: function(objectInfo) {
                            onObjectInfoRetrieved(objectInfo);
                        }
                    });

                    var onObjectInfoRetrieved = function(objectInfo) {
                        $scope.objectInfo = objectInfo;
                        $scope.permissions = {
                            actionName: 'editPerson',
                            objectProperties: objectInfo,
                            opts: {
                                objectType: ObjectService.ObjectTypes.PERSON
                            }
                        };
                    };

                    // ---------------------   mention   ---------------------------------
                    $scope.paramsSummernote = {
                        emailAddresses: [],
                        usersMentioned: [],
                    };

                    $scope.saveDetails = function() {
                        var promiseSaveInfo = Util.errorPromise($translate.instant("common.service.error.invalidData"));
                        if (PersonInfoService.validatePersonInfo($scope.objectInfo)) {
                            var objectInfo = Util.omitNg($scope.objectInfo);
                            promiseSaveInfo = PersonInfoService.savePersonInfo(objectInfo);
                            promiseSaveInfo.then(function(objectInfo) {
                                $scope.$emit("report-object-updated", objectInfo);
                                MessageService.info($translate.instant("people.comp.details.informSaved"));
                                MentionsService.sendEmailToMentionedUsers($scope.paramsSummernote.emailAddresses, $scope.paramsSummernote.usersMentioned, ObjectService.ObjectTypes.PERSON, "DETAILS", objectInfo.id, objectInfo.details);
                                return objectInfo;
                            }, function(error) {
                                $scope.$emit("report-object-update-failed", error);
                                return error;
                            });
                        }
                        return promiseSaveInfo;
                    }
                } ]);