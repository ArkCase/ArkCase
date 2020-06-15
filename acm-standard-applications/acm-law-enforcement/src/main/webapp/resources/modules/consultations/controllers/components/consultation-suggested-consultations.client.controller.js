'use strict';

angular.module('consultations').controller(
    'Consultations.SuggestedConsultationsController',
    ['$scope', '$translate', '$stateParams', 'Helper.UiGridService', 'UtilService', 'Helper.ObjectBrowserService', 'Consultation.InfoService', 'Consultation.SuggestedConsultations',
    function ($scope, $translate, $stateParams, HelperUiGridService,  Util, HelperObjectBrowserService, ConsultationInfoService, SuggestedConsultationsService) {


        new HelperObjectBrowserService.Component({
            scope: $scope,
            stateParams: $stateParams,
            moduleId: "consultations",
            componentId: "suggestedConsultations",
            retrieveObjectInfo: ConsultationInfoService.getConsultationInfo,
            validateObjectInfo: ConsultationInfoService.validateConsultationInfo,
            onConfigRetrieved: function(componentConfig) {
                return onConfigRetrieved(componentConfig);
            },
            onObjectInfoRetrieved: function(objectInfo) {
                onObjectInfoRetrieved(objectInfo);
            }
        });

        var gridHelper = new HelperUiGridService.Grid({
            scope: $scope
        });
        var onConfigRetrieved = function(config) {

            $scope.config = config;
            $scope.objectTypeValue = config.objectTypeValue;

            gridHelper.setColumnDefs(config);
            gridHelper.setBasicOptions(config);
            gridHelper.disableGridScrolling(config);

            $scope.gridOptions = {
                enableColumnResizing: true,
                enableRowSelection: true,
                enableRowHeaderSelection: false,
                multiSelect: false,
                noUnselect: false,
                columnDefs: $scope.config.columnDefs,
                paginationPageSizes: $scope.config.paginationPageSizes,
                paginationPageSize: $scope.config.paginationPageSize,
                totalItems: 0,
                data: []
            };
        };

        function retrieveGridData(){
            SuggestedConsultationsService.getSuggestedConsultations($scope.objectInfo.title, $scope.objectInfo.id).then(function(data){
                $scope.suggestedConsultations = data.data;
                $scope.gridOptions = $scope.gridOptions || {};
                $scope.gridOptions.data = $scope.suggestedConsultations;
                $scope.gridOptions.totalItems = $scope.suggestedConsultations.length;
            });
        }

        $scope.getObjectTypeValue = function (key) {
            return $scope.objectTypeValue[key];
        };

        var onObjectInfoRetrieved = function(objectInfo) {
            $scope.objectInfo = objectInfo;
            retrieveGridData();
        };

        $scope.onClickObjLink = function(event, rowEntity) {
            event.preventDefault();

            var targetType = Util.goodMapValue(rowEntity, "objectType");
            var targetId = Util.goodMapValue(rowEntity, "consultationId");
            gridHelper.showObject(targetType, targetId);
        };

    }]);