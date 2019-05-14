'use strict';

/**
 * @ngdoc service
 * @name admin.service:Admin.TranscriptionManagementService
 *
 * @description
 * Contains REST calls for Admin Transcribe Configuration
 *
 * The Admin.TranscriptionManagementService provides $http services for Transcribe Configuration.
 */
angular.module('admin').factory('Admin.TranscriptionManagementService', [ '$http', function($http) {
    return {
        /**
         * @ngdoc method
         * @name getTranscribeConfiguration
         * @methodOf admin.service:Admin.TranscriptionManagementService
         *
         * @description
         * Gets the transcribe configuration.
         *
         *
         * @returns {Object} http promise
         */
        getTranscribeConfiguration: function() {
            return $http({
                method: 'GET',
                url: 'api/v1/plugin/admin/transcribe/configuration'
            });
        },

        /**
         * @ngdoc method
         * @name saveTranscribeConfiguration
         * @methodOf admin.service:Admin.TranscriptionManagementService
         *
         * @description
         * Performs saving of the transcribe configuration.
         *
         * @param {Object} transcribeConfig - the configuration that should be saved
         *
         * @returns {Object} http promise
         */
        saveTranscribeConfiguration: function(transcribeConfig) {
            return $http({
                method: 'POST',
                url: 'api/v1/plugin/admin/transcribe/configuration',
                data: transcribeConfig
            })

        },

        /**
         * @ngdoc method
         * @name getTranscriptionFailureReason
         * @methodOf admin.service:Admin.TranscriptionManagementService
         *
         * @description
         * Gets transcription failure reason
         *
         * @param mediaVersionId - id of the failed media version transcription object
         *
         * @returns {Object} http promise
         */
        getTranscriptionFailureReason: function(mediaVersionId) {
            return $http({
                method: 'GET',
                url: 'api/v1/service/transcribe/mediaFailure/' + mediaVersionId
            });
        }
    };

} ]);
