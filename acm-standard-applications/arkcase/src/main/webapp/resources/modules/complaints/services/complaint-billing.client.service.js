'use strict';

angular.module('services').factory('Complaint.BillingService', ['$http', function($http){

    return {
        getBillingItems: function (parentObjectId, parentObjectType) {
            return $http({
                url: 'api/latest/plugin/billing/items',
                method: 'GET',
                isArray: true,
                params: {
                    parentObjectId: parentObjectId,
                    parentObjectType: parentObjectType
                }
            });
        },
        addBillingItem: function(data) {
            return $http({
                url: 'api/latest/plugin/billing/items',
                method: 'POST',
                data: data
            })
        },
        getBillingInvoices: function (parentObjectId, parentObjectType) {
            return $http({
                url: 'api/latest/plugin/billing/invoices',
                method: 'GET',
                isArray: true,
                params: {
                    parentObjectId: parentObjectId,
                    parentObjectType: parentObjectType
                }
            });
        },
        createBillingInvoice: function(data) {
            return $http({
                url: 'api/latest/plugin/billing/invoices',
                method: 'POST',
                data: data
            })
        },
        generateBillingInvoiceDocument: function (data) {
            return $http({
                url: 'api/latest/plugin/billing/invoices/document',
                method: 'PUT',
                data: data
            });
        },
        sendBillingInvoiceByEmail: function (data) {
            return $http({
                url: 'api/latest/plugin/complaint/billing/invoices/document/email',
                method: 'PUT',
                data: data
            });
        }
    }

}]);