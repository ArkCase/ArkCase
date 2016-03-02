'use strict';

//Start by defining the main module and adding the module dependencies
var app = angular.module(ApplicationConfiguration.applicationModuleName, ApplicationConfiguration.applicationModuleVendorDependencies);

var ACM_SETTINGS = {
    LANG: 'en'
};


// Setting HTML5 Location Mode
angular.module(ApplicationConfiguration.applicationModuleName).config([
    '$locationProvider', '$translateProvider', '$translatePartialLoaderProvider', '$httpProvider',
    function ($locationProvider, $translateProvider, $translatePartialLoaderProvider, $httpProvider) {
        $locationProvider.hashPrefix('!');

        $httpProvider.interceptors.push(httpInterceptor);

            // Initialize angular-translate
            $translateProvider.useLoader('$translatePartialLoader', {
                urlTemplate: 'proxy/arkcase/api/latest/plugin/admin/labelmanagement/resource?ns={part}&lang={lang}'
            });

        $translateProvider.preferredLanguage(ACM_SETTINGS.LANG);
        //$translateProvider.useSanitizeValueStrategy('sanitize');

        // Add HTTP error interceptor
        function httpInterceptor($q, MessageService) {
            return {
                responseError: responseError
            };

            // Intercept the failed response.
            function responseError(response) {
                // Send error message to MessageService
                MessageService.httpError(response);
                return (
                    $q.reject(response)
                );
            }
        }
    }
]);


// Load language info before start Angular application
angular.element(document).ready(function () {
    $.getJSON('proxy/arkcase/api/latest/plugin/admin/labelmanagement/default-language', function (result) {
        ACM_SETTINGS.LANG = result.defaultLang || ACM_SETTINGS.LANG;
        angular.bootstrap(document, [ApplicationConfiguration.applicationModuleName]);
    }).fail(function(){
        // If language is missed then use default lang settings (en)
        angular.bootstrap(document, [ApplicationConfiguration.applicationModuleName]);
    });
});