'use strict';

angular.module('document-details').controller('Document.AnnotationTagsModalController',
    ['$scope', '$q', '$modalInstance', 'UtilService', 'params', function ($scope, $q, $modalInstance, Utils, params) {

        $scope.allAnnotationTags = convertToKeyName(params);
        $scope.annotationTags = [];
        $scope.annotationNotes = [];
        $scope.data = {
            "chooseObject": [],
            "selectedNotAuthorized": [],
            "selectedAuthorized": []
        };

        $scope.init = function () {
            $scope.data.selectedNotAuthorized = $scope.allAnnotationTags;
            $scope.data.selectedAuthorized = [];
            $scope.data.firstSelectHide = true;
            $scope.data.hideFilter = true;
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

        function convertToFlatArray(array) {
            if (!Utils.isArrayEmpty(array)) {
                return array.map(function (item) {
                    return item["key"];
                });
            }

            return [];
        }
    }]);
