'use strict';

/**
 * @ngdoc controller
 * @name cost-tracking.controller:CostTrackingController
 *
 * @description
 * {@link https://github.com/Armedia/ACM3/blob/develop/acm-user-interface/ark-web/src/main/webapp/resources/modules/cost-tracking/controllers/cost-tracking.client.controller.js modules/cost-tracking/controllers/cost-tracking.client.controller.js}
 *
 * The Cost Tracking module main controller
 */
angular.module('cost-tracking').controller('CostTrackingController', ['$scope', '$stateParams', '$state', '$translate'
	, 'UtilService', 'ConfigService', 'CostTracking.InfoService', 'ObjectService', 'Helper.ObjectBrowserService'
    , function ($scope, $stateParams, $state, $translate
		, Util, ConfigService, CostTrackingInfoService, ObjectService, HelperObjectBrowserService) {

		new HelperObjectBrowserService.Content({
			scope: $scope
			, state: $state
			, stateParams: $stateParams
			, moduleId: "cost-tracking"
			, getObjectInfo: CostTrackingInfoService.getCostsheetInfo
			, updateObjectInfo: CostTrackingInfoService.saveCostsheetInfo
			, initComponentLinks: function (config) {
				return HelperObjectBrowserService.createComponentLinks(config, ObjectService.ObjectTypes.COSTSHEET);
			}
		});

	}
]);