'use strict';

angular.module('document-details').controller('Document.TagsModalController', ['$scope', '$q', '$modalInstance', 'ConfigService', '$scopeTag', 'Object.TagsService',
    function ($scope, $q, $modalInstance, ConfigService, $scopeTag, ObjectTagsService) {

        var promiseTypes = ObjectTagsService.retrieveAllTags().then(
            function (tags) {
                $scope.tags = tags;
                return tags;
            }
        );

        $scope.$watchCollection('selectedTag', function (newValue, oldValue) {
            $scopeTag.selectedTag = $scope.selectedTag;
        });

        $scope.ok = function () {
            $modalInstance.close({tag: $scope.tag});
        };
        $scope.cancel = function () {
            $modalInstance.dismiss('cancel');
        }
    }
]);