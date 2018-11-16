'use strict';

/**
 * @ngdoc service
 * @name services:Ecm.EmailService
 *
 * @description
 *
 * {@link https://gitlab.armedia.com/arkcase/ACM3/tree/develop/acm-standard-applications/acm-law-enforcement/src/main/webapp/resources/services/ecm/ecm-email.client.service.js services/ecm/ecm-email.client.service.js}

 * Email service for ECM.
 */
angular.module('services').factory('Ecm.EmailService', [ '$resource', '$translate', 'Acm.StoreService', 'UtilService', 'MessageService', function($resource, $translate, Store, Util, MessageService) {

    var Service = $resource('api/latest/service', {}, {
        /**
         * @ngdoc method
         * @name _sendEmail
         * @methodOf services:Ecm.EmailService
         *
         * @description
         * Send email
         *
         * @returns {Object} Object returned by $resource
         */
        _sendEmail: {
            method: 'POST',
            url: 'api/latest/service/email/send/withembeddedlinks/:objectType'
        }

        /**
         * @ngdoc method
         * @name _sendEmailWithAttachments
         * @methodOf services:Ecm.EmailService
         *
         * @description
         * Send email with attachments
         *
         * @returns {Object} Object returned by $resource
         */
        ,
        _sendEmailWithAttachments: {
            method: 'POST',
            url: 'api/latest/service/email/send/withattachments/:objectType'
        }
        /**
         * @ngdoc method
         * @name _sendEmailWithAttachmentsAndLinks
         * @methodOf services:Ecm.EmailService
         *
         * @description
         * Send email with attachments
         *
         * @returns {Object} Object returned by $resource
         */
        ,
        _sendEmailWithAttachmentsAndLinks: {
            method: 'POST',
            url: 'api/latest/service/email/send/withattachmentsandlinks/:objectType'
        }

        /**
         * @ngdoc method
         * @name _sendMentionsEmail
         * @methodOf services:Ecm.EmailService
         *
         * @description
         * Send email when a user is mentioned
         *
         * @returns {Object} Object returned by $resource
         */
        ,
        _sendMentionsEmail: {
            method: 'POST',
            url: 'api/latest/service/email/send/mentions'
        }
    });

    /**
     * @ngdoc method
     * @name sentEmail
     * @methodOf services:Ecm.EmailService
     *
     * @description
     * Send email
     *
     * @param {Object} emailData Email data
     * @param {String} objectType Type of the object
     *
     * @returns {Object} Object returned by $resource
     */
    Service.sendEmail = function(emailData, objectType) {
        var failed = "";
        return Util.serviceCall({
            service: Service._sendEmail,
            param: {
                objectType: objectType
            },
            data: emailData,
            onSuccess: function(data) {
                if (Service.validateSentEmails(data)) {
                    for (var i = 0; i < data.length; i++) {
                        if ("NOT_SENT" == data[i].state) {
                            failed += data[i].userEmail + ";";
                        }
                    }
                    if (Util.isEmpty(failed)) {
                        return emailData;
                    }
                }
            },
            onInvalid: function(data) {
                return failed;
            }
        });
    };

    /**
     * @ngdoc method
     * @name sendEmailWithAttachments
     * @methodOf services:Ecm.EmailService
     *
     * @description
     * Send email with attachments
     *
     * @param {Object} emailData Email data
     * @param {String} objectType Type of the object
     *
     * @returns {Object} Object returned by $resource
     */
    Service.sendEmailWithAttachments = function(emailData, objectType) {
        var failed = "";
        return Util.serviceCall({
            service: Service._sendEmailWithAttachments,
            param: {
                objectType: objectType
            },
            data: emailData,
            onSuccess: function(data) {
                MessageService.info($translate.instant("common.directive.docTree.email.successMessage"));
                if (Service.validateSentEmail(data)) {
                    return data;
                }
            },
            onInvalid: function(data) {
                return failed;
            }
        });
    };

    /**
     * @ngdoc method
     * @name sendEmailWithAttachmentsAndLinks
     * @methodOf services:Ecm.EmailService
     *
     * @description
     * Send email with attachments
     *
     * @param {Object} emailData Email data
     * @param {String} objectType Type of the object
     *
     * @returns {Object} Object returned by $resource
     */
    Service.sendEmailWithAttachmentsAndLinks = function(emailData, objectType) {
        var failed = "";
        return Util.serviceCall({
            service: Service._sendEmailWithAttachmentsAndLinks,
            param: {
                objectType: objectType
            },
            data: emailData,
            onSuccess: function(data) {
                MessageService.info($translate.instant("common.directive.docTree.email.successMessage"));
                if (Service.validateSentEmail(data)) {
                    return data;
                }
            },
            onInvalid: function(data) {
                return failed;
            }
        });
    };

    /**
     * @ngdoc method
     * @name sendMentionsEmail
     * @methodOf services:Ecm.EmailService
     *
     * @description
     * Send email when a user is mentioned
     *
     * @param {Object} emailData Email data
     *
     * @returns {Object} Object returned by $resource
     */
    Service.sendMentionsEmail = function(emailData) {
        var failed = "";
        return Util.serviceCall({
            service: Service._sendMentionsEmail,
            data: emailData,
            onSuccess: function(data) {
                MessageService.info($translate.instant("common.directive.docTree.email.successMessage"));
                if (Service.validateSentEmail(data)) {
                    return data;
                }
            },
            onInvalid: function(data) {
                return failed;
            }
        });
    };

    /**
     * @ngdoc method
     * @name validateSentEmails
     * @methodOf services:Ecm.EmailService
     *
     * @description
     * Validate array of SentEmail data
     *
     * @param {Object} data  Data to be validated
     *
     * @returns {Boolean} Return true if data is valid
     */
    Service.validateSentEmails = function(data) {
        if (!Util.isArray(data)) {
            return false;
        }
        for (var i = 0; i < data.length; i++) {
            if (!Service.validateSentEmail(data[i])) {
                return false;
            }
        }
        return true;
    };

    /**
     * @ngdoc method
     * @name validateSentEmail
     * @methodOf services:Ecm.EmailService
     *
     * @description
     * Validate SentEmail data
     *
     * @param {Object} data  Data to be validated
     *
     * @returns {Boolean} Return true if data is valid
     */
    Service.validateSentEmail = function(data) {
        if (Util.isEmpty(data)) {
            return false;
        }

        if (!Util.isEmpty(data.emailAddresses) && !Util.isArrayEmpty(data.emailAddresses)) {
            return true;
        }

        if (Util.isEmpty(data.state)) {
            return false;
        }
        if (Util.isEmpty(data.userEmail)) {
            return false;
        }
        return true;
    };

    return Service;
} ]);