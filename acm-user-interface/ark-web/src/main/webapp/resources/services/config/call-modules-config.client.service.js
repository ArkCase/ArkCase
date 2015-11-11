'use strict';

/**
 * @ngdoc service
 * @name services.service:CallConfigService
 *
 * @description
 *
 * {@link https://github.com/Armedia/ACM3/blob/develop/acm-user-interface/ark-web/src/main/webapp/resources/services/resource/call-cases.client.service.js services/resource/call-cases.client.service.js}

 * CallConfigService contains wrapper functions of ConfigService to support default error handling, data validation and data cache.
 */
angular.module('services').factory('CallConfigService', ['$resource', 'StoreService', 'UtilService', 'ValidationService', 'ConfigService', 'ConstantService',
    function ($resource, Store, Util, Validator, ConfigService, Constant) {
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
                MODULE_CONFIG: "ModuleConfig"
            }
            , queryModules: function () {
                var cacheCaseList = new Store.CacheFifo(this.CacheNames.CASE_LIST);
                var cacheKey = start + "." + n + "." + sort + "." + filters;
                var treeData = cacheCaseList.get(cacheKey);

                var param = {};
                param.start = start;
                param.n = n;
                param.sort = sort;
                param.filters = filters;
                return Util.serviceCall({
                    service: ConfigService.queryConfig
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
            , getModuleConfig: function (moduleId) {
                var cacheModuleConfig = new Store.CacheFifo({name: this.CacheNames.MODULE_CONFIG, maxSize: 64});
                var moduleConfig = cacheModuleConfig.get(moduleId);
                return Util.serviceCall({
                    service: ConfigService.getModule
                    , param: {moduleId: moduleId}
                    , result: moduleConfig
                    , onSuccess: function (data) {
                        if (ServiceCall.validateModuleConfig(data, moduleId)) {
                            var config = data;
                            cacheModuleConfig.put(moduleId, config);
                            return config;
                        }
                    }
                });
            }
            , validateModuleConfig: function (data, moduleId) {
                if (Util.isEmpty(data)) {
                    return false;
                }
                if (moduleId != Util.goodValue(data.id)) {
                    return false;
                }
                if (!Util.isArray(data.components)) {
                    return false;
                }
                return true;
            }

        };

        return ServiceCall;
    }
]);
