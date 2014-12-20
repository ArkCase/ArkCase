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
    ,onInitialized: function() {
        if (this.Suggestion.onInitialized) {Topbar.Service.Suggestion.onInitialized();}
        if (this.Asn.onInitialized)        {Topbar.Service.Asn.onInitialized();}
    }

    ,Suggestion: {
        create: function() {
        }
        ,onInitialized: function() {
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
        ,onInitialized: function() {
        }

        //,API_RETRIEVE_ASN_LIST_       : "/resources/asn.json"
        ,API_RETRIEVE_ASN_LIST_       : "/api/latest/plugin/notification/"
        ,API_SAVE_ASN                 : "/api/latest/plugin/notification"
        ,API_DELETE_ASN_              : "/api/latest/plugin/notification/"

        ,retrieveAsnList: function(user) {
            Acm.Service.asyncGet(
                function(response) {
                    if (response.hasError) {
                        Topbar.Controller.Asn.modelRetrievedAsnList(response);
                    } else {
                        if (Topbar.Model.Asn.validateAsnList(response)) {
                            var asnList = response;
                            Topbar.Model.Asn.setAsnList(asnList);
                            Topbar.Controller.Asn.modelRetrievedAsnList(asnList);
                        }
                    }
                }
                ,App.getContextPath() + this.API_RETRIEVE_ASN_LIST_ + user
            )
        }

        ,saveAsn: function(asn, handler) {
            Acm.Service.asyncPost(
                function(response) {
                    if (response.hasError) {
                        if (handler) {
                            handler(response);
                        } else {
                            Topbar.Controller.Asn.modelSavedAsn(response);
                        }

                    } else {
                        if (Topbar.Model.Asn.validateAsn(response)) {
                            var asn = response;
                            Topbar.Model.Asn.setAsn(asn);
                            if (handler) {
                                handler(asn);
                            } else {
                                Topbar.Controller.Asn.modelSavedAsn(asn);
                            }
                        }
                    }
                }
                ,App.getContextPath() + this.API_SAVE_ASN
                ,JSON.stringify(asn)
            )
        }
        ,updateAsnAction: function(asnId, action) {
            var asnList = Topbar.Model.Asn.getAsnList();
            var asn = Topbar.Model.Asn.findAsn(asnId, asnList);
            if (asn) {
                asn.action = action;
                if (Topbar.Model.Asn.STATUS_MARKED != asn.status && Topbar.Model.Asn.STATUS_DELETED != asn.status) {
                    asn.status = Topbar.Model.Asn.STATUS_UNMARKED;
                }
                Topbar.Service.Asn.saveAsn(asn
                    ,function(data) {
                        Topbar.Controller.Asn.modelUpdatedAsnAction(asnId, Acm.Service.responseWrapper(data, data.action));
                    }
                );
            }
        }
        ,updateAsnStatus: function(asnId, status) {
            var asnList = Topbar.Model.Asn.getAsnList();
            var asn = Topbar.Model.Asn.findAsn(asnId, asnList);
            if (asn) {
                asn.status = status;
                Topbar.Service.Asn.saveAsn(asn
                    ,function(data) {
                        Topbar.Controller.Asn.modelUpdatedAsnAction(asnId, Acm.Service.responseWrapper(data, data.status));
                    }
                );
            }
        }
        ,_validateDeletedAsn: function(data) {
            if (Acm.isEmpty(data)) {
                return false;
            }
            if (Acm.isEmpty(data.deletedNotificationId)) {
                return false;
            }
            return true;
        }
        ,deleteAsn: function(asnId) {
            Acm.Service.asyncDelete(
                function(response) {
                    if (response.hasError) {
                            Topbar.Controller.Asn.modelDeleteAsn(response);

                    } else {
                        if (Topbar.Service.Asn._validateDeletedAsn(response)) {
                            if (response.deletedNotificationId == asnId) {
                                var asnList = Topbar.Model.Asn.getAsnList();
                                if (Topbar.Model.Asn.validateAsnList(asnList)) {
                                    for (var i = 0; i < asnList.length; i++) {
                                        if (asnId == asnList[i].id) {
                                            asnList.splice(i, 1);
                                            Topbar.Controller.Asn.modelDeletedAsn(Acm.Service.responseWrapper(response, asnId));
                                            return;
                                        }
                                    }
                                }
                            }
                        }
//                        if (Topbar.Model.Asn.validateAsn(response)) {
//                            var asn = response;
//                            Topbar.Model.Asn.setAsn(asn);
//                            if (handler) {
//                                handler(asn);
//                            } else {
//                                Topbar.Controller.Asn.modelSavedAsn(asn);
//                            }
//                        }
//
//
//                        Topbar.Controller.Asn.modelDeletedAsn(asnId, Acm.Service.responseWrapper(data, data.status));
                        var a = response;
                        deletedNotificationId
                        var z = 1;

                    }
                }
                ,App.getContextPath() + this.API_DELETE_ASN_ + asnId
            )
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
