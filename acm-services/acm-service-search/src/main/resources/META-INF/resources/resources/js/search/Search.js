/**
 * Search is namespace component for Search plugin
 *
 * @author jwu
 */
var Search = Search || {
    initialize: function() {
        Search.Object.initialize();
        Search.Event.initialize();
        Search.Page.initialize();
        Search.Rule.initialize();
        Search.Service.initialize();
        Search.Callback.initialize();

        Acm.deferred(Search.Event.onPostInit);
        //Search.Event.onPostInit();
    }

    ,Object: {}
    ,Event:{}
    ,Page: {}
    ,Rule: {}
    ,Service: {}
    ,Callback: {}

    ,EMPTY_RESULT: {"Result": "OK","Records": [],"TotalRecordCount": 0}
    ,DEFAULT_PAGE_SIZE: 4

    ,OBJTYPE_CASE: "Case"
    ,OBJTYPE_COMPLAINT: "Complaint"
    ,OBJTYPE_TASK: "Task"
    ,OBJTYPE_DOCUMENT: "Document"

    ,_quickSearchTerm: undefined
    ,getQuickSearchTerm: function() {
        return this._quickSearchTerm;
    }
    ,setQuickSearchTerm: function(term) {
        this._quickSearchTerm = term;
    }

};

