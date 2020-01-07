'use strict';

angular.module('admin').controller(
        'Admin.StateOfArkcaseController',
        [ '$scope', 'Admin.StateOfArkcaseService', '$translate', 'UtilService', 'Util.DateService', 'FileSaver', 'Blob',
                function($scope, StateOfArkcaseService, $translate, Util, UtilDateService, FileSaver, Blob) {
                    $scope.date = new Date();
                    $scope.opened = false;

                    $scope.download = function() {
                        var dateStr = new Date($scope.date).toISOString().substring(0, 10);
                        StateOfArkcaseService.getStateOfArkcase(dateStr).then(function(responseData) {
                            var data = new Blob([ responseData.data ], {
                                type : 'application/octet-stream'
                            });
                            FileSaver.saveAs(data, 'state_of_arkcase-' + dateStr + '.zip');
                        });

                    };
                } ]);