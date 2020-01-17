'use strict';

angular.module('services').factory('FOIA.Data', [ function() {
    var data = {
        className: 'gov.foia.model.FOIARequest',
        title: '',
        requestCategory: '',
        details: '',
        requestSubType: '',
        requestType: 'New Request',
        originalRequestNumber: '',
        payFee: '',
        originator: {
            className: 'gov.foia.model.FOIARequesterAssociation',
            personType: 'Requester',
            parentType: 'CASE_FILE',
            person: {
                className: 'gov.foia.model.FOIAPerson',
                givenName: '',
                familyName: '',
                position: '',
                title: '',
                addresses: [ {
                    type: 'Business',
                    country: 'USA',
                    state: '',
                    zip: '',
                    city: '',
                    streetAddress: ''

                } ],
                organizationAssociations: [],
                personAssociations: [],
                contactMethods: [],
                defaultEmail: {
                    type: 'email'
                },
                defaultPhone: {
                    type: 'phone'
                },
                defaultFax: {
                    type: 'fax'
                },
                organizations: []
            }
        }

    };

    return {
        /**
         * @ngdoc method
         * @name getData
         * @methodOf services.foia:createRequest.Data
         *
         * @description
         * Return basic foiaRequest structure
         *
         * @returns {Object} Initial foiaRequest structure
         */
        getData: function() {
            return data;
        }
    }
} ]);
