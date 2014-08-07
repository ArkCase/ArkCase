/**
 * Admin.Object
 *
 * manages screen objects
 *
 * @author jwu
 */
Admin.Object = {
    initialize : function() {
        this.$btnTest = $("#test");
        this.$btnTest.click(function(e) {Admin.Event.onClickBtnTest(e);});
    }

};




