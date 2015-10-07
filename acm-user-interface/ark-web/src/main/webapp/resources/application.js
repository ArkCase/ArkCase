'use strict';

//Start by defining the main module and adding the module dependencies
var app = angular.module(ApplicationConfiguration.applicationModuleName, ApplicationConfiguration.applicationModuleVendorDependencies);

// Setting HTML5 Location Mode
angular.module(ApplicationConfiguration.applicationModuleName).config(['$locationProvider', '$translateProvider', '$httpProvider',
    function ($locationProvider, $translateProvider, $httpProvider) {
        $locationProvider.hashPrefix('!');

        $httpProvider.interceptors.push(httpInterceptor);

        // Initialize angular-translate
        $translateProvider.useLoader('$translatePartialLoader', {
            urlTemplate: 'modules_config/config/modules/{part}/resources/{lang}.json'
//            urlTemplate: '/api/config/resources/{part}/{lang}'
        });

        $translateProvider.preferredLanguage('en');


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
    //Then init the app
    angular.bootstrap(document, [ApplicationConfiguration.applicationModuleName]);
});