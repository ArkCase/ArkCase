'use strict';

angular.module('time-tracking').controller('TimeTracking.TagsModalController', ['$scope', '$q', '$modalInstance', 'ConfigService', 'Object.TagsService', 'Tags.TagsService', 'MessageService', '$translate', 'Search.AutoSuggestService',
    function ($scope, $q, $modalInstance, ConfigService, ObjectTagsService, TagsService, messageService, $translate, AutoSuggestService) {

        $scope.tags = [];
        $scope.modalInstance = $modalInstance;
        $scope.checkTag = checkTag;
        $scope.loadTags = loadTags;

        ConfigService.getComponentConfig("time-tracking", "tags").then(function (componentConfig) {
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
            selectedTag.object_id_s = selectedTag.id.indexOf("-") >= 0 ? selectedTag.id.split("-")[0] : selectedTag.id;
            selectedTag.tag_s = selectedTag.title_parseable;
            if (!selectedTag.object_id_s) {
                var tagsCreated = _.filter($scope.createdTags, function (tag) {
                    return tag.tagName == selectedTag.tags_s || tag.tagDescription == selectedTag.tags_s
                        || tag.tagText == selectedTag.tags_s;
                });
                if (tagsCreated.length == 0) {
                    ObjectTagsService.createTag(selectedTag.tags_s, selectedTag.tags_s, selectedTag.tags_s).then(
                        function (tagCreated) {
                            //add newly created tag 
                            _.remove($scope.tags, function (tag) {
                                return selectedTag.tags_s == tag.tags_s;
                            });
                            var tagToAdd = angular.copy(tagCreated);
                            tagToAdd.tags_s = selectedTag.tags_s;
                            $scope.tags.push(tagToAdd);
                        }
                    )
                }
                else {
                    messageService.info($translate.instant('timeTracking.comp.tags.message.tagExists'));
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
            AutoSuggestService.autoSuggest(query, "QUICK", $scope.config.autoSuggestObjectType).then(function (tags) {
                deferred.resolve(tags);
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