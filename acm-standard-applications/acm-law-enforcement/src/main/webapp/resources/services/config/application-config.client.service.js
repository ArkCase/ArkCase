'use strict';

/**
 * @ngdoc service
 * @name services.service:ApplicationConfigService
 *
 * @description
 *
 * {@link https://gitlab.armedia.com/arkcase/ACM3/tree/develop/acm-standard-applications/acm-law-enforcement/src/main/webapp/resources/services/config/application-config.client.service.js services/config/application-config.client.service.js}

 * ApplicationConfigService loads application configuration
 */
angular.module('services').factory('ApplicationConfigService', [ '$resource', 'Acm.StoreService', 'UtilService', function($resource, Store, Util) {

    var Service = $resource('api/latest/plugin/admin/app-properties', {}, {
        _getApplicationConfig: {
            method: 'GET',
            url: 'api/latest/plugin/admin/app-properties'
        }
    });

    Service.SessionCacheNames = {
        APP_CONFIG: 'ApplicationConfig'
    };

    Service.PROPERTIES = {
        DISPLAY_USERNAME: 'application.properties.displayUserName'
    };

    /**
     * @ngdoc method
     * @name getConfiguration
     * @methodOf services.service:ApplicationConfigService
     *
     * @description
     * Query application configuration information
     *
     * @returns {Object} Future application configuration information.
     */
    Service.getConfiguration = function() {
        var cacheAppConfig = new Store.SessionData(Service.SessionCacheNames.APP_CONFIG);
        var appConfig = cacheAppConfig.get();
        return Util.serviceCall({
            service: Service._getApplicationConfig,
            result: appConfig,
            onSuccess: function(data) {
                appConfig = data;
                cacheAppConfig.set(appConfig);
                return appConfig;
            }
        });
    };

    /**
     * @ngdoc method
     * @name getProperty
     * @methodOf services.service:ApplicationConfigService
     *
     * @description
     * Retrieve property value from application configuration. If property is absent then return 'undefined'
     *
     * @returns {Object} Future application configuration property value.
     */
    Service.getProperty = function(propName) {
        return Service.getConfiguration().then(function(config) {
            var value;
            if (_.isObject(config)) {
                value = _.get(config, propName);
            }
            return value;
        })
    };

    return Service;
} ]);