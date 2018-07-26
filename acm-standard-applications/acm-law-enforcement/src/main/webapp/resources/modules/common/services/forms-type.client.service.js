'use strict';

/**
 * @ngdoc service
 * @name services:FormsType.Service
 *
 * @description
 *
 * {@link https://***REMOVED***/arkcase/ACM3/tree/develop/acm-standard-applications/acm-law-enforcement/src/main/webapp/resources/modules/common/services/forms-type.client.service.js modules/common/services/forms-type.client.service.js}
 *
 * FormsType.Service provides function which returns boolean whether the type of the forms is angular or not
 */
angular.module('services').service('FormsType.Service', [ 'Admin.ApplicationSettingsService', function(ApplicationSettingsService) {

    return ({
        isAngularFormType: isAngularFormType
    });

    function isAngularFormType() {
        var isAngularFormType = true;
        ApplicationSettingsService.getProperty(ApplicationSettingsService.PROPERTIES.FORMS_TYPE).then(function(response) {
            var formsType = response.data[ApplicationSettingsService.PROPERTIES.FORMS_TYPE];
            if (formsType == "angular") {
                isAngularFormType = true;
            } else {
                isAngularFormType = false;
            }
        });

        return isAngularFormType;
    }

} ]);