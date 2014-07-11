/**
 * Search.Event
 *
 * event handlers for objects
 *
 * @author jwu
 */
Search.Event = {
    initialize : function() {
    }

    ,onClickBtnSearch : function(e) {
//alert("go");
        var search = Search.Object.getValueEdtSearch();
        var hasComplaints = Search.Object.isCheckChkComplaints();
        var hasCases = Search.Object.isCheckChkCases();
        var hasTasks = Search.Object.isCheckChkTasks();
        var hasDocuments = Search.Object.isCheckChkDocuments();

        e.preventDefault();
    }

    ,onClickBtnToggleSubNav : function(e) {
        //alert("toggle");
        //Search.Object.showSubNav(true);
        //e.preventDefault();
    }


    ,onPostInit: function() {
        var term = Topbar.Object.getQuickSearchTerm();
        if (Acm.isNotEmpty(term)) {
            Search.Object.reloadJTableResults();
        }
    }
};
