/**
 * Topbar.Service
 *
 * manages all service call to application server
 *
 * @author jwu
 */
Topbar.Service = {
    create : function() {
        if (this.Asn.create) {Topbar.Service.Asn.create();}
    }
    ,initialize: function() {
        if (this.Asn.initialize) {Topbar.Service.Asn.initialize();}
    }

    ,Suggestion: {
        create: function() {
        }
        ,initialize: function() {
        }
        ,API_TYPEAHEAD_SUGGESTION_BEGIN_      : "/api/latest/plugin/search/quickSearch?q=*"
        ,API_TYPEAHEAD_SUGGESTION_END         : "*&start=0&n=16"

        ,getTypeAheadUrl: function(query) {
            var url = App.getContextPath() + this.API_TYPEAHEAD_SUGGESTION_BEGIN_
                + query
                + this.API_TYPEAHEAD_SUGGESTION_END;
            return url;
        }

        ,_validateSuggestionData: function(data) {
            if (Acm.isEmpty(data.responseHeader) || Acm.isEmpty(data.response)) {
                return false;
            }
            if (Acm.isEmpty(data.response.numFound) || Acm.isEmpty(data.response.start) || Acm.isEmpty(data.response.docs)) {
                return false;
            }
            return true;
        }
        ,retrieveSuggestion: function(query, process){
            $.ajax({
                url: Topbar.Service.Suggestion.getTypeAheadUrl(query)
                ,cache: false
                ,success: function(data){
                    if (Topbar.Service.Suggestion._validateSuggestionData(data)) {
                        var docs = data.response.docs;
                        Topbar.Model.Suggestion.buildSuggestion(query, docs);
                        Topbar.Controller.Suggestion.onModelChangeSuggestion(process);
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

