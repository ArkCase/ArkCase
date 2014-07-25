/**
 * Search.Service
 *
 * manages all service call to application server
 *
 * @author jwu
 */
Search.Service = {
    initialize : function() {
    }

    ,API_QUICK_SEARCH       : "/api/v1/plugin/search/quickSearch"

    //,API_SEARCH       : "/api/latest/plugin/search"
    ,API_SEARCH       : "/api/latest/plugin/complaint/list"
    ,API_SEARCH_DEMO       : "/api/latest/plugin/complaint/byId/"

    //for demo
    ,search : function(term) {
        if (Acm.isEmpty(term)) {
            Search.Page.fillResults([]);
            return;
        }
        Acm.Ajax.asyncGet(App.getContextPath() + this.API_SEARCH_DEMO + term
            ,Search.Callback.EVENT_RESULT_RETRIEVED
        );
    }

    ,search_save : function(term) {
        if (Acm.isEmpty(term)) {
            Search.Page.fillResults([]);
            return;
        }
        Acm.Ajax.asyncGet(App.getContextPath() + this.API_SEARCH
            ,Search.Callback.EVENT_RESULT_RETRIEVED
        );
    }
};

