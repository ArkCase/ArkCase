'use strict';

var ACM_SETTINGS = {
    LANG: 'en'
};

//Start by defining the main module and adding the module dependencies
var app = angular.module(ApplicationConfiguration.applicationModuleName, ApplicationConfiguration.applicationModuleVendorDependencies);

// Setting HTML5 Location Mode
angular.module(ApplicationConfiguration.applicationModuleName).config([
        '$locationProvider', '$translateProvider', '$translatePartialLoaderProvider', '$httpProvider',
        function ($locationProvider, $translateProvider, $translatePartialLoaderProvider, $httpProvider) {
            $locationProvider.hashPrefix('!');

            $httpProvider.interceptors.push(httpInterceptor);

            $httpProvider.interceptors.push(noCacheInterceptor);

            function noCacheInterceptor() {
                return {
                    request: function (config) {
                        // Appends timestamp to url to avoid caching issues on IE
                        // only on GET requests with no explicit cache=true
                        if (config.method == 'GET') {
                            if (!config.cache) {
                                var separator = config.url.indexOf('?') === -1 ? '?' : '&';
                                config.url += separator + 'noCache=' + new Date().getTime();
                            }
                        }
                        return config;
                    }
                }
            }


            // Initialize angular-translate
            $translateProvider.useLoader('$translatePartialLoader', {
                urlTemplate: 'api/latest/plugin/admin/labelmanagement/resource?ns={part}&lang={lang}'
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
                    // Send error message to MessageService if is not suppressed
                    if (isErrorSuppressed(response)) {
                        return (
                            $q.reject(null)
                        );
                    } else {
                        // Only throw http error as last resort
                        if (response.data) {
                            //e.g. Task already claimed .. exception type is ...
                            MessageService.error(response.data);
                        }
                        else if (response.statusText) {
                            //e.g. Unknown Error
                            MessageService.error(response.statusText);
                        }
                        else {
                            //e.g. Error 404 /api/latest..
                            MessageService.httpError(response);
                        }
                        return (
                            $q.reject(response)
                        );
                    }
                }

                function isErrorSuppressed(response) {
                    var isSuppressed = false;
                    angular.forEach(ApplicationConfiguration.suppressedErrorList, function (error) {
                        if (error.url == response.config.url && error.status == response.status) {
                            isSuppressed = true;
                        }
                    });
                    return isSuppressed;
                }
            }
        }
    ])
    .run(['$translate', '$translatePartialLoader',
        function ($translate, $translatePartialLoader) {
            $translatePartialLoader.addPart('core');
            $translate.refresh();
        }
    ]);


// Load language info before start Angular application
angular.element(document).ready(function () {
    $.getJSON('api/latest/plugin/admin/labelmanagement/default-language', function (result) {
        ACM_SETTINGS.LANG = result.defaultLang || ACM_SETTINGS.LANG;
        angular.bootstrap(document, [ApplicationConfiguration.applicationModuleName]);
    }).fail(function () {
        // If language is missed then use default lang settings (en)
        angular.bootstrap(document, [ApplicationConfiguration.applicationModuleName]);
    });
});