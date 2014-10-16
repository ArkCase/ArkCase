/**
 * Sidebar.Event
 *
 * event handlers for objects
 *
 * @author jwu
 */
Sidebar.Event = {
    initialize : function() {
    }

    ,onClickLnkNav : function(e) {
        Topbar.Model.QuickSearch.setQuickSearchTerm(null);
    }

    ,onPostInit: function() {
        Sidebar.Object.hiliteActivePlugin();
    }

};
