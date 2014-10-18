/**
 * Admin.Object
 *
 * manages screen objects
 *
 * @author jwu
 */
Admin.Object = {
    create : function() {
        this.$btnTest = $("#test");
        this.$btnTest.click(function(e) {Admin.Event.onClickBtnTest(e);});
    }

};




