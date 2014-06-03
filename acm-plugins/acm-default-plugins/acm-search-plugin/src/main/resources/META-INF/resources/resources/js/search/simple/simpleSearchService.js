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


    ,search : function(term) {

        Acm.Ajax.asyncGet(Acm.getContextPath() + this.API_SEARCH
            ,SimpleSearch.Callback.EVENT_RESULT_RETRIEVED
        );
    }
};

