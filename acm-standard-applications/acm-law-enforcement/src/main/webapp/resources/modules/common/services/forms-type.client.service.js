'use strict';

/**
 * @ngdoc service
 * @name services:FormsType.Service
 *
 * @description
 *
 * {@link https://gitlab.armedia.com/arkcase/ACM3/tree/develop/acm-standard-applications/acm-law-enforcement/src/main/webapp/resources/modules/common/services/forms-type.client.service.js modules/common/services/forms-type.client.service.js}
 *
 * FormsType.Service provides function which returns boolean whether the type of the forms is angular or not
 */
angular.module('services').service('FormsType.Service', [ 'Admin.ApplicationFormsTypeConfigService', function(ApplicationFormsTypeConfigService) {

    return ({
        isAngularFormType: isAngularFormType,
        isFrevvoFormType: isFrevvoFormType
    });

    function isAngularFormType() {
        return ApplicationFormsTypeConfigService.getProperty(ApplicationFormsTypeConfigService.PROPERTIES.FORMS_TYPE).then(function(response) {
            var formsType = response.data[ApplicationFormsTypeConfigService.PROPERTIES.FORMS_TYPE];
            return formsType == "angular";
        });
    }

    function isFrevvoFormType() {
        return ApplicationFormsTypeConfigService.getProperty(ApplicationFormsTypeConfigService.PROPERTIES.FORMS_TYPE).then(function(response) {
            var formsType = response.data[ApplicationFormsTypeConfigService.PROPERTIES.FORMS_TYPE];
            return formsType == "frevvo";
        });
    }

} ]);