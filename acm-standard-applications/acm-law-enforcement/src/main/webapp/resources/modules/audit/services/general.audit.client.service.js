'use strict';
/**
 * @ngdoc service
 * @name order-info.service:OrderInfo.AttachmentsService
 *
 * @description
 *
 * {@link https://github.com/Armedia/bactes360/blob/develop/bactes-user-interface/src/main/resources/META-INF/resources/resources/modules/order-info/services/attachments.client.service.js order-info/services/attachments.client.service.js}
 *
 * The AttachmentsService provides Order's attachments management functionality.
 */
angular.module('audit').factory('SessionTimeoutAudit', ['$http',
    function ($http) {
        return {

            /**
             * @ngdoc method
             * @name geenricAudit
             * @methodOf audit.service:GeneralAudit
             *
             * @description
             * Audit
             *
             *
             * @returns {HttpPromise} Future info about sessionTimeout Audit
             */
            geenricAudit: function (auditType) {
                //FIXME add some filtering here to disable or enable some of the logging types
                return $http({
                    method: 'GET',
                    url: '/api/v1/plugin/audit/generic?type=' + auditType,
                });
            }
        }
    }
]);