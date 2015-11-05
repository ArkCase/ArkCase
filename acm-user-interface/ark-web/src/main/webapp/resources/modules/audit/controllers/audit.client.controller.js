'use strict';

/**
 * @ngdoc controller
 * @name audit.controller:AuditController
 *
 * @description
 * {@link https://github.com/Armedia/ACM3/tree/develop/acm-user-interface/ark-web/src/main/webapp/resources/modules/audit/controllers/audit.client.controller.js modules/audit/controllers/audit.client.controller.js}
 *
 * The Audit module main controller
 */
angular.module('audit').controller('AuditController', ['$scope', '$sce', '$q','ConfigService', 'LookupService', 'AuditController.BuildUrl', 'UtilService',
	function($scope, $sce, $q,ConfigService, LookupService, BuildUrl, Util) {
		$scope.config = ConfigService.getModule({moduleId: 'audit'});
		$scope.$on('req-component-config', onConfigRequest);
		$scope.$on('send-type-id', getObjectValues);
		$scope.$on('send-date', getDateValues);

		$scope.objectType = null;
		$scope.objectId = null;
		$scope.dateFrom = null;
		$scope.dateTo = null;

		$scope.showIframe = showIframe;

		function onConfigRequest(e, componentId) {
			$scope.config.$promise.then(function(config){
				var componentConfig = _.find(config.components, {id: componentId})
				$scope.$broadcast('component-config', componentId, componentConfig);
			});
		}

		/**
		 * @ngdoc method
		 * @name getObjectValues
		 * @methodOf audit.controller:AuditController
		 *
		 * @description
		 * This function is callback function which gets called when "send-type-id" event is emitted.
		 * In this function values are being assigned for $scope.objectType and $scope.objectId from selected dropdown and input text
		 *
		 * @param {Object} e This is event object which have several useful properties and functions
		 * @param {String} selectedObjectType String that represents value that is selected from dropdown
		 * @param {String} inputObjectId String that represents value from text input(default is empty string "")
		 */
		function getObjectValues(e, selectedObjectType, inputObjectId){
			$scope.objectType = selectedObjectType;
			$scope.objectId = Util.goodValue(inputObjectId);
		}

		/**
		 * @ngdoc method
		 * @name getDateValues
		 * @methodOf audit.controller:AuditController
		 *
		 * @description
		 * This function is callback function which gets called when "send-date" event is emitted.
		 * In this function values are being assigned to $scope.dateFrom and $scope.dateTo from selected datepickers
		 * as string. Also if value for dateFrom is bigger than value for dateTo event is emitted.
		 *
		 * @param {Object} e This is event object which have several useful properties and functions
		 * @param {Object} dateFrom Object of type date that represents value for date chosen from dateFrom input
		 * @param {Object} dateTo Object of type date that represents value for date chosen from dateTo input
		 */
		function getDateValues(e, dateFrom, dateTo){
			$scope.dateFrom = moment(dateFrom).format($scope.config.dateFormat);
			$scope.dateTo = moment(dateTo).format($scope.config.dateFormat);

			if(moment($scope.dateFrom).isAfter($scope.dateTo)){
				$scope.$broadcast('fix-date-values', $scope.dateFrom, $scope.dateFrom);
			}
		}

		// Retrieves the properties from the acm-reports-server-config.properties file
		var acmReportsConfig = LookupService.getConfig({name: 'acm-reports-server-config'});

		// Retrieves the properties from the auditPlugin.properties file
		var auditPlugin = LookupService.getConfig({name: 'audit'});

		$q.all([acmReportsConfig.$promise, auditPlugin.$promise])
			.then(function(data) {
				$scope.acmReportsProperties = data[0];
				$scope.auditPluginProperties = data[1];

				$scope.pentahoHost = $scope.acmReportsProperties['PENTAHO_SERVER_URL'];
				$scope.pentahoPort = $scope.acmReportsProperties['PENTAHO_SERVER_PORT'];
				$scope.auditReportUri = $scope.auditPluginProperties['AUDIT_REPORT'];
			});

		/**
		 * @ngdoc method
		 * @name showIframe
		 * @methodOf audit.controller:AuditController
		 *
		 * @description
		 * This function is called when Generate Audit Report button is clicked.
		 * In $scope.auditReportUrl is setting builder url from BuildUrl service.
		 *
		 */
		function showIframe(){
			$scope.auditReportUrl = BuildUrl.getUrl($scope.pentahoHost, $scope.pentahoPort, $scope.auditReportUri,
			$scope.dateFrom, $scope.dateTo, $scope.objectType, $scope.objectId, $scope.config.pentahoDateFormat);
		}
	}
]);