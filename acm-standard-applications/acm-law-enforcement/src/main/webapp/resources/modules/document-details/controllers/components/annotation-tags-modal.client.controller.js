'use strict';

angular.module('document-details').controller('Document.AnnotationTagsModalController',
        [ '$scope', '$q', '$modalInstance', function($scope, $q, $modalInstance) {

            $scope.annotationTags = [];
            $scope.annotationNotes = [];
            $scope.onClickOk = function() {
                // TODO: populate $scope.annotationTags and $scope.annotationNotes with selected/added values on modal UI
                $modalInstance.close({annotationTags: $scope.annotationTags, annotationNotes: $scope.annotationNotes});
            };
            $scope.onClickCancel = function() {
                $modalInstance.dismiss('cancel');
            }
        } ]);