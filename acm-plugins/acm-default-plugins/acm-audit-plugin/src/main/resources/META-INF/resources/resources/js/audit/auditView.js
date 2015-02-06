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
        ,openAuditReport: function(event,ctrl){
            var auditFieldsValues = Audit.View.AuditCriteria.getAuditFieldsValues();
            var pageUrl = Audit.View.MicroData.auditReportUrl
                + "&startDate=" + auditFieldsValues.startDate
                + "&endDate=" + auditFieldsValues.endDate
                + "&objectType=" + auditFieldsValues.objectType;

            window.open(pageUrl, 'audit_iframe');
        }
    }
    ,AuditCriteria:{
        create: function() {
            this.$reportSubmitSection = $("#reportSubmitSection");
            this.$btnGenerateReport = $("#generateReport");
            this.$btnGenerateReport.on("click", function(e){Audit.View.AuditReport.openAuditReport(e,this);});

            this.buildAuditCriteriaPanel(Audit.View.MicroData.auditCriteria);

            this.$objectType = $("#objectType");
            this.$edtStartDate = $("#startDate");
            this.$edtEndDate = $("#endDate");
        }

        ,onInitialized: function() {
            //since datepickers are loaded dynamically,
            // we need to initialize datepickers once the markup is ready
            //additionally, set default dates to current date
            this.$datePickers = $(".datepicker-input");
            this.$datePickers.datepicker();
            this.$datePickers.datepicker("setDate", Acm.getCurrentDay());
        }
        ,getValueStartDate: function() {
            var startDate = Acm.Object.getValue(this.$edtStartDate).replace(/\//g, "-");
            return startDate;
        }
        ,getValueEndDate: function() {
            var endDate = Acm.Object.getValue(this.$edtEndDate).replace(/\//g, "-");
            return endDate;
        }
        ,getObjectType:function(){
            return Acm.Object.getValue(this.$objectType);
        }
        ,getAuditFieldsValues: function(){
            var auditFieldsValues = {};
            auditFieldsValues.startDate = this.getValueStartDate();
            auditFieldsValues.endDate = this.getValueEndDate();
            auditFieldsValues.objectType = this.getObjectType();
            return auditFieldsValues;
        }
        ,setValueStartDate: function(val) {
            Acm.Object.setValueDatePicker(this.$edtStartDate, val);
        }
        ,setValueEndDate: function(val) {
            Acm.Object.setValueDatePicker(this.$edtEndDate, val);
        }
        ,setHtmlDivAuditCriteria: function(val) {
            this.$btnGenerateReport.before(val);
        }

        ,buildAuditCriteriaPanel: function(auditCriteria) {
            var html = "";
            for (var i = 0; i < auditCriteria.length; i++) {

                html += "<div class='col-sm-12 text-center' >"
                        + "<h3>" + Acm.goodValue(auditCriteria[i].name)
                        + "</h3>"
                        + "</div>";

                for (var j = 0; j < auditCriteria[i].inputs.length; j++) {
                    var field = auditCriteria[i].inputs[j];
                    if (field.name && field.type) {

                        if ("text" == field.type) {

                            html+= "<label class='label col-sm-12'>" + Acm.goodValue(field.desc) + "</label>"
                            + "<div class='col-sm-12'>"
                            + "<input type='text' class='form-control"
                            + "' id='" + Acm.goodValue(field.name)
                            + "' value='" + Acm.goodValue(field.value)
                            + "' placeholder='" + Acm.goodValue(field.desc) + "'>"
                            +"</div>";

                        }
                        else if ("dateRange" == field.type) {

                            html+= "<label class='label col-sm-12'>" + "From" + "</label>"
                            + "<div class='col-sm-12'>"
                            +"<input class='datepicker-input form-control' type='text' data-date-format='mm-dd-yyyy' placeholder='mm/dd/yyyy"
                            + "' id='" + Acm.goodValue(field.nameStartDate)
                            + "' value='" + Acm.goodValue(field.value)
                            + "' placeholder='" + Acm.goodValue(Acm.getCurrentDay())
                            + "' >"
                            +"</div>";

                            html+= "<label class='label col-sm-12'>" + "To" + "</label>"
                            + "<div class='col-sm-12'>"
                            +"<input class='datepicker-input form-control' type='text' data-date-format='mm-dd-yyyy' placeholder='mm/dd/yyyy"
                            + "' id='" + Acm.goodValue(field.nameEndDate)
                            + "' value='" + Acm.goodValue(field.value)
                            + "' placeholder='" + Acm.goodValue(Acm.getCurrentDay())
                            + "' >"
                            +"</div>";
                        }
                        else if("select" == field.type) {

                            html += "<label class='label col-sm-12'>" + Acm.goodValue(field.desc) + "</label>"
                            + "<div class='col-sm-12'>"
                            + "<select class='form-control"
                            + "' id='" + Acm.goodValue(field.name)
                            + "' value='" + Acm.goodValue(field.value)
                            + "' placeholder='" + Acm.goodValue(field.desc)
                            + "' >";
                            if (field.options) {

                                for (var k = 0; k < field.options.length; k++) {
                                    var option = field.options[k];
                                    html += "<option";
                                    if (option.value) {
                                        html += " value='" + Acm.goodValue(option.value) + "'";
                                    }
                                    html += ">";
                                    if (option.text) {
                                        html += Acm.goodValue(option.text);
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
        }
    }
};
