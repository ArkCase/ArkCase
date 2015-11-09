'use strict';

/**
 * @ngdoc service
 * @name services.service:CallCasesService
 *
 * @description
 *
 * {@link https://github.com/Armedia/ACM3/blob/develop/acm-user-interface/ark-web/src/main/webapp/resources/services/resource/call-cases.client.service.js services/resource/call-cases.client.service.js}

 * CallCasesService contains wrapper functions of CasesService to support default error handling, data validation and data cache.
 */
angular.module('services').factory('CallCasesService', ['$resource', 'StoreService', 'UtilService', 'ValidationService', 'CasesService', 'ConstantService',
    function ($resource, Store, Util, Validator, CasesService, Constant) {
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

                , CASE_CONFIG: "AcmCaseConfig"
                , CASE_TYPES: "AcmCaseTypes"
                , CASE_CORRESPONDENCE_FORMS: "AcmCaseCorrespondenceForms"

                , COMPLAINT_CONFIG: "AcmComplaintConfig"
                , COMPLAINT_TYPES: "AcmComplaintTypes"
                , COMPLAINT_CORRESPONDENCE_FORMS: "AcmComplaintCorrespondenceForms"

                , TASK_CONFIG: "AcmTaskConfig"
                , TASK_TYPES: "AcmTaskTypes"
                , TASK_CORRESPONDENCE_FORMS: "AcmTaskCorrespondenceForms"

            }
            , CacheNames: {
                CASE_LIST: "CaseList"
                , CASE_INFO: "CaseInfo"

                , MY_TASKS: "MyTasks"
                , CASE_HISTORY_DATA: "CaseHistoryData"
                , CASE_CORRESPONDENCE_DATA: "CaseCorrespondenceData"
                , CASE_NOTES: "CaseNotes"
                , CASE_COST_SHEETS: "CaseCostSheets"
                , CASE_TIME_SHEETS: "CaseTimeSheets"

                , COMPLAINT_LIST: "ComplaintList"
                , COMPLAINT_INFO: "ComplaintInfo"
                , COMPLAINT_HISTORY_DATA: "ComplaintHistoryData"
                , COMPLAINT_CORRESPONDENCE_DATA: "ComplaintCorrespondenceData"
                , COMPLAINT_NOTES: "ComplaintNotes"
                , COMPLAINT_COST_SHEETS: "ComplaintCostSheets"
                , COMPLAINT_TIME_SHEETS: "ComplaintTimeSheets"

                , TASK_LIST: "TaskList"
                , TASK_INFO: "TaskInfo"
                , TASK_HISTORY_DATA: "TaskHistoryData"
                , TASK_NOTES: "TaskNotes"
            }
            , queryCasesTreeData: function (start, n, sort, filters) {
                var cacheCaseList = new Store.CacheFifo(this.CacheNames.CASE_LIST);
                var cacheKey = start + "." + n + "." + sort + "." + filters;
                var treeData = cacheCaseList.get(cacheKey);

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
                                    , nodeType: Constant.ObjectTypes.CASE_FILE
                                    , nodeTitle: Util.goodValue(doc.title_parseable)
                                    , nodeToolTip: Util.goodValue(doc.title_parseable)
                                });
                            });
                            cacheCaseList.put(cacheKey, treeData);
                            return treeData;
                        }
                    }
                });
            }
            , getCaseInfo: function (id) {
                var cacheCaseInfo = new Store.CacheFifo(this.CacheNames.CASE_INFO);
                var caseInfo = cacheCaseInfo.get(id);
                return Util.serviceCall({
                    service: CasesService.get
                    , param: {id: id}
                    , result: caseInfo
                    , onSuccess: function (data) {
                        if (ServiceCall.validateCaseFile(data)) {
                            cacheCaseInfo.put(id, data);
                            return data;
                        }
                    }
                });
            }
            , validateCaseFile: function (data) {
                if (Util.isEmpty(data)) {
                    return false;
                }
                if (0 >= Util.goodValue(data.id), 0) {
                    return false;
                }
                if (Util.isEmpty(data.caseNumber)) {
                    return false;
                }
                if (!Util.isArray(data.childObjects)) {
                    return false;
                }
                if (!Util.isArray(data.milestones)) {
                    return false;
                }
                if (!Util.isArray(data.participants)) {
                    return false;
                }
                if (!Util.isArray(data.personAssociations)) {
                    return false;
                }
                if (!Util.isArray(data.references)) {
                    return false;
                }
                return true;
            }

        };

        return ServiceCall;
    }
]);
