'use strict';

angular.module('cases').controller('CasesListController', ['$scope', '$state', '$stateParams', 'UtilService', 'ValidationService', 'StoreService', 'CasesService', 'ConfigService', 'CasesModelsService',
    function ($scope, $state, $stateParams, Util, Validator, Store, CasesService, ConfigService, CasesModelsService) {
        Util.serviceCall({
            service: ConfigService.getModule
            , param: {moduleId: 'cases'}
            , onSuccess: function (data) {
                if (Validator.validateConfigComponents(data)) {
                    var config = data;
                    $scope.treeConfig = config.tree;
                    $scope.componentsConfig = config.components;
                    return config;
                }
            }
        });

        var cache = new Store.CacheFifo();
        $scope.onLoad = function (start, n, sort, filters) {
            var cacheKey = start + "." + n + "." + sort + "." + filters;
            var treeData = cache.get(cacheKey);

            var param = {};
            param.start = start;
            param.n = n;
            param.sort = sort;
            param.filters = filters;
            return Util.serviceCall({
                service: CasesService.queryCases
                , param: param
                , result: treeData
                , onSuccess: function (data) {
                    if (Validator.validateSolrData(data)) {
                        treeData = {docs: [], total: data.response.numFound};
                        var docs = data.response.docs;
                        _.forEach(docs, function (doc) {
                            treeData.docs.push({
                                nodeId: Util.goodValue(doc.object_id_s, 0)
                                , nodeType: Util.Constant.OBJTYPE_CASE_FILE
                                , nodeTitle: Util.goodValue(doc.title_parseable)
                                , nodeToolTip: Util.goodValue(doc.title_parseable)
                            });
                        });
                        cache.put(cacheKey, treeData);
                        return treeData;
                    }
                }
            }).then(
                function (treeData) {
                    $scope.treeData = treeData;
                    return treeData;
                }
                , function (errorData) {
                    //$scope.treeData = {total: 0, docs: []};
                    return errorData;
                }
            );
        };

        $scope.onSelect = function (selectedCase) {
            $scope.$emit('req-select-case', selectedCase);
            var components = Util.goodArray(selectedCase.components);
            var componentType = (1 == components.length) ? components[0] : "main";
            $state.go('cases.' + componentType, {
                id: selectedCase.nodeId
            });
        };
    }
]);