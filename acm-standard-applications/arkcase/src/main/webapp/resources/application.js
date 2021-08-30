'use strict';

// Start by defining the main module and adding the module dependencies
var app = angular.module(ApplicationConfiguration.applicationModuleName, ApplicationConfiguration.applicationModuleVendorDependencies);

// Setting HTML5 Location Mode
angular.module(ApplicationConfiguration.applicationModuleName).config(
        [
                '$locationProvider',
                '$translateProvider',
                '$translatePartialLoaderProvider',
                'tmhDynamicLocaleProvider',
                '$httpProvider',
                'AnalyticsProvider',
                '$provide',
                'momentPickerProvider',
                function($locationProvider, $translateProvider, $translatePartialLoaderProvider, dynamicLocaleProvider, $httpProvider,
                        AnalyticsProvider, $provide, momentPickerProvider) {
                    $locationProvider.hashPrefix('!');

                    $httpProvider.interceptors.push(httpInterceptor);

                    $httpProvider.interceptors.push(noCacheInterceptor);

                    $httpProvider.defaults.transformResponse.splice(0, 0, function(data, headersGetter) {
                        var contentType = headersGetter()['content-type'] || '';
                        if (data && contentType.indexOf('application/json') > -1) {
                            return JSOG.parse(data);
                        }
                        return data;
                    });

                    // angular moment picker global configuration. For all available properties refer to github repo
                    // https://github.com/indrimuska/angular-moment-picker#momentpickerprovider
                    momentPickerProvider.options({
                        minutesStep: 1
                    });

                    var timezoneOffset = new Date().toString().match(/([\+-][0-9]+)/g);
                    timezoneOffset = timezoneOffset ? timezoneOffset[0] : "UTC";

                    $provide.decorator('dateFilter', ['$delegate', '$injector', function($delegate, $injector) {
                        var oldDelegate = $delegate;

                        var dateFilterInterceptor = function(date, format, timezone) {
                            if(angular.isUndefined(timezone)) {
                                timezone = timezoneOffset;
                            }
                            return oldDelegate.apply(this, [date, format, timezone]);
                        };
                        return dateFilterInterceptor;
                    }]);

                    function noCacheInterceptor() {
                        return {
                            request : function(config) {
                                // Appends timestamp to url to avoid
                                // caching issues on IE
                                // only on GET requests with no explicit
                                // cache=true
                                if (config.method == 'GET') {
                                    if (config.cache === false) {
                                        config.headers["Cache-Control"] = 'no-cache';
                                        config.headers["Pragma"] = 'no-cache';
                                    }
                                }
                                return config;
                            }
                        }
                    }

                    // Initialize angular-translate
                    $translateProvider.useLoader('$translatePartialLoader', {
                        urlTemplate : 'api/latest/plugin/admin/labelmanagement/resource?ns={part}&lang={lang}'
                    });
                    $translateProvider.determinePreferredLanguage(function() {
                        var preferredLocale = "en";

                        // handle incompatible old format; this code will be removed after locale setting implementation
                        // is stable and long enough for all user and developers to update to this code
                        if (localStorage.AcmLocale) {
                            var lastLocale = angular.fromJson(localStorage.AcmLocale);
                            if (!lastLocale) {
                                localStorage.AcmLocale = null;
                            } else if (!lastLocale.locales) {
                                localStorage.AcmLocale = null;
                            } else if (0 >= lastLocale.locales.length) {
                                localStorage.AcmLocale = null;
                            } else if (lastLocale.locales[0].locale) {
                                localStorage.AcmLocale = null;
                            } else if (!lastLocale.locales[0]["native"]) {
                                localStorage.AcmLocale = null;
                            } else if (!lastLocale.locales[0].currencySymbol) {
                                localStorage.AcmLocale = null;
                            } else if (!lastLocale.code) {
                                localStorage.AcmLocale = null;
                            } else if (!lastLocale.iso) {
                                localStorage.AcmLocale = null;
                            }
                        }
                        //TODO: remove above block

                        if (localStorage.AcmLocale && "null" != localStorage.AcmLocale) {
                            var lastLocale = angular.fromJson(localStorage.AcmLocale);
                            if (lastLocale && lastLocale.code) {
                                preferredLocale = lastLocale.code;
                            }
                        }

                        dynamicLocaleProvider.localeLocationPattern('modules/common/angular-i18n/angular-locale_{{locale}}.js');
                        dynamicLocaleProvider.defaultLocale('en');

                        return preferredLocale;
                    });

                    // The 'escape' strategy seems to cause failed translation of {{'xxx' | translate}}, and also
                    // when texts contain '&'. Disable this until the bug is fixed by Angular.
                    //
                    //$translateProvider.useSanitizeValueStrategy('escape');

                    // Add HTTP error interceptor
                    function httpInterceptor($q, $window, $rootScope, MessageService) {
                        return {
                            responseError: responseError
                        };

                        // Intercept the failed response.
                        function responseError(response) {
                            // Redirect to login page on 401
                            // TODO Should the application caches be
                            // emptied? If the same user logs in no, but
                            // what about for a different user?
                            if (response.status === 401) {
                                // reload the current page to get the
                                // user redirected to the login page and
                                // return to the same page after login
                                // Spring security on the server
                                // remembers the last requested page
                                var redirectUrl = response.headers()['acm_concurrent_session_redirect'];
                                if (redirectUrl) {
                                    $window.location.href = redirectUrl;
                                    return ($q.reject(null));
                                }
                                sessionStorage.redirectState = angular.toJson($window.location);
                                $window.location.reload();
                                return ($q.reject(null));
                            }

                            if (response.status === 403) {
                                // user is authenticated but tries to modify some entity
                                // with no granted permission
                                MessageService.error('User has no granted permission for this action');
                            }

                            if (response.status >= 500) {
                                MessageService.serverError();
                            }
                            //Reject all unrejected responses
                            return($q.reject(response));
                        }
                    }

                    if (typeof GOOGLE_ANALYTICS_ENABLED === 'undefined') { // sanity check
                        // this means that "api/latest/plugin/admin/googleAnalytics/config.js" couldn't
                        // be generated (very unlikely, but possible) and we are disabling Google Analytics
                        AnalyticsProvider.useAnalytics(false);
                    } else {
                        AnalyticsProvider.useAnalytics(GOOGLE_ANALYTICS_ENABLED); // configuration toggle
                        AnalyticsProvider.setAccount(GOOGLE_ANALYTICS_TRACKING_ID); // configuration property
                        AnalyticsProvider.enterDebugMode(GOOGLE_ANALYTICS_DEBUG); // configuration debug flag
                        AnalyticsProvider.setPageEvent('$stateChangeSuccess');
                    }
                }]).run(
    ['$translate', '$translatePartialLoader', '$rootScope', '$http', 'CacheFactory', 'LookupService',
        function ($translate, $translatePartialLoader, $rootScope, $http, CacheFactory, LookupService) {
            $translatePartialLoader.addPart('core');
            $translatePartialLoader.addPart('welcome');
            $translate.refresh();
            $rootScope.utils = utils;
            /*
             *this is cache for all $http calls if cache is enabled
             */
            $http.defaults.cache = CacheFactory('defaultCache', {
                maxAge: 10 * 1000, // Items added to this cache expire after 10 seconds
                cacheFlushInterval: 60 * 60 * 1000, // This cache will clear itself every hour
                deleteOnExpire: 'aggressive' // Items will be deleted from this cache when they expire
            });

            // Load lookups in cache
            LookupService.getLookups();

            $rootScope.$bus.subscribe('lookups-updated', function (result) {
                var lookups = JSON.parse(result.lookupsData);
                LookupService.reloadLookups(lookups);
            });

        }]);

var utils = {};
utils.getLookupValue = function(objArray, key) {
    if (objArray && objArray instanceof Array) {
        for (var i = 0; i < objArray.length; i++) {
            if (objArray[i].key === key) {
                return objArray[i].value;
            }
        }
    }
    // this should happen if the key is not found in the array. then return the key itself
    return key;
};

angular.element(document).ready(function() {
    angular.bootstrap(document, [ ApplicationConfiguration.applicationModuleName ]);
});
