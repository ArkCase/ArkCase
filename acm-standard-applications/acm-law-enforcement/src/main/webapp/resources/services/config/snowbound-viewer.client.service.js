'use strict';

/**
 * @ngdoc service
 * @name services.service:SnowboundService
 *
 * @description
 * {@link https://gitlab.armedia.com/arkcase/ACM3/tree/develop/acm-standard-applications/acm-law-enforcement/src/main/webapp/resources/services/config/snowbound-viewer.client.service.js services/config/snowbound-viewer.client.service.js}
 *
 * This service contains functionality for Snowbound Viewer management.
 */
angular.module('services').factory('SnowboundService', [
    function () {
        return {

            /**
             * @ngdoc method
             * @name extractViewerBaseUrl
             * @methodOf services.service:SnowboundService
             *
             * @param {JSON} data properties from the ecmFileService.properties configuration file
             *
             * @description
             * This method takes the configuration from ecmFileService.properties and
             * extracts the base part of the snowbound url (host/port, etc.)
             */
            extractViewerBaseUrl: function (data) {
                var viewerUrl = "";
                if (data && data["ecm.viewer.snowbound"]) {
                    var viewerUrlConfig = data["ecm.viewer.snowbound"];
                    var urlConfigComponents = viewerUrlConfig.split("?");
                    if (urlConfigComponents && urlConfigComponents.length > 0) {
                        viewerUrl = urlConfigComponents[0];
                    }
                }
                return viewerUrl;
            }

            /**
             * @ngdoc method
             * @name buildSnowboundUrl
             * @methodOf services.service:SnowboundService
             *
             * @param {JSON} ecmFileProperties properties from the ecmFileService.properties configuration file
             * @param {String} authentication token for ArkCase for the currently logged in user
             *
             * @description
             * This method takes the configuration from ecmFileService.properties and generates the
             * Snowbound viewer url including the arguments necessary to open a document in it
             * and allow snowbound to callback Alfresco (for merge/split, etc.) and authenticate.
             */
            , buildSnowboundUrl: function (ecmFileProperties, acmTicket, userId, file) {

                // Obtains the base portion of the viewer url (host/port, etc)
                var viewerBaseUrl = this.extractViewerBaseUrl(ecmFileProperties);

                // Forces the viewer iframe to be reloaded with the latest version of the document
                var randomUrlArgToCauseIframeRefresh = (new Date()).getTime();

                return viewerBaseUrl +
                    "?documentId=ecmFileId=" + file.id + "&acm_ticket=" + acmTicket + "&userid=" + userId +
                    "&refreshCacheTimestamp=" + randomUrlArgToCauseIframeRefresh + "&documentName=" + file.name +
                    "&parentObjectId=" + file.containerId + "&parentObjectType=" + file.containerType + "&selectedIds=" + file.selectedIds;
            }

            /**
             * @ngdoc method
             * @name encryptSnowboundUrlQueryString
             * @methodOf services.service:SnowboundService
             *
             * @param {String} queryString part of Snowbound viewer URL
             * @param {JSON} ecmFileProperties properties from the ecmFileService.properties configuration file
             *
             * @description
             * This method takes the configuration from ecmFileService.properties and takes
             * given encryption key and iv to provide AES encrypted string
             */
            , encryptSnowboundUrlQueryString: function (queryString, ecmFileProperties) {
                var key = ecmFileProperties['ecm.viewer.snowbound.encryptionKey'];
                var iv = ecmFileProperties['ecm.viewer.snowbound.encryptionIv'];

                if (key && iv) {
                    var encryptionKey = CryptoJS.enc.Base64.parse(key);
                    var encryptionIv = CryptoJS.enc.Base64.parse(iv);
                    try {
                        var encrypted = CryptoJS.AES.encrypt(queryString, encryptionKey,
                            {mode: CryptoJS.mode.CBC, padding: CryptoJS.pad.Pkcs7, iv: encryptionIv});
                        var base64Encoded = encrypted.ciphertext.toString(CryptoJS.enc.Base64);
                        return encodeURIComponent(base64Encoded);
                    }
                    catch (e){
                        console.log("Error on encryption, returning plain query string");
                    }
                }
                return queryString;
            }
        }
    }
]);