'use strict';

/**
 * @ngdoc service
 * @name services:Helper.ModulesServices
 *
 * @description
 *
 * {@link https://***REMOVED***/arkcase/ACM3/tree/develop/acm-standard-applications/acm-law-enforcement/src/main/webapp/resources/services/helper/helper-dashboard.client.service.js services/helper/helper-dashboard.client.service.js}

 * Helper.ModulesServices provides modules service functions structure
 */
angular.module('services').factory(
        'Helper.ModulesServicesStructure',
        [ '$timeout', '$translate', 'UtilService', 'ConfigService', 'TimeTracking.InfoService',
                function($timeout, $translate, Util, ConfigService, TimeTrackingInfoService) {

                    var Service = {};

                    /**
                     * @ngdoc method
                     * @name getModulesServiceStructure
                     * @methodOf services:Helper.ModulesServices
                     *
                     * @description
                     * addLocales() adds all locale support from dashboard config file.
                     */
                    Service.getModulesServiceStructure = function() {
                        return [ {
                            name : "TIMESHEET",
                            configName : "time-tracking",
                            getInfo : TimeTrackingInfoService.getTimesheetInfo,
                            validateInfo : TimeTrackingInfoService.validateTimesheet
                        } ];
                    };

                    return Service;
                } ]);
