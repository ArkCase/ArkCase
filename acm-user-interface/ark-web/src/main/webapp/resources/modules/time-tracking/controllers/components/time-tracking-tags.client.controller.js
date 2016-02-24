'use strict';

angular.module('time-tracking').controller('TimeTracking.TagsController', ['$scope', '$q', '$stateParams', '$translate'
    , 'UtilService', 'ObjectService', 'TimeTracking.InfoService'
    , 'Helper.UiGridService', 'Helper.ObjectBrowserService', 'Object.TagsService', '$modal', 'MessageService'
    , function ($scope, $q, $stateParams, $translate
        , Util, ObjectService, TimeTrackingInfoService
        , HelperUiGridService, HelperObjectBrowserService, ObjectTagsService, $modal, messageService) {

		$scope.tags = [];
	
        var componentHelper = new HelperObjectBrowserService.Component({
            scope: $scope
            , stateParams: $stateParams
            , moduleId: "time-tracking"
            , componentId: "tags"
            , retrieveObjectInfo: TimeTrackingInfoService.getTimesheetInfo
            , validateObjectInfo: TimeTrackingInfoService.validateTimesheet
            , onConfigRetrieved: function (componentConfig) {
                return onConfigRetrieved(componentConfig);
            }
            , onObjectInfoRetrieved: function (objectInfo) {
            	onObjectInfoRetrieved(objectInfo);
            }
        });

        var gridHelper = new HelperUiGridService.Grid({scope: $scope});
        
        $scope.addNew = function () {
            var modalInstance = $modal.open({
                animation: $scope.animationsEnabled,
                templateUrl: 'modules/time-tracking/views/components/time-tracking-tags-modal.client.view.html',
                controller: 'TimeTracking.TagsModalController',
                size: 'lg'
            });

            modalInstance.result.then(function (tags) {            	
            	_.forEach(tags, function(tag) {         	
            		if(tag.id){
            			if(tag.object_id_s){
		            		var tagsFound = _.filter($scope.tags, function (tagAss) {
		                        return tagAss.id == tag.object_id_s;
		                    });
		            		if(tagsFound.length == 0) {
			            		ObjectTagsService.associateTag(componentHelper.currentObjectId, ObjectService.ObjectTypes.TIMESHEET, tag.object_id_s).then(
			                            function (returnedTag) {
			                            	var tagToAdd = angular.copy(returnedTag);
											tagToAdd.tagName = tag.tags_s;
			                                $scope.tags.push(tagToAdd);
			                                $scope.gridOptions.data = $scope.tags;
			                                $scope.gridOptions.totalItems = $scope.tags.length;
			                            }
			                        );
		            		}
		            		else {
		            			messageService.info(tag.tags_s + " " + $translate.instant('timeTracking.comp.tags.message.tagAssociated'));
		            			_.remove(tagsFound, function(){
		            				return tag;
		            			});
		            		}
            			}
            			else {
            				ObjectTagsService.associateTag(componentHelper.currentObjectId, ObjectService.ObjectTypes.TIMESHEET, tag.id).then(
		                            function () {
		                                $scope.tags.push(tag);
		                                $scope.gridOptions.data = $scope.tags;
		                                $scope.gridOptions.totalItems = $scope.tags.length;
		                            }
		                        );
            			}
            		}
            	});             
            	
            }, function () {
                // Cancel button was clicked.
            });
        };

        var onConfigRetrieved = function (config) {

        	$scope.config = config;
            gridHelper.setColumnDefs(config);
            gridHelper.setBasicOptions(config);
            gridHelper.disableGridScrolling(config);

        };

        var onObjectInfoRetrieved = function (objectInfo) {
            $scope.objectInfo = objectInfo;

            var currentObjectId = Util.goodMapValue(objectInfo, "id");
            if (Util.goodPositive(currentObjectId, false)) {
            	var promiseQueryTags = ObjectTagsService.getAssociateTags(currentObjectId, ObjectService.ObjectTypes.TIMESHEET);
                $q.all([promiseQueryTags]).then(function (data) {
                    $scope.tags = data[0];
                    $scope.gridOptions = $scope.gridOptions || {};
                    $scope.gridOptions.data = $scope.tags;
                    $scope.gridOptions.totalItems = $scope.tags.length;
                });
            }
        };
    }
]);