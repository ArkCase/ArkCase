'use strict';

/**
 * @ngdoc service
 * @name services.service:ConstantService
 *
 * @description
 *
 * {@link https://github.com/Armedia/ACM3/blob/develop/acm-user-interface/ark-web/src/main/webapp/resources/services/common/helper.client.service.js services/common/helper.client.service.js}
 *
 * This service package contains various commonly used miscellaneous help functions.
 */

angular.module('services').factory('ConstantService', [
    function () {
        var Constant = {
            ObjectTypes: {
                CASE_FILE: "CASE_FILE"
                , COMPLAINT: "COMPLAINT"
                , TASK: "TASK"
                , ADHOC_TASK: "ADHOC"
                , TIMESHEET: "TIMESHEET"
                , COSTSHEET: "COSTSHEET"
                , FILE: "FILE"
            }
            , Lookups: {
                USER_FULL_NAMES: "userFullNames"
                , PERSON_TYPES: "personTypes"
                , PARTICIPANT_TYPES: "participantTypes"
                , PARTICIPANT_NAMES: "participantNames"
                , TASK_OUTCOMES: "taskOutcomes"
                , CONTACT_METHODS_TYPES: "contactMethodTypes"
                , ORGANIZATION_TYPES: "organizationTypes"
                , ADDRESS_TYPES: "addressTypes"
                , ALIAS_TYPES: "aliasTypes"
                , SECURITY_TAG_TYPES: "securityTagTypes"
            }
            , SessionCacheNames: {
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
                MY_TASKS: "MyTasks"
                //, CASE_LIST: "CaseList"
                //, CASE_INFO: "CaseInfo"
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

        };
        return Constant;
    }
]);