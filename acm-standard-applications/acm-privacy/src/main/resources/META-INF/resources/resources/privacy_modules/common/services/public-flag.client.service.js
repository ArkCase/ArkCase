'use strict';


angular.module('services').factory('PublicFlag.Service', [ '$http', 'UtilService', function($http, Util) {

    var _updatePublicFlag = function(publicFiles, publicStatus) {
            var url = 'api/latest/service/publicFlag/' +  publicStatus;
            return $http({
                method: 'POST',
                url: url,
                data: publicFiles
            });
        }

    return {
        updatePublicFlag: _updatePublicFlag
    };

}]);
