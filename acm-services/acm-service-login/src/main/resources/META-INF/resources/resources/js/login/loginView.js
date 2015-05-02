/**
 * Login.View
 *
 * @author jwu
 */
Login.View = Login.View || {
    create : function(args) {
        this.$form = $("form");
        this.$form.attr("action", App.getContextPath() + "/j_spring_security_check");
    }
    ,onInitialized: function() {
    }

};

