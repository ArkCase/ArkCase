'use strict';

angular.module('preference').factory('Preference.PreferenceService', [ '$resource', function($resource) {
    return $resource('', {}, {
        getPreferredWidgets: {
            method: 'GET',
            url: 'api/latest/plugin/dashboard/widgets/preferred/:moduleName',
            data: ''
        },

        setPreferredWidgets: {
            method: 'PUT',
            url: 'api/latest/plugin/dashboard/widgets/preferred'
        }
    })
} ]);