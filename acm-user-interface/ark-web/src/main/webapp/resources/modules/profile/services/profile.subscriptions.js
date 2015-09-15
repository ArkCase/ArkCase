'use strict';
angular.module('profile').service('subscriptionService', function ($http, $q) {
    return({
        getSubscriptions: getSubscriptions,
        removeSubscriptions:removeSubscriptions
    });
    function getSubscriptions() {
        var request = $http({
            method: "GET",
            url: "proxy/arkcase/api/v1/service/subscription/ann-acm"
        });
        return(request.then(handleSuccess, handleError));
    };
    function removeSubscriptions(userId,parentType,parentId) {
        var request = $http({
            method: "DELETE",
            url: "proxy/arkcase/api/v1/service/subscription/"+ userId + "/" + parentType + "/" +  parentId
        });
        return(request.then(handleSuccess, handleError));
    };
    function handleError(response) {
        if (
                !angular.isObject(response.data) ||
                !response.data.message
                ) {
            return($q.reject("An unknown error occurred."));
        }
        return($q.reject(response.data.message));
    }
    function handleSuccess(response) {
        return(response.data);
    }
});
