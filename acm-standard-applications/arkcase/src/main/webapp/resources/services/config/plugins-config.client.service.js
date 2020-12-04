'use strict';

/**
 * @ngdoc service
 * @name services.service:PluginService
 *
 */
angular.module('services').factory('PluginService', [ '$resource', function($resource) {
   return $resource('api/latest/plugins/', {}, {
        getConfigurablePlugins: {
            method: 'GET',
            cache: false,
            url: 'api/latest/plugins/configurablePlugins'
        }
    });
} ]);