'use strict';

/**
 * @ngdoc service
 * @name services:Object.Ecm.EmailService
 *
 * @description
 *
 * {@link https://github.com/Armedia/ACM3/blob/develop/acm-user-interface/ark-web/src/main/webapp/resources/services/ecm/ecm.client.service.js services/ecm/ecm.client.service.js}

 * Email service for ECM.
 */
angular.module('services').factory('Ecm.EmailService', ['$resource', 'StoreService', 'UtilService'
    , function ($resource, StoreService, UtilService) {

        var Service = $resource('proxy/arkcase/api/latest/service', {}, {
            _sendEmail: {
                method: 'POST',
                url: 'proxy/arkcase/api/latest/service/notification/email'
            }
            , _sendEmailWithAttachments: {
                method: 'POST',
                url: 'proxy/arkcase/api/latest/plugin/outlook/email/withattachments'
            }
        });

        return Service;
    }
]);