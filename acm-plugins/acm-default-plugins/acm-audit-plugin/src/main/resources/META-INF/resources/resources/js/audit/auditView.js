/**
 * Created by manoj.dhungana on 2/5/2015.
 */

Audit.View = Audit.View || {
    create: function() {
        if (Audit.View.MicroData.create)       {Audit.View.MicroData.create();}
        if (Audit.View.AuditCriteria.create)   {Audit.View.AuditCriteria.create();}
        if (Audit.View.AuditReport.create)     {Audit.View.AuditReport.create();}
    }
    ,onInitialized: function() {
        if (Audit.View.MicroData.onInitialized)       {Audit.View.MicroData.onInitialized();}
        if (Audit.View.AuditCriteria.onInitialized)   {Audit.View.AuditCriteria.onInitialized();}
        if (Audit.View.AuditReport.onInitialized)     {Audit.View.AuditReport.onInitialized();}
    }
    ,MicroData:{
        create: function() {
            this.auditCriteria   = Acm.Object.MicroData.getJson("auditCriteria");
            this.auditReportUrl   = Acm.Object.MicroData.get("auditReportUrl");
        }
        ,onInitialized: function() {
        }
    }
    ,AuditReport:{
        create: function() {
        }
        ,onInitialized: function() {
        }
        ,onClickBtnGenerateReport: function(event,ctrl){
            var auditFieldsValues = Audit.View.AuditCriteria.getFieldsValues();
            var dateFormat = $.t("common:date.pentaho");
            var pageUrl = Audit.View.MicroData.auditReportUrl
                + "&startDate=" + auditFieldsValues.startDate
                + "&endDate=" + auditFieldsValues.endDate
                + "&objectType=" + auditFieldsValues.objectType
                + "&objectId=" + auditFieldsValues.objectId
                + "&dateFormat=" + encodeURIComponent(dateFormat);

            window.open(pageUrl, 'audit_iframe');
        }
    }
    ,AuditCriteria:{
        create: function() {
            this.$btnGenerateReport = $("#generateReport");
            this.$btnGenerateReport.on("click", function(e){Audit.View.AuditReport.onClickBtnGenerateReport(e,this);});
        }
        ,onInitialized: function() {
            if(Audit.Model.validateAuditCriteria(Audit.View.MicroData.auditCriteria)){
                this.buildAuditCriteriaPanel(Audit.View.MicroData.auditCriteria);
            };
        }
        ,setDatePickerDefaultValue: function(){
            //since datepickers are loaded dynamically,
            // we need to initialize datepickers once the markup is ready
            //additionally, set default dates to current date
            this.$datePickers = $(".datepicker-input");
            this.$datePickers.datepicker({dateFormat: $.t("common:date.datepicker")});
            this.$datePickers.datepicker("setDate", Acm.getPentahoDateFromDateTime(new Date()));
        }
        ,getDate: function(selector) {
            var date = Acm.Object.getValue(selector).replace(/\//g, "-");
            return date;
        }
        ,getText: function(selector) {
            var text = Acm.Object.getPlaceHolderInput(selector);
            return text;
        }
        ,getSelectValue:function(selector){
            return Acm.Object.getValue(selector);
        }
        ,getFieldsValues: function(){
            var fieldsValues = {};
            for (var i = 0; i < Audit.View.MicroData.auditCriteria[0].inputs.length; i++) {
                var field = Audit.View.MicroData.auditCriteria[0].inputs[i];
                if (Acm.isNotEmpty(field) && Acm.isNotEmpty(field.name) && Acm.isNotEmpty(field.type)) {
                    if("select" == field.type){
                        fieldsValues.objectType = Audit.View.AuditCriteria.getSelectValue($("#" + field.name));
                    }
                    else if("dateRange" == field.type){
                        if(Acm.isNotEmpty(field.nameStartDate)){
                            fieldsValues.startDate = Audit.View.AuditCriteria.getDate(($("#" + field.nameStartDate)));
                        }
                        if(Acm.isNotEmpty(field.nameEndDate)){
                            fieldsValues.endDate = Audit.View.AuditCriteria.getDate(($("#" + field.nameEndDate)));
                        }
                    }
                    else if("text"== field.type){
                        fieldsValues.objectId = Audit.View.AuditCriteria.getText(($("#" + field.name)));
                    }
                }
            }
            return fieldsValues;
        }
        ,setDate: function(selector,val) {
            Acm.Object.setValueDatePicker(selector, val);
        }
        ,setHtmlDivAuditCriteria: function(val) {
            this.$btnGenerateReport.before(val);
        }

        ,buildAuditCriteriaPanel: function(auditCriteria) {
            var html = "";
            for (var i = 0; i < auditCriteria.length; i++) {

                html += "<div class='col-sm-12 text-center' >"
                        + "<h3>" + Acm.goodValue($.t(auditCriteria[i].name))
                        + "</h3>"
                        + "</div>";

                for (var j = 0; j < auditCriteria[i].inputs.length; j++) {
                    var field = auditCriteria[i].inputs[j];
                    if (field.name && field.type) {

                        if ("text" == field.type) {

                            html+= "<label class='label col-sm-12'>" + Acm.goodValue($.t(field.label)) + "</label>"
                            + "<div class='col-sm-12'>"
                            + "<input type='text' class='form-control"
                            + "' id='" + Acm.goodValue(field.name)
                            + "' value='" + Acm.goodValue(field.value)
                            + "' placeholder='" + Acm.goodValue($.t(field.desc)) + "'>"
                            +"</div>";

                        }
                        else if ("dateRange" == field.type) {

                            html+= "<label class='label col-sm-12'>" + $.t("audit:label.date-from") + "</label>"
                            + "<div class='col-sm-12'>"
                            +"<input class='datepicker-input form-control' type='text' data-i18n='[data-date-format;placeholder]common:date.datepicker'"
                            + "' id='" + Acm.goodValue(field.nameStartDate)
                            + "' value='" + Acm.goodValue(field.value)
                            + "' placeholder='" + Acm.goodValue(Acm.getCurrentDay())
                            + "' >"
                            +"</div>";

                            html+= "<label class='label col-sm-12'>" + $.t("audit:label.date-to") + "</label>"
                            + "<div class='col-sm-12'>"
                            +"<input class='datepicker-input form-control' type='text' data-i18n='[data-date-format;placeholder]common:date.datepicker'"
                            + "' id='" + Acm.goodValue(field.nameEndDate)
                            + "' value='" + Acm.goodValue(field.value)
                            + "' placeholder='" + Acm.goodValue(Acm.getCurrentDay())
                            + "' >"
                            +"</div>";
                        }
                        else if("select" == field.type) {

                            html += "<label class='label col-sm-12'>" + Acm.goodValue($.t(field.label)) + "</label>"
                            + "<div class='col-sm-12'>"
                            + "<select class='form-control"
                            + "' id='" + Acm.goodValue(field.name)
                            + "' value='" + Acm.goodValue(field.value)
                            + "' placeholder='" + Acm.goodValue($.t(field.desc))
                            + "' >";
                            if (field.options) {

                                for (var k = 0; k < field.options.length; k++) {
                                    var option = field.options[k];
                                    html += "<option";
                                    if (option.value) {
                                        html += " value='" + Acm.goodValue(option.value) + "'";
                                    }
                                    html += ">";
                                    if ($.t(option.text)) {
                                        html += Acm.goodValue($.t(option.text));
                                    }
                                    html += "</option>";
                                }
                            }
                            html += "</select>";
                            html+="</div>";
                        }
                    }
                }
                html+="<BR>&nbsp;<BR>";
            }
            this.setHtmlDivAuditCriteria(html);
            this.setDatePickerDefaultValue();
        }
    }
};
