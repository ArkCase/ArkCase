'use strict';

/**
 * @ngdoc service
 * @name services:DocumentPrintingService
 *
 * @description
 *
 * services:DocumentPrintingService contains function to invoke printing of documents
 */
angular.module('services').factory('DocumentPrintingService', [ '$resource', function($resource) {

    return $resource('', {}, {
        printDocuments: {
            method: 'GET',
            cache: false,
            url: 'api/latest/service/casefile/print/:caseFileIds',
            responseType: 'arraybuffer',
            transformResponse: function(data, headersGetter) {
                return {
                    data: data
                };
            }
        }
    });

} ]);