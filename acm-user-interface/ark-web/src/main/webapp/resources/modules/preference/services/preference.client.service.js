'use strict';

angular.module('preference').factory('Preference.PreferenceService', ['$resource',
    function ($resource) {
        return $resource('', {}, {
            getEnabledWidgets: {
                method: 'GET',
                url: 'proxy/arkcase/api/v1/plugin/dashboard/get',
                data: ''
            },

            updateWidgetStatus: {
                method: 'POST',
                url: 'proxy/arkcase/api/v1/plugin/casefile/number/by/queue'
            }
        })
    }
]);