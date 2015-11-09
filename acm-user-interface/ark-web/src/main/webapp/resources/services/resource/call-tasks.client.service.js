'use strict';

/**
 * @ngdoc service
 * @name services.service:CallTasksService
 *
 * @description
 *
 * {@link https://github.com/Armedia/ACM3/blob/develop/acm-user-interface/ark-web/src/main/webapp/resources/services/resource/call-tasks.client.service.js services/resource/call-tasks.client.service.js}

 * CallTasksService contains wrapper functions of TasksService to support default error handling, data validation and data cache.
 */
angular.module('services').factory('CallTasksService', ['$resource', 'StoreService', 'UtilService', 'ValidationService', 'TasksService', 'ConstantService',
    function ($resource, Store, Util, Validator, TasksService, Constant) {
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

                , TASK_CONFIG: "AcmTaskConfig"
                , TASK_TYPES: "AcmTaskTypes"
                , TASK_CORRESPONDENCE_FORMS: "AcmTaskCorrespondenceForms"

            }
            , CacheNames: {
                TASK_LIST: "TaskList"
                , TASK_INFO: "TaskInfo"

                , MY_TASKS: "MyTasks"

                , TASK_HISTORY_DATA: "TaskHistoryData"
                , TASK_NOTES: "TaskNotes"
            }
            , queryTasksTreeData: function (start, n, sort, filters) {
                var cacheTaskList = new Store.CacheFifo(this.CacheNames.TASK_LIST);
                var cacheKey = start + "." + n + "." + sort + "." + filters;
                var treeData = cacheTaskList.get(cacheKey);

                var param = {};
                param.start = start;
                param.n = n;
                param.sort = sort;
                param.filters = filters;
                return Util.serviceCall({
                    service: TasksService.queryTasks
                    , param: param
                    , result: treeData
                    , onSuccess: function (data) {
                        if (Validator.validateSolrData(data)) {
                            treeData = {docs: [], total: data.response.numFound};
                            var docs = data.response.docs;
                            _.forEach(docs, function (doc) {
                                var nodeType = (Util.goodValue(doc.adhocTask_b, false)) ? Constant.ObjectTypes.ADHOC_TASK : Constant.ObjectTypes.TASK;

                                //jwu: for testing
                                if (doc.object_id_s == 9601) {
                                    nodeType = Constant.ObjectTypes.ADHOC_TASK;
                                }

                                treeData.docs.push({
                                    nodeId: Util.goodValue(doc.object_id_s, 0)
                                    , nodeType: nodeType
                                    , nodeTitle: Util.goodValue(doc.title_parseable)
                                    , nodeToolTip: Util.goodValue(doc.title_parseable)
                                });
                            });
                            cacheTaskList.put(cacheKey, treeData);
                            return treeData;
                        }
                    }
                });
            }
            , getTaskInfo: function (id) {
                var cacheTaskInfo = new Store.CacheFifo(this.CacheNames.TASK_INFO);
                var taskInfo = cacheTaskInfo.get(id);
                return Util.serviceCall({
                    service: TasksService.get
                    , param: {id: id}
                    , result: taskInfo
                    , onSuccess: function (data) {
                        if (ServiceCall.validateTask(data)) {
                            cacheTaskInfo.put(id, data);
                            return data;
                        }
                    }
                });
            }
            , validateTask: function (data) {
                if (Util.isEmpty(data)) {
                    return false;
                }
                if (Util.isEmpty(data.taskId)) {
                    return false;
                }
//            if (Util.isEmpty(data.id) || Util.isEmpty(data.caseNumber)) {
//             return false;
//             }
//             if (!Util.isArray(data.childObjects)) {
//             return false;
//             }
//             if (!Util.isArray(data.participants)) {
//             return false;
//             }
//             if (!Util.isArray(data.personAssociations)) {
//             return false;
//             }
                return true;
            }

        };

        return ServiceCall;
    }
]);
