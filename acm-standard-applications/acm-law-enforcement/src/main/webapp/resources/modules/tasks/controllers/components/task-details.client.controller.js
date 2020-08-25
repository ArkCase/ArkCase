'use strict';

angular.module('tasks').controller(
        'Tasks.DetailsController',
        [ '$scope', '$stateParams', '$translate', 'UtilService', 'ConfigService', 'Task.InfoService', 'MessageService',
            'Helper.ObjectBrowserService', 'Mentions.Service', 'ObjectService',
                function($scope, $stateParams, $translate, Util, ConfigService, TaskInfoService, MessageService, HelperObjectBrowserService,
                         MentionsService, ObjectService) {

                    new HelperObjectBrowserService.Component({
                        moduleId: "tasks",
                        componentId: "details",
                        scope: $scope,
                        stateParams: $stateParams,
                        retrieveObjectInfo: TaskInfoService.getTaskInfo,
                        validateObjectInfo: TaskInfoService.validateTaskInfo,
                        onObjectInfoRetrieved: function(objectInfo) {
                            onObjectInfoRetrieved(objectInfo);
                        }
                    });

                    var onObjectInfoRetrieved = function(objectInfo) {
                        $scope.objectInfo = objectInfo;
                    };

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
                        var taskInfo = Util.omitNg($scope.objectInfo);
                        TaskInfoService.saveTaskInfo(taskInfo).then(function(taskInfo) {
                            $scope.$emit("report-object-updated", taskInfo);
                            MentionsService.sendEmailToMentionedUsers($scope.emailAddresses, $scope.usersMentioned, ObjectService.ObjectTypes.TASK, "DETAILS", taskInfo.taskId, taskInfo.details);
                            MessageService.info($translate.instant("tasks.comp.details.informSaved"));
                            return taskInfo;
                        });
                    };

                } ]);