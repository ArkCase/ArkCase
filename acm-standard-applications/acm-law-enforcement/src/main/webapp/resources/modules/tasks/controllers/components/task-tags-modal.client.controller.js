'use strict';

angular.module('cases').controller('Tasks.TagsModalController', ['$scope', '$q', '$modalInstance', 'ConfigService', 'Object.TagsService', 'Tags.TagsService', 'MessageService', '$translate', 'SearchService',
    function ($scope, $q, $modalInstance, ConfigService, ObjectTagsService, TagsService, messageService, $translate, SearchService) {

        $scope.tags = [];
        $scope.modalInstance = $modalInstance;
        $scope.checkTag = checkTag;
        $scope.loadTags = loadTags;

        ConfigService.getComponentConfig("tasks", "tags").then(function (componentConfig) {
            $scope.config = componentConfig;
            return componentConfig;
        });

        var promiseTypes = ObjectTagsService.getTags().then(
            function (tags) {
                $scope.createdTags = tags;
                return tags;
            }
        );

        function checkTag(selectedTag) {
            // Check if tag is created. If not, create new tag
            if (!selectedTag.id) {
                var tagsCreated = _.filter($scope.createdTags, function (tag) {
                    return tag.tagName == selectedTag.title_parseable || tag.tagDescription == selectedTag.title_parseable
                        || tag.tagText == selectedTag.title_parseable;
                });
                if (tagsCreated.length == 0) {
                    ObjectTagsService.createTag(selectedTag.title_parseable, selectedTag.title_parseable, selectedTag.title_parseable).then(
                        function (tagCreated) {
                            //add newly created tag 
                            _.remove($scope.tags, function (tag) {
                                return selectedTag.title_parseable == tag.title_parseable;
                            });
                            var tagToAdd = angular.copy(tagCreated);
                            tagToAdd.title_parseable = selectedTag.title_parseable;
                            $scope.tags.push(tagToAdd);
                        }
                    )
                }
                else {
                    messageService.info($translate.instant('tasks.comp.tags.message.tagExists'));
                    _.remove(tagsCreated, function () {
                        return selectedTag;
                    });
                    return false;
                }
            }
            return true;
        }

        // Load tags information
        function loadTags(query) {
            var deferred = $q.defer();
            autoSuggest(query, "QUICK", $scope.config.autoSuggestObjectType).then(function (tags) {
                deferred.resolve(tags);
            });
            return deferred.promise;
        }

        function autoSuggest(autoSuggestQuery, core, objectType) {
            var deferred = $q.defer();
            SearchService.queryAutoSuggestSearch({
                    query: autoSuggestQuery,
                    filter: "object_type_s:" + objectType,
                    core: core
                },
                function (res) {
                    deferred.resolve(res.response.docs);
                });
            return deferred.promise;
        }

        $scope.onClickOk = function () {
            $modalInstance.close($scope.tags);
        };
        $scope.onClickCancel = function () {
            $modalInstance.dismiss('cancel');
        }
    }
]);