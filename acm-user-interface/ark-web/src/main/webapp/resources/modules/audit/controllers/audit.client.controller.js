'use strict';

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

		function getObjectValues(e, selectedObjectType, inputObjectId){
			$scope.objectType = selectedObjectType;
			$scope.objectId = Util.goodValue(inputObjectId);
		}

		function getDateValues(e, dateFrom, dateTo){
			$scope.dateFrom = moment(dateFrom).format($scope.config.dateFormat);
			$scope.dateTo = moment(dateTo).format($scope.config.dateFormat);

			 /*Check if user trying to select value for dateFrom that is bigger than dateTo.
			 If that is the case make value for dateTo to be same as dateFrom*/
			if (((new Date($scope.dateTo))) < ((new Date($scope.dateFrom)))){
				$scope.$emit('fix-date-values', $scope.dateFrom, $scope.dateFrom);
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

		function showIframe(){
			$scope.auditReportUrl = BuildUrl.getUrl($scope.pentahoHost, $scope.pentahoPort, $scope.auditReportUri,
			$scope.dateFrom, $scope.dateTo, $scope.objectType, $scope.objectId, $scope.config.pentahoFormatDate);
		}
	}
]);