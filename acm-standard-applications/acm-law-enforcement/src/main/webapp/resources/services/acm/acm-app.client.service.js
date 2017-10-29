'use strict';

/**
 * @ngdoc service
 * @name services:Acm.AppService
 *
 * @description
 *
 * {@link https://gitlab.armedia.com/arkcase/ACM3/tree/develop/acm-standard-applications/acm-law-enforcement/src/main/webapp/resources/services/acm/app.client.service.js services/acm/app.client.service.js}
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
             * Return current context name of current app.
             * For example, if the url is http://localhost:8080/home.html#!/dashboard, The app context name is '/'
             * If the url is http://localhost:8080/arkcase/home.html#!/dashboard, The app context name is 'arkcase'
             */
            , getAppContext: function () {
                var cacheAppContext = new Store.SessionData({name: Service.SessionCacheNames.APP_CONTEXT, noOwner: true});
                var appContext = cacheAppContext.get();
                if (Util.isEmpty(appContext)) {
                    var host = $location.host();
                    var str = $location.absUrl();
                    var idxHost = str.indexOf(host);
                    var idxHome = str.indexOf("/home.html");
                    if (idxHost < idxHome) {
                        str = str.substring(idxHost, idxHome);
                        var idxSlash = str.lastIndexOf("/");
                        appContext = (0 > idxSlash)? "/" : str.substring(idxSlash + 1);
                        cacheAppContext.set(appContext);
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
             * For example:
             * With app context 'arkcase', input '/some/url' or 'some/url' becomes '/arkcase/some/url'.
             * With app context '/', input '/some/url' or 'some/url' becomes '/some/url'.
             */
            , getAppUrl: function (url) {
                var appUrl = "";
                var appContext = Service.getAppContext();
                if (!Util.isEmpty(appContext)) {
                    if ("/" != appContext) {
                        appUrl = "/" + appContext;
                    }
                    if (0 != url.indexOf("/")) {
                        appUrl += "/";
                    }
                    appUrl += url;
                }
                return appUrl;
            }

        };

        return Service;
    }
]);