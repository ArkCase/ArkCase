'use strict';

angular.module('services').factory('RequestResponseFolder.Service', [ '$http', 'UtilService', function($http, Util) {

    var _compressAndSendResponseFolder = function(requestId, folderId) {
            var url = 'api/latest/plugin/casefile/'+ requestId + '/compressAndSendResponseFolder/' + folderId;
            return $http({
                method: 'POST',
                url: url
            });
        }

    return {
        compressAndSendResponseFolder: _compressAndSendResponseFolder
    };

}]);
