/**
 * Report.Object
 *
 * manages screen objects
 *
 * @author jwu
 */
Report.Object = {
    initialize : function() {
        this.$btnTest = $("#test");
        this.$btnTest.click(function(e) {Report.Event.onClickBtnTest(e);});
    }

};




