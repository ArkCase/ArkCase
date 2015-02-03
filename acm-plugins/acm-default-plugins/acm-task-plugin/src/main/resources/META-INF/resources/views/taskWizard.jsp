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
    <script type="text/javascript" src="<c:url value='/resources/js/task/task.js'/>"></script>
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
<%--<section id="content">--%>
<%--<section class="vbox">--%>
<%--<section class="scrollable padder">--%>
<%--<section class="row m-b-md">--%>
<%--<div class="col-sm-12">--%>
<%--<h3 class="m-b-xs text-black">${pageDescriptor.descShort}</h3>--%>
<%--</div>--%>
<%--</section>--%>
<%--<div class="row">--%>
<%--<div class="col-sm-12">--%>
<%--<form id="wizardform" method="get" action="">--%>
<%--<div class="panel panel-default">--%>
<%--<div class="panel-heading">--%>
<%--<ul class="nav nav-tabs font-bold">--%>
<%--<li><a href="#step1" data-toggle="tab">Step 1: Task Information</a></li>--%>
<%--</ul>--%>
<%--<span class="hidden-sm">--%>
<%--<button class="btn btn-default btn-sm" data-toggle="tooltip" data-title="Save"><i class="fa fa-save"></i>Create Task</button>--%>
<%--</span>--%>
<%--</div>--%>
<%--<div class="panel-body">--%>
<%--<div class="progress progress-xs m-t-sm">--%>
<%--<div class="progress-bar bg-success"></div>--%>
<%--</div>--%>
<%--<div class="tab-content">--%>

<%--<div class="tab-pane" id="step1">--%>
<%--<h4>Task Information</h4>--%>
<%--<p>Description</p>--%>
<%--<section class="row m-b-md">--%>
<%--<div class="col-sm-4">--%>
<%--<label for="owner"  class="label">Owner</label>--%>
<%--<select name="owner" class="form-control m-b">--%>
<%--<option>Choose Owner</option>--%>
<%--</select>--%>

<%--<label for="dueDate" class="label">Due Date</label>--%>
<%--<input id="dueDate" type="text" class="datepicker-input form-control" placeholder="Due Date"  value="" data-date-format="yyyy-mm-dd">--%>

<%--<label for="subject"  class="label">Subject</label>--%>
<%--<input id="subject" type="text" class="form-control" placeholder="Subject">--%>

<%--</div>--%>
<%--<div class="col-sm-4">--%>
<%--<label for="priority"  class="label">Priority</label>--%>
<%--<input id="priority" type="text" class="form-control" placeholder="Priority"  >--%>

<%--<label for="startDate" class="label">Start Date</label>--%>
<%--<input id="startDate" type="text" class="datepicker-input form-control" placeholder="Start Date"  value="" data-date-format="yyyy-mm-dd" >--%>

<%--<label for="case" class="label">Associate Case</label>--%>
<%--<input id="case" type="text" class="form-control" placeholder="Case"  >--%>
<%--</div>--%>


<%--<div class="col-sm-4">--%>
<%--<label for="status"  class="label">Status</label>--%>
<%--<select name="status" class="form-control m-b">--%>
<%--<option>Choose Status</option>--%>
<%--</select>--%>

<%--<label for="taskFlags" class="label">Task Flags</label>--%>
<%--<select data-placeholder="Choose Task Flags..." id="taskFlags" class="choose-taskFlags form-control" multiple >--%>
<%--<option value=""></option>--%>
<%--<option value="Protected Source">Protected Source</option>--%>
<%--</select>--%>

<%--<label for="complaint" class="label">Associate Complaint</label>--%>
<%--<input id="complaint" type="text" class="form-control" placeholder="Complaint"  >--%>
<%--</div>--%>

<%--<div class="col-sm-12">--%>
<%--<hr />--%>

<%--<div class="detail"></div>--%>
<%--</div>--%>

<%--</section>--%>
<%--</div>--%>

<%--<ul class="pager wizard m-b-sm">--%>
<%--<li class="previous first" style="display:none;"><a href="#">First</a></li>--%>
<%--<li class="previous"><a href="#">Previous</a></li>--%>
<%--<li class="next"><a href="#">Next</a></li>--%>
<%--</ul>--%>
<%--</div>--%>
<%--</div>--%>
<%--</div>--%>
<%--</form>--%>
<%--</div>--%>
<%--</div>--%>
<%--</section>--%>
<%--</section>--%>
<%--</section>--%>


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
                            <%--<option value="albert-acm">Albert Acm</option>
                            <option value="ann-acm">Ann Acm</option>
                            <option value="AJ McClary">AJ McClary</option>
                            <option value="charles-acm">Charles Acm</option>
                            <option value="David Miller">David Miller</option>
                            <option value="ian-acm">Ian Acm</option>
                            <option value="James Bailey">James Bailey</option>
                            <option value="Jim Nasr">Jim Nasr</option>
                            <option value="Judy Hsu">Judy Hsu</option>
                            <option value="Ronda Ringo">Ronda Ringo</option>
                            <option value="sally-acm">Sally Acm</option>
                            <option value="samuel-acm">Samuel Acm</option>--%>
                        </select>
                    </div>

                    <div class="col-sm-6">
                        <label  class="label">Associate with Complaint or Case</label>
                        <input type="text" class="input-sm form-control" placeholder="Complaint or Case #" id="complaintId">
                        <%--<input type="hidden" class="span1" title="refCtrId" id="refCtrId" value="" />--%>
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
                            <option value="Pending">Pending</option>
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





