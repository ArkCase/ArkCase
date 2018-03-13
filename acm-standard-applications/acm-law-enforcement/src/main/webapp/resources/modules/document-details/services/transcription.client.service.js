'use strict';

angular.module('document-details').factory('DocumentDetails.TranscriptionAppService', [ '$http', function($http) {
    return {

        getTranscribeObject : function() {
            return $http({
                method : 'GET',
                url : '/arkcase/modules/document-details/services/mockup-data.json'
            });

        }
    }

} ]);