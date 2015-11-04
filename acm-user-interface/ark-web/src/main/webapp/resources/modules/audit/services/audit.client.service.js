'use strict';

// Service for building url
angular.module('audit').factory('AuditController.BuildUrl', ['$sce',
    function ($sce) {
        return {
            getUrl: function (pentahoHost, pentahoPort, auditReportUri, startDate, endDate, objectType, objectId, dateFormat) {
                var reportUrl = pentahoHost + pentahoPort + auditReportUri
                    + "&startDate=" + startDate
                    + "&endDate=" + endDate
                    + "&objectType=" + objectType
                    + "&objectId=" + objectId
                    + "&dateFormat=" + encodeURIComponent(dateFormat);
                return $sce.trustAsResourceUrl(reportUrl);
            }
        }
    }
]);