'use strict';

angular.module('cases').controller('CaseParticipantsController', ['$scope', '$stateParams', "$q", 'CasesService', 'LookupService',
    function($scope, $stateParams, $q, CasesService, LookupService) {
        $scope.$emit('req-component-config', 'participants');

        $scope.config = null;
        $scope.$on('component-config', applyConfig);
        function applyConfig(e, componentId, config) {
            if (componentId == 'participants' && !$scope.config) {
                $scope.config = config;
                $scope.gridOptions = {
                    enableColumnResizing: true,
                    enableRowSelection: true,
                    enableRowHeaderSelection: false,
                    multiSelect: false,
                    noUnselect : false,

                    paginationPageSizes: config.paginationPageSizes,
                    paginationPageSize: config.paginationPageSize,
                    enableFiltering: config.enableFiltering,

                    columnDefs: config.columnDefs,
                    onRegisterApi: function(gridApi) {
                        $scope.gridApi = gridApi;
                        gridApi.edit.on.afterCellEdit($scope,function(rowEntity, colDef, newValue, oldValue){
                            Acm.log("do save, newValue=" + newValue);
                            if ("participantTypes" === colDef.lookup) {
                                if ("*" === newValue) {
                                    rowEntity.participantNames = [
                                        {id: "*", name: "*"}
                                    ];
                                } else if ("owning group" === newValue) {
                                    rowEntity.participantNames = $scope.participantGroups;
                                } else {
                                    rowEntity.participantNames = $scope.participantUsers;
                                }

                                $scope.$apply();
                            }
                        });
                    }
                };


                var promiseTypes = Acm.servicePromise($q, {
                    service: LookupService.getParticipantTypes
                    ,callback: function(data){
                        $scope.participantTypes = [{type: "*", name: "*"}];
                        _.forEach(data, function(v, k) {
                            $scope.participantTypes.push({type: k, name: v});
                        });
                        return $scope.participantTypes;
                    }
                });
                var promiseUsers = Acm.servicePromise($q, {
                    service: LookupService.getUsers
                    ,callback: function(data){
                        $scope.participantUsers = [];
                        var arr = Acm.goodObjValue([data, "response", "docs"], []);
                        for (var i = 0; i < arr.length; i++) {
                            var user = {};
                            user.id = arr[i].object_id_s;
                            user.name = arr[i].name;
                            $scope.participantUsers.push(user);
                        }
                        return $scope.participantUsers;
                    }
                });
                var promiseGroups = Acm.servicePromise($q, {
                    service: LookupService.getGroups
                    ,callback: function(data){
                        $scope.participantGroups = [];
                        var arr = Acm.goodObjValue([data, "response", "docs"], []);
                        for (var i = 0; i < arr.length; i++) {
                            var group = {};
                            group.id = arr[i].object_id_s;
                            group.name = arr[i].name;
                            $scope.participantGroups.push(group);
                        }
                        return $scope.participantGroups;
                    }
                });

                $q.all([promiseTypes, promiseUsers, promiseGroups]).then(function(data) {
                    $scope.gridOptions.enableRowSelection = false;    //need to turn off for inline edit
                    //$scope.gridOptions.enableCellEdit = true;
                    //$scope.gridOptions.enableCellEditOnFocus = true;
                    for (var i = 0; i < $scope.config.columnDefs.length; i++) {
                        if ("participantTypes" == $scope.config.columnDefs[i].lookup) {
                            $scope.gridOptions.columnDefs[i].enableCellEdit = true;
                            $scope.gridOptions.columnDefs[i].editableCellTemplate = "ui-grid/dropdownEditor";
                            $scope.gridOptions.columnDefs[i].editDropdownIdLabel = "type";
                            $scope.gridOptions.columnDefs[i].editDropdownValueLabel = "name";
                            $scope.gridOptions.columnDefs[i].editDropdownOptionsArray = $scope.participantTypes;
                            $scope.gridOptions.columnDefs[i].cellFilter = "mapParticipantTypeName: col.colDef.editDropdownOptionsArray";


                        } else if ("participantNames" == $scope.config.columnDefs[i].lookup) {
                            $scope.gridOptions.columnDefs[i].enableCellEdit = true;
                            $scope.gridOptions.columnDefs[i].editableCellTemplate = "ui-grid/dropdownEditor";
                            $scope.gridOptions.columnDefs[i].editDropdownValueLabel = "name";
                            $scope.gridOptions.columnDefs[i].editDropdownRowEntityOptionsArrayPath = "participantNames";
                            $scope.gridOptions.columnDefs[i].cellFilter = "mapParticipantName: row.entity.participantNames";
                        }
                    }

                    //$scope.$apply();
                });

            }
        }


        $scope.$on('case-retrieved', function(e, data){
            if (data) {
                $scope.gridOptions.data = data.participants;
                _.each($scope.gridOptions.data, function(item) {
                    if ("*" === item.participantType) {
                        item.participantNames = [
                            {id: "*", name: "*"}
                        ];
                    } else if ("owning group" === item.participantType) {
                        item.participantNames = $scope.participantGroups;
                    } else {
                        item.participantNames = $scope.participantUsers;
                    }
                });
            }
        });
    }
])

.filter('mapParticipantTypeName', function() {
    return function(input, typeNames) {
        var find = _(typeNames).filter(function(typeName) {
            return typeName.type == input;
        })
        .pluck("name")
        .value()
        ;

        return (0 < find.length)? find[0] : input;
    };

})
.filter('mapParticipantName', function() {
    return function(input, participantNames) {
        var find = _(participantNames).filter(function(participantName) {
            return participantName.id == input;
        })
        .pluck("name")
        .value()
        ;

        return (0 < find.length)? find[0] : input;
    };

})
;

//
//jwu: Commented out code are alternative solutions; will remove when most suitable solution chosen
//
//.filter('mapParticipantTypeName', ["LookupService", function(LookupService) {
//	var participantTypeHash = null;
//	var serviceInvoked = false;
//
//	var doFilter = function(input) {
//		if (!input) {
//			return '';
//		} else if ("*" === input) {
//			return "*";
//		} else {
//			return participantTypeHash[input];
//		}
//	};
//
//	return function(input) {
//
//		if (null === participantTypeHash) {
//			if (!serviceInvoked) {
//				serviceInvoked = true;
//				LookupService.getParticipantTypes({}, function(data) {
//					participantTypeHash = data;
//				});
//			}
//			return input;
//			//return "..."; //placeholder while loading
//
//		} else {
//			return doFilter(input);
//		}
//	};
//}])
//
//.filter('mapParticipantTypeName', function() {
//	return function(input, participantTypeHash) {
//		if (!input) {
//			return '';
//		} else if ("*" === input) {
//			return "*";
//		} else if (!participantTypeHash) {
//			return input;
//		} else {
//			//return participantTypeHash[input];
//			return "ooo" + input;
//		}
//	};
//})
