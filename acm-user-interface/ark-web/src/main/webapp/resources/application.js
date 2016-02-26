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


//Then define the init function for starting up the application
angular.element(document).ready(function () {
    //Get language
    $.getJSON('proxy/arkcase/api/latest/plugin/admin/labelmanagement/default-language', function(result){
        ACM_SETTINGS.LANG = result.defaultLang || ACM_SETTINGS.LANG;
        // Then init the app
        angular.bootstrap(document, [ApplicationConfiguration.applicationModuleName]);
    });
});