angular.module('admin').factory('DocumentDetails.BillingItemPrivilegeService', [ '$http', function($http) {

    return ({
        getBillingItemPrivilege: getBillingItemPrivilege
    });

    function getBillingItemPrivilege() {
        return $http({
            method: 'GET',
            url: 'api/latest/plugin/billing/billingItemPrivilege'
        });
    }
} ]);