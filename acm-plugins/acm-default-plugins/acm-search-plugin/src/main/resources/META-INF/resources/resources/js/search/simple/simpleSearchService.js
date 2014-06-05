/**
 * SimpleSearch.Service
 *
 * manages all service call to application server
 *
 * @author jwu
 */
SimpleSearch.Service = {
    initialize : function() {
    }

    //,API_SEARCH       : "/api/latest/plugin/search"
    ,API_SEARCH       : "/api/latest/plugin/complaint/list"
    ,API_SEARCH_DEMO       : "/api/latest/plugin/complaint/byId/"

    //for demo
    ,search : function(term) {
        if (Acm.isEmpty(term)) {
            SimpleSearch.Page.fillResults([]);
            return;
        }
        Acm.Ajax.asyncGet(Acm.getContextPath() + this.API_SEARCH_DEMO + term
            ,SimpleSearch.Callback.EVENT_RESULT_RETRIEVED
        );
    }

    ,search_save : function(term) {
        if (Acm.isEmpty(term)) {
            SimpleSearch.Page.fillResults([]);
            return;
        }
        Acm.Ajax.asyncGet(Acm.getContextPath() + this.API_SEARCH
            ,SimpleSearch.Callback.EVENT_RESULT_RETRIEVED
        );
    }
};

