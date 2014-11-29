/**
 * Topbar.Service
 *
 * manages all service call to application server
 *
 * @author jwu
 */
Topbar.Service = {
    create : function() {
        if (this.Suggestion.create) {Topbar.Service.Suggestion.create();}
        if (this.Asn.create)        {Topbar.Service.Asn.create();}
    }
    ,initialize: function() {
        if (this.Suggestion.initialize) {Topbar.Service.Suggestion.initialize();}
        if (this.Asn.initialize)        {Topbar.Service.Asn.initialize();}
    }

    ,Suggestion: {
        create: function() {
        }
        ,initialize: function() {
        }
        ,API_TYPEAHEAD_SUGGESTION_BEGIN_      : "/api/latest/plugin/search/quickSearch?q=*"
        ,API_TYPEAHEAD_SUGGESTION_END         : "*&start=0&n=16"

        ,retrieveSuggestion: function(query, process){
            var url = App.getContextPath() + this.API_TYPEAHEAD_SUGGESTION_BEGIN_
                + query
                + this.API_TYPEAHEAD_SUGGESTION_END;

            $.ajax({
                url: url
                ,cache: false
                ,success: function(data){
                    if (Acm.Validator.validateSolrData(data)) {
                        var docs = data.response.docs;
                        Topbar.Model.Suggestion.buildSuggestion(query, docs);
                        Topbar.Controller.Suggestion.modelChangedSuggestion(process);
                    }
                }
            });
        }
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
                        Topbar.Controller.Asn.modelRetrievedAsnList(response);
                    } else {
                        var asnList = response;
                        Topbar.Model.Asn.setAsnList(asnList);
                        Topbar.Controller.Asn.modelRetrievedAsnList(asnList);
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
                        Topbar.Controller.Asn.modelUpdatedAsnList(response);
                    } else {
                        Topbar.Controller.Asn.modelUpdatedAsnList();
                    }
                }
                ,App.getContextPath() + this.API_UPDATE_ASN_LIST
                ,asnList
            )
        }

        ,updateAsnAction: function(asnId, action) {
            var asnList = this.getAsnList();
            var asn = this.findAsn(asnId, asnList);
            if (asn) {
                asn.action = action;
                this.setAsnList(asnList);
            }
            Topbar.Service.Asn.updateAsnList(asnList);
        }


    } //Asn



//    ,API_TYPEAHEAD_SUGGESTION       : "/resources/ctrs.json"
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
