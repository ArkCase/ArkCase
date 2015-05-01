<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>


<t:layout>
<jsp:attribute name="endOfHead">
    <title><spring:message code="taskNew.page.title" text="Task | ACM | Armedia Case Management" /></title>
    <div id="wizardData" itemscope="true" style="display: none">
        <span itemprop="parentType">${parentType}</span>
        <span itemprop="reference">${reference}</span>
    </div>
</jsp:attribute>

<jsp:attribute name="endOfBody">
    <script type="text/javascript" src="<c:url value='/resources/js/task/wizard/taskOld.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/resources/js/task/wizard/taskWizard.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/resources/js/task/wizard/taskWizardObject.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/resources/js/task/wizard/taskWizardEvent.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/resources/js/task/wizard/taskWizardPage.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/resources/js/task/wizard/taskWizardRule.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/resources/js/task/wizard/taskWizardService.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/resources/js/task/wizard/taskWizardCallback.js'/>"></script>

    <script type="text/javascript" src="<c:url value='/resources/vendors/${vd_datepicker}/${js_datepicker}'/>"></script>
    <script type="text/javascript" src="<c:url value='/resources/vendors/${vd_slimscroll}/${js_slimscroll}'/>"></script>
    <script type="text/javascript" src="<c:url value='/resources/vendors/${vd_parsley}/${js_parsley}'/>"></script>
    <script type="text/javascript" src="<c:url value='/resources/vendors/${vd_wizard}/${js_wizard_bootstrap}'/>"></script>
    <script type="text/javascript" src="<c:url value='/resources/vendors/${vd_wizard}/${js_wizard_demo}'/>"></script>

    <!-- Summernote WYSIWYG -->
    <link rel="stylesheet" href="<c:url value='/resources/vendors/${vd_summernote}/summernote.css'/>" type="text/css"/>
    <script type="text/javascript" src="<c:url value='/resources/vendors/${vd_summernote}/${js_summernote}'/>"></script>

    <!-- Multi-Select Field WYSIWYG -->
    <script type="text/javascript" charset="utf-8" src="<c:url value='/resources/vendors/${vd_chosen}/${js_chosen}'/>"></script>
</jsp:attribute>

<jsp:body>
    <section class="vbox">
        <section class="scrollable padder">
            <section class="row m-b-md">
                <div class="col-sm-12">
                    <h3 class="m-b-xs text-black" data-i18n="task:wizard.title">New Task</h3>
                </div>
            </section>


            <section class="panel panel-default">


                <div class="row wrapper">
                    <div class="col-sm-6">
                        <label  class="label" data-i18n="task:wizard.label.assign-to">Assign To</label>

                        <%--<select data-placeholder="Choose Assignees..." id="assignee" class="form-control" multiple style="width:350px;" >--%>
                        <select class="input-sm form-control inline v-middle" id="assignee">
                            <option value="null" data-i18n="task:wizard.label.select-assignee">Select Assignee</option>
                        </select>
                    </div>

                    <div class="col-sm-6">
                        <label  class="label" data-i18n="task:wizard.label.associate-complaint-case">Associate with Complaint or Case</label>
                        <input type="text" class="input-sm form-control" data-i18n="[placeholder]task:wizard.label.complaint-or-case" placeholder="Complaint or Case #" id="complaintId">
                    </div>
                </div>


                <div class="row wrapper">
                    <div class="col-sm-12">
                        <label  class="label" data-i18n="task:wizard.label.subject">Subject</label>
                        <input type="text" class="input-sm form-control" data-i18n="[placeholder]task:wizard.label.subject" placeholder="Subject" id="subject">
                    </div>
                </div>


                <div class="row wrapper">
                    <div class="col-sm-4">
                        <label class="label" data-i18n="task:wizard.label.start-date">Start Date</label>
                        <input class="datepicker-input form-control" type="text" value="" data-date-format="mm/dd/yyyy" placeholder="mm/dd/yyyy" id="startDate">

                    </div>
                    <div class="col-sm-8">
                        <label class="label" data-i18n="task:wizard.label.status">Status</label>
                        <select class="input-sm form-control inline v-middle" id="statusSel">
                            <option value="null" data-i18n="task:wizard.label.select-status">Select Status</option>
                            <option value="ACTIVE" data-i18n="task:wizard.label.active">Active</option>
                        </select>
                    </div>

                </div>

                <div class="row wrapper">
                    <div class="col-sm-4">
                        <label class="label" data-i18n="task:wizard.label.due-date">Due Date</label>

                        <input class="datepicker-input form-control"  type="text" value="" data-date-format="mm/dd/yyyy" placeholder="mm/dd/yyyy" id="dueDate">

                    </div>
                    <div class="col-sm-4">
                        <label class="label" data-i18n="task:wizard.label.priority">Priority</label>
                        <select class="input-sm form-control inline v-middle" id="prioritySel">

                            <option value="null" data-i18n="task:wizard.label.select-priority">Select Priority</option>
                            <option value="Low" data-i18n="task:wizard.label.low">Low</option>
                            <option value="Medium" data-i18n="task:wizard.label.medium">Medium</option>
                            <option value="High" data-i18n="task:wizard.label.high">High</option>
                            <option value="Expedited" data-i18n="task:wizard.label.expedited">Expedited</option>
                        </select>
                    </div>
                    <div class="col-sm-4">
                        <label class="label" data-i18n="task:wizard.label.complete-percents">% Complete</label>
                        <input type="text" class="input-sm form-control" data-i18n="[placeholder]task:wizard.label.complete-percents" placeholder="% Complete" id="completedStatus">
                    </div>
                </div>
                <div class="row wrapper">
                    <div class="col-sm-12">
                        <label class="label" data-i18n="task:wizard.label.notes">Notes</label>
                        <div class="complaintDetails" id="taskDetail">
                        </div>

                    </div>
                </div>
            </section>
        </section>
        <footer class="footer bg-white b-t">
            <div class="row text-center-xs padder-v2">

                <div class="col-md-12 text-right text-center-xs">
                    <button class="btn btn-primary" type="button" id="saveBtn" data-i18n="task:wizard.button.save">Save</button>
                </div>
            </div>
        </footer>

    </section>
</jsp:body>
</t:layout>



<!-- Summernote WYSIWYG -->
<script>
    $(document).ready(function() {
        $('.complaintDetails').summernote({
            height: 300
        });
    });
</script>


<!-- Multi-Select Field WYSIWYG -->

<script type="text/javascript">
    var config = {
        '.choose-intitiatorFlags' : {},
        '.choose-complaintFlags' : {},
        '.choose-approvers' : {},
        '.choose-collab' : {},
        '.choose-notifications' : {}
    }
    for (var selector in config) {
        $(selector).chosen(config[selector]);
    }
</script>





