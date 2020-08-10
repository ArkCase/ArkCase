'use strict';

angular.module('document-details').controller('Document.AnnotationTagsModalController',
    ['$scope', '$q', '$modalInstance', 'UtilService', 'params', function ($scope, $q, $modalInstance, Utils, params) {

        if (!params.annotationTags) {
            // Backward compatibility with Snowbound 4.10
            $scope.allAnnotationTags = convertToKeyName(params);
            $scope.selectedAnnotations = [];
        } else {
            $scope.allAnnotationTags = convertToKeyName(params.annotationTags);
            if (params.existingAnnotationTags) {
                $scope.selectedAnnotations = convertStringsToKeyName(params.existingAnnotationTags);
            } else {
                $scope.selectedAnnotations = [];
            }
        }

        $scope.annotationTags = [];
        $scope.annotationNotes = [];
        $scope.data = {
            "chooseObject": [],
            "selectedNotAuthorized": [],
            "selectedAuthorized": []
        };

        $scope.init = function () {
            $scope.data.selectedNotAuthorized = $scope.allAnnotationTags;
            $scope.data.selectedAuthorized = $scope.selectedAnnotations;
            $scope.data.firstSelectHide = true;
            $scope.data.hideFilter = true;
            for (var i = $scope.data.selectedNotAuthorized.length - 1; i >= 0; --i) {
                for (var j = 0; j < $scope.data.selectedAuthorized.length; j++) {
                    if ($scope.data.selectedNotAuthorized[i].key == $scope.data.selectedAuthorized[j].key) {
                        $scope.data.selectedNotAuthorized.splice(i,1);
                    }
                }
            }
        }
        $scope.onChange = function (selectedObject, authorized, notAuthorized) {
            var deferred = $q.defer();
            return deferred.promise;
        }

        $scope.init();

        $scope.addNote = function () {
            $scope.annotationNotes.push({'key': $scope.newNote, 'done': false})
            $scope.newNote = ''
        }

        $scope.deleteNote = function (index) {
            $scope.annotationNotes.splice(index, 1);
        }

        $scope.onClickOk = function () {
            $scope.annotationTags = convertToFlatArray($scope.data.selectedAuthorized);
            $scope.annotationNotes = convertToFlatArray($scope.annotationNotes);
            $modalInstance.close({annotationTags: $scope.annotationTags, annotationNotes: $scope.annotationNotes});
        };
        $scope.onClickCancel = function () {
            $modalInstance.dismiss('cancel');
        }

        function convertToKeyName(array) {
            if (!Utils.isArrayEmpty(array)) {
                return array.map(function (item) {
                    return {"key": item["key"], "name": item["key"], "value": item["value"]};
                });
            }

            return [];
        }

        function convertStringsToKeyName(array) {
            if (!Utils.isArrayEmpty(array)) {
                return array.map(function (item) {
                    return {"key": item, "name": item, "value": item};
                });
            }
            
            return [];
        }

        function convertToFlatArray(array) {
            if (!Utils.isArrayEmpty(array)) {
                return array.map(function (item) {
                    return item["key"];
                });
            }

            return [];
        }
    }]);
