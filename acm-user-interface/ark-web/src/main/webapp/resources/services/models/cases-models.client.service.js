'use strict';

// Authentication service for user variables
angular.module('services').factory('CasesModelsService', ['$q', '$resource', 'CasesService', 'ConfigService',
    function ($q, $resource, CasesService, ConfigService) {
        return {
            queryCasesTree: function () {
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
        }
    }
]);