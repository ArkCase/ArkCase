/**
 * Created by nebojsha on 11/17/2015.
 */

'use strict';

/**
 * @ngdoc service
 * @name admin.services:Admin.FormWorkflowsLinkService
 *
 * @description
 *
 * {@link https://github.com/Armedia/ACM3/blob/develop/acm-user-interface/ark-web/src/main/webapp/resources/modules/admin/services/form_workflows.link.service.js modules/admin/services/form_workflows.link.service.js}
 *
 * The Admin.ReportsConfigService provides Form workflows link calls functionality
 */
angular.module('admin').service('Admin.FormWorkflowsLinkService', function ($http) {
    return ({
        getFormWorkflowsData: getFormWorkflowsData,
        saveData: saveData
    });
    /**
     * @ngdoc method
     * @name getReports
     * @methodOf admin.services:Admin.FormWorkflowsLinkService
     *
     * @description
     * Performs retrieving all data
     *
     * @returns {HttpPromise} Future info about Form Workflows Link data
     */
    function getFormWorkflowsData() {
        return $http({
            method: "GET",
            url: "proxy/arkcase/api/latest/plugin/admin/linkformsworkflows/configuration"
        });
    };

    /**
     * @ngdoc method
     * @name saveData
     * @methodOf admin.services:Admin.FormWorkflowsLinkService
     *
     * @description
     * Performs saving form workflow links.
     *
     * @param {object} data all data from the table
     *
     * @returns {HttpPromise} Future info about Form Workflows Link data saved
     */
    function saveData(reports) {
        return $http({
            method: "PUT",
            url: "proxy/arkcase/api/latest/plugin/admin/linkformsworkflows/configuration",
            data: angular.toJson(reports),
            headers: {
                "Content-Type": "application/json"
            }
        });
    };
});
