'use strict';

/**
 * @ngdoc service
 * @name services:Helper.ModulesServices
 *
 * @description
 *
 * {@link /acm-standard-applications/arkcase/src/main/webapp/resources/services/helper/helper-dashboard.client.service.js services/helper/helper-dashboard.client.service.js}

 * Helper.ModulesServices provides modules service functions structure
 */
angular.module('services').factory(
        'Helper.ModulesServicesStructure',
        [ '$timeout', '$translate', 'UtilService', 'ConfigService', 'TimeTracking.InfoService', 'ObjectService', 'Case.InfoService', 'Complaint.InfoService', 'Person.InfoService', 'Organization.InfoService',
                function($timeout, $translate, Util, ConfigService, TimeTrackingInfoService, ObjectService, CaseInfoService, ComplaintInfoService, PersonInfoService, OrganizationInfoService) {

                    var Service = {};

                    /**
                     * @ngdoc method
                     * @name getModulesServiceStructure
                     * @methodOf services:Helper.ModulesServices
                     *
                     * @description
                     * getModulesServiceStructure() get modules service structure to be reused.
                     */
                    Service.getModulesServiceStructure = function() {
                        return [ {
                            name: ObjectService.ObjectTypes.TIMESHEET,
                            configName: "time-tracking",
                            getInfo: TimeTrackingInfoService.getTimesheetInfo,
                            validateInfo: TimeTrackingInfoService.validateTimesheet
                        }, {
                            name: ObjectService.ObjectTypes.CASE_FILE,
                            configName: "time-tracking",
                            getInfo: CaseInfoService.getCaseInfo,
                            validateInfo: TimeTrackingInfoService.validateCaseInfo
                        }, {
                            name: ObjectService.ObjectTypes.COMPLAINT,
                            configName: "time-tracking",
                            getInfo: ComplaintInfoService.getComplaintInfo,
                            validateInfo: TimeTrackingInfoService.validateComplaintInfo
                        }, {
                            name: ObjectService.ObjectTypes.PERSON,
                            configName: "people",
                            getInfo: PersonInfoService.getPersonInfo,
                            validateInfo: PersonInfoService.validatePersonInfo
                        }, {
                            name: ObjectService.ObjectTypes.ORGANIZATION,
                            configName: "organizations",
                            getInfo: OrganizationInfoService.getOrganizationInfo,
                            validateInfo: OrganizationInfoService.validateOrganizationInfo
                        } ];
                    };

                    return Service;
                } ]);
