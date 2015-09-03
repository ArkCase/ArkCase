'use strict';

angular.module('cases').controller('Cases.ParticipantsController', ['$scope', '$stateParams', '$q', 'Acm', 'CasesService', 'LookupService',
    function($scope, $stateParams, $q, Acm, CasesService, LookupService) {
        $scope.$emit('req-component-config', 'participants');

        $scope.addNew = function() {
            var lastPage = $scope.gridApi.pagination.getTotalPages();
            $scope.gridApi.pagination.seek(lastPage);
            $scope.gridOptions.data.push({});
        };
        $scope.saveRow = function(row) {
            //$scope.gridApi.rowEdit.flushDirtyRows( $scope.gridApi.grid );
            alert("saveRow=" + row);
        };
        $scope.deleteRow = function(row) {
            var id = Acm.goodObjValue([row, "entity", "id"], 0);
            var idx = _.findIndex($scope.gridOptions.data, 'id', id);
            if (0 <= idx) {
                $scope.gridOptions.data.splice(idx, 1);

                //
                // save data to server
                //
            }
        };

        $scope.config = null;
        $scope.$on('component-config', applyConfig);
        function applyConfig(e, componentId, config) {
            if (componentId == 'participants' && !$scope.config) {

                var cdef = {name: "act"
                    ,cellEditableCondition: false
                    //,enableFiltering: false
                    //,enableHiding: false
                    //,enableSorting: false
                    //,enableColumnResizing: false
                    ,width: 40
                    ,headerCellTemplate: "<span></span>"
                    //,cellTemplate: "<span><i class='fa fa-trash-o fa-lg' ng-click='grid.appScope.deleteRow($event, row)'></i></span>"
                    ,cellTemplate: "<span><i class='fa fa-trash-o fa-lg' ng-click='grid.appScope.deleteRow(row)'></i></span>"
                };
                config.columnDefs.push(cdef);

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

                            //
                            //Insert code here to save data to service   //Acm.log("do save, newValue=" + newValue);
                            //

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


                        //gridApi.rowEdit.on.saveRow($scope, function(rowEntity) {
                        //    var z = 1;
                        //});
                        //gridApi.core.on.rowsRendered($scope, function(rowEntity) {
                        //    var z = 1;
                        //});
                    }
                };


                var promiseTypes = Acm.servicePromise({
                    service: LookupService.getParticipantTypes
                    ,callback: function(data){
                        $scope.participantTypes = [{type: "*", name: "*"}];
                        _.forEach(data, function(v, k) {
                            $scope.participantTypes.push({type: k, name: v});
                        });
                        return $scope.participantTypes;
                    }
                });
                var promiseUsers = Acm.servicePromise({
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
                var promiseGroups = Acm.servicePromise({
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
