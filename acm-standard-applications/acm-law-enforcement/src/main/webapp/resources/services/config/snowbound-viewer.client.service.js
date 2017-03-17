'use strict';

/**
 * @ngdoc service
 * @name services.service:SnowboundService
 *
 * @description
 * {@link https://***REMOVED***/arkcase/ACM3/tree/develop/acm-standard-applications/acm-law-enforcement/src/main/webapp/resources/services/config/snowbound-viewer.client.service.js services/config/snowbound-viewer.client.service.js}
 *
 * This service contains functionality for Snowbound Viewer management.
 */
angular.module('services').factory('SnowboundService', ['UtilService',
    function (Util) {
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
                var encryptionPassphrase = ecmFileProperties['ecm.viewer.snowbound.encryptionKey'];
                // Forces the viewer iframe to be reloaded with the latest version of the document
                var randomUrlArgToCauseIframeRefresh = (new Date()).getTime();
                return viewerBaseUrl + "?" + Util.encryptString(
                    "documentId=ecmFileId=" + file.id + "&acm_ticket=" + acmTicket + "&userid=" + userId +
                    "&refreshCacheTimestamp=" + randomUrlArgToCauseIframeRefresh + "&documentName=" + file.name +
                    "&parentObjectId=" + file.containerId + "&parentObjectType=" + file.containerType +
                    "&selectedIds=" + file.selectedIds, encryptionPassphrase);
            }
        }
    }
]);