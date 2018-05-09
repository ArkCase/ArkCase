'use strict';
/**
 * @ngdoc service
 * @name audit.service:AuditService
 *
 * @description
 *
 * {@link https://github.com/Armedia/ACM3/tree/develop/acm-user-interface/ark-web/src/main/webapp/resources/META-INF/resources/resources/modules/audit/services/generic.audit.client.service.js audit/services/generic.audit.client.service.js}
 *
 * The Audit service provides functionality to audit events.
 */
angular.module('audit').factory('AuditService', [ '$http', function($http) {
    return {

        /**
         * @ngdoc method
         * @name genericAudit
         * @methodOf audit.service:AuditService
         *
         * @description
         * Generic audit with provided type
         *
         * @param {string} auditType audit type
         *
         * @returns {HttpPromise} Future info about generic Audit
         */
        genericAudit: function(auditType) {
            //FIXME add some filtering here to disable or enable logging types, useful in extensions
            return $http({
                method: 'POST',
                url: 'api/v1/plugin/audit/generic?type=' + auditType,
            });
        }
    }
} ]);