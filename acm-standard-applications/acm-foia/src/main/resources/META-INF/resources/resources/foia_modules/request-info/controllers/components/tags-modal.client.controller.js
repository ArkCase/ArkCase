'use strict';

angular.module('cases').controller('RequestInfo.TagsModalController',
        [ '$scope', '$q', '$modalInstance', 'ConfigService', 'Object.TagsService', 'Tags.TagsService', 'MessageService', '$translate', function($scope, $q, $modalInstance, ConfigService, ObjectTagsService, TagsService, messageService, $translate) {

            $scope.tags = [];
            $scope.modalInstance = $modalInstance;
            $scope.checkTag = checkTag;
            $scope.loadTags = loadTags;

            ConfigService.getComponentConfig("request-info", "tags").then(function(componentConfig) {
                $scope.config = componentConfig;
                return componentConfig;
            });

            var promiseTypes = ObjectTagsService.getTags().then(function(tags) {
                $scope.createdTags = tags;
                return tags;
            });

            function checkTag(selectedTag) {
                // Check if tag is created. If not, create new tag
                if (!selectedTag.object_id_s) {
                    var tagsCreated = _.filter($scope.createdTags, function(tag) {
                        return tag.tagName == selectedTag.tags_s || tag.tagDescription == selectedTag.tags_s || tag.tagText == selectedTag.tags_s;
                    });
                    if (tagsCreated.length == 0) {
                        ObjectTagsService.createTag(selectedTag.tags_s, selectedTag.tags_s, selectedTag.tags_s).then(function(tagCreated) {
                            //add newly created tag 
                            _.remove($scope.tags, function(tag) {
                                return selectedTag.tags_s == tag.tags_s;
                            });
                            var tagToAdd = angular.copy(tagCreated);
                            tagToAdd.tags_s = selectedTag.tags_s;
                            tagToAdd.title_parseable = selectedTag.tags_s;
                            tagToAdd.id = tagToAdd.id + '-TAG';
                            $scope.tags.push(tagToAdd);
                            $scope.createdTags.push(tagToAdd);
                        })
                    } else {
                        messageService.info($translate.instant('cases.comp.tags.message.tagExists'));
                        _.remove(tagsCreated, function() {
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
                TagsService.searchTags({
                    query: query,
                    filter: 'fq=' + $scope.config.filters
                }).then(function(tags) {
                    deferred.resolve(tags);
                });
                return deferred.promise;
            }

            $scope.onClickOk = function() {
                $modalInstance.close($scope.tags);
            };
            $scope.onClickCancel = function() {
                $modalInstance.dismiss('cancel');
            }
        } ]);