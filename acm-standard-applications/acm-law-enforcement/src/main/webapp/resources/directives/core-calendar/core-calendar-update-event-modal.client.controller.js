'use strict';

angular.module('directives').controller('Directives.CoreCalendarUpdateEventController', ['$scope', '$modalInstance', 'params', 'Object.CalendarService', '$rootScope', '$modal', 'coreCalendarConfig',
	function($scope, $modalInstance, params, CalendarService, $rootScope, $modal, coreCalendarConfig) {
		$scope.eventDataModel = params.eventDataModel;
		$scope.coreCalendarConfig = coreCalendarConfig;
		$scope.objectId = params.objectId;
		$scope.objectType = params.objectType;
		$scope.updateModel = {
			updateMaster: false
		};

		$scope.updateEvent = function() {
			$scope.modalScope = $rootScope.$new();

			if($scope.updateModel.updateMaster) {
				CalendarService.getCalendarEventDetails($scope.objectType, $scope.objectId, $scope.eventDataModel.eventId, true).then(function(res) {
					$scope.modalScope.existingEvent = res.data;
					openEditModal();
				});

			} else {
				$scope.modalScope.existingEvent = $scope.eventDataModel;
				openEditModal();
			}
		};

		var openEditModal = function() {
			var modalInstance = $modal.open({
				animation: true,
				templateUrl: 'directives/core-calendar/core-calendar-new-event-modal.client.view.html',
				controller: 'Directives.CoreCalendarNewEventModalController',
				size: 'lg',
				backdrop: 'static',
				scope: $scope.modalScope,
				resolve: {
					coreCalendarConfig: function() {
						return $scope.coreCalendarConfig;
					},
					params: function() {
						return {
							objectType: $scope.objectType,
							objectId: $scope.objectId,
							updateMaster: $scope.updateModel.updateMaster
						};
					}
				}
			});

			modalInstance.result.then(function(data) {
				$modalInstance.close('EDIT_EVENT');
			}, function() {
				$modalInstance.dismiss();
			});
		};

		$scope.cancel = function() {
			$modalInstance.dismiss();
		};
	}
]);