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
 * {@link https://github.com/Armedia/ACM3/blob/develop/acm-user-interface/ark-web/src/main/webapp/resources/modules/admin/services/cm.templates.client.service.js modules/admin/services/cm.templates.client.service.js}
 *
 * The Admin.DashboardConfigService provides correspondence Management calls functionality
 */
angular.module('admin').service('Admin.CMTemplatesService', ['$http', 'Upload',
    function ($http, Upload) {
        return ({
            retrieveTemplatesList: retrieveTemplatesList,
            fullDownloadPath: fullDownloadPath,
            uploadTemplate: uploadTemplate

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
                url: "proxy/arkcase//api/latest/plugin/admin/template/list"
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
            return 'proxy/arkcase/api/latest/plugin/admin/template?filePath=' + path;
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
                url: 'proxy/arkcase/api/latest/plugin/admin/template',
                file: files
            });
        };
    }]);
