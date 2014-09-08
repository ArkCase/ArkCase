<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>


<t:layout>
<jsp:attribute name="endOfHead">
    <title>${pageDescriptor.title}</title>
    <div id="wizardData" itemscope="true" style="display: none">
        <span itemprop="parentType">${parentType}</span>
        <span itemprop="parentId">${parentId}</span>
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

    <script type="text/javascript" src="<c:url value='/'/>resources/vendors/${vd_datepicker}/bootstrap-datepicker.js"></script>
    <script type="text/javascript" src="<c:url value='/'/>resources/vendors/${vd_slimscroll}/jquery.slimscroll.min.js"></script>
    <script type="text/javascript" src="<c:url value='/'/>resources/vendors/${vd_parsley}/parsley.min.js"></script>
    <script type="text/javascript" src="<c:url value='/'/>resources/vendors/${vd_wizard}/jquery.bootstrap.wizard.js"></script>
    <script type="text/javascript" src="<c:url value='/'/>resources/vendors/${vd_wizard}/demo.js"></script>

    <!-- Summernote WYSIWYG -->
    <link rel="stylesheet" href="<c:url value='/'/>resources/vendors/${vd_summernote}/summernote.css" type="text/css"/>
    <script type="text/javascript" src="<c:url value='/'/>resources/vendors/${vd_summernote}/summernote.js"></script>

    <!-- Multi-Select Field WYSIWYG -->
    <script type="text/javascript" charset="utf-8" src="<c:url value='/'/>resources/vendors/${vd_chosen}/chosen.js"></script>
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
                        <h3 class="m-b-xs text-black">New Task</h3>
                    </div>
                </section>


                <section class="panel panel-default">


                    <div class="row wrapper">
                        <div class="col-sm-6">
                            <label  class="label">Assign To</label>

                            <select data-placeholder="Choose Assignees..." id="approvers" class="choose-approvers form-control" multiple style="width:350px;" >
                                <option value=""></option>
                                <option value="David Miller">David Miller</option>
                                <option value="James Bailey">James Bailey</option>
                                <option value="Judy Hsu">Judy Hsu</option>
                                <option value="Ronda Ringo">Ronda Ringo</option>
                                <option value="AJ McClary">AJ McClary</option>
                                <option value="Jim Nasr">Jim Nasr</option>
                            </select>
                        </div>

                        <div class="col-sm-6">
                            <label  class="label">Associate with Complaint or Case</label>
                            <input type="text" class="input-sm form-control" placeholder="Complaint or Case #">
                        </div>
                    </div>


                    <div class="row wrapper">
                        <div class="col-sm-12">
                            <label  class="label">Subject</label>
                            <input type="text" class="input-sm form-control" placeholder="Subject">
                        </div>
                    </div>


                    <div class="row wrapper">
                        <div class="col-sm-4">
                            <label class="label">Start Date</label>

                            <input class="datepicker-input form-control" type="text" value="12-02-2013" data-date-format="dd-mm-yyyy" >

                        </div>
                        <div class="col-sm-8">
                            <label class="label">Status</label>
                            <select class="input-sm form-control inline v-middle" >
                                <option value="0">Select Status</option>
                                <option value="1">Pending</option>
                            </select>
                        </div>

                    </div>

                    <div class="row wrapper">
                        <div class="col-sm-4">
                            <label class="label ">Due Date</label>

                            <input class="datepicker-input form-control"  type="text" value="12-02-2013" data-date-format="dd-mm-yyyy" >

                        </div>
                        <div class="col-sm-4">
                            <label class="label">Priority</label>
                            <select class="input-sm form-control inline v-middle">

                                <option value="0">Select Priority</option>
                                <option value="1">Low</option>
                                <option value="1">Medium</option>
                                <option value="1">High</option>
                                <option value="1">Expedited</option>
                            </select>
                        </div>
                        <div class="col-sm-4">
                            <label class="label">% Complete</label>
                            <input type="text" class="input-sm form-control" placeholder="% Complete">
                        </div>
                    </div>



                    <div class="row wrapper">
                        <div class="col-sm-12">
                            <label class="label">Notes</label>
                            <div class="complaintDetails"><p>Lorem ipsum dolor sit amet, consectetur adipiscing elit. Integer blandit pellentesque tincidunt. Ut tristique sed augue non mollis. Praesent luctus massa nisl, eu iaculis felis mollis sed. Nullam sit amet urna at nisi lobortis pharetra a vitae diam. Proin porttitor velit quis justo fermentum, sed porttitor enim vulputate. Ut pulvinar mauris vitae pellentesque pharetra. Sed scelerisque leo in libero tincidunt tincidunt. Fusce dictum vulputate suscipit. Duis at sodales libero. In placerat in urna quis condimentum. Suspendisse lacinia odio lobortis aliquam mattis. Praesent felis mauris, volutpat vitae eleifend sed, ultricies eget massa. Donec aliquet luctus ultrices. Phasellus nec lobortis nulla, eget bibendum turpis. Proin semper a tortor eget pulvinar.</p>

                                <p>Donec faucibus augue vitae est porttitor venenatis. Etiam enim sem, malesuada non laoreet pellentesque, auctor ac augue. Nulla facilisi. Nullam sit amet dui magna. Aliquam leo velit, semper sit amet faucibus eu, pretium et tellus. Donec tempor leo et porttitor rutrum. Quisque lobortis cursus augue, a porta purus egestas eu. Pellentesque iaculis ipsum velit, eget gravida velit ornare sed. In at sem vitae leo cursus aliquam. Fusce vitae erat rhoncus, ultricies leo eget, ultrices est. In condimentum congue porttitor.</p>

                            </div>

                        </div>
                    </div>


                </section>
            </section>
            <footer class="footer bg-white b-t">
                <div class="row text-center-xs padder-v2">

                    <div class="col-md-12 text-right text-center-xs">
                        <button class="btn btn-primary" type="button">Save</button>
                    </div>
                </div>
            </footer>

        </section>
    </section>
    </section>
</jsp:body>
</t:layout>



<!-- Summernote WYSIWYG -->

<link rel="stylesheet" href="resources/js/summernote/summernote.css" type="text/css"/>
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





