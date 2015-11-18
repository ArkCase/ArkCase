'use strict';

/**
 * @ngdoc service
 * @name cost-tracking.service:CallCostTrackingService
 *
 * @description
 *
 * {@link https://github.com/Armedia/ACM3/blob/develop/acm-user-interface/ark-web/src/main/webapp/resources/modules/cost-tracking/services/call-cost-tracking.client.service.js modules/cost-tracking/services/call-cost-tracking.client.service.js}

 * CallCostTrackingService contains functions of CostTracking service to support default error handling and data validation.
 */
angular.module('services').factory('CallCostTrackingService', ['$resource', '$translate', 'UtilService', 'ValidationService', 'CostTrackingService', 'ConstantService',
    function ($resource, $translate, Util, Validator, CostTrackingService, Constant) {
        var ServiceCall = {

            /**
             * @ngdoc method
             * @name queryCostTrackingTreeData
             * @methodOf cost-tracking.service:CallCostTrackingService
             *
             * @description
             * Query list of costsheets from SOLR and pack result for Object Tree.
             *
             * @param {String} userId  String that contains logged user
             * @param {Number} start  Zero based index of result starts from
             * @param {Number} n max Number of list to return
             * @param {String} sort  Sort value. Allowed choice is based on backend specification
             *
             *
             * @returns {Object} Promise
             */
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

            /**
             * @ngdoc method
             * @name getCostTrackingInfo
             * @methodOf cost-tracking.service:CallCostTrackingService
             *
             * @description
             * Query costsheet data
             *
             * @param {Number} id  Costsheet ID
             *
             * @returns {Object} Promise
             */
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

            /**
             * @ngdoc method
             * @name saveCostsheetInfo
             * @methodOf cost-tracking.service:CallCostTrackingService
             *
             * @description
             * Save costsheet data
             *
             * @param {Object} costsheetInfo  Costsheet data
             *
             * @returns {Object} Promise
             */
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

            /**
             * @ngdoc method
             * @name validateCostsheet
             * @methodOf cost-tracking.service:CallCostTrackingService
             *
             * @description
             * Validate costsheet
             *
             * @param {Object} data  Data to be validated
             *
             * @returns {Boolean} Return true if data is valid
             */
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

