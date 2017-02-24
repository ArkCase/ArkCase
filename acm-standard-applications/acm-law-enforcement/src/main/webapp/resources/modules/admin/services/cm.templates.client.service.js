/**
 * Created by nebojsha on 11/20/2015.
 */

'use strict';

/**
 * @ngdoc service
 * @name admin.service:Admin.CMTemplatesService
 *
 * @description
 *
 * {@link https://gitlab.armedia.com/arkcase/ACM3/tree/develop/acm-standard-applications/acm-law-enforcement/src/main/webapp/resources/modules/admin/services/cm.templates.client.service.js modules/admin/services/cm.templates.client.service.js}
 *
 * The Admin.DashboardConfigService provides correspondence Management calls functionality
 */
angular.module('admin').service('Admin.CMTemplatesService', ['$http', 'Upload',
    function ($http, Upload) {
        return ({
            retrieveTemplatesList: retrieveTemplatesList,
            retrieveActiveVersionTemplatesList : retrieveActiveVersionTemplatesList,
            retrieveQuerySelectList: retrieveQuerySelectList,
            downloadByFilename: downloadByFilename,
            uploadTemplate: uploadTemplate,
            uploadTemplateWithTimestamp: uploadTemplateWithTimestamp,
            getTemplateData: getTemplateData,
            getTemplateVersionData: getTemplateVersionData,
            saveTemplateData: saveTemplateData,
            deleteTemplate: deleteTemplate,
            deleteTemplateByIdAndVersion : deleteTemplateByIdAndVersion
        });

        /**
         * @ngdoc method
         * @name retrieveTemplatesList
         * @methodOf admin.service:Admin.CMTemplatesService
         *
         * @description
         * Performs retrieving correspondence management templates.
         *
         * @returns {HttpPromise} Future info about widgets
         */
        function retrieveTemplatesList() {
            return $http({
                method: "GET",
                url: "api/latest/plugin/admin/templates",
                cache: false
            });
        };

        /**
         * @ngdoc method
         * @name retrieveActiveVersionTemplatesList
         * @methodOf admin.service:Admin.CMTemplatesService
         *
         * @description
         * Performs retrieving correspondence management active version templates.
         *
         * @returns {HttpPromise} Future info about widgets
         */
        function retrieveActiveVersionTemplatesList() {
            return $http({
                method: "GET",
                url: "api/latest/plugin/admin/templates/active",
                cache: false
            });
        };
        
        /**
         * @ngdoc method
         * @name retrieveQuerySelectList
         * @methodOf admin.service:Admin.CMTemplatesService
         *
         * @description
         * Performs retrieving correspondence management templates query select list.
         *
         * @param {string} objectType Object Type
         *
         * @returns {HttpPromise} Future info about widgets
         */
        function retrieveQuerySelectList(objectType) {
            return $http({
                method: "GET",
                url: 'api/latest/plugin/admin/queries/' + objectType
            });
        };

        /**
         * @ngdoc method
         * @name getTemplateData
         * @methodOf admin.service:Admin.CMTemplatesService
         *
         * @description
         * Get query and mapped fields for template.
         *
         * @param {string} fileName FileName
         *
         * @returns {HttpPromise} Future info about widgets
         */
        function getTemplateData(fileName) {
            return $http({
                method: "GET",
                url: 'api/latest/plugin/admin/template/' + fileName
            });
        };

        /**
         * @ngdoc method
         * @name getTemplateData
         * @methodOf admin.service:Admin.CMTemplatesService
         *
         * @description
         * Get query and mapped fields for template.
         *
         * @param {string} templateId Id of template
         * @param {string} fileName File name of template
         *
         * @returns {HttpPromise} Future info about widgets
         */
        function getTemplateData(templateId, fileName) {
            return $http({
                method: "GET",
                url: 'api/latest/plugin/admin/template/' + templateId + '/' + fileName
            });
        };        

        /**
         * @ngdoc method
         * @name getTemplateVersionData
         * @methodOf admin.service:Admin.CMTemplatesService
         *
         * @description
         * Get query and mapped fields for template.
         *
         * @param {string} templateId Id of template
         *
         * @returns {HttpPromise} Future info about widgets
         */
        function getTemplateVersionData(templateId) {
            return $http({
                method: "GET",
                url: 'api/latest/plugin/admin/template/versions/' + templateId
            });
        };        
        
        /**
         * @ngdoc method
         * @name saveTemplateData
         * @methodOf admin.service:Admin.CMTemplatesService
         *
         * @description
         * Saving query and mapped fields for template.
         *
         * @param {object} template Contains template data
         * @returns {HttpPromise} Future info about widgets
         */
        function saveTemplateData(template) {
            return $http({
                method: "PUT",
                url: 'api/latest/plugin/admin/template',
                data: template
            });
        };

        /**
         * @ngdoc method
         * @name deleteTemplateData
         * @methodOf admin.service:Admin.CMTemplatesService
         *
         * @description
         * Delete template.
         *
         * @param {string} templateId Id of template
         * @returns {HttpPromise} Future info about widgets
         */
        function deleteTemplate(templateId) {
            return $http({
                method: "DELETE",
                url: 'api/latest/plugin/admin/template/' + templateId
            });
        };

        /**
         * @ngdoc method
         * @name deleteTemplateByIdAndFilename
         * @methodOf admin.service:Admin.CMTemplatesService
         *
         * @description
         * Saving query and mapped fields for template.
         *
         * @param {string} templateId Id of template
         * @param {string} templateVersion File name of template
         * @returns {HttpPromise} Future info about widgets
         */
        function deleteTemplateByIdAndVersion(templateId, templateVersion) {
            return $http({
                method: "DELETE",
                url: 'api/latest/plugin/admin/template/' + templateId + '/' + templateVersion
            });
        };
        
        /**
         * @ngdoc method
         * @name fullDownloadPath
         * @methodOf admin.service:Admin.CMTemplatesService
         *
         * @description
         * Performs get full path for download
         *
         * @param {String} path file name
         *
         * @returns {String} full download path
         */
        function downloadByFilename(downloadFileName) {
            return 'api/latest/plugin/admin/template?downloadFileName=' + downloadFileName;
        };

        /**
         * @ngdoc method
         * @name uploadTemplate
         * @methodOf admin.service:Admin.CMTemplatesService
         *
         * @description
         * Uploads correspondence management template
         *
         * @param {array} files array of files
         *
         * @returns {HttpPromise} Future info about file upload
         */
        function uploadTemplate(files) {
            return Upload.upload({
                url: 'api/latest/plugin/admin/template',
                file: files
            });
        };

        /**
         * @ngdoc method
         * @name uploadTemplateTimestamp
         * @methodOf admin.service:Admin.CMTemplatesService
         *
         * @description
         * Uploads correspondence management template with timestamp name
         *
         * @param {array} files array of files
         *
         * @returns {HttpPromise} Future info about file upload
         */
        function uploadTemplateWithTimestamp(files) {
            return Upload.upload({
                url: 'api/latest/plugin/admin/template/timestamp',
                file: files
            });
        };
    }]);
