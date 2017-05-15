/**
 * Created by nebojsha on 11/17/2015.
 */

'use strict';

/**
 * @ngdoc service
 * @name admin.service:Admin.FormWorkflowsLinkService
 *
 * @description
 *
 * {@link https://gitlab.armedia.com/arkcase/ACM3/tree/develop/acm-standard-applications/acm-law-enforcement/src/main/webapp/resources/modules/admin/services/form-workflows.link.client.service.js modules/admin/services/form-workflows.link.client.service.js}
 *
 * The Admin.FormWorkflowsLinkService provides Form workflows link calls functionality
 */
angular.module('admin').service('Admin.FormWorkflowsLinkService', function ($http) {
    return ({
        getFormWorkflowsData: getFormWorkflowsData,
        saveData: saveData
    });
    /**
     * @ngdoc method
     * @name getReports
     * @methodOf admin.service:Admin.FormWorkflowsLinkService
     *
     * @description
     * Performs retrieving all data
     *
     * @returns {HttpPromise} Future info about Form Workflows Link data
     */
    function getFormWorkflowsData() {
        return $http({
            method: "GET",
            url: "api/latest/plugin/admin/linkformsworkflows/configuration"
        });
    };

    /**
     * @ngdoc method
     * @name saveData
     * @methodOf admin.service:Admin.FormWorkflowsLinkService
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
            url: "api/latest/plugin/admin/linkformsworkflows/configuration",
            data: reports,
            headers: {
                "Content-Type": "application/json"
            }
        });
    };
});
