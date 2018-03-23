'use strict';

angular.module('document-details').factory('DocumentDetails.TranscriptionAppService', [ '$http', function($http) {
    return ({
        getTranscribeObject : getTranscribeObject
    });

    function getTranscribeObject(mediaVersionId) {
        return $http({
            method : 'GET',
            url : 'api/v1/service/transcribe/media/' + mediaVersionId
        });
    }
    ;

} ]);