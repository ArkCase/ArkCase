/**
 * Sidebar.Object
 *
 * manages screen objects
 *
 * @author jwu
 */
Sidebar.Object = {
    initialize : function() {
        this.$ulPlugin = $("#ulPlugin");
        this.$lnkNav   = $("nav a");
        this.$lnkNav.click(function(e){Sidebar.Event.onClickLnkNav(e);});
    }

    ,hiliteActivePlugin: function() {
        //
        //looking for href url that pathname begins with to hilite.
        //Added "/" is necessary to avoid false match when href url is partial of pathname.
        //ex) Following should not match:
        //  pathname = "/acm/somepath/location"
        //  href     = "/acm/somepa"
        //
        var pathname = window.location.pathname + "/";
        this.$ulPlugin.find("a").each(function(index) {
            var url = $(this).attr("href") + "/";
            if (0 == pathname.indexOf(url)) {
                $(this).parent().attr("class", "active");
                return false;
            }
        });
    }
};




