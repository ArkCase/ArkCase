angular.module('admin').factory('Admin.OutgoingIncomingEmailConversionService', [ '$http', function($http) {
    return {

        saveOutgoingIncomingEmailConversionConfiguration: function(config) {
            return $http({
                method: 'POST',
                url: 'api/latest/plugin/admin/outgoingIncomingEmailConversion',
                data: config
            });
        },
        getOutgoingIncomingEmailConversionConfiguration: function() {
            return $http({
                method: 'GET',
                url: 'api/latest/plugin/admin/outgoingIncomingEmailConversion'
            });
        }
    };
}]);
