'use strict';

angular.module('services').factory('RequestResponseFolder.Service', [ '$http', 'UtilService', function($http, Util) {

    var _compressAndSendResponseFolder = function(requestId) {
            var url = 'api/latest/plugin/casefile/'+ requestId + '/compressAndSendResponseFolder';
            return $http({
                method: 'POST',
                url: url
            });
        }

    return {
        compressAndSendResponseFolder: _compressAndSendResponseFolder
    };

}]);
