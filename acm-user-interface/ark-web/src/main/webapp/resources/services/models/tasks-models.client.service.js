'use strict';

// Authentication service for user variables
angular.module('services').factory('TasksModelsService', ['$q', '$resource', 'TasksService', 'ConfigService', 'ValidationService', 'UtilService',
    function ($q, $resource, TasksService, ConfigService, Validator, Util) {
        return {
            x_no_longer_in_use_queryTasksTree: function () {
                var deferred = $q.defer();
                var configPromise = ConfigService.getModule({moduleId: 'tasks'}).$promise.then(function (config) {
                    return config;
                });
                var tasksPromise = TasksService.queryTasks().$promise.then(function (tasks) {
                    return tasks;
                });

                $q.all([
                    configPromise,
                    tasksPromise
                ]).then(function (responses) {
                    var config = responses[0];
                    var tasks = responses[1];
                    var result = [];
                    if (tasks && tasks.response && _.isArray(tasks.response.docs)) {
                        var docs = tasks.response.docs;
                        _.forEach(docs, function (docItem) {
                            var components = [];
                            _.forEach(config.components, function (componentItem) {
                                if (componentItem.enabled) {
                                    components.push({
                                        key: docItem.object_id_s + componentItem.id,
                                        title: componentItem.title,
                                        id: docItem.object_id_s,
                                        type: componentItem.id
                                    });
                                }
                            });

                            result.push({
                                key: docItem.object_id_s,
                                title: docItem.title_parseable,
                                children: components,
                                id: docItem.object_id_s,
                                type: 'main'
                            });
                        });

                        //for (var i = 0; i < result.length; i++) {
                        //    result[i].nodeType = 'main';
                        //    result[i].actions = [];
                        //    for (var j = 0; j < config.components.length; j++) {
                        //        if (config.components[j].enabled) {
                        //            result[i].actions.push({
                        //                nodeType: config.components[j].id,
                        //                title_parseable: config.components[j].title,
                        //                parent: result[i]
                        //            });
                        //        }
                        //    }
                        //}
                    }
                    deferred.resolve(result);
                });

                return deferred.promise;
            }
            , getAssignee: function (task) {
                var assignee = null;
                if (Validator.validateTask(task)) {
                    if (Util.isArray(task.participants)) {
                        for (var i = 0; i < task.participants.length; i++) {
                            var participant = task.participants[i];
                            if ("assignee" == participant.participantType) {
                                assignee = participant.participantLdapId;
                                break;
                            }
                        }
                    }
                }
                return assignee;
            }
            , setAssignee: function (task, assignee) {
                if (task) {
                    if (!Util.isArray(task.participants)) {
                        task.participants = [];
                    }

                    for (var i = 0; i < task.participants.length; i++) {
                        if ("assignee" == task.participants[i].participantType) {
                            task.participants[i].participantLdapId = assignee;
                            return;
                        }
                    }

                    var participant = {};
                    participant.participantType = "assignee";
                    participant.participantLdapId = assignee;
                    task.participants.push(participant);
                }
            }
            , getGroup: function (task) {
                var group = null;
                if (Validator.validateTask(task)) {
                    if (Util.isArray(task.participants)) {
                        for (var i = 0; i < task.participants.length; i++) {
                            var participant = task.participants[i];
                            if ("owning group" == participant.participantType) {
                                group = participant.participantLdapId;
                                break;
                            }
                        }
                    }
                }
                return group;
            }
            , setGroup: function (task, group) {
                if (task) {
                    if (!Util.isArray(task.participants)) {
                        task.participants = [];
                    }

                    for (var i = 0; i < task.participants.length; i++) {
                        if ("owning group" == task.participants[i].participantType) {
                            task.participants[i].participantLdapId = group;
                            return;
                        }
                    }

                    var participant = {};
                    participant.participantType = "owning group";
                    participant.participantLdapId = group;
                    task.participants.push(participant);
                }
            }
        }
    }
]);