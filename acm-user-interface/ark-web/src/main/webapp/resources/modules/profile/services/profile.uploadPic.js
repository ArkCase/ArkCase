'use strict';
angular.module('profile').service('userPicService', function ($http, $q) {
    return({
        changePic: changePic,
        getPic: getPic
    });
    function changePic(formData) {
        var request = $http({
            method: "POST",
            processData: false,
            url: "proxy/arkcase/api/latest/service/ecm/upload",
            data: formData,
            headers: {'Content-Type': undefined}
        });
        return(request.then(handleSuccess, handleError));
    };
    function getPic() {
        
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