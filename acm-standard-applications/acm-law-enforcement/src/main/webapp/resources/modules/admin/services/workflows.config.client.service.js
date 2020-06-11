/**
 * Created by nebojsha on 11/20/2015.
 */

'use strict';
/**
 * @ngdoc service
 * @name admin.service:Admin.WorkflowsConfigService
 *
 * @description
 *
 * {@link https://***REMOVED***/arkcase/ACM3/tree/develop/acm-standard-applications/acm-law-enforcement/src/main/webapp/resources/modules/admin/services/workflows.config.client.service.js modules/admin/services/workflows.config.client.service.js}
 *
 * The Admin.WorkflowsConfigService provides Workflows Config REST calls functionality
 */
angular.module('admin').service('Admin.WorkflowsConfigService', [ '$http', 'Upload', function($http, Upload) {
    return ({
        retrieveWorkflows: retrieveWorkflows,
        retrieveHistory: retrieveHistory,
        activate: activate,
        uploadDefinition: uploadDefinition,
        diagram: diagram,
        retrieveDeactivatedWorkflows: retrieveDeactivatedWorkflows,
        deactivate: deactivate
    });
    /**
     * @ngdoc method
     * @name retrieveWorkflows
     * @methodOf admin.service:Admin.WorkflowsConfigService
     *
     * @description
     * Performs retrieving all workflows
     *
     * @returns {HttpPromise} Future info about workflows
     */
    function retrieveWorkflows() {
        return $http({
            method: "GET",
            url: "api/latest/plugin/admin/workflowconfiguration/workflows"
        });
    }
    ;

    /**
     * @ngdoc method
     * @name retrieveHistory
     * @methodOf admin.service:Admin.WorkflowsConfigService
     *
     * @description
     * Retrieve workflow history
     *
     *
     * @param {string} key workflow key
     *
     * @param {string} version workflow version
     *
     * @returns {HttpPromise} Future info about workflow history
     */
    function retrieveHistory(key, version) {
        return $http({
            method: 'GET',
            url: 'api/latest/plugin/admin/workflowconfiguration/workflows/' + key + '/versions/' + version + '/history'
        });
    }
    ;

    /**
     * @ngdoc method
     * @name activate
     * @methodOf admin.service:Admin.WorkflowsConfigService
     *
     * @description
     * Activate workflow
     *
     *
     * @param {string} key workflow key
     *
     * @param {string} version workflow version
     *
     * @returns {HttpPromise} Future info about workflow activation
     */
    function activate(key, version) {
        return $http({
            method: 'PUT',
            url: 'api/latest/plugin/admin/workflowconfiguration/workflows/' + key + '/versions/' + version + '/active',
            data: {},
            headers: {
                'Content-Type': 'application/json'
            }
        });
    }
    ;

    /**
     * @ngdoc method
     * @name deactivate
     * @methodOf admin.service:Admin.WorkflowsConfigService
     *
     * @description
     * Deactivate workflow
     *
     *
     * @param {string} key workflow key
     *
     * @param {string} version workflow version
     *
     * @returns {HttpPromise} Future info about workflow deactivation
     */
    function deactivate(key, version) {
        return $http({
            method: 'PUT',
            url: 'api/latest/plugin/admin/workflowconfiguration/workflows/' + key + '/versions/' + version + '/inactive',
            data: {},
            headers: {
                'Content-Type': 'application/json'
            }
        });
    }
    ;

    /**
     * @ngdoc method
     * @name diagram
     * @methodOf admin.service:Admin.WorkflowsConfigService
     *
     * @description
     * Retrieve diagram
     *
     *
     * @param {string} deployment id of workflow
     *
     * @param {string} key workflow key
     *
     * @param {string} version workflow version
     *
     * @returns {HttpPromise} Workflow diagram
     */
    function diagram(deploymentId, key, version) {
        return $http({
            method: 'GET',
            url: 'api/latest/plugin/admin/workflowconfiguration/diagram/' + deploymentId + '/' + key + '/' + version,
            data: {},
            headers: {
                'Content-Type': 'application/json'
            }
        });
    }
    ;

    /**
     * @ngdoc method
     * @name uploadDefinition
     * @methodOf admin.service:Admin.WorkflowsConfigService
     *
     * @description
     * Performs upload the BPMN file
     *
     * @param {object} file file to be uploaded
     * @param {string} description description of the file
     *
     *
     * @returns {HttpPromise} Future info about file upload progress
     */
    function uploadDefinition(file, description) {
        return Upload.upload({
            url: 'api/latest/plugin/admin/workflowconfiguration/files',
            method: 'POST',
            params: {
                description: description
            },
            file: file,
        });
    };

    /**
     * @ngdoc method
     * @name retrieveDeactivatedWorkflows
     * @methodOf admin.service:Admin.WorkflowsConfigService
     *
     * @description
     * Performs retrieving all deactivated workflows
     *
     * @returns {HttpPromise} Future info about workflows
     */
    function retrieveDeactivatedWorkflows() {
        return $http({
            method: "GET",
            url: "api/latest/plugin/admin/workflowconfiguration/deactivated/workflows"
        });
    };

} ]);
