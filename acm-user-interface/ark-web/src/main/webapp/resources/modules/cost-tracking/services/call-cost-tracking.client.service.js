'use strict';

angular.module('services').factory('CallCostTrackingService', ['$resource', '$translate', 'UtilService', 'ValidationService', 'CostTrackingService', 'ConstantService',
    function ($resource, $translate, Util, Validator, CostTrackingService, Constant) {
        var ServiceCall = {

            queryCostTrackingTreeData: function (userId, start, n, sort) {
                var treeData = null;

                var param = {};
                param.userId = userId;
                param.start = start;
                param.n = n;
                param.sort = sort;

                return Util.serviceCall({
                    service: CostTrackingService.listObjects
                    , param: param
                    , onSuccess: function (data) {
                        if (Validator.validateSolrData(data)) {
                            treeData = {docs: [], total: data.response.numFound};
                            var docs = data.response.docs;
                            _.forEach(docs, function (doc) {
                                treeData.docs.push({
                                    nodeId: Util.goodValue(doc.object_id_s, 0)
                                    , nodeType: Constant.ObjectTypes.COSTSHEET
                                    , nodeTitle: Util.goodValue(doc.name)
                                    , nodeToolTip: Util.goodValue(doc.name)
                                });
                            });
                            return treeData;
                        }
                    }
                });
            }

            , getCostTrackingInfo: function (id) {
                return Util.serviceCall({
                    service: CostTrackingService.get
                    , param: {id: id}
                    , onSuccess: function (data) {
                        if (Validator.validateCostsheet(data)) {
                            return data;
                        }
                    }
                });
            }

            , saveCostsheetInfo: function (costsheetInfo) {
                if (!ServiceCall.validateCostsheet(costsheetInfo)) {
                    return Util.errorPromise($translate.instant("common.service.error.invalidData"));
                }
                return Util.serviceCall({
                    service: CostTrackingService.save
                    , data: costsheetInfo
                    , onSuccess: function (data) {
                        if (ServiceCall.validateCostsheet(data)) {
                            return data;
                        }
                    }
                });
            }

            ,validateCostsheet: function(data) {
                if (Util.isEmpty(data)) {
                    return false;
                }
                if (Util.isEmpty(data.id)) {
                    return false;
                }
                if (Util.isEmpty(data.user)) {
                    return false;
                }
                if (Util.isEmpty(data.user.userId)) {
                    return false;
                }
                if (Util.isEmpty(data.parentId)) {
                    return false;
                }
                if (Util.isEmpty(data.parentType)) {
                    return false;
                }
                if (Util.isEmpty(data.parentNumber)) {
                    return false;
                }
                if (Util.isEmpty(data.costs)) {
                    return false;
                }
                if (Util.isEmpty(data.status)) {
                    return false;
                }
                if (Util.isEmpty(data.creator)) {
                    return false;
                }
                return true;
            }
        };

        return ServiceCall;
    }
]);

