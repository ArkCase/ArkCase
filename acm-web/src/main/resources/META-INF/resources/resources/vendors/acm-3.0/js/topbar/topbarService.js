/**
 * Topbar.Service
 *
 * manages all service call to application server
 *
 * @author jwu
 */
Topbar.Service = {
    create : function() {
        if (this.Asn.create) {this.Asn.create();}
    }
    ,initialize: function() {
        if (this.Asn.initialize) {this.Asn.initialize();}
    }

    ,Asn: {
        create : function() {
        }
        ,initialize: function() {
        }

        ,API_RETRIEVE_ASN_LIST_       : "/resources/asn.json"
        ,API_UPDATE_ASN_LIST          : "to be determined"

        ,retrieveAsnList: function(user) {
            return; //wait for back end implementation
            Acm.Service.asyncGet(
                function(response) {
                    if (response.hasError) {
                        Topbar.Controller.Asn.onModelChangedAsnListError(response.errorMsg);
                    } else {
                        var asnList = response;
                        Topbar.Model.Asn.setAsnList(asnList);
                        Topbar.Controller.Asn.onModelChangedAsnList(asnList);
                    }
                }
                ,App.getContextPath() + this.API_RETRIEVE_ASN_LIST_
            )
        }
        ,updateAsnList: function(asnList) {
            return; //wait for back end implementation
            Acm.Service.asyncPost(
                function(response) {
                    if (response.hasError) {
                        Topbar.Controller.Asn.onModelChangedAsnListUpdateError(response.errorMsg);
                    } else {
                        Topbar.Controller.Asn.onModelChangedAsnListUpdateSuccess();
                    }
                }
                ,App.getContextPath() + this.API_UPDATE_ASN_LIST
                ,asnList
            )
        }


    } //Asn



    ,API_TYPEAHEAD_SUGGESTION       : "/resources/ctrs.json"
//    ,API_GET_TYPEAHEAD_TERMS       : "/api/latest/plugin/complaint/types"
//
//
//    ,getTypeAheadTerms : function() {
//        var typeAheadTerms = Topbar.Object.getTypeAheadTerms();
//        if (Acm.isEmpty()) {
//            Acm.Ajax.asyncGet(App.getContextPath() + this.API_GET_TYPEAHEAD_TERMS
//                ,Topbar.Callback.EVENT_TYPEAHEAD_TERMS_RETRIEVED
//            );
//        } else {
//            Topbar.Callback.onTypeAheadTermsRetrieved(Acm.Dispatcher, typeAheadTerms);
//        }
//
//    }

};

