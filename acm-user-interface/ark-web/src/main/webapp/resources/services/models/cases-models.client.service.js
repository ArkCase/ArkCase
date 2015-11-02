'use strict';

// Authentication service for user variables
angular.module('services').factory('CasesModelsService', ['$q', '$resource', 'CasesService', 'ConfigService', 'ValidationService', 'UtilService',
    function ($q, $resource, CasesService, ConfigService, Validator, Util) {
        return {
            x_no_longer_in_use_queryCasesTree: function () {
                var deferred = $q.defer();
                var configPromise = ConfigService.getModule({moduleId: 'cases'}).$promise.then(function(config){
                    return config;
                });
                var casesPromise = CasesService.queryCases().$promise.then(function(cases){
                    return cases;
                });

                $q.all([
                    configPromise,
                    casesPromise
                ]).then(function (responses) {
                        var config = responses[0];
                        var cases = responses[1];
                        var result = [];
                        if (cases && cases.response && _.isArray(cases.response.docs)) {
                            var docs = cases.response.docs;
                            _.forEach(docs, function(docItem){
                                var components = [];
                                _.forEach(config.components, function(componentItem){
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
            , getAssignee: function(caseFile) {
                var assignee = null;
                if (Validator.validateCaseFile(caseFile)) {
                    if (Util.isArray(caseFile.participants)) {
                        for (var i = 0; i < caseFile.participants.length; i++) {
                            var participant =  caseFile.participants[i];
                            if ("assignee" == participant.participantType) {
                                assignee = participant.participantLdapId;
                                break;
                            }
                        }
                    }
                }
                return assignee;
            }
            , setAssignee: function(caseFile, assignee) {
                if (caseFile) {
                    if (!Util.isArray(caseFile.participants)) {
                        caseFile.participants = [];
                    }

                    for (var i = 0; i < caseFile.participants.length; i++) {
                        if ("assignee" == caseFile.participants[i].participantType) {
                            caseFile.participants[i].participantLdapId = assignee;
                            return;
                        }
                    }

                    var participant = {};
                    participant.participantType = "assignee";
                    participant.participantLdapId = assignee;
                    caseFile.participants.push(participant);
                }
            }
            , getGroup: function(caseFile) {
                var group = null;
                if (Validator.validateCaseFile(caseFile)) {
                    if (Util.isArray(caseFile.participants)) {
                        for (var i = 0; i < caseFile.participants.length; i++) {
                            var participant =  caseFile.participants[i];
                            if ("owning group" == participant.participantType) {
                                group = participant.participantLdapId;
                                break;
                            }
                        }
                    }
                }
                return group;
            }
            , setGroup: function(caseFile, group) {
                if (caseFile) {
                    if (!Util.isArray(caseFile.participants)) {
                        caseFile.participants = [];
                    }

                    for (var i = 0; i < caseFile.participants.length; i++) {
                        if ("owning group" == caseFile.participants[i].participantType) {
                            caseFile.participants[i].participantLdapId = group;
                            return;
                        }
                    }

                    var participant = {};
                    participant.participantType = "owning group";
                    participant.participantLdapId = group;
                    caseFile.participants.push(participant);
                }
            }
        }
    }
]);