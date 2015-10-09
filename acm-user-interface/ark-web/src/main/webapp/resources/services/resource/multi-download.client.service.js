'use strict';

// Authentication service for user variables
angular.module('services').factory('MultiDownloadService', [
    function () {
        return {

            /**
             * Performs multidownload of files
             * This service uses multidownload library public/lib/multi-download/browser.js
             * TODO Create angular style multidownload functionality instead of this library usage
             * @param files list of files URLs
             */
            multiDownload: function (files) {
                multiDownload(files);
            }
        }
    }
]);