'use strict';

angular.module('complaints').controller(
        'Complaints.GroupPickerController',
        [ '$scope', '$modal', '$modalInstance', '$translate', 'UtilService', 'ConfigService', '$q', 'owningGroup',
                function($scope, $modal, $modalInstance, $translate, Util, ConfigService, $q, owningGroup) {

                    ConfigService.getComponentConfig("complaints", "participants").then(function(componentConfig) {
                        $scope.config = componentConfig;
                    });

                    $scope.onClickOk = function() {
                        $modalInstance.close({
                            participant : $scope.participant
                        });
                    };
                    $scope.onClickCancel = function() {
                        $modalInstance.dismiss('cancel');
                    };
                    $scope.pickGroup = function() {

                        var params = {};
                        $scope.owningGroup = owningGroup;

                        params.header = $translate.instant("complaints.comp.groupPickerModal.searchGroupHeader");
                        params.filter = 'fq="object_type_s": GROUP';
                        params.extraFilter = '&fq="name": ';
                        params.config = Util.goodMapValue($scope.config, "dialogGroupPicker");

                        var modalInstance = $modal.open({
                            templateUrl : "modules/complaints/views/components/complaint-group-picker-search-modal.client.view.html",
                            controller : [ '$scope', '$modalInstance', 'params', function($scope, $modalInstance, params) {
                                $scope.modalInstance = $modalInstance;
                                $scope.header = params.header;
                                $scope.filter = params.filter;
                                $scope.extraFilter = params.extraFilter;
                                $scope.config = params.config;
                            } ],
                            animation : true,
                            size : 'lg',
                            backdrop : 'static',
                            resolve : {
                                params : function() {
                                    return params;
                                }
                            }
                        });
                        modalInstance.result.then(function(selected) {
                            if (!Util.isEmpty(selected)) {
                                $scope.participant.participantLdapId = selected.object_id_s;
                                $scope.participant.id = selected.id;
                                $scope.participant.selectedAssigneeName = selected.name;
                            }
                        });
                    };
                } ]);