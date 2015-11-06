'use strict';

angular.module('complaints').controller('ComplaintsListController', ['$scope', '$state', '$stateParams', 'UtilService', 'ValidationService', 'StoreService', 'HelperService', 'ComplaintsService', 'ConfigService',
    function ($scope, $state, $stateParams, Util, Validator, Store, Helper, ComplaintsService, ConfigService) {
        var cacheComplaintsConfig = new Store.SessionData(Helper.SessionCacheNames.COMPLAINTS_CONFIG);
        var config = cacheComplaintsConfig.get();
        var promiseGetModule = Util.serviceCall({
            service: ConfigService.getModule
            , param: {moduleId: 'complaints'}
            , result: config
            , onSuccess: function (data) {
                if (Validator.validateComplaintsConfig(data)) {
                    config = data;
                    cacheComplaintsConfig.set(config);
                    return config;
                }
            }
        }).then(
            function (config) {
                $scope.treeConfig = config.tree;
                $scope.componentsConfig = config.components;
                return config;
            }
        );

        var cacheComplaintList = new Store.CacheFifo(Helper.CacheNames.COMPLAINT_LIST);
        $scope.onLoad = function (start, n, sort, filters) {
            var cacheKey = start + "." + n + "." + sort + "." + filters;
            var treeData = cacheComplaintList.get(cacheKey);

            var param = {};
            param.start = start;
            param.n = n;
            param.sort = sort;
            param.filters = filters;
            return Util.serviceCall({
                service: ComplaintsService.queryComplaints
                , param: param
                , result: treeData
                , onSuccess: function (data) {
                    if (Validator.validateSolrData(data)) {
                        treeData = {docs: [], total: data.response.numFound};
                        var docs = data.response.docs;
                        _.forEach(docs, function (doc) {
                            treeData.docs.push({
                                nodeId: Util.goodValue(doc.object_id_s, 0)
                                , nodeType: Helper.ObjectTypes.COMPLAINT
                                , nodeTitle: Util.goodValue(doc.title_parseable)
                                , nodeToolTip: Util.goodValue(doc.title_parseable)
                            });
                        });
                        cacheComplaintList.put(cacheKey, treeData);
                        return treeData;
                    }
                }
            }).then(
                function (treeData) {
                    $scope.treeData = treeData;
                    return treeData;
                }
            );
        };

        $scope.onSelect = function (selectedComplaint) {
            $scope.$emit('req-select-complaint', selectedComplaint);
            var components = Util.goodArray(selectedComplaint.components);
            var componentType = (1 == components.length) ? components[0] : "main";
            //$state.go('complaints.' + componentType, {
            //    id: selectedComplaint.nodeId
            //});
        };
    }
]);