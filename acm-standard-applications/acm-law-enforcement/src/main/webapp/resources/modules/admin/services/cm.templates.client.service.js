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
 * {@link https://***REMOVED***/arkcase/ACM3/tree/develop/acm-standard-applications/acm-law-enforcement/src/main/webapp/resources/modules/admin/services/cm.templates.client.service.js modules/admin/services/cm.templates.client.service.js}
 *
 * The Admin.DashboardConfigService provides correspondence Management calls functionality
 */
angular.module('admin').service('Admin.CMTemplatesService', ['$http', 'Upload',
    function ($http, Upload) {
        return ({
            retrieveTemplatesList: retrieveTemplatesList,
            retrieveQuerySelectList: retrieveQuerySelectList,
            fullDownloadPath: fullDownloadPath,
            uploadTemplate: uploadTemplate,
            saveTemplateData: saveTemplateData,
            deleteTemplate: deleteTemplate
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
                url: "api/latest/plugin/admin/template/list"
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
         * @name saveTemplateData
         * @methodOf admin.service:Admin.CMTemplatesService
         *
         * @description
         * Saving query and mapped fields for template.
         *
         * @returns {HttpPromise} Future info about widgets
         */
        function saveTemplateData(template) {
            return $http({
                method: "POST",
                url: 'api/latest/plugin/admin/template/',
                data: template
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
         * @returns {HttpPromise} Future info about widgets
         */
        function deleteTemplate(fileName) {
            return $http({
                method: "DELETE",
                url: 'api/latest/plugin/admin/template',
                data: {fileName: fileName}
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
         * @param {String} path file path
         *
         * @returns {String} full download path
         */
        function fullDownloadPath(path) {
            return 'api/latest/plugin/admin/template?filePath=' + path;
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
    }]);
