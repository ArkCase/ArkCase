'use strict';

angular.module('preference').factory('Preference.PreferenceService', ['$resource',
    function ($resource) {
        return $resource('', {}, {
            getPreferredWidgets: {
                method: 'GET',
                url: 'proxy/arkcase/api/latest/plugin/dashboard/widgets/preferred/{moduleName}',
                data: ''
            },

            setPreferredWidgets: {
                method: 'POST',
                url: 'proxy/arkcase/api/latest/plugin/dashboard/widgets/preferred'
            }
        })
    }
]);