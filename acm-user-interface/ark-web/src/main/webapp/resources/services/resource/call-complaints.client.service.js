'use strict';

/**
 * @ngdoc service
 * @name services.service:CallComplaintsService
 *
 * @description
 *
 * {@link https://github.com/Armedia/ACM3/blob/develop/acm-user-interface/ark-web/src/main/webapp/resources/services/resource/call-complaints.client.service.js services/resource/call-complaints.client.service.js}

 * CallComplaintsService contains wrapper functions of ComplaintsService to support default error handling, data validation and data cache.
 */
angular.module('services').factory('CallComplaintsService', ['$resource', '$translate', 'StoreService', 'UtilService', 'ValidationService', 'ComplaintsService', 'ConstantService',
    function ($resource, $translate, Store, Util, Validator, ComplaintsService, Constant) {
        var ServiceCall = {
            SessionCacheNames: {
                USER_INFO: "AcmUserInfo"
                , USER_FULL_NAMES: "AcmUserFullNames"
                , USERS: "AcmUsers"
                , GROUPS: "AcmGroups"
                , PRIORITIES: "AcmPriorities"
                , OBJECT_TYPES: "AcmObjectTypes"
                , FILE_TYPES: "AcmFileTypes"
                , FORM_TYPES: "AcmFormTypes"
                , PARTICIPANT_TYPES: "AcmParticipantTypes"
                , PARTICIPANT_USERS: "AcmParticipantUsers"
                , PARTICIPANT_GROUPS: "AcmParticipantGroups"
                , PERSON_TYPES: "AcmPersonTypes"
                , CONTACT_METHOD_TYPES: "AcmContactMethodTypes"
                , ORGANIZATION_TYPES: "AcmOrganizationTypes"
                , ADDRESS_TYPES: "AcmAddressTypes"
                , ALIAS_TYPES: "AcmAliasTypes"
                , SECURITY_TAG_TYPES: "AcmSecurityTagTypes"

                , COMPLAINT_CONFIG: "AcmComplaintConfig"
                , COMPLAINT_TYPES: "AcmComplaintTypes"
                , COMPLAINT_CORRESPONDENCE_FORMS: "AcmComplaintCorrespondenceForms"

            }
            , CacheNames: {
                COMPLAINT_LIST: "ComplaintList"
                , COMPLAINT_INFO: "ComplaintInfo"

                , MY_TASKS: "MyTasks"

                , COMPLAINT_HISTORY_DATA: "ComplaintHistoryData"
                , COMPLAINT_CORRESPONDENCE_DATA: "ComplaintCorrespondenceData"
                , COMPLAINT_NOTES: "ComplaintNotes"
                , COMPLAINT_COST_SHEETS: "ComplaintCostSheets"
                , COMPLAINT_TIME_SHEETS: "ComplaintTimeSheets"
            }

            /**
             * @ngdoc method
             * @name queryComplaintsTreeData
             * @methodOf services.service:CallComplaintsService
             *
             * @description
             * Query list of complaints from SOLR, pack result for Object Tree.
             *
             * @param {Number} start  Zero based index of result starts from
             * @param {Number} n max Number of list to return
             * @param {String} sort  Sort value. Allowed choice is based on backend specification
             * @param {String} filters  Filter value. Allowed choice is based on backend specification
             *
             * @returns {Object} Promise
             */
            //, queryComplaintsTreeData: function (start, n, sort, filters) {
            //    var cacheComplaintList = new Store.CacheFifo(this.CacheNames.COMPLAINT_LIST);
            //    var cacheKey = start + "." + n + "." + sort + "." + filters;
            //    var treeData = cacheComplaintList.get(cacheKey);
            //
            //    var param = {};
            //    param.start = start;
            //    param.n = n;
            //    param.sort = sort;
            //    param.filters = filters;
            //    return Util.serviceCall({
            //        service: ComplaintsService.queryComplaints
            //        , param: param
            //        , result: treeData
            //        , onSuccess: function (data) {
            //            if (Validator.validateSolrData(data)) {
            //                treeData = {docs: [], total: data.response.numFound};
            //                var docs = data.response.docs;
            //                _.forEach(docs, function (doc) {
            //                    treeData.docs.push({
            //                        nodeId: Util.goodValue(doc.object_id_s, 0)
            //                        , nodeType: Constant.ObjectTypes.COMPLAINT
            //                        , nodeTitle: Util.goodValue(doc.title_parseable)
            //                        , nodeToolTip: Util.goodValue(doc.title_parseable)
            //                    });
            //                });
            //                cacheComplaintList.put(cacheKey, treeData);
            //                return treeData;
            //            }
            //        }
            //    });
            //}

            /**
             * @ngdoc method
             * @name getComplaintInfo
             * @methodOf services.service:CallComplaintsService
             *
             * @description
             * Query complaint data
             *
             * @param {Number} id  Complaint ID
             *
             * @returns {Object} Promise
             */
            //, getComplaintInfo: function (id) {
            //    var cacheComplaintInfo = new Store.CacheFifo(this.CacheNames.COMPLAINT_INFO);
            //    var complaintInfo = cacheComplaintInfo.get(id);
            //    return Util.serviceCall({
            //        service: ComplaintsService.get
            //        , param: {id: id}
            //        , result: complaintInfo
            //        , onSuccess: function (data) {
            //            if (ServiceCall.validateComplaintInfo(data)) {
            //                cacheComplaintInfo.put(id, data);
            //                return data;
            //            }
            //        }
            //    });
            //}

            /**
             * @ngdoc method
             * @name saveComplaintInfo
             * @methodOf services.service:CallComplaintsService
             *
             * @description
             * Save complaint data
             *
             * @param {Object} complaintInfo  Complaint data
             *
             * @returns {Object} Promise
             */
            //, saveComplaintInfo: function (complaintInfo) {
            //    if (!ServiceCall.validateComplaintInfo(complaintInfo)) {
            //        return Util.errorPromise($translate.instant("common.service.error.invalidData"));
            //    }
            //    return Util.serviceCall({
            //        service: ComplaintsService.save
            //        , data: complaintInfo
            //        , onSuccess: function (data) {
            //            if (ServiceCall.validateComplaintInfo(data)) {
            //                return data;
            //            }
            //        }
            //    });
            //}

            /**
             * @ngdoc method
             * @name validateComplaintInfo
             * @methodOf services.service:CallComplaintsService
             *
             * @description
             * Validate complaint data
             *
             * @param {Object} data  Data to be validated
             *
             * @returns {Boolean} Return true if data is valid
             */
            //, validateComplaintInfo: function (data) {
            //    if (Util.isEmpty(data)) {
            //        return false;
            //    }
            //    if (0 >= Util.goodValue(data.complaintId, 0)) {
            //        return false;
            //    }
            //    if (Util.isEmpty(data.complaintNumber)) {
            //        return false;
            //    }
            //    if (!Util.isArray(data.childObjects)) {
            //        return false;
            //    }
            //    if (!Util.isArray(data.participants)) {
            //        return false;
            //    }
            //    if (!Util.isArray(data.personAssociations)) {
            //        return false;
            //    }
            //    return true;
            //}

        };

        return ServiceCall;
    }
]);
