'use strict';

/**
 * @ngdoc service
 * @name services:Acm.AppService
 *
 * @description
 *
 * {@link https://***REMOVED***/arkcase/ACM3/tree/develop/acm-standard-applications/acm-law-enforcement/src/main/webapp/resources/services/acm/app.client.service.js services/acm/app.client.service.js}
 *
 * This service is used to hold login information, for example login status, idle time, error statistics, etc.
 */

angular.module('services').factory('Acm.AppService', ['$location', 'Acm.StoreService', 'UtilService'
    , function ($location, Store, Util) {

        var Service = {
            SessionCacheNames: {
                APP_CONTEXT: "AcmAppContext"
            }

            /**
             * @ngdoc method
             * @name getAppContext
             * @methodOf services:Acm.AppService
             *
             * @description
             * Return current context name of current app. For example, if the url is http://localhost:8080/arkcase/home.html#!/dashboard
             * The app context name is 'arkcase'
             */
            , getAppContext: function () {
                var cacheAppContext = new Store.SessionData(Service.SessionCacheNames.APP_CONTEXT);
                var appContext = cacheAppContext.get();
                if (Util.isEmpty(appContext)) {
                    var str = $location.absUrl();
                    var idxHome = str.indexOf("/home.html");
                    if (0 < idxHome) {
                        str = str.substring(0, idxHome);
                        var idxSlash = str.lastIndexOf("/");
                        if (0 < idxSlash) {
                            appContext = str.substring(idxSlash + 1);
                            cacheAppContext.set(appContext);
                        }
                    }
                }
                return appContext;
            }

            /**
             * @ngdoc method
             * @name getAppUrl
             * @methodOf services:Acm.AppService
             *
             * @description
             * Prefix partial url with app context.
             * For example, with app context 'arkcase', input '/some/url' becomes '/arkcase/some/url'.
             */
            , getAppUrl: function (url) {
                return "/" + Service.getAppContext() + url;
            }

        };

        return Service;
    }
]);