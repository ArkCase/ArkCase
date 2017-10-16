'use strict';

/**
 * @ngdoc service
 * @name services:DocTreeService
 *
 * @description
 *
 * {@link https://***REMOVED***/arkcase/ACM3/tree/develop/acm-standard-applications/acm-law-enforcement/src/main/webapp/resources/services/helper/helper-doc-tree.client.service.js services/helper/helper-doc-tree.client.service.js}
 *
 * This service contains functionality for Frevvo plain forms for doc-tree directive.
 */
angular.module('services').factory('DocTreeService', ['UtilService', 'Object.LookupService'
    , function (Util, ObjectLookupService) {
        return {

            /**
             * @ngdoc method
             * @name uploadFrevvoForm
             * @methodOf services:DocTreeService
             *
             * @param {String} type Type of frevvo plain form
             * @param {Integer} folderId Folder Id
             * @param {String} onCloseForm (Optional) Optional parametar for doc-tree
             * @param {String} objectType Object type
             * @param {String} fileTypes List of file types
             * @param {String} containerId Container Id for the file
             *
             * @description
             * This method generates Frevvo plain form url.
             */
            uploadFrevvoForm: function (type, folderId, onCloseForm, objectType, fileTypes, containerId) {
                if (objectType) {
                    var fileType = _.find(fileTypes, {type: type});
                    if (ObjectLookupService.validatePlainForm(fileType)) {
                        var data = "_data=(";

                        var url = fileType.url;
                        var urlParameters = fileType.urlParameters;
                        var parametersAsString = '';
                        for (var i = 0; i < urlParameters.length; i++) {
                            var key = urlParameters[i].name;
                            var value = '';
                            if (!Util.isEmpty(urlParameters[i].defaultValue)) {
                                value = this.silentReplace(urlParameters[i].defaultValue, "'", "_0027_");
                            } else if (!Util.isEmpty(urlParameters[i].keyValue)) {
                                var _value = _.get(objectType, urlParameters[i].keyValue);
                                if (!Util.isEmpty(_value)) {
                                    value = this.silentReplace(_value, "'", "_0027_");
                                }
                            }
                            value = encodeURIComponent(value);
                            parametersAsString += key + ":'" + Util.goodValue(value) + "',";
                        }
                        parametersAsString += "folderId:'" + folderId + "',";

                        //we will use same function for upload and edit form
                        //if it is edit we need containerId for the file and mode
                        if (containerId) {
                            parametersAsString += "containerId:'" + containerId + "',";
                            parametersAsString += "mode:'edit',";
                        }
                        data += parametersAsString;

                        url = url.replace("_data=(", data);
                        return url;
                    }
                }
            },

            silentReplace: function (value, replace, replacement) {
                if (!Util.isEmpty(value) && value.replace) {
                    value = value.replace(replace, replacement);
                }
                return value;
            }
        };
    }
]);