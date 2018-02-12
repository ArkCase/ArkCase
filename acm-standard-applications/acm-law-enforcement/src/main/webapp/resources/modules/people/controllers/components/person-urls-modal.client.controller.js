angular.module('people').controller(
        'People.UrlsModalController',
        [ '$scope', '$translate', '$modalInstance', 'Object.LookupService', 'params',
                function($scope, $translate, $modalInstance, ObjectLookupService, params) {

                    ObjectLookupService.getContactMethodTypes().then(function(contactMethodTypes) {
                        $scope.urlTypes = _.find(contactMethodTypes, {
                            key : 'url'
                        }).subLookup;
                        return contactMethodTypes;
                    });

                    //This function checks for Url is  valid or not
                    //You can add patterns if more complicated valid  url is required.

                    function validate() {
                        var urls = document.getElementById("urls").value;
                        var pattern = /(http|https):\/\/(\w+:{0,1}\w*@)?(\S+)(:[0-9]+)?(\/|\/([\w#!:.?+=&%@!\-\/]))?/;
                        if (pattern.test(urls)) {
                            return true;
                        }
                        alert("Url is not valid! Please type valid Url");
                        return false;

                    }
                    $scope.url = params.url;
                    $scope.isEdit = params.isEdit;
                    $scope.isDefault = params.isDefault;
                    $scope.hideNoField = params.isDefault;

                    $scope.onClickCancel = function() {
                        $modalInstance.dismiss('Cancel');
                    };
                    $scope.onClickOk = function() {
                        if (validate()) {
                            $modalInstance.close({
                                url : $scope.url,
                                isDefault : $scope.isDefault,
                                isEdit : $scope.isEdit
                            });
                        }
                    };
                } ]);
