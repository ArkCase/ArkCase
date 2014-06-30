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
        console.log("Sidebar.Event.onClickBtnSearch");
        Topbar.Object.setQuickSearchTerm(null);
    }

    ,onPostInit: function() {
        Sidebar.Object.hiliteActivePlugin();
    }

};
