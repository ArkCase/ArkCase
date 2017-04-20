'use strict';
/**
 * @ngdoc service
 * @name admin.service:Admin.ExchangeConfigurationService
 *
 * @description
 * Contains REST calls for Admin Exchange Configuration
 *
 * The Admin.ExchangeConfigurationService provides $http services for Exchange Configuration.
 */
 angular.module('admin').service('Admin.ExchangeConfigurationService', ['$http',
    function($http) {
        return ({
            saveExchangeConfiguration: saveExchangeConfiguration,
            getExchangeConfiguration: getExchangeConfiguration
        });
            /**
             * @ngdoc method
             * @name saveExchangeConfiguration
             * @methodOf admin.service:Admin.ExchangeConfigurationService
             *
             * @description
             * Performs saving of the exchange configuration.
             *
             * @param {Object} exchangeConfig - the configuration that should be saved
             *
             * @returns {Object} http promise
             */
             function saveExchangeConfiguration(exchangeConfig) {
                return $http({
                    method: 'PUT',
                    url: 'api/latest/plugin/admin/exchange/configuration',
                    data: exchangeConfig
                });
            };
            /**
             * @ngdoc method
             * @name getExchangeConfiguration
             * @methodOf admin.service:Admin.ExchangeConfigurationService
             *
             * @description
             * Gets the current exchange configurations.
             *
             *
             * @returns {Object} http promise
             */
             function getExchangeConfiguration() {
                return $http({
                    method: 'GET',
                    url: 'api/latest/plugin/admin/exchange/configuration'
                });
            }
        }]);