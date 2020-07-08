'use strict';

angular.module('services').factory('SAR.Data', [function () {
    var data = {
        className: 'gov.privacy.model.SubjectAccessRequest',
        title: '',
        requestCategory: '',
        details: '',
        requestSubType: '',
        requestType: 'Data Access Request',
        originalRequestNumber: '',
        originator: {
            className: 'gov.privacy.model.SARRequesterAssociation',
            personType: 'Requester',
            parentType: 'CASE_FILE',
            person: {
                className: 'gov.privacy.model.SARPerson',
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
        },
        subject: {
            className: 'gov.privacy.model.SARRequesterAssociation',
            personType: 'Subject',
            parentType: 'CASE_FILE',
            person: {
                className: 'gov.privacy.model.SARPerson',
                givenName: '',
                familyName: '',
                position: '',
                title: '',
                addresses: [{
                    type: 'Business',
                    country: 'USA',
                    state: '',
                    zip: '',
                    city: '',
                    streetAddress: ''
                }],
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
                organizations: [],
                dateOfBirth: new Date()
            }
        }

    };

    return {
        /**
         * @ngdoc method
         * @name getData
         * @methodOf services.privacy:createRequest.Data
         *
         * @description
         * Return basic subjectAccessRequest structure
         *
         * @returns {Object} Initial subjectAccessRequest structure
         */
        getData: function() {
            return data;
        }
    }
} ]);
