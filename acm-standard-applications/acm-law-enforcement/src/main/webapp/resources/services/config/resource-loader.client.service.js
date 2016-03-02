'use strict';

angular.module('services').factory('ResourceLoaderService', ['$q', '$http',
    function ($q, $http) {
        return {
            loadResource: function (lang, module) {
                return $http.get('modules_config/config/modules/'+  module +'/resources/' + lang + '.json');
            }
        }
    }
]);