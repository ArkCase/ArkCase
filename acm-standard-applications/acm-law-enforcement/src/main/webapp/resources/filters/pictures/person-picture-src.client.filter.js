'use strict';
/**
 * @ngdoc filter
 * @name person-picture-src.filter:personPictureSrc
 *
 * @description
 *
 * {@link https://***REMOVED***/arkcase/ACM3/tree/develop/acm-standard-applications/acm-law-enforcement/src/main/webapp/resources/filters/pictures/person-picture-src.client.filter.js filters/pictures/person-picture-src.client.filter.js}
 *
 * @param {string} Image ID;
 *
 * personPictureSrc generate link to person image
 */
angular.module('filters').filter('personPictureSrc', function () {
    return function (input) {
        if (typeof(input) == "undefined") {
            //TODO: add link to default image if person don't have image uploaded
            return "";
        } else {
            return 'api/latest/plugin/ecm/download?ecmFileId=' + input + '&noCache=' + Math.round(Math.random() * 999999);
        }
    }
});