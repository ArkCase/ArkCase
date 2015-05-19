<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>


<t:layout>
<jsp:attribute name="endOfHead">
    <title><spring:message code="task.page.title" text="Tasks | ACM | Armedia Case Management" /></title>
    <div id="detailData" itemscope="true" style="display: none">
        <span itemprop="resourceNamespace">task,search,subscription</span>
        <span itemprop="objType">TASK</span>
        <span itemprop="objId">${objId}</span>
        <span itemprop="treeFilter">${treeFilter}</span>
        <span itemprop="treeSort">${treeSort}</span>

        <%--<span itemprop="closeComplaintFormUrl">${closeComplaintFormUrl}</span>--%>
        <span itemprop="editCloseComplaintFormUrl">${editCloseComplaintFormUrl}</span>
        <span itemprop="roiFormUrl">${roiFormUrl}</span>
        <span itemprop="changeCaseStatusFormUrl">${changeCaseStatusFormUrl}</span>
        <span itemprop="fileTypes">${fileTypes}</span>

    </div>
</jsp:attribute>

<jsp:attribute name="endOfBody">
    <script type="text/javascript" src="<c:url value='/resources/vendors/${vd_acm}/js/objnav/objNav.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/resources/vendors/${vd_acm}/js/objnav/objNavService.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/resources/vendors/${vd_acm}/js/objnav/objNavModel.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/resources/vendors/${vd_acm}/js/objnav/objNavView.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/resources/vendors/${vd_acm}/js/objnav/objNavController.js'/>"></script>

    <script type="text/javascript" src="<c:url value='/resources/js/task/task.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/resources/js/task/taskModel.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/resources/js/task/taskView.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/resources/js/task/taskController.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/resources/js/task/taskService.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/resources/js/subscription/subscriptionOp.js'/>"></script>

    <script type="text/javascript" src="<c:url value='/resources/js/docTree/docTree.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/resources/js/docTree/docTreeModel.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/resources/js/docTree/docTreeView.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/resources/js/docTree/docTreeController.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/resources/js/docTree/docTreeService.js'/>"></script>

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
    <script src="<c:url value='/resources/vendors/${vd_fancytree}/${js_fancytree_gridnav}'/>"></script>
    <script src="<c:url value='/resources/vendors/${vd_fancytree}/${js_fancytree_edit}'/>"></script>
    <script src="<c:url value='/resources/vendors/${vd_fancytree}/${js_fancytree_dnd}'/>"></script>
    <script src="<c:url value='/resources/vendors/${vd_contextmenu}/${js_contextmenu}'/>"></script>

    <!-- X-Editable -->
    <link rel="stylesheet" href="<c:url value='/resources/vendors/${vd_acm}/themes/basic/${vd_x_editable}/css/bootstrap-editable.css'/>" type="text/css"/>
    <script src="<c:url value='/resources/vendors/${vd_x_editable}/js/${js_x_editable}'/>"></script>
</jsp:attribute>

<jsp:body>
    <section class="vbox">
    <section class="scrollable">
    <section class="hbox stretch">
    <aside class="aside-lg" id="email-list">
        <section class="vbox animated fadeInLeft">
            <section class="scrollable">
                <header class="dk header">
                    <h3 class="m-b-xs text-black pull-left" data-i18n="task:title">Tasks</h3>
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
                        <input type="text" class="input-sm form-control" id="searchQuery" data-i18n="[placeholder]task:header.search.search" placeholder="Search">
                            <span class="input-group-btn">
                              <button class="btn btn-sm btn-default" type="button" data-i18n="task:header.search.btn-go">Go!</button>
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
                                            <div class="row">
                                                <div class="col-xs-6 b-r">
                                                    <h4><a href="#" id="parentObjTitle" data-type="text" data-pk="1" data-i18n="[data-title]task:header.label.enter-parent-title" data-title="Enter Parent Title"></a><a href="#" id="parentObjStatus" ></a></h4>
                                                </div>
                                                <div class="col-xs-6 text-right">
                                                    <h4><a href="#" id="parentObjNumber"></a></h4>
                                                </div>
                                            </div>
                                            <div class="row">
                                                <div class="col-xs-4 b-r">
                                                    <div class="h4 font-bold"><a href="#" id="parentObjSubjectType" data-type="select" data-pk="1"  data-i18n="[data-title]task:header.label.enter-subject-type" data-title="Enter Subject Type"></a></div>
                                                    <small class="text-muted" data-i18n="task:header.label.subject-type">Subject Type</small>
                                                </div>
                                                <div class="col-xs-4 b-r">
                                                    <div class="h4 font-bold"><a href="#" id="parentObjIncidentDate" data-type="date" data-pk="1" data-i18n="[data-title]task:header.label.enter-incident-date" data-title="Enter Incident Date"></a></div>
                                                    <small class="text-muted" data-i18n="task:header.label.incident-date">Incident Date</small>
                                                </div>
                                                <div class="col-xs-4 b-r">
                                                    <div class="h4 font-bold"><a href="#" id="parentObjPriority" data-type="select" data-pk="1" data-i18n="[data-title]task:header.label.enter-priority" data-title="Enter Priority"></a></div>
                                                    <small class="text-muted" data-i18n="task:header.label.priority">Priority</small>
                                                </div>
                                            </div>
                                            <div class="row">
                                                <div class="col-xs-4 b-r">
                                                    <div class="h4 font-bold"><a href="#" id="parentObjAssigned" data-type="select" data-pk="1"  data-i18n="[data-title]task:header.label.enter-assignee" data-title="Enter Assignee"></a></div>
                                                    <small class="text-muted" data-i18n="task:header.label.assigned-to">Assigned To</small>
                                                </div>
                                                <div class="col-xs-4  b-r">
                                                    <div class="h4 font-bold"><a href="#" id="parentObjOwningGroup" data-type="select" data-pk="1" data-i18n="[data-title]task:header.label.enter-owning-group" data-title="Enter Owning Group"></a></div>
                                                    <small class="text-muted" data-i18n="task:header.label.owning-group">Owning Group</small>
                                                </div>
                                            </div>
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
                                <button class="btn btn-default btn-sm" id="btnSignature" data-toggle="modal" data-title="Sign" data-target="#signatureModal"><i class="fa fa-certificate"></i></button>

                                <!-- from the mockup -->
                                <%--<button class="btn btn-info btn-sm" id="btnReject" data-toggle="modal" data-target="#reject" title="Reject Task">Reject</button>--%>
                                <button class="btn btn-default btn-sm" id="btnReject" data-toggle="modal" data-target="#reject" data-i18n="[title]task:header.button.reject-task;task:header.button.reject" title="Reject Task">Reject</button>
                                <button class="btn btn-default btn-sm" id="btnDelete" data-toggle="modal" data-i18n="[title]task:header.button.delete-task;task:header.button.delete" title="Delete Task">Delete</button>
                                <button class="btn btn-default btn-sm" id="btnComplete" data-toggle="modal" data-i18n="[title]task:header.button.complete-task;task:header.button.complete" title="Complete Task">Complete</button>


                                <%--<button class="btn btn-default btn-sm businessProcess" id="btnReassign" data-title="Reassign Task"><i class="fa fa-share"></i> Reassign</button>
                                <button class="btn btn-default btn-sm businessProcess" id="btnUnassign" data-title="Unassign Task"><i class="fa fa-circle-o"></i> Unassign</button>--%>
                                <button class="btn btn-default btn-sm" id="btnSubscribe" data-i18n="[data-title]task:header.button.subscribe"><i class="fa fa-bullhorn"></i>
                                </button>
                            </div>

                        </div>
                            <%--
                                            <h4 class="m-n"> <a href="#" id="caseTitle" data-type="text" data-title="Enter Case Title"></a></h4>
                            --%>
                        <h4 class="m-n"> <a href="#" id="taskSubject" data-type="text" data-pk="1" data-i18n="[data-title]task:header.label.enter-task-subject" data-title="Enter Task Subject"></a></h4>
                        <%--<small class="text-muted"><a href="#" id="parentNumber" >2014-03-12321</a></small></div>--%>

                    <hr/>
                        <div class="row">
                            <div class="col-xs-12">
                                <div class="">
                                    <div class=" clearfix">
                                        <div class="col-xs-2 b-r">
                                            <div class="h4 font-bold"><a href="#" id="percentageCompleted" data-type="text" data-pk="1" data-i18n="[data-title]task:header.label.enter-completion-percent" data-title="Enter % of Completion"></a></div>
                                            <small class="text-muted" data-i18n="task:header.label.completion-percent">% of Completion</small></div>
                                        <div class="col-xs-2 b-r">
                                            <div class="h4 font-bold"><a href="#" id="taskOwner" data-type="select" data-pk="1" data-i18n="[data-title]task:header.label.enter-owner" data-title="Enter Owner"></a></div>
                                            <small class="text-muted" data-i18n="task:header.label.assignee">Assignee</small></div>
                                        <div class="col-xs-2 b-r">
                                            <div class="h4 font-bold"><a href="#" id="priority" data-type="select" data-pk="1" data-i18n="[data-title]task:header.label.enter-priority" data-title="Enter Priority"></a></div>
                                            <small class="text-muted" data-i18n="task:header.label.priority">Priority</small></div>
                                        <div class="col-xs-2 b-r">
                                            <div class="h4 font-bold"><a href="#" id="startDate" data-type="date" data-pk="1" data-i18n="[data-title]task:header.label.enter-start-date" data-title="Enter Start Date"></a></div>
                                            <small class="text-muted" data-i18n="task:header.label.start-date">Start Date</small></div>
                                        <div class="col-xs-2 b-r">
                                            <div class="h4 font-bold"><a href="#" id="dueDate" data-type="date" data-pk="1" data-i18n="[data-title]task:header.label.enter-due-date" data-title="Enter Due Date"></a></div>
                                            <small class="text-muted" data-i18n="task:header.label.due-date">Due Date</small></div>
                                        <div class="col-xs-2">
                                            <div class="h4 font-bold"><a href="#" id="status" data-type="text" data-i18n="[data-title]task:header.label.enter-task-state" data-title="Enter Task State"></a></div>
                                            <small class="text-muted" data-i18n="task:header.label.state">State</small></div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>

                <div class="row" id="tabTopBlank">
                    <p>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<span data-i18n="task:msg.no-task-data">(No task data)</span></p>
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
                                                <button class="btn btn-default btn-sm" data-toggle="tooltip" data-i18n="[data-title]task:task-details.button.edit" data-title="Edit"><i class="fa fa-pencil"></i></button>
                                                <button class="btn btn-default btn-sm" data-toggle="tooltip" data-i18n="[data-title]task:task-details.button.save" data-title="Save"><i class="fa fa-save"></i></button>
                                                <ul class="dropdown-menu pull-right">
                                                    <li><a href="#" data-i18n="task:task-details.label.other-menu-items">Other menu items</a></li>
                                                </ul>
                                            </div>
                                        </li>
                                        <li> <a href="#" class="panel-toggle text-muted"><i class="fa fa-caret-down text-active"></i><i class="fa fa-caret-up text"></i></a> </li>
                                    </ul>
                                    </span> <a href="#" class="font-bold" data-i18n="task:task-details.label.task-details">Task Details</a> </div>
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
                                                <button class="btn btn-default btn-sm" data-toggle="tooltip" data-i18n="[data-title]task:rework-details.button.edit" data-title="Edit"><i class="fa fa-pencil"></i></button>
                                                <button class="btn btn-default btn-sm" data-toggle="tooltip" data-i18n="[data-title]task:rework-details.button.save" data-title="Save"><i class="fa fa-save"></i></button>
                                                <ul class="dropdown-menu pull-right">
                                                    <li><a href="#" data-i18n="task:rework-details.label.other-menu-items">Other menu items</a></li>
                                                </ul>
                                            </div>
                                        </li>
                                        <li> <a href="#" class="panel-toggle text-muted"><i class="fa fa-caret-down text-active"></i><i class="fa fa-caret-up text"></i></a> </li>
                                    </ul>
                                    </span> <a href="#" class="font-bold" data-i18n="task:rework-details.label.rework-details">Rework Details</a> </div>
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
                            <%--<section class="panel b-a">--%>
                                <%--<div id="divAttachments" style="width:100%"></div>--%>
                            <%--</section>--%>

                            <section class="panel b-a">
                                <div class="panel-heading b-b bg-info">  <ul class="nav nav-pills pull-right">
                                    <li style="margin-right:5px"></li>
                                        <%--<li>--%>
                                        <%--<div class="btn-group padder-v2">--%>
                                        <%--<button class="btn btn-default btn-sm" data-toggle="modal" data-target="#createnewfolder"><i class="fa fa-folder"></i> New Folder</button>--%>
                                        <%--</div>--%>
                                        <%--</li>--%>
                                        <%--<li>--%>
                                        <%--<div class="btn-group padder-v2">--%>
                                        <%--<button class="btn btn-default btn-sm" data-toggle="modal" data-target="#emailDocs"><i class="fa fa-share"></i> Email</button>--%>
                                        <%--</div>--%>
                                        <%--</li>--%>
                                        <%--<li>--%>
                                        <%--<div class="btn-group padder-v2">--%>
                                        <%--<button class="btn btn-default btn-sm" onClick="window.open('documents.html', '_blank');"><i class="fa fa-print"></i> Print</button>--%>
                                        <%--</div>--%>
                                        <%--</li>--%>
                                    <li> </li>
                                </ul>


                                    <a href="#" class="font-bold"><div data-i18n="task:attachments.label.attachments">Attachments</div> </a>
                                </div>


                                <div class="modal fade" id="createnewfolder" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
                                    <div class="modal-dialog">
                                        <div class="modal-content">
                                            <div class="modal-header">
                                                <button type="button" class="close" data-dismiss="modal">&times;<span class="sr-only">Close</span></button>
                                                <h4 class="modal-title" id="myModalLabel" data-i18n="task:create-new-folder-dialog.title">Create Folder</h4>
                                            </div>
                                            <div class="modal-body">

                                                <p data-i18n="task:create-new-folder-dialog.body">Enter a name for the folder you would like to create:</p>

                                                <label for="folderName2" data-i18n="task:create-new-folder-dialog.label.folder-name">Folder Name</label><br/>
                                                <input type="text" id="folderName2" class="input-lg" data-i18n="[placeholder]task:create-new-folder-dialog.label.folder-name" placeholder="Folder Name" />
                                            </div>
                                            <div class="modal-footer">
                                                <button type="button" class="btn btn-default" data-dismiss="modal" data-i18n="task:create-new-folder-dialog.button.cancel">Cancel</button>
                                                <button type="button" class="btn btn-primary" data-i18n="task:create-new-folder-dialog.button.ok">Create Folder<</button>
                                            </div>
                                        </div>
                                    </div>
                                </div>


                                <div class="modal fade" id="emailDocs" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
                                    <div class="modal-dialog">
                                        <div class="modal-content">
                                            <div class="modal-header">
                                                <button type="button" class="close" data-dismiss="modal">&times;<span class="sr-only" data-i18n="task:email-documents-dialog.button.close">Close</span></button>
                                                <h4 class="modal-title" id="myModalLabel" data-i18n="task:email-documents-dialog.title">Send Email</h4>
                                            </div>
                                            <div class="modal-body">

                                                <p data-i18n="task:email-documents-dialog.body">Where would you like to email this file?</p>

                                                <label for="emailaddy" data-i18n="task:email-documents-dialog.label.email-address">Email Address</label><br/>
                                                <input type="text" id="emailaddy" class="input-lg" placeholder="Email Address" data-i18n="[placeholder]task:email-documents-dialog.label.email-address" />

                                            </div>
                                            <div class="modal-footer">
                                                <button type="button" class="btn btn-default" data-dismiss="modal" data-i18n="task:email-documents-dialog.button.cancel">Cancel</button>
                                                <button type="button" class="btn btn-primary" data-i18n="task:email-documents-dialog.button.ok">Send Email</button>
                                            </div>
                                        </div>
                                    </div>
                                </div>

                                <table id="treeDoc" class="table table-striped th-sortable table-hover">
                                    <thead>
                                    <tr>
                                        <th width2="6%"><span class='fancytree-checkbox'></span></th>
                                        <th width2="4%" data-i18n="task:attachments.table.field.id" >ID</th>
                                        <th width="35%" data-i18n="task:attachments.table.field.title">Title</th>
                                        <th width="12%" data-i18n="task:attachments.table.field.type">Type</th>
                                        <th width="10%" data-i18n="task:attachments.table.field.created">Created</th>
                                        <th width="16%" data-i18n="task:attachments.table.field.author">Author</th>
                                        <th width="6%" data-i18n="task:attachments.table.field.version">Version</th>
                                        <th width="8%" data-i18n="task:attachments.table.field.status">Status</th>
                                            <%--<th width2="6%" colspan="2"></th>--%>
                                    </tr>
                                    </thead>
                                    <tbody>
                                    <tr>
                                        <td></td>
                                        <td></td>
                                        <td></td>
                                        <td></td>
                                        <td></td>
                                        <td></td>
                                        <td></td>
                                        <td></td>
                                            <%--<td></td>--%>
                                    </tr>
                                    </tbody>
                                </table>

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
                                    <button class="btn btn-default btn-sm" data-toggle="tooltip" id = "editCloseComplaint" data-i18n="[data-title]task:close-complaint.button.close-complaint;task:close-complaint.button.edit-close-complaint" data-title="Close Complaint" style="display:none;"><i class="fa fa-archive"></i> Edit Close Complaint</button>
                                    <button class="btn btn-default btn-sm" data-toggle="tooltip" id = "changeCaseStatus" data-i18n="[data-title]task:close-complaint.button.close-complaint;task:close-complaint.button.change-case-status" data-title="Close Complaint" style="display:none;"><i class="fa fa-archive"></i> Change Case Status</button>
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
                            <section class="panel b-a">
                                <div id="divElectronicSignature" style="width:100%"></div>
                            </section>
                        </div>
                    </div>


                    <%--<div class="row" id="tabSignature" style="display:none;">
                        <div class="col-md-12">
                            <section class="panel b-a ">
                                <div class="panel-heading b-b bg-info">
                                    <ul class="nav nav-pills pull-right">
                                        <li> <a href="#" class="panel-toggle text-muted"><i class="fa fa-caret-down text-active"></i><i class="fa fa-caret-up text"></i></a> </li>
                                    </ul>
                                    <a href="#" class="font-bold" data-i18n="task:signature.label.electronic-signatures">Electronic Signatures</a> </div>
                                <div class="panel-body max-200 no-padder">
                                    <table class="table table-striped th-sortable table-hover">
                                        <thead>
                                        <tr>
                                            <th data-i18n="task:signature.table.field.signed-by">Signed By</th>
                                            <th data-i18n="task:signature.table.field.date">Date</th>
                                        </tr>
                                        </thead>
                                        <tbody id="signatureList" >
                                        </tbody>
                                    </table>
                                </div>
                            </section>
                        </div>
                    </div>--%>


                </div>
            </section>
        </section>
    </aside>
    <!-- /.aside -->

    <aside class="aside bg-light lt hide" id="chat">
        <section class="vbox animated fadeInLeft">
            <section class="scrollable">
                <header class="dk header">
                    <p data-i18n="task:chat.label.approvers">Approvers</p>
                </header>
                <div class="list-group auto list-group-alt no-radius no-borders"> <a class="list-group-item" href="#"> <i class="fa fa-fw fa-circle-o text-success text-xs"></i> <span>James Bailey</span> </a> </div>
                <header class="dk header">
                    <p data-i18n="task:chat.label.collaborators">Collaborators</p>
                </header>
                <div class="list-group auto list-group-alt no-radius no-borders"> <a class="list-group-item" href="#"> <i class="fa fa-fw fa-circle-o text-success text-xs"></i> <span>James Bailey</span> </a> </div>
                <header class="dk header">
                    <p data-i18n="task:chat.label.watchers">Watchers</p>
                </header>
                <div class="list-group auto list-group-alt no-radius no-borders"> <a class="list-group-item" href="#"> <i class="fa fa-fw fa-circle-o text-success text-xs"></i> <span>James Bailey</span> </a> </div>
            </section>
            <footer class="footer text-center">
                <button class="btn btn-light bg-empty btn-sm"><i class="fa fa-plus"></i> <span data-i18n="task:chat.button.new-participant">New Participant</span></button>
            </footer>
        </section>
    </aside>



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
                <h4 class="modal-title" id="signatureModalLabel" data-i18n="task:signature-dialog.title">Electronically Sign</h4>
            </div>
            <%-- Using a form post ajax submit --%>
            <form id="signatureConfirmForm" method="post" >
                <div class="modal-body">
                    <div class="clearfix">
                        <label for="confirmPassword" data-i18n="task:signature-dialog.label.password">Password</label>
                        <input id="confirmPassword" name="confirmPassword" type="password" data-i18n="[placeholder]task:signature-dialog.label.password" placeholder="Password" >
                    </div>
                </div>
            </form>
            <div class="modal-footer">
                <button type="button" class="btn btn-default" data-dismiss="modal" data-i18n="task:signature-dialog.button.close">Close</button>
                <button type="button" class="btn btn-primary" id="signatureConfirmBtn" data-i18n="task:signature-dialog.button.ok">Confirm</button>
            </div>
        </div>
    </div>
</div>



<div class="modal fade" id="assign" tabindex="-1" role="dialog" aria-labelledby="assignModalLabel" aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal">&times;<span class="sr-only" data-i18n="task:assign-dialog.button.close">Close</span></button>
                <h4 class="modal-title" id="assignModalLabel" data-i18n="task:assign-dialog.title">Assign Task</h4>
            </div>
            <div class="modal-body">
                <p data-i18n="task:assign-dialog.label.who-assign-task-to">Who would you like to assign this task to?</p>
                <section class="panel panel-default">
                    <div class="row wrapper">
                        <div class="col-sm-12">
                            <div class="input-group">
                                <input type="text" class="input-sm form-control" data-i18n="[placeholder]task:assign-dialog.button.search-people" placeholder="Search people..">
                                          <span class="input-group-btn">
                                          <button class="btn btn-sm btn-default" type="button" data-i18n="task:assign-dialog.button.go">Go!</button>
                                          </span> </div>
                        </div>
                    </div>
                    <div class="table-responsive">
                        <table class="table table-striped b-t b-light">
                            <thead>
                            <tr>
                                <th width="20"></th>
                                <th class="th-sortable" data-toggle="class" data-i18n="task:assign-dialog.table.field.first-name">First Name <span class="th-sort"> <i class="fa fa-sort-down text"></i> <i class="fa fa-sort-up text-active"></i> <i class="fa fa-sort"></i> </span> </th>
                                <th data-i18n="task:assign-dialog.table.field.last-name">Last Name</th>
                                <th data-i18n="task:assign-dialog.table.field.username">Username</th>
                                <th data-i18n="task:assign-dialog.table.field.organization">Organization</th>
                            </tr>
                            </thead>
                            <tbody>
                            <tr>
                                <td><label class="checkbox m-n">
                                    <input type="radio" name="post[]">
                                    <i></i></label></td>
                                <td data-i18n="task:assign-dialog.table.value.first-name">[First Name]</td>
                                <td data-i18n="task:assign-dialog.table.value.last-name">[Last Name]</td>
                                <td data-i18n="task:assign-dialog.table.value.username">[Username]</td>
                                <td data-i18n="task:assign-dialog.table.value.organization">[Organization]</td>
                            </tr>
                            </tbody>
                        </table>
                    </div>
                    <footer class="panel-footer">
                        <div class="row">
                            <div class="col-sm-6">
                                <small class="text-muted inline m-t-sm m-b-sm" data-i18n="task:assign-dialog.table.field.showing-items">
                                Showing 20-30 of 50 items
                                </small>
                            </div>
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
                <button type="button" class="btn btn-default" data-dismiss="modal" data-i18n="task:assign-dialog.button.cancel">Cancel</button>
                <button type="button" class="btn btn-primary" data-i18n="task:assign-dialog.button.ok">Assign Task</button>
            </div>
        </div>
    </div>
</div>
<div class="modal fade" id="reassign" tabindex="-1" role="dialog" aria-labelledby="reassignModalLabel" aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal">&times;<span class="sr-only" data-i18n="task:reassign-dialog.button.close">Close</span></button>
                <h4 class="modal-title" id="reassignModalLabel" data-i18n="task:reassign-dialog.title">Reassign Task</h4>
            </div>
            <div class="modal-body">
                <p data-i18n="task:reassign-dialog.label.who-reassign-task-to">Who would you like to reassign this task to?</p>
                <section class="panel panel-default">
                    <div class="row wrapper">
                        <div class="col-sm-12">
                            <div class="input-group">
                                <input type="text" class="input-sm form-control" data-i18n="[placeholder]task:reassign-dialog.button.search-people" placeholder="Search people..">
                                          <span class="input-group-btn">
                                          <button class="btn btn-sm btn-default" type="button" data-i18n="task:reassign-dialog.button.go">Go!</button>
                                          </span> </div>
                        </div>
                    </div>
                    <div class="table-responsive">
                        <table class="table table-striped b-t b-light">
                            <thead>
                            <tr>
                                <th width="20"></th>
                                <th class="th-sortable" data-toggle="class" data-i18n="task:reassign-dialog.table.field.first-name" >First Name <span class="th-sort"> <i class="fa fa-sort-down text"></i> <i class="fa fa-sort-up text-active"></i> <i class="fa fa-sort"></i> </span> </th>
                                <th data-i18n="task:reassign-dialog.table.field.last-name">Last Name</th>
                                <th data-i18n="task:reassign-dialog.table.field.username">Username</th>
                                <th data-i18n="task:reassign-dialog.table.field.organization">Organization</th>
                            </tr>
                            </thead>
                            <tbody>
                            <tr>
                                <td><label class="checkbox m-n">
                                    <input type="radio" name="post[]">
                                    <i></i></label></td>
                                <td data-i18n="task:reassign-dialog.table.value.first-name">[First Name]</td>
                                <td data-i18n="task:reassign-dialog.table.value.last-name">[Last Name]</td>
                                <td data-i18n="task:reassign-dialog.table.value.username">[Username]</td>
                                <td data-i18n="task:reassign-dialog.table.value.organization">[Organization]</td>
                            </tr>
                            </tbody>
                        </table>
                    </div>
                    <footer class="panel-footer">
                        <div class="row">
                            <div class="col-sm-6"> <small class="text-muted inline m-t-sm m-b-sm" data-i18n="task:reassign-dialog.table.field.showing-items">Showing 20-30 of 50 items</small> </div>
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
                <button type="button" class="btn btn-default" data-dismiss="modal" data-i18n="task:reassign-dialog.button.cancel">Cancel</button>
                <button type="button" class="btn btn-primary" data-i18n="task:reassign-dialog.button.ok">Reassign Task</button>
            </div>
        </div>
    </div>
</div>
<div class="modal fade" id="unassign" tabindex="-1" role="dialog" aria-labelledby="unassignModalLabel" aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal">&times;<span class="sr-only" data-i18n="task:unassign-dialog.button.close">Close</span></button>
                <h4 class="modal-title" id="unassignModalLabel" data-i18n="task:unassign-dialog.title">Unassign Task</h4>
            </div>
            <div class="modal-body">
                <p data-i18n="task:unassign-dialog.label.sure-to-unassign">Are you sure you want to unassign this task?</p>
                <label data-i18n="task:unassign-dialog.label.reason">Reason</label>
                <textarea class="form-control"></textarea>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-default" data-dismiss="modal" data-i18n="task:unassign-dialog.button.cancel">Cancel</button>
                <button type="button" class="btn btn-primary" data-i18n="task:unassign-dialog.button.ok">Unassign Task</button>
            </div>
        </div>
    </div>
</div>

<div class="modal fade" id="approve" tabindex="-1" role="dialog" aria-labelledby="approveModalLabel" aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal">&times;<span class="sr-only" data-i18n="task:approve-dialog.button.close">Close</span></button>
                <h4 class="modal-title" id="approveModalLabel" data-i18n="task:approve-dialog.title">Approve Task</h4>
            </div>
            <div class="modal-body">
                <p data-i18n="task:approve-dialog.label.sure-to-approve">Are you sure you want to approve this task?</p>
                <label data-i18n="task:approve-dialog.label.reason">Reason</label>
                <textarea class="form-control"></textarea>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-default" data-dismiss="modal" data-i18n="task:approve-dialog.button.cancel">Cancel</button>
                <button type="button" class="btn btn-primary" data-i18n="task:approve-dialog.button.ok">Approve Task</button>
            </div>
        </div>
    </div>
</div>
<div class="modal fade" id="reject" tabindex="-1" role="dialog" aria-labelledby="rejectModalLabel" aria-hidden="true">
	<div class="modal-dialog">
		<div class="modal-content">
  			<div class="modal-header">
                <button type="button" class="close" data-dismiss="modal">&times;<span class="sr-only" data-i18n="task:reject-dialog.button.close">Close</span></button>
                <h4 class="modal-title" id="rejectModalLabel" data-i18n="task:">Reject Task</h4>
            </div>
  			<div class="modal-body">
  				<p data-i18n="task:reject-dialog.label.sure-to-reject">Are you sure you want to reject this task?</p>
    			<p data-i18n="task:reject-dialog.label.task-will-return-to-owner">This task will be returned to the owner:</p>
    			<section class="panel panel-default">
					<div class="table-responsive">
    					<table class="table table-striped b-t b-light" id="ownerTableRejectTask">
							<thead>
								<tr>
									<th width="20"></th>
									<th data-i18n="task:reject-dialog.table.field.first-name">First Name</th>
									<th data-i18n="task:reject-dialog.table.field.last-name">Last Name</th>
									<th data-i18n="task:reject-dialog.table.field.username">Username</th>
									<th data-i18n="task:reject-dialog.table.field.organization">Organization</th>
								</tr>
							</thead>
							<tbody>
								<!-- This area is filled dynamically depending of the result of the service -->
							</tbody>
						</table>
					</div>
				</section>
				<p data-i18n="task:">Or select some other people from below:</p>
				<section class="panel panel-default">
					<div class="row wrapper">
						<div class="col-sm-12">
							<div class="input-group">
								<input type="text" class="input-sm form-control" name="searchKeywordRejectTask" data-i18n="[placeholder]task:reject-dialog.button.search-button" placeholder="Search people..">
								<span class="input-group-btn">
									<button class="btn btn-sm btn-default" type="button" name="searchUsersRejectTask" data-i18n="task:reject-dialog.button.go">Go!</button>
								</span> 
							</div>
                       	</div>
                     </div>
                     <div class="table-responsive">
                       <table class="table table-striped b-t b-light" id="usersTableRejectTask">
			               <thead>
			                 	<tr>
			                   		<th width="20"></th>
			                   		<th class="th-sortable" data-toggle="class" data-i18n="task:reject-dialog.table.field.first-name">First Name <span class="th-sort"> <i class="fa fa-sort-down text"></i> <i class="fa fa-sort-up text-active"></i> <i class="fa fa-sort"></i> </span> </th>
			                   		<th data-i18n="task:reject-dialog.table.field.last-name">Last Name</th>
			                   		<th data-i18n="task:reject-dialog.table.field.username">Username</th>
			                   		<th data-i18n="task:reject-dialog.table.field.organization">Organization</th>
			                 	</tr>
			               	</thead>
	               			<tbody>
	               				<!-- This area is filled dynamically depending of the result of the service -->
	               			</tbody>
             			</table>
           			</div>
					<footer class="panel-footer">
					  <div class="row">
					    <div class="col-sm-6"> <small class="text-muted inline m-t-sm m-b-sm" data-i18n="task:reject-dialog.label.show-items">Showing 0-0 of 0 items</small> </div>
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
         		<button type="button" class="btn btn-default" data-dismiss="modal" name="cancelRejectTask" data-i18n="task:reject-dialog.button.cancel">Cancel</button>
         		<button type="button" class="btn btn-primary" name="submitRejectTask" data-i18n="task:reject-dialog.button.ok">Reject Task</button>
       		</div>
     	</div>
   	</div>
</div>
<div class="modal fade" id="delete" tabindex="-1" role="dialog" aria-labelledby="deleteModalLabel" aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal">&times;<span class="sr-only" data-i18n="task:delete-dialog.button.close">Close</span></button>
                <h4 class="modal-title" id="deleteModalLabel" data-i18n="task:">Delete Task</h4>
            </div>
            <div class="modal-body">
                <p data-i18n="task:delete-dialog.label.sure-to-delete">Are you sure you want to delete this task?</p>
                <label data-i18n="task:delete-dialog.label.reason">Reason</label>
                <textarea class="form-control"></textarea>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-default" data-dismiss="modal" data-i18n="task:delete-dialog.button.cancel">Cancel</button>
                <button type="button" class="btn btn-primary" data-i18n="task:delete-dialog.button.ok">Delete Task</button>
            </div>
        </div>
    </div>
</div>



