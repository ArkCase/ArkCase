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

<!-- /.aside -->
<section id="content">
    <section class="vbox">
        <section class="scrollable padder">
            <section class="row m-b-md">
                <div class="col-sm-12">
                    <h3 class="m-b-xs text-black"><spring:message code="taskNew.page.descShort" text="New Task" /></h3>
                </div>
            </section>


            <section class="panel panel-default">


                <div class="row wrapper">
                    <div class="col-sm-6">
                        <label  class="label">Assign To</label>

                        <%--<select data-placeholder="Choose Assignees..." id="assignee" class="form-control" multiple style="width:350px;" >--%>
                        <select class="input-sm form-control inline v-middle" id="assignee">
                            <option value="null">Select Assignee</option>
                        </select>
                    </div>

                    <div class="col-sm-6">
                        <label  class="label">Associate with Complaint or Case</label>
                        <input type="text" class="input-sm form-control" placeholder="Complaint or Case #" id="complaintId">
                    </div>
                </div>


                <div class="row wrapper">
                    <div class="col-sm-12">
                        <label  class="label">Subject</label>
                        <input type="text" class="input-sm form-control" placeholder="Subject" id="subject">
                    </div>
                </div>


                <div class="row wrapper">
                    <div class="col-sm-4">
                        <label class="label">Start Date</label>
                        <input class="datepicker-input form-control" type="text" value="" data-date-format="mm/dd/yyyy" placeholder="mm/dd/yyyy" id="startDate">

                    </div>
                    <div class="col-sm-8">
                        <label class="label">Status</label>
                        <select class="input-sm form-control inline v-middle" id="statusSel">
                            <option value="null">Select Status</option>
                            <option value="ACTIVE">Active</option>
                        </select>
                    </div>

                </div>

                <div class="row wrapper">
                    <div class="col-sm-4">
                        <label class="label ">Due Date</label>

                        <input class="datepicker-input form-control"  type="text" value="" data-date-format="mm/dd/yyyy" placeholder="mm/dd/yyyy" id="dueDate">

                    </div>
                    <div class="col-sm-4">
                        <label class="label">Priority</label>
                        <select class="input-sm form-control inline v-middle" id="prioritySel">

                            <option value="null">Select Priority</option>
                            <option value="Low">Low</option>
                            <option value="Medium">Medium</option>
                            <option value="High">High</option>
                            <option value="Expedited">Expedited</option>
                        </select>
                    </div>
                    <div class="col-sm-4">
                        <label class="label">% Complete</label>
                        <input type="text" class="input-sm form-control" placeholder="% Complete" id="completedStatus">
                    </div>
                </div>
                <div class="row wrapper">
                    <div class="col-sm-12">
                        <label class="label">Notes</label>
                        <div class="complaintDetails" id="taskDetail">
                        </div>

                    </div>
                </div>
            </section>
        </section>
        <footer class="footer bg-white b-t">
            <div class="row text-center-xs padder-v2">

                <div class="col-md-12 text-right text-center-xs">
                    <button class="btn btn-primary" type="button" id="saveBtn">Save</button>
                </div>
            </div>
        </footer>

    </section>
</section>
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





