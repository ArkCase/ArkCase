'use strict';

/**
 * @ngdoc service
 * @name services:Solr.SearchService
 *
 * @description
 *
 * {@link https://github.com/Armedia/ACM3/blob/develop/acm-user-interface/ark-web/src/main/webapp/resources/services/solr/solr-search.client.service.js services/solr/solr-search.client.service.js}

 * SOLR search related functions
 */
angular.module('services').factory('Solr.SearchService', ["UtilService",
    function (Util) {
        return {

            /**
             * @ngdoc method
             * @name validateSolrData
             * @methodOf services:Solr.SearchService
             *
             * @description
             * Validate SOLR search data
             *
             * @param {Object} data  Data to be validated
             *
             * @returns {Boolean} Return true if data is valid
             */
            validateSolrData: function (data) {
                if (!data) {
                    return false;
                }
                if (Util.isEmpty(data.responseHeader) || Util.isEmpty(data.response)) {
                    return false;
                }
                if (Util.isEmpty(data.responseHeader.status)) {
                    return false;
                }
//            if (0 != responseHeader.status) {
//                return false;
//            }
                if (Util.isEmpty(data.responseHeader.params)) {
                    return false;
                }
                if (Util.isEmpty(data.responseHeader.params.q)) {
                    return false;
                }

                if (Util.isEmpty(data.response.numFound) || Util.isEmpty(data.response.start)) {
                    return false;
                }
                if (!Util.isArray(data.response.docs)) {
                    return false;
                }
                return true;
            }

        }
    }
]);