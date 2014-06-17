/**
 * SimpleSearch.Object
 *
 * manages screen objects
 *
 * @author jwu
 */
SimpleSearch.Object = {
    initialize : function() {
        //this.$formSearch = $("form[role='search']");
        this.$edtSearch = $("form[role='search'] input");


        this.$tabMyTasks = $("#tabMyTasks");
    }

    ,getValueEdtSearch: function() {
        return Acm.Object.getPlaceHolderInput(this.$edtSearch);
    }
    ,setValueEdtSearch: function(val) {
        Acm.Object.setPlaceHolderInput(this.$edtSearch, val);
    }

    ,resetTableMyTasks: function() {
        this.$tabMyTasks.find("tbody > tr").remove();
    }
    ,addRowTableMyTasks: function(row) {
        this.$tabMyTasks.find("tbody:last").append(row);
    }
};




