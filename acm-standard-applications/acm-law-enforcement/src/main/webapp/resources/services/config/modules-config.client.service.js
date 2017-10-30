'use strict';

/**
 * @ngdoc service
 * @name services.service:ConfigService
 *
 * @description
 *
 * {@link https://gitlab.armedia.com/arkcase/ACM3/tree/develop/acm-standard-applications/acm-law-enforcement/src/main/webapp/resources/services/config/modules-config.client.service.js services/config/modules-config.client.service.js}

 * ConfigService contains wrapper functions of ConfigService to support default error handling, data validation and data cache.
 */
angular.module('services').factory('ConfigService', ['$resource', 'Acm.StoreService', 'UtilService',
    function ($resource, Store, Util) {
        var Service = $resource('api/config/', {}, {
            getModule: {
                method: 'GET',
                cache: true,
                url: 'modules_config/config/modules/:moduleId/config.json',
                isArray: false
            },

            queryModules: {
                method: 'GET',
                cache: true,
                url: 'modules_config/config/modules.json',
                isArray: true
            },

            updateModule: {
                method: 'PUT',
                url: 'modules_config/config/modules/:moduleId/config.json',
                isArray: false
            }
        });

        Service.SessionCacheNames = {
            MODULE_CONFIG_MAP: "AcmModuleConfigMap"
        };
        Service.CacheNames = {};


        /**
         * @ngdoc method
         * @name getModuleConfig
         * @methodOf services.service:ConfigService
         *
         * @description
         * Query config of a module
         *
         * @param {String} moduleId  Module ID
         *
         * @returns {Object} Promise
         */
        Service.getModuleConfig = function (moduleId) {
            var cacheModuleConfigMap = new Store.SessionData(Service.SessionCacheNames.MODULE_CONFIG_MAP);
            //var cacheModuleConfigMap = new Store.SessionData({name: Service.SessionCacheNames.MODULE_CONFIG_MAP, noOwner: true});
            var moduleConfigMap = cacheModuleConfigMap.get();
            var moduleConfig = Util.goodMapValue(moduleConfigMap, moduleId, null);
            return Util.serviceCall({
                service: Service.getModule
                , param: {moduleId: moduleId}
                , result: moduleConfig
                , onSuccess: function (data) {
                    if (Service.validateModuleConfig(data, moduleId)) {
                        moduleConfig = data;
                        moduleConfigMap = moduleConfigMap || {};
                        moduleConfigMap[moduleId] = moduleConfig;
                        cacheModuleConfigMap.set(moduleConfigMap);
                        return moduleConfig;
                    }
                }
            });
        };


        /**
         * @ngdoc method
         * @name validateModuleConfig
         * @methodOf services.service:ConfigService
         *
         * @description
         * Validate module config data
         *
         * @param {Object} data  Data to be validated
         *
         * @returns {Boolean} Return true if data is valid
         */
        Service.validateModuleConfig = function (data, moduleId) {
            if (Util.isEmpty(data)) {
                return false;
            }
            if (moduleId != Util.goodValue(data.id)) {
                return false;
            }
            //if (!Util.isArray(data.components)) {
            //    return false;
            //}
            return true;
        };


        /**
         * @ngdoc method
         * @name getComponentConfig
         * @methodOf services.service:ConfigService
         *
         * @description
         * Query config of a component in a module
         *
         * @param {String} moduleId  Module ID
         * @param {String} componentId  Component ID
         *
         * @returns {Object} Promise
         */
        Service.getComponentConfig = function (moduleId, componentId) {
            return Service.getModuleConfig(moduleId).then(function (moduleConfig) {
                var components = Util.goodMapValue(moduleConfig, "components", []);
                var componentConfig = _.find(moduleConfig.components, {id: componentId});
                return componentConfig;
            });
        }

        return Service;
    }
]);