'use strict';

angular.module('consultations').controller(
    'ConsultationsListController',
    [ '$scope', '$state', '$stateParams', '$translate', 'UtilService', 'ObjectService', 'Consultation.ListService', 'Consultation.InfoService', 'Helper.ObjectBrowserService', 'ServCommService', 'MessageService', 'Authentication',
        function($scope, $state, $stateParams, $translate, Util, ObjectService, ConsultationListService, ConsultationInfoService, HelperObjectBrowserService, ServCommService, MessageService, Authentication) {

            var eventName = "object.inserted";
            $scope.userPrivileges = [];
            Authentication.getUserPrivileges().then(function (data) {
                $scope.userPrivileges = data;
            });

            $scope.$bus.subscribe(eventName, function(data) {
                if (data.objectType === ObjectService.ObjectTypes.CONSULTATION) {

                    var objectTypeString = $translate.instant('common.objectTypes.' + data.objectType);
                    var objectWasCreatedMessage = $translate.instant('common.objects.objectWasCreatedMessage ', {
                        objectTypeString: objectTypeString,
                        objectId: data.objectId
                    });
                    MessageService.info(objectWasCreatedMessage);
                }
            });

            //"treeConfig", "treeData", "onLoad", and "onSelect" will be set by Tree Helper
            new HelperObjectBrowserService.Tree({
                scope: $scope,
                state: $state,
                stateParams: $stateParams,
                moduleId: "consultations",
                resetTreeData: function() {
                    return ConsultationListService.resetConsultationsTreeData();
                },
                updateTreeData: function(start, n, sort, filters, query, nodeData) {
                    return ConsultationListService.updateConsultationsTreeData(start, n, sort, filters, query, nodeData);
                },
                getTreeData: function(start, n, sort, filters, query) {
                    return ConsultationListService.queryConsultationsTreeData(start, n, sort, filters, query);
                },
                getNodeData: function(consultationId) {
                    return ConsultationInfoService.getConsultationInfo(consultationId);
                },
                makeTreeNode: function(consultationInfo) {
                    return {
                        nodeId: Util.goodValue(consultationInfo.id, 0),
                        nodeType: ObjectService.ObjectTypes.CONSULTATION,
                        nodeNumber: Util.goodValue(consultationInfo.consultationNumber),
                        nodeTitle: Util.goodValue(consultationInfo.title),
                        nodeToolTip: Util.goodValue(consultationInfo.title)
                    };
                }
            });

        } ]);