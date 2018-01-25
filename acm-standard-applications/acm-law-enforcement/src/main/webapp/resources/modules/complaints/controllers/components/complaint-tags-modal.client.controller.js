'use strict';

angular.module('complaints').controller(
        'Complaints.TagsModalController',
        [
                '$scope',
                '$q',
                '$stateParams',
                '$modalInstance',
                'ConfigService',
                'Object.TagsService',
                'Tags.TagsService',
                'MessageService',
                '$translate',
                'Search.AutoSuggestService',
                'Complaint.InfoService',
                function($scope, $q, $stateParams, $modalInstance, ConfigService, ObjectTagsService, TagsService, messageService,
                        $translate, AutoSuggestService, ComplaintInfoService) {

                    $scope.tags = [];
                    $scope.modalInstance = $modalInstance;
                    $scope.checkTag = checkTag;
                    $scope.loadTags = loadTags;

                    ConfigService.getComponentConfig("complaints", "tags").then(function(componentConfig) {
                        $scope.config = componentConfig;
                        return componentConfig;
                    });

                    var promiseTypes = ObjectTagsService.getTags().then(function(tags) {
                        $scope.createdTags = tags;
                        return tags;
                    });

                    function checkTag(selectedTag) {
                        // Check if tag is created. If not, create new tag
                        if (selectedTag.id) {
                            selectedTag.object_id_s = selectedTag.id.indexOf("-") >= 0 ? selectedTag.id.split("-")[0] : selectedTag.id;
                        }
                        selectedTag.tags_s = selectedTag.title_parseable;
                        if (!selectedTag.object_id_s) {
                            var tagsCreated = _.filter($scope.createdTags, function(tag) {
                                return tag.tagName == selectedTag.tags_s || tag.tagDescription == selectedTag.tags_s
                                        || tag.tagText == selectedTag.tags_s;
                            });
                            if (tagsCreated.length == 0) {
                                ObjectTagsService.createTag(selectedTag.tags_s, selectedTag.tags_s, selectedTag.tags_s).then(
                                        function(tagCreated) {
                                            //add newly created tag
                                            _.remove($scope.tags, function(tag) {
                                                return selectedTag.tags_s == tag.tags_s;
                                            });
                                            var tagToAdd = angular.copy(tagCreated);
                                            tagToAdd.tags_s = selectedTag.tags_s;
                                            tagToAdd.title_parseable = selectedTag.tags_s;
                                            tagToAdd.id = tagToAdd.id + '-TAG';
                                            $scope.tags.push(tagToAdd);
                                        });
                            } else {
                                messageService.info($translate.instant('complaints.comp.tags.message.tagExists'));
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
                        AutoSuggestService.autoSuggest(query, "QUICK", $scope.config.autoSuggestObjectType).then(function(tags) {
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