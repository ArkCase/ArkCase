'use strict';

angular.module('services').factory('CallTimeTrackingService', ['$resource', '$translate', 'StoreService', 'UtilService', 'ValidationService', 'TimeTrackingService', 'ConstantService',
    function ($resource, $translate, Store, Util, Validator, TimeTrackingService, Constant) {
        var ServiceCall = {
             queryTimeTrackingTreeData: function (userId, start, n, sort) {
                var treeData = null;

                var param = {};
                param.userId = userId;
                param.start = start;
                param.n = n;
                param.sort = sort;

                return Util.serviceCall({
                    service: TimeTrackingService.listObjects
                    , param: param
                    , onSuccess: function (data) {
                        if (Validator.validateSolrData(data)) {
                            treeData = {docs: [], total: data.response.numFound};
                            var docs = data.response.docs;
                            _.forEach(docs, function (doc) {
                                treeData.docs.push({
                                    nodeId: Util.goodValue(doc.object_id_s, 0)
                                    , nodeType: Constant.ObjectTypes.TIMESHEET
                                    , nodeTitle: Util.goodValue(doc.name)
                                    , nodeToolTip: Util.goodValue(doc.name)
                                });
                            });
                            return treeData;
                        }
                    }
                });
            }
            , getTimeTrackingInfo: function (id) {
                return Util.serviceCall({
                    service: TimeTrackingService.get
                    , param: {id: id}
                    , onSuccess: function (data) {
                        if (Validator.validateTimesheet(data)) {
                            return data;
                        }
                    }
                });
            }
            , saveTimesheetInfo: function (timesheetInfo) {
                if (!ServiceCall.validateTimesheet(timesheetInfo)) {
                    return Util.errorPromise($translate.instant("common.service.error.invalidData"));
                }
                return Util.serviceCall({
                    service: TimeTrackingService.save
                    , data: timesheetInfo
                    , onSuccess: function (data) {
                        if (ServiceCall.validateTimesheet(data)) {
                            return data;
                        }
                    }
                });
            }
            , validateTimesheet: function(data) {
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
                if (Util.isEmpty(data.startDate)) {
                    return false;
                }
                if (Util.isEmpty(data.endDate)) {
                    return false;
                }
                if (!Util.isArray(data.times)) {
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
