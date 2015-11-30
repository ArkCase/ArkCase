'use strict';

/**
 * @ngdoc service
 * @name services:Helper.ConfigService
 *
 * @description
 *
 * {@link https://github.com/Armedia/ACM3/blob/develop/acm-user-interface/ark-web/src/main/webapp/resources/services/helper/helper-config.client.service.js services/helper/helper-config.client.service.js}

 * Helper.ConfigService has functions for typical usage in ArCase of 'ui-grid' directive
 */
angular.module('services').factory('Helper.ConfigService', ['$q',
    function ($q) {
        var Helper = {

            /**
             * @ngdoc method
             * @name requestComponentConfig
             * @methodOf services:Helper.ConfigService
             *
             * @param {Object} scope Angular scope
             * @param {String} theComponentId Component ID
             * @param {Function} onConfigAcquired Callback function when configuration is acquired
             *
             * @description
             * This method asks for config data for a specified component
             */
            requestComponentConfig: function (scope, theComponentId, onConfigAcquired) {
                var dfd = $q.defer();
                scope.$emit('req-component-config', theComponentId);
                scope.$on('component-config', function (e, componentId, config) {
                    if (theComponentId == componentId) {
                        onConfigAcquired(config);
                        dfd.resolve(config);
                    }
                });
                return dfd.promise;
            }

        };
        return Helper;
    }
]);