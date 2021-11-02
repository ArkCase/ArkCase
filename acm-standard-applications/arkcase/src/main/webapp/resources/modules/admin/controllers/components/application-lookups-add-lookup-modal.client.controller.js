'use strict';

angular.module('admin').controller('Admin.AddLookupModalController', [ '$scope', '$modal', '$modalInstance', 'params', 'Object.LookupService', 'RegexValidationService', 'ObjectService',function($scope, $modal, $modalInstance, params, ObjectLookupService, RegexValidationService, ObjectService) {

    $scope.entry = params.entry;

    $scope.lookupTypes = ObjectLookupService.getLookupTypes;

    $scope.validateInput = function(inputValue)
    {
        var validatedObject = $scope.entry.lookupType !== 'standardLookup' ?
          RegexValidationService.validateInput(inputValue, ObjectService.RegexTypes.RULE_REGEX)
        : RegexValidationService.validateInput(inputValue, ObjectService.RegexTypes.LOOKUP_REGEX);
        $scope.entry.name = validatedObject.inputValue;
        $scope.showError = validatedObject.showRegexError;
    }

    $scope.onClickCancel = function() {
        $modalInstance.dismiss('Cancel');
    };

    $scope.onClickOk = function() {
        $modalInstance.close({
            entry: $scope.entry
        });
    };
} ]);