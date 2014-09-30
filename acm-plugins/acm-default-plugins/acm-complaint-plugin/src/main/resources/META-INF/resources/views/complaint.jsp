<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>


<t:layout>
<jsp:attribute name="endOfHead">
    <title>${pageDescriptor.title}</title>
    <div id="detailData" itemscope="true" style="display: none">
        <span itemprop="complaintId">${complaintId}</span>
        <span itemprop="token">${token}</span>
    </div>
</jsp:attribute>

<jsp:attribute name="endOfBody">
    <script type="text/javascript" src="<c:url value='/resources/js/complaint/complaint.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/resources/js/complaint/complaintObject.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/resources/js/complaint/complaintEvent.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/resources/js/complaint/complaintPage.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/resources/js/complaint/complaintRule.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/resources/js/complaint/complaintService.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/resources/js/complaint/complaintCallback.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/resources/js/complaint/complaintJTable.js'/>"></script>

    <script type="text/javascript" src="<c:url value='/'/>resources/vendors/${vd_slimscroll}/${js_slimscroll}"></script>

    <!-- File Manager -->
    <script type="text/javascript" src="<c:url value='/'/>resources/vendors/${vd_knob}/js/${js_knob}"></script>
    <script type="text/javascript" src="<c:url value='/'/>resources/vendors/${vd_upload}/js/${js_upload_fileupload}"></script>
    <script type="text/javascript" src="<c:url value='/'/>resources/vendors/${vd_upload}/js/${js_upload_iframe}"></script>

    <!-- Summernote WYSIWYG -->
    <link rel="stylesheet" href="<c:url value='/'/>resources/vendors/${vd_summernote}/summernote.css" type="text/css"/>
    <script type="text/javascript" src="<c:url value='/'/>resources/vendors/${vd_summernote}/${js_summernote}"></script>

    <!-- JTable -->
    <link rel="stylesheet" href="<c:url value='/'/>resources/vendors/${vd_acm}/themes/basic/${vd_jtable}/blue/jtable.css" type="text/css"/>
    <script type="text/javascript" src="<c:url value='/'/>resources/vendors/${vd_jtable}/${js_jtable}"></script>


    <link href="<c:url value='/'/>resources/vendors/${vd_fancytree}/skin-win8/ui.fancytree.css" rel="stylesheet">
    <script src="<c:url value='/'/>resources/vendors/${vd_fancytree}/${js_fancytree}"></script>
    <script src="<c:url value='/'/>resources/vendors/${vd_fancytree}/${js_fancytree_table}"></script>
    <script src="<c:url value='/'/>resources/vendors/${vd_contextmenu}/${js_contextmenu}"></script>

    <!-- X-Editable -->
    <link rel="stylesheet" href="<c:url value='/'/>resources/vendors/${vd_acm}/themes/basic/${vd_x_editable}/css/bootstrap-editable.css" type="text/css"/>
    <script src="<c:url value='/'/>resources/vendors/${vd_x_editable}/js/${js_x_editable}"></script>

/////////////////////////////////////////////////////////////////////
<style>
    table.fancytree-ext-table {
        width: 100%;
        outline: 0;
    }

    table.fancytree-ext-table tbody tr td {
        border: 0px;
    }
</style>
//////////////////////////////////////////////////////////////////////
</jsp:attribute>

<jsp:body>
<section id="content">
    <section class="vbox">
        <section class="scrollable">
            <section class="hbox stretch"><!-- /.aside -->
                <!-- .aside -->

                <%--<aside class="aside-xl bg-light lt">   used with tree table--%>
                <aside class="aside-lg bg-light lt">
                    <section class="vbox animated fadeInLeft">
                        <section class="scrollable">
                            <header class="dk header">
                                <h3 class="m-b-xs text-black pull-left">Complaints</h3>
                                <div class="btn-group inline select pull-right">
                                    <button class="btn btn-default btn-sm  dropdown-toggle" data-toggle="dropdown"> <span class="dropdown-label" style="width: 65px;"><i class="fa fa-sort"></i></span> <span class="caret"></span> </button>
                                    <ul class="dropdown-menu text-left text-sm">
                                        <li><a href="#">Sort Date Ascending</a></li>
                                        <li><a href="#">Sort Date Descending</a></li>
                                        <li><a href="#">Sort Complaint ID Ascending</a></li>
                                        <li><a href="#">Sort Complaint ID Ascending</a></li>
                                    </ul>
                                </div>
                                <div class="btn-group select pull-right">
                                    <button class="btn btn-default btn-sm  dropdown-toggle" data-toggle="dropdown"> <span class="dropdown-label" style="width: 65px;"><i class="fa fa-filter"></i></span> <span class="caret"></span> </button>
                                    <ul class="dropdown-menu text-left text-sm">
                                        <li><a href="#">All Open Complaint</a></li>
                                        <li><a href="#">Complaint I've Opened</a></li>
                                        <li><a href="#">Unapproved Complaint</a></li>
                                        <li><a href="#">Approved Complaint</a></li>
                                        <li><a href="#">Complaint From Group</a></li>
                                        <li><a href="#">Closed or Expired Complaint</a></li>
                                        <li><a href="<c:url value='/'/>plugin/complaint/wizard">New Complaint</a></li>
                                    </ul>
                                </div>
                            </header>
                            <div class="wrapper">
                                <div class="input-group">
                                    <input type="text" class="input-sm form-control" id="searchQuery" placeholder="Search">
                        <span class="input-group-btn">
                        <button class="btn btn-sm btn-default" type="button">Go!</button>
                        </span> </div>
                            </div>
                            <div class="row m-b">
                                <div class="col-sm-12">
                                    <%--<table id="tree">--%>
                                        <%--<colgroup>--%>
                                            <%--<col width="*">--%>
                                            <%--</col>--%>

                                            <%--<col width="*">--%>
                                            <%--</col>--%>

                                            <%--<col width="*">--%>
                                            <%--</col>--%>

                                        <%--</colgroup>--%>
                                        <%--<thead>--%>
                                        <%--<tr>--%>
                                            <%--<th></th>--%>
                                            <%--<th></th>--%>
                                            <%--<th></th>--%>
                                        <%--</tr>--%>
                                        <%--</thead>--%>
                                        <%--<tbody>--%>
                                        <%--<tr>--%>
                                            <%--<td></td>--%>
                                            <%--<td></td>--%>
                                        <%--</tr>--%>
                                        <%--</tbody>--%>
                                    <%--</table>--%>
                                    <div id="tree"></div>
                                </div>
                            </div>
                        </section>
                    </section>
                </aside>
                <aside id="email-content" class="bg-light lter">
                    <section class="vbox">
                        <section class="scrollable">
                            <div class="wrapper dk  clearfix">
                                <div class="row">
                                    <div class="col-xs-12">
                                        <div class="">
                                            <div class=" clearfix">
                                                <div class="col-xs-4 b-r">
                                                    <%--<div class="h4 font-bold"><a href="#" id="caseTitle" data-type="text" data-pk="1" data-url="/post" data-title="Enter Complaint Title"></a> </div>--%>
                                                    <div class="h4 font-bold"><a href="#" id="caseTitle" data-type="text" data-pk="1" data-title="Enter Complaint Title"></a> </div>
                                                    <small class="text-muted"><a href="#" id="complaintNum" ></a></small></div>
                                                <div class="col-xs-2 b-r">
                                                    <%--<div class="h4 font-bold"><a href="#" id="incident" data-type="date" data-pk="1" data-url="/post" data-title="Enter Incident Date"></a></div>--%>
                                                    <div class="h4 font-bold"><a href="#" id="incident" data-type="date" data-pk="1" data-title="Enter Incident Date"></a></div>
                                                    <small class="text-muted">Incident Date</small></div>
                                                <div class="col-xs-1 b-r">
                                                    <%--<div class="h4 font-bold"><a href="#" id="priority" data-type="select" data-pk="1" data-url="/post" data-title="Enter Priority"></a></div>--%>
                                                    <div class="h4 font-bold"><a href="#" id="priority" data-type="select" data-pk="1" data-title="Enter Priority"></a></div>
                                                    <small class="text-muted">Priority</small></div>
                                                <div class="col-xs-2 b-r">
                                                    <%--<div class="h4 font-bold"><a href="#" id="assigned" data-type="select" data-pk="1" data-url="/post" data-title="Enter Assignee"></a></div>--%>
                                                    <div class="h4 font-bold"><a href="#" id="assigned" data-type="select" data-pk="1" data-title="Enter Assignee"></a></div>
                                                    <small class="text-muted">Assigned To</small></div>
                                                <div class="col-xs-2 b-r">
                                                    <%--<div class="h4 font-bold"><a href="#" id="type" data-type="select" data-pk="1" data-url="/post" data-title="Enter Subject Type"></a></div>--%>
                                                    <div class="h4 font-bold"><a href="#" id="type" data-type="select" data-pk="1" data-title="Enter Subject Type"></a></div>
                                                    <small class="text-muted">Subject Type</small></div>
                                                <div class="col-xs-1">
                                                    <div class="h4 font-bold"><a href="#" id="status" ></a></div>
                                                    <small class="text-muted">Status</small></div>
                                            </div>
                                        </div>

                                    </div></div>
                            </div>

                            <div>
                                <div class="wrapper">
                                    <div class="row" id="tabBlank" style="display:none;">
                                        <p></p>
                                    </div>


                                    <div class="row" id="tabDetail" style="display:none;">
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
                                                    </span> <a href="#" class="font-bold">Details</a> </div>
                                                <div class="panel-body">
                                                    <div class="complaintDetails"></div>
                                                </div>
                                            </section>
                                        </div>
                                    </div>


                                    <div class="row" id="tabInitiator" style="display:none;">
                                        <div class="col-md-12">
                                            <section class="panel b-a">
                                                <div id="divInitiator" style="width:100%"></div>
                                            </section>
                                        </div>
                                    </div>


                                    <div class="row" id="tabPeople" style="display:none;">
                                        <div class="col-md-12">
                                            <section class="panel b-a">
                                                <div id="divPeople" style="width:100%"></div>
                                            </section>
                                        </div>
                                    </div>


                                    <div class="row" id="tabNotes" style="display:none;">
                                        <div class="col-md-12">
                                            <section class="panel b-a">
                                                <div class="panel-heading b-b bg-info">
                                                    <ul class="nav nav-pills pull-right">
                                                        <li>
                                                            <div class="btn-group padder-v2">
                                                                <button class="btn btn-default btn-sm" data-toggle="tooltip" data-title="New Person"><i class="fa fa-comments-o"></i> New</button>
                                                                <ul class="dropdown-menu pull-right">
                                                                    <li><a href="#">Other menu items</a></li>
                                                                </ul>
                                                            </div>
                                                        </li>
                                                        <li> <a href="#" class="panel-toggle text-muted"><i class="fa fa-caret-down text-active"></i><i class="fa fa-caret-up text"></i></a> </li>
                                                    </ul>
                                                    <a href="#" class="font-bold">Notes</a> </div>
                                                <div class="panel-body max-200">
                                                    <ul class="list-group list-group-lg no-bg auto">
                                                        <a href="#" class="list-group-item clearfix"> <span class="pull-left thumb-sm avatar m-r"> <img src="<c:url value='/'/>resources/vendors/${acm_theme}/images/a4.png" alt="John said"> <i class="on b-white bottom"></i> </span> <span class="clear"> <small class="text-muted pull-right">5m ago</small> <span>Judy Hsu</span> <small class="text-muted clear text-ellipsis">Sample notes go here.</small> </span> </a>
                                                    </ul>
                                                </div>
                                            </section>
                                        </div>
                                    </div>


                                    <div class="row" id="tabDocuments" style="display:none;">
                                        <div class="col-md-12">
                                            <section class="panel b-a">
                                                <div id="divDocuments" style="width:100%"></div>
                                                <input id="roiFormUrl" type="hidden" value="${roiFormUrl}" />
                                            </section>
                                        </div>
                                    </div>



                                    <div class="row" id="tabTasks" style="display:none;">
                                        <div class="col-md-12">
                                            <section class="panel b-a">
                                                <%--<div class="panel-heading b-b bg-info">--%>
                                                    <%--<ul class="nav nav-pills pull-right">--%>
                                                        <%--<li style="margin-right:5px">--%>
                                                            <%--<div class="btn-group" style="margin-top:4px;">--%>
                                                                <%--<button data-toggle="dropdown" class="btn btn-sm btn-rounded btn-default dropdown-toggle"> <span class="dropdown">Filter</span> <span class="caret"></span> </button>--%>
                                                                <%--<ul class="dropdown-menu dropdown-select">--%>
                                                                    <%--<li><a href="#">--%>
                                                                        <%--<input type="radio" name="b">--%>
                                                                        <%--Filter 1</a></li>--%>
                                                                    <%--<li><a href="#">--%>
                                                                        <%--<input type="radio" name="b">--%>
                                                                        <%--Filter 2</a></li>--%>
                                                                    <%--<li><a href="#">--%>
                                                                        <%--<input type="radio" name="b">--%>
                                                                        <%--Filter 3</a></li>--%>
                                                                <%--</ul>--%>
                                                            <%--</div>--%>
                                                        <%--</li>--%>
                                                        <%--<li>--%>
                                                            <%--<div class="btn-group padder-v2">--%>
                                                                <%--<button class="btn btn-default btn-sm" data-toggle="tooltip" data-title="New Device"><i class="i i-checkmark"></i> New</button>--%>
                                                            <%--</div>--%>
                                                        <%--</li>--%>
                                                        <%--<li> <a href="#" class="panel-toggle text-muted"><i class="fa fa-caret-down text-active"></i><i class="fa fa-caret-up text"></i></a> </li>--%>
                                                    <%--</ul>--%>
                                                    <%--<a href="#" class="font-bold">Tasks [Status]</a> </div>--%>
                                                <%--<div class="panel-body max-200 no-padder">--%>
                                                    <%--<table class="table table-striped th-sortable table-hover">--%>
                                                        <%--<thead>--%>
                                                        <%--<tr>--%>
                                                            <%--<th>ID</th>--%>
                                                            <%--<th>Title</th>--%>
                                                            <%--<th>Created</th>--%>
                                                            <%--<th>Priority</th>--%>
                                                            <%--<th>Due</th>--%>
                                                            <%--<th>Status</th>--%>
                                                            <%--<th width="10%">Action</th>--%>
                                                        <%--</tr>--%>
                                                        <%--</thead>--%>
                                                        <%--<tbody>--%>
                                                        <%--<tr class="odd gradeA">--%>
                                                            <%--<td>[ID]</td>--%>
                                                            <%--<td>[Title]</td>--%>
                                                            <%--<td>[Created]</td>--%>
                                                            <%--<td>[Priority]</td>--%>
                                                            <%--<td>[Due]</td>--%>
                                                            <%--<td>[Status]</td>--%>
                                                            <%--<td><select class="input-sm form-control input-s-sm inline v-middle">--%>
                                                                <%--<option value="0">Choose Action</option>--%>
                                                                <%--<option value="1">Assign</option>--%>
                                                                <%--<option value="1">Delete</option>--%>
                                                            <%--</select></td>--%>
                                                        <%--</tr>--%>
                                                        <%--</tbody>--%>
                                                    <%--</table>--%>
                                                <%--</div>--%>

                                                <div class="panel-body max-200 no-padder">
                                                    <div id="divTasks" style="width:100%"></div>
                                                </div>
                                            </section>
                                        </div>
                                    </div>




                                    <div class="row" id="tabRefComplaints" style="display:none;">
                                        <%--Other Complaints--%>
                                    </div>

                                    <div class="row" id="tabRefCases" style="display:none;">
                                        <%--Other Cases--%>
                                    </div>

                                    <div class="row" id="tabRefTasks" style="display:none;">
                                        <%--Other Tasks--%>
                                    </div>

                                    <div class="row" id="tabRefDocuments" style="display:none;">
                                        <%--Other Documents--%>
                                    </div>

                                    <div class="row" id="tabApprovers" style="display:none;">
                                        <%--Approvers--%>
                                    </div>

                                    <div class="row" id="tabCollaborators" style="display:none;">
                                        <%--Collaborators--%>
                                    </div>

                                    <div class="row" id="tabWatchers" style="display:none;">
                                        <%--Watchers--%>
                                    </div>

                                </div>
                            </div>
                        </section>
                    </section>
                </aside>
                <!-- /.aside -->

            </section>
        </section>
    </section>
</section>
</jsp:body>
</t:layout>



