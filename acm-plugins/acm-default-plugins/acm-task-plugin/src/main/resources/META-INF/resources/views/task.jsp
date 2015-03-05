<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>


<t:layout>
<jsp:attribute name="endOfHead">
    <title><spring:message code="task.page.title" text="Tasks | ACM | Armedia Case Management" /></title>
    <div id="detailData" itemscope="true" style="display: none">
        <span itemprop="objType">TASK</span>
        <span itemprop="objId">${objId}</span>
        <span itemprop="treeFilter">${treeFilter}</span>
        <span itemprop="treeSort">${treeSort}</span>

        <%--<span itemprop="closeComplaintFormUrl">${closeComplaintFormUrl}</span>--%>
        <span itemprop="editCloseComplaintFormUrl">${editCloseComplaintFormUrl}</span>
        <span itemprop="roiFormUrl">${roiFormUrl}</span>
        <span itemprop="changeCaseStatusFormUrl">${changeCaseStatusFormUrl}</span>

    </div>
</jsp:attribute>

<jsp:attribute name="endOfBody">
    <script type="text/javascript" src="<c:url value='/resources/vendors/${vd_acm}/js/objnav/objNav.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/resources/vendors/${vd_acm}/js/objnav/objNavController.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/resources/vendors/${vd_acm}/js/objnav/objNavService.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/resources/vendors/${vd_acm}/js/objnav/objNavModel.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/resources/vendors/${vd_acm}/js/objnav/objNavView.js'/>"></script>

    <script type="text/javascript" src="<c:url value='/resources/js/task/task.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/resources/js/task/taskModel.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/resources/js/task/taskView.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/resources/js/task/taskController.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/resources/js/task/taskService.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/resources/js/subscription/subscriptionOp.js'/>"></script>

    <%--<script type="text/javascript" src="<c:url value='/resources/js/task/taskOld.js'/>"></script>--%>
    <%--<script type="text/javascript" src="<c:url value='/resources/js/task/list/taskList.js'/>"></script>--%>
    <%--<script type="text/javascript" src="<c:url value='/resources/js/task/list/taskListObject.js'/>"></script>--%>
    <%--<script type="text/javascript" src="<c:url value='/resources/js/task/list/taskListEvent.js'/>"></script>--%>
    <%--<script type="text/javascript" src="<c:url value='/resources/js/task/list/taskListPage.js'/>"></script>--%>
    <%--<script type="text/javascript" src="<c:url value='/resources/js/task/list/taskListRule.js'/>"></script>--%>
    <%--<script type="text/javascript" src="<c:url value='/resources/js/task/list/taskListService.js'/>"></script>--%>
    <%--<script type="text/javascript" src="<c:url value='/resources/js/task/list/taskListCallback.js'/>"></script>--%>
    <%--<script type="text/javascript" src="<c:url value='/resources/js/task/list/taskListJTable.js'/>"></script>--%>

    <script type="text/javascript" src="<c:url value='/resources/vendors/${vd_slimscroll}/${js_slimscroll}'/>"></script>


    <!-- Summernote WYSIWYG -->
    <link rel="stylesheet" href="<c:url value='/resources/vendors/${vd_summernote}/summernote.css'/>" type="text/css"/>
    <script type="text/javascript" src="<c:url value='/resources/vendors/${vd_summernote}/${js_summernote}'/>"></script>

    <!-- JTable -->
    <link rel="stylesheet" href="<c:url value='/resources/vendors/${vd_acm}/themes/basic/${vd_jtable}/blue/jtable.css'/>" type="text/css"/>
    <script type="text/javascript" src="<c:url value='/resources/vendors/${vd_jtable}/${js_jtable}'/>"></script>

    <!-- Fancy Tree -->
    <link href="<c:url value='/resources/vendors/${vd_fancytree}/skin-win8/ui.fancytree.css'/>" rel="stylesheet">
    <script src="<c:url value='/resources/vendors/${vd_fancytree}/${js_fancytree}'/>"></script>
    <script src="<c:url value='/resources/vendors/${vd_fancytree}/${js_fancytree_table}'/>"></script>
    <script src="<c:url value='/resources/vendors/${vd_contextmenu}/${js_contextmenu}'/>"></script>
    <%--<script>--%>

    <%--var edit = function() {--%>
    <%--$('.taskDetails').summernote({focus: true});--%>
    <%--};--%>
    <%--var save = function() {--%>
    <%--var aHTML = $('.click2edit').code(); //save HTML If you need(aHTML: array).--%>
    <%--$('.taskDetails').destroy();--%>
    <%--};--%>

    <%--</script>--%>

    <!-- X-Editable -->
    <link rel="stylesheet" href="<c:url value='/resources/vendors/${vd_acm}/themes/basic/${vd_x_editable}/css/bootstrap-editable.css'/>" type="text/css"/>
    <script src="<c:url value='/resources/vendors/${vd_x_editable}/js/${js_x_editable}'/>"></script>
</jsp:attribute>

<jsp:body>
<section id="content">
<section class="vbox">
<section class="scrollable">
<section class="hbox stretch">
<aside class="aside-lg" id="email-list">
    <section class="vbox animated fadeInLeft">
        <section class="scrollable">
            <header class="dk header">
                <h3 class="m-b-xs text-black pull-left">Tasks</h3>
                <div class="btn-group inline select pull-right">
                    <button class="btn btn-default btn-sm  dropdown-toggle" data-toggle="dropdown"> <span class="dropdown-label" style="width: 65px;"><i class="fa fa-sort"></i></span> <span class="caret"></span></button>
                    <ul class="dropdown-menu text-left text-sm" id="ulSort">
                        <%--<li><a href="#">Sort Date Ascending</a></li>--%>
                        <%--<li><a href="#">Sort Date Descending</a></li>--%>
                        <%--<li><a href="#">Sort Task ID Ascending</a></li>--%>
                        <%--<li><a href="#">Sort Task ID Ascending</a></li>--%>
                    </ul>
                </div>
                <div class="btn-group select pull-right">
                    <button class="btn btn-default btn-sm  dropdown-toggle" data-toggle="dropdown"> <span class="dropdown-label" style="width: 65px;"><i class="fa fa-filter"></i></span> <span class="caret"></span></button>
                    <ul class="dropdown-menu text-left text-sm" id="ulFilter">
                        <%--<li><a href="#">All Open Task</a></li>--%>
                        <%--<li><a href="#">Tasks I've Opened</a></li>--%>
                        <%--<li><a href="#">Unapproved Tasks</a></li>--%>
                        <%--<li><a href="#">Approved Tasks</a></li>--%>
                        <%--<li><a href="#">Tasks From Group</a></li>--%>
                        <%--<li><a href="#">Closed or Expired Tasks</a></li>--%>
                        <%--<li><a href="#">New Tasks</a></li>--%>
                    </ul>
                </div>
            </header>
            <div class="wrapper">
                <div class="input-group">
                    <input type="text" class="input-sm form-control" id="searchQuery" placeholder="Search">
                        <span class="input-group-btn">
                          <button class="btn btn-sm btn-default" type="button">Go!</button>
                        </span></div>
            </div>
            <div class="row m-b">
                <div class="col-sm-12">
                    <div id="tree"></div>
                </div>
            </div>
        </section>
    </section>
</aside>
<!-- /.aside -->
<!-- .aside -->




<aside class="bg-light lter">
    <section class="vbox">
        <%--<h4 id="noTaskFoundMeassge" class="m-n">No task assigned to you was found.</h4>--%>
        <section id="taskDetailView" class="scrollable">
            <div id="tabTop"  style="display:none;">
                <div class="wrapper dk  clearfix" id="divParentDetail" style="display:none;">
                    <div class="row">
                        <div class="col-xs-12">
                            <div class="">
                                <div class=" clearfix">
                                    <div class="col-xs-4 b-r">
                                        <div class="h4 font-bold"><a href="#" id="parentObjTitle" data-type="text" data-pk="1" data-title="Enter Task Title"></a></div>
                                        <small class="text-muted"><a href="#" id="parentObjNumber" ></a></small></div>
                                    <div class="col-xs-2 b-r">
                                        <div class="h4 font-bold"><a href="#" id="parentObjIncidentDate" data-type="date" data-pk="1" data-title="Enter Incident Date"></a></div>
                                        <small class="text-muted">Incident Date</small></div>
                                    <div class="col-xs-1 b-r">
                                        <div class="h4 font-bold"><a href="#" id="parentObjPriority" data-type="select" data-pk="1" data-title="Enter Priority"></a></div>
                                        <small class="text-muted">Priority</small></div>
                                    <div class="col-xs-2 b-r">
                                        <div class="h4 font-bold"><a href="#" id="parentObjAssigned" data-type="select" data-pk="1"  data-title="Enter Assignee"></a></div>
                                        <small class="text-muted">Assigned To</small></div>
                                    <div class="col-xs-2 b-r">
                                        <div class="h4 font-bold"><a href="#" id="parentObjSubjectType" data-type="select" data-pk="1"  data-title="Enter Subject Type"></a></div>
                                        <small class="text-muted">Subject Type</small></div>
                                    <div class="col-xs-1">
                                        <div class="h4 font-bold"><a href="#" id="parentObjStatus" ></a></div>
                                        <small class="text-muted">State</small></div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>

                <div class="wrapper bg-empty  clearfix">
                    <div class="pull-right inline">
                        <div class="btn-group-task">
                            <!-- TODO: when data-toggle is modal, the tooltip won't come up
                            -->
                            <%--<button class="btn btn-default btn-sm" id="btnSignature" data-toggle="modal" data-title="Sign" data-target="#signatureModal"><i class="fa fa-certificate"></i></button>--%>

                            <!-- from the mockup -->
                            <%--<button class="btn btn-info btn-sm" id="btnReject" data-toggle="modal" data-target="#reject" title="Reject Task">Reject</button>--%>
                            <button class="btn btn-default btn-sm" id="btnReject" data-toggle="modal" data-target="#reject" title="Reject Task">Reject</button>
                            <button class="btn btn-default btn-sm" id="btnDelete" data-toggle="modal" title="Delete Task">Delete</button>
                            <button class="btn btn-default btn-sm" id="btnComplete" data-toggle="modal" title="Complete Task">Complete</button>


                            <%--<button class="btn btn-default btn-sm businessProcess" id="btnReassign" data-title="Reassign Task"><i class="fa fa-share"></i> Reassign</button>
                            <button class="btn btn-default btn-sm businessProcess" id="btnUnassign" data-title="Unassign Task"><i class="fa fa-circle-o"></i> Unassign</button>--%>

                            <button class="btn btn-default btn-sm" id="btnSubscribe"><i class="i i-alarm"></i> Subscribe</button>
                        </div>

                    </div>
                        <%--
                                        <h4 class="m-n"> <a href="#" id="caseTitle" data-type="text" data-title="Enter Case Title"></a></h4>
                        --%>
                    <h4 class="m-n"> <a href="#" id="taskSubject" data-type="text" data-pk="1" data-title="Enter Task Subject"></a></h4>
                    <%--<small class="text-muted"><a href="#" id="parentNumber" >2014-03-12321</a></small></div>--%>

                <hr/>
                    <div class="row">
                        <div class="col-xs-12">
                            <div class="">
                                <div class=" clearfix">
                                    <div class="col-xs-2 b-r">
                                        <div class="h4 font-bold"><a href="#" id="percentageCompleted" data-type="text" data-pk="1" data-title="Enter % of Completion"></a></div>
                                        <small class="text-muted">% of Completion</small></div>
                                    <div class="col-xs-2 b-r">
                                        <div class="h4 font-bold"><a href="#" id="taskOwner" data-type="select" data-pk="1" data-title="Enter Owner"></a></div>
                                        <small class="text-muted">Assignee</small></div>
                                    <div class="col-xs-2 b-r">
                                        <div class="h4 font-bold"><a href="#" id="priority" data-type="select" data-pk="1" data-title="Enter priority"></a></div>
                                        <small class="text-muted">Priority</small></div>
                                    <div class="col-xs-2 b-r">
                                        <div class="h4 font-bold"><a href="#" id="startDate" data-type="date" data-pk="1" data-title="Enter Start Date"></a></div>
                                        <small class="text-muted">Start Date</small></div>
                                    <div class="col-xs-2 b-r">
                                        <div class="h4 font-bold"><a href="#" id="dueDate" data-type="date" data-pk="1" data-title="Enter Due Date"></a></div>
                                        <small class="text-muted">Due Date</small></div>
                                    <div class="col-xs-2">
                                        <div class="h4 font-bold"><a href="#" id="status" data-type="text" data-title="Enter Task State"></a></div>
                                        <small class="text-muted">State</small></div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>

            <div class="row" id="tabTopBlank">
                <p>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;(No task data)</p>
            </div>

            <div class="wrapper">
                <div class="row" id="tabBlank" style="display:none;">
                    <p>tabBlank</p>
                </div>

                <div class="row" id="tabDetails" style="display:none;">
                    <div class="col-md-12">
                        <section class="panel b-a ">
                            <div class="panel-heading b-b bg-info">
                                <ul class="nav nav-pills pull-right">
                                    <li>
                                        <div class="btn-group padder-v2">
                                            <button class="btn btn-default btn-sm" data-toggle="tooltip" data-title="Edit"><i class="fa fa-pencil"></i></button>
                                            <button class="btn btn-default btn-sm" data-toggle="tooltip" data-title="Save"><i class="fa fa-save"></i></button>
                                            <ul class="dropdown-menu pull-right">
                                                <li><a href="#">Other menu items</a></li>
                                            </ul>
                                        </div>
                                    </li>
                                    <li> <a href="#" class="panel-toggle text-muted"><i class="fa fa-caret-down text-active"></i><i class="fa fa-caret-up text"></i></a> </li>
                                </ul>
                                </span> <a href="#" class="font-bold">Task Details</a> </div>
                            <div class="panel-body">
                                <div class="divDetail"></div>
                            </div>
                        </section>
                    </div>
                </div>

                <div class="row" id="tabReworkInstructions" style="display:none;">
                    <div class="col-md-12">
                        <section class="panel b-a ">
                            <div class="panel-heading b-b bg-info">
                                <ul class="nav nav-pills pull-right">
                                    <li>
                                        <div class="btn-group padder-v2">
                                            <button class="btn btn-default btn-sm" data-toggle="tooltip" data-title="Edit"><i class="fa fa-pencil"></i></button>
                                            <button class="btn btn-default btn-sm" data-toggle="tooltip" data-title="Save"><i class="fa fa-save"></i></button>
                                            <ul class="dropdown-menu pull-right">
                                                <li><a href="#">Other menu items</a></li>
                                            </ul>
                                        </div>
                                    </li>
                                    <li> <a href="#" class="panel-toggle text-muted"><i class="fa fa-caret-down text-active"></i><i class="fa fa-caret-up text"></i></a> </li>
                                </ul>
                                </span> <a href="#" class="font-bold">Rework Details</a> </div>
                            <div class="panel-body">
                                <div class="taskReworkInstructions" data-field=""></div>
                            </div>
                        </section>
                    </div>
                </div>
                
              	<div class="row" id="tabRejectComments" style="display:none;">
                    <div class="col-md-12">
                        <section class="panel b-a">
                            <div id="divRejectComments" style="width:100%"></div>
                        </section>
                    </div>
                </div>

                <div class="row" id="tabDocuments" style="display:none;">
                    <div class="col-md-12">
                        <section class="panel b-a">
                            <div id="divDocuments" style="width:100%"></div>
                            <%--<input id="roiFormUrl" type="hidden" value="${roiFormUrl}" style="width:100% display:none;" />--%>
                        </section>
                    </div>
                </div>


                <div class="row" id="tabAttachments" style="display:none;">
                    <div class="col-md-12">
                        <section class="panel b-a">
                            <div id="divAttachments" style="width:100%"></div>
                        </section>
                    </div>
                </div>


                <div class="row" id="tabNotes" style="display:none;">
                    <div class="col-md-12">
                        <section class="panel b-a">
                            <div id="divNotes" style="width:100%"></div>
                        </section>
                    </div>
                </div>


                <div class="row" id="tabWorkflowOverview" style="display:none;">
                    <div class="col-md-12">
                        <section class="panel b-a">
                            <div id="divWorkflowOverview" style="width:100%"></div>
                        </section>
                    </div>
                </div>

                <div class="row" id="tabHistory" style="display:none;">
                    <div class="col-md-12">
                        <section class="panel b-a">
                            <div id="divHistory" style="width:100%"></div>
                        </section>
                    </div>
                </div>


                <div class="row" id="tabCloseComplaintButton" style="display:none;">
                    <div class="col-md-12">
                        <div class="pull-right inline">
                            <div class="btn-group">
                                <button class="btn btn-default btn-sm" data-toggle="tooltip" id = "editCloseComplaint" data-title="Close Complaint" style="display:none;"><i class="fa fa-archive"></i> Edit Close Complaint</button>
                                <button class="btn btn-default btn-sm" data-toggle="tooltip" id = "changeCaseStatus" data-title="Close Complaint" style="display:none;"><i class="fa fa-archive"></i> Change Case Status</button>
                                <%--<input id="editCloseComplaintFormUrl" type="hidden" value="${editCloseComplaintFormUrl}" />--%>
                                <%--<input id="changeCaseStatusFormUrl" type="hidden" value="${changeCaseStatusFormUrl}" />--%>
                                <form id="formAttachments" style="display:none;">
                                            <%--<input type="file" id="file" name="file">--%>
                                        <input id="addNewAttachments" type="file" name="files[]" multiple/>

                                        <%--<input type="submit">--%>
                                </form>
                            </div>
                        </div>
                    </div>
                </div>

                <div class="row" id="tabSignature" style="display:none;">
                    <div class="col-md-12">
                        <section class="panel b-a ">
                            <div class="panel-heading b-b bg-info">
                                <ul class="nav nav-pills pull-right">
                                    <li> <a href="#" class="panel-toggle text-muted"><i class="fa fa-caret-down text-active"></i><i class="fa fa-caret-up text"></i></a> </li>
                                </ul>
                                <a href="#" class="font-bold">Electronic Signatures</a> </div>
                            <div class="panel-body max-200 no-padder">
                                <table class="table table-striped th-sortable table-hover">
                                    <thead>
                                    <tr>
                                        <th>Signed By</th>
                                        <th>Date</th>
                                    </tr>
                                    </thead>
                                    <tbody id="signatureList" >
                                    </tbody>
                                </table>
                            </div>
                        </section>
                    </div>
                </div>


            </div>
        </section>
    </section>
</aside>
<!-- /.aside -->

<aside class="aside bg-light lt hide" id="chat">
    <section class="vbox animated fadeInLeft">
        <section class="scrollable">
            <header class="dk header">
                <p>Approvers</p>
            </header>
            <div class="list-group auto list-group-alt no-radius no-borders"> <a class="list-group-item" href="#"> <i class="fa fa-fw fa-circle-o text-success text-xs"></i> <span>James Bailey</span> </a> </div>
            <header class="dk header">
                <p>Collaborators</p>
            </header>
            <div class="list-group auto list-group-alt no-radius no-borders"> <a class="list-group-item" href="#"> <i class="fa fa-fw fa-circle-o text-success text-xs"></i> <span>James Bailey</span> </a> </div>
            <header class="dk header">
                <p>Watchers</p>
            </header>
            <div class="list-group auto list-group-alt no-radius no-borders"> <a class="list-group-item" href="#"> <i class="fa fa-fw fa-circle-o text-success text-xs"></i> <span>James Bailey</span> </a> </div>
        </section>
        <footer class="footer text-center">
            <button class="btn btn-light bg-empty btn-sm"><i class="fa fa-plus"></i> New Partipant</button>
        </footer>
    </section>
</aside>



</section>
</section>
</section>
</section>
</jsp:body>
</t:layout>




<!-- Modal -->
<!-- TODO this should be moved to a common jspf file -->
<div class="modal fade" id="signatureModal" tabindex="-1" role="dialog" aria-labelledby="signatureModalLabel" aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal"><span aria-hidden="true">&times;</span><span class="sr-only">Close</span></button>
                <h4 class="modal-title" id="signatureModalLabel">Electronically Sign</h4>
            </div>
            <%-- Using a form post ajax submit --%>
            <form id="signatureConfirmForm" method="post" >
                <div class="modal-body">
                    <div class="clearfix">
                        <label for="confirmPassword">Password</label>
                        <input id="confirmPassword" name="confirmPassword" type="password" placeholder="Password" >
                    </div>
                </div>
            </form>
            <div class="modal-footer">
                <button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
                <button type="button" class="btn btn-primary" id="signatureConfirmBtn">Confirm</button>
            </div>
        </div>
    </div>
</div>



<div class="modal fade" id="assign" tabindex="-1" role="dialog" aria-labelledby="assignModalLabel" aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal">&times;<span class="sr-only">Close</span></button>
                <h4 class="modal-title" id="assignModalLabel">Assign Task</h4>
            </div>
            <div class="modal-body">
                <p>Who would you like to assign this task to?</p>
                <section class="panel panel-default">
                    <div class="row wrapper">
                        <div class="col-sm-12">
                            <div class="input-group">
                                <input type="text" class="input-sm form-control" placeholder="Search people..">
                                          <span class="input-group-btn">
                                          <button class="btn btn-sm btn-default" type="button">Go!</button>
                                          </span> </div>
                        </div>
                    </div>
                    <div class="table-responsive">
                        <table class="table table-striped b-t b-light">
                            <thead>
                            <tr>
                                <th width="20"></th>
                                <th class="th-sortable" data-toggle="class">First Name <span class="th-sort"> <i class="fa fa-sort-down text"></i> <i class="fa fa-sort-up text-active"></i> <i class="fa fa-sort"></i> </span> </th>
                                <th>Last Name</th>
                                <th>Username</th>
                                <th>Organization</th>
                            </tr>
                            </thead>
                            <tbody>
                            <tr>
                                <td><label class="checkbox m-n">
                                    <input type="radio" name="post[]">
                                    <i></i></label></td>
                                <td>[First Name]</td>
                                <td>[Last Name]</td>
                                <td>[Username]</td>
                                <td>[Organization]</td>
                            </tr>
                            </tbody>
                        </table>
                    </div>
                    <footer class="panel-footer">
                        <div class="row">
                            <div class="col-sm-6"> <small class="text-muted inline m-t-sm m-b-sm">Showing 20-30 of 50 items</small> </div>
                            <div class="col-sm-6 text-right text-center-xs">
                                <ul class="pagination pagination-sm m-t-none m-b-none">
                                    <li><a href="#"><i class="fa fa-chevron-left"></i></a></li>
                                    <li><a href="#">1</a></li>
                                    <li><a href="#">2</a></li>
                                    <li><a href="#">3</a></li>
                                    <li><a href="#"><i class="fa fa-chevron-right"></i></a></li>
                                </ul>
                            </div>
                        </div>
                    </footer>
                </section>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-default" data-dismiss="modal">Cancel</button>
                <button type="button" class="btn btn-primary">Assign Task</button>
            </div>
        </div>
    </div>
</div>
<div class="modal fade" id="reassign" tabindex="-1" role="dialog" aria-labelledby="reassignModalLabel" aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal">&times;<span class="sr-only">Close</span></button>
                <h4 class="modal-title" id="reassignModalLabel">Reassign Task</h4>
            </div>
            <div class="modal-body">
                <p>Who would you like to reassign this task to?</p>
                <section class="panel panel-default">
                    <div class="row wrapper">
                        <div class="col-sm-12">
                            <div class="input-group">
                                <input type="text" class="input-sm form-control" placeholder="Search people..">
                                          <span class="input-group-btn">
                                          <button class="btn btn-sm btn-default" type="button">Go!</button>
                                          </span> </div>
                        </div>
                    </div>
                    <div class="table-responsive">
                        <table class="table table-striped b-t b-light">
                            <thead>
                            <tr>
                                <th width="20"></th>
                                <th class="th-sortable" data-toggle="class">First Name <span class="th-sort"> <i class="fa fa-sort-down text"></i> <i class="fa fa-sort-up text-active"></i> <i class="fa fa-sort"></i> </span> </th>
                                <th>Last Name</th>
                                <th>Username</th>
                                <th>Organization</th>
                            </tr>
                            </thead>
                            <tbody>
                            <tr>
                                <td><label class="checkbox m-n">
                                    <input type="radio" name="post[]">
                                    <i></i></label></td>
                                <td>[First Name]</td>
                                <td>[Last Name]</td>
                                <td>[Username]</td>
                                <td>[Organization]</td>
                            </tr>
                            </tbody>
                        </table>
                    </div>
                    <footer class="panel-footer">
                        <div class="row">
                            <div class="col-sm-6"> <small class="text-muted inline m-t-sm m-b-sm">Showing 20-30 of 50 items</small> </div>
                            <div class="col-sm-6 text-right text-center-xs">
                                <ul class="pagination pagination-sm m-t-none m-b-none">
                                    <li><a href="#"><i class="fa fa-chevron-left"></i></a></li>
                                    <li><a href="#">1</a></li>
                                    <li><a href="#">2</a></li>
                                    <li><a href="#">3</a></li>
                                    <li><a href="#"><i class="fa fa-chevron-right"></i></a></li>
                                </ul>
                            </div>
                        </div>
                    </footer>
                </section>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-default" data-dismiss="modal">Cancel</button>
                <button type="button" class="btn btn-primary">Reassign Task</button>
            </div>
        </div>
    </div>
</div>
<div class="modal fade" id="unassign" tabindex="-1" role="dialog" aria-labelledby="unassignModalLabel" aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal">&times;<span class="sr-only">Close</span></button>
                <h4 class="modal-title" id="unassignModalLabel">Unassign Task</h4>
            </div>
            <div class="modal-body">
                <p>Are you sure you want to unassign this task?</p>
                <label>Reason</label>
                <textarea class="form-control"></textarea>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-default" data-dismiss="modal">Cancel</button>
                <button type="button" class="btn btn-primary">Unassign Task</button>
            </div>
        </div>
    </div>
</div>

<div class="modal fade" id="approve" tabindex="-1" role="dialog" aria-labelledby="approveModalLabel" aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal">&times;<span class="sr-only">Close</span></button>
                <h4 class="modal-title" id="approveModalLabel">Approve Task</h4>
            </div>
            <div class="modal-body">
                <p>Are you sure you want to approve this task?</p>
                <label>Reason</label>
                <textarea class="form-control"></textarea>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-default" data-dismiss="modal">Cancel</button>
                <button type="button" class="btn btn-primary">Approve Task</button>
            </div>
        </div>
    </div>
</div>
<div class="modal fade" id="reject" tabindex="-1" role="dialog" aria-labelledby="rejectModalLabel" aria-hidden="true">
	<div class="modal-dialog">
		<div class="modal-content">
  			<div class="modal-header">
                <button type="button" class="close" data-dismiss="modal">&times;<span class="sr-only">Close</span></button>
                <h4 class="modal-title" id="rejectModalLabel">Reject Task</h4>
            </div>
  			<div class="modal-body">
  				<p>Are you sure you want to reject this task?</p>
    			<p>This task will be returned to the owner:</p>
    			<section class="panel panel-default">
					<div class="table-responsive">
    					<table class="table table-striped b-t b-light" id="ownerTableRejectTask">
							<thead>
								<tr>
									<th width="20"></th>
									<th>First Name</th>
									<th>Last Name</th>
									<th>Username</th>
									<th>Organization</th>
								</tr>
							</thead>
							<tbody>
								<!-- This area is filled dynamically depending of the result of the service -->
							</tbody>
						</table>
					</div>
				</section>
				<p>Or select some other people from below:</p>
				<section class="panel panel-default">
					<div class="row wrapper">
						<div class="col-sm-12">
							<div class="input-group">
								<input type="text" class="input-sm form-control" name="searchKeywordRejectTask" placeholder="Search people..">
								<span class="input-group-btn">
									<button class="btn btn-sm btn-default" type="button" name="searchUsersRejectTask">Go!</button>
								</span> 
							</div>
                       	</div>
                     </div>
                     <div class="table-responsive">
                       <table class="table table-striped b-t b-light" id="usersTableRejectTask">
			               <thead>
			                 	<tr>
			                   		<th width="20"></th>
			                   		<th class="th-sortable" data-toggle="class">First Name <span class="th-sort"> <i class="fa fa-sort-down text"></i> <i class="fa fa-sort-up text-active"></i> <i class="fa fa-sort"></i> </span> </th>
			                   		<th>Last Name</th>
			                   		<th>Username</th>
			                   		<th>Organization</th>
			                 	</tr>
			               	</thead>
	               			<tbody>
	               				<!-- This area is filled dynamically depending of the result of the service -->
	               			</tbody>
             			</table>
           			</div>
					<footer class="panel-footer">
					  <div class="row">
					    <div class="col-sm-6"> <small class="text-muted inline m-t-sm m-b-sm">Showing 0-0 of 0 items</small> </div>
					    <div class="col-sm-6 text-right text-center-xs">
					      <ul class="pagination pagination-sm m-t-none m-b-none">
					      	<!-- This area is filled dinamically depending of the result of the service -->
					      </ul>
					    </div>
					  </div>
					</footer>
         		</section>
       			<label>Reason</label>
            	<textarea class="form-control" id="commentRejectTask"></textarea>
       		</div>
       		<div class="modal-footer">
         		<button type="button" class="btn btn-default" data-dismiss="modal" name="cancelRejectTask">Cancel</button>
         		<button type="button" class="btn btn-primary" name="submitRejectTask">Reject Task</button>
       		</div>
     	</div>
   	</div>
</div>
<div class="modal fade" id="delete" tabindex="-1" role="dialog" aria-labelledby="deleteModalLabel" aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal">&times;<span class="sr-only">Close</span></button>
                <h4 class="modal-title" id="deleteModalLabel">Delete Task</h4>
            </div>
            <div class="modal-body">
                <p>Are you sure you want to delete this task?</p>
                <label>Reason</label>
                <textarea class="form-control"></textarea>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-default" data-dismiss="modal">Cancel</button>
                <button type="button" class="btn btn-primary">Delete Task</button>
            </div>
        </div>
    </div>
</div>



