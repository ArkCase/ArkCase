<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>


<t:layout>
<jsp:attribute name="endOfHead">
    <title>${pageDescriptor.title}</title>
    <div id="detailData" itemscope="true" style="display: none">
        <span itemprop="complaintId">${complaintId}</span>
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

    <script type="text/javascript" src="<c:url value='/'/>resources/vendors/${vd_slimscroll}/jquery.slimscroll.min.js"></script>

    <!-- File Manager -->
    <script type="text/javascript" src="<c:url value='/'/>resources/vendors/${vd_knob}/js/jquery.knob.js"></script>
    <script type="text/javascript" src="<c:url value='/'/>resources/vendors/${vd_upload}/js/jquery.fileupload.js"></script>
    <script type="text/javascript" src="<c:url value='/'/>resources/vendors/${vd_upload}/js/jquery.iframe-transport.js"></script>

    <!-- Summernote WYSIWYG -->
    <link rel="stylesheet" href="<c:url value='/'/>resources/vendors/${vd_summernote}/summernote.css" type="text/css"/>
    <script type="text/javascript" src="<c:url value='/'/>resources/vendors/${vd_summernote}/summernote.js"></script>

    <!-- JTable -->
    <%--<link rel="stylesheet" href="<c:url value='/'/>resources/vendors/${vd_jtable}/themes/lightcolor/blue/jtable.css" type="text/css"/>--%>
    <link rel="stylesheet" href="<c:url value='/'/>resources/vendors/${vd_acm}/themes/basic/jtable/blue/jtable.css" type="text/css"/>
    <script type="text/javascript" src="<c:url value='/'/>resources/vendors/${vd_jtable}/jquery.jtable.js"></script>


    <link href="<c:url value='/'/>resources/vendors/${vd_fancytree}/skin-win8/ui.fancytree.css" rel="stylesheet">
    <script src="<c:url value='/'/>resources/vendors/${vd_fancytree}/jquery.fancytree.js"></script>
    <script src="<c:url value='/'/>resources/vendors/${vd_fancytree}/jquery.fancytree.table.js"></script>
    <script src="<c:url value='/'/>resources/vendors/${vd_contextmenu}/jquery.ui-contextmenu.js"></script>

    <!-- X-Editable -->
    <link href="<c:url value='/'/>resources/vendors/${vd_x_editable}/css/bootstrap-editable.css" rel="stylesheet">
    <script src="<c:url value='/'/>resources/vendors/${vd_x_editable}/js/bootstrap-editable.min.js"></script>

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

                <aside class="aside-xl bg-light lt">
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
                                        <li><a href="#">New Complaint</a></li>
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
                                    <table id="tree">
                                        <colgroup>
                                            <col width="*">
                                            </col>

                                            <col width="*">
                                            </col>

                                            <col width="*">
                                            </col>

                                        </colgroup>
                                        <thead>
                                        <tr>
                                            <th></th>
                                            <th></th>
                                            <th></th>
                                        </tr>
                                        </thead>
                                        <tbody>
                                        <tr>
                                            <td></td>
                                            <td></td>
                                        </tr>
                                        </tbody>
                                    </table>
                                </div>
                            </div>
                        </section>
                    </section>
                </aside>
                <aside id="email-content" class="bg-light lter">
                    <section class="vbox">
                        <section class="scrollable">
                            <div class="wrapper dk  clearfix">
                                <h4 class="m-n"> <a href="#" id="caseTitle" data-type="text" data-pk="1" data-url="/post" data-title="Enter Complaint Title"> </a> </h4>
                            </div>
                            <div>
                                <div class="wrapper">
                                    <div class="row" id="tabBlank" style="display:none;">
                                        <p></p>
                                    </div>


                                    <div class="row" id="tabMain" style="display:none;">
                                        <div class="col-xs-6">
                                            <div class="panel b-a  bg-gradient">
                                                <div class="padder-v text-center clearfix">
                                                    <div class="col-xs-4 b-r">
                                                        <div class="h4 font-bold"><a href="#" id="incident" data-type="date" data-pk="1" data-url="/post" data-title="Enter Incident Date"></a></div>
                                                        <small class="text-muted">Incident Date</small> </div>
                                                    <div class="col-xs-4 b-r">
                                                        <div class="h4 font-bold"><a href="#" id="priority" data-type="select" data-pk="1" data-url="/post" data-title="Enter Priority"></a></div>
                                                        <small class="text-muted">Priority</small> </div>
                                                    <div class="col-xs-4">
                                                        <div class="h4 font-bold"><a href="#" id="assigned" data-type="select" data-pk="1" data-url="/post" data-title="Enter Assignee"></a></div>
                                                        <small class="text-muted">Assigned To</small> </div>
                                                </div>
                                            </div>
                                        </div>
                                        <div class="col-xs-3">
                                            <div class="panel b-a  bg-gradient">
                                                <div class="padder-v text-center clearfix">
                                                    <div class="col-xs-12">
                                                        <div class="h4 font-bold"><a href="#" id="type" data-type="select" data-pk="1" data-url="/post" data-title="Enter Subject Type"></a></div>
                                                        <small class="text-muted">Subject Type</small> </div>
                                                </div>
                                            </div>
                                        </div>
                                        <div class="col-xs-3 ">
                                            <div class="panel b-a bg-gradient">
                                                <div class="padder-v text-center clearfix">
                                                    <div class="col-xs-12">
                                                        <div class="h4 font-bold"><a href="#" id="status" ></a></div>
                                                        <small class="text-muted">Status</small> </div>
                                                </div>
                                            </div>
                                        </div>
                                    </div>


                                    <div class="row" id="tabDetail" style="display:none;">
                                        <div class="col-md-12">
                                            <section class="panel b-a ">
                                                <div class="panel-heading b-b bg-info">
                                                    <ul class="nav nav-pills pull-right">
                                                        <li>
                                                            <div class="btn-group padder-v2">
                                                                <button class="btn btn-default btn-sm" data-toggle="tooltip" data-title="Edit" onclick="edit()"><i class="fa fa-pencil"></i></button>
                                                                <button class="btn btn-default btn-sm" data-toggle="tooltip" data-title="Save" onclick="save()"><i class="fa fa-save"></i></button>
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
                                            <section class="panel b-a" id="secIncident">
                                                <div id="divInitiator" style="width:100%"></div>
                                            </section>
                                        </div>
                                    </div>


                                    <div class="row" id="tabPeople" style="display:none;">
                                    <div class="col-md-12">
                                    <section class="panel b-a">
                                    <div class="panel-heading b-b bg-info">
                                        <ul class="nav nav-pills pull-right">
                                            <li>
                                                <div class="btn-group padder-v2">
                                                    <button class="btn btn-default btn-sm" data-toggle="tooltip" data-title="New Person"><i class="fa fa-user"></i> New</button>
                                                    <ul class="dropdown-menu pull-right">
                                                        <li><a href="#">Other menu items</a></li>
                                                    </ul>
                                                </div>
                                            </li>
                                            <li> <a href="#" class="panel-toggle text-muted"><i class="fa fa-caret-down text-active"></i><i class="fa fa-caret-up text"></i></a> </li>
                                        </ul>
                                        <a href="#" class="font-bold">People</a> </div>
                                    <div class="panel-body no-padder">
                                        <table class="table table-striped th-sortable table-hover" >
                                            <thead>
                                            <tr>
                                                <th width="140">Entities</th>
                                                <th>Title</th>
                                                <th>First Name</th>
                                                <th>Last Name</th>
                                                <th>Type</th>
                                                <th width="85">Action</th>
                                            </tr>
                                            </thead>
                                            <tbody>
                                            <tr class="odd gradeA">
                                                <td><a href="#communicationDevices1" class="inline animated btn btn-default btn-xs" data-toggle="class:show"><i class="fa fa-phone"></i></a> <a href="#organizations1" class="inline animated btn btn-default btn-xs" data-toggle="class:show"><i class="fa fa-book"></i></a> <a href="#locations1" class="inline animated btn btn-default btn-xs" data-toggle="class:show"><i class="fa fa-map-marker"></i></a> <a href="#aliases1" class="inline animated btn btn-default btn-xs" data-toggle="class:show"><i class="fa fa-users"></i></a></td>
                                                <td>[Title]</td>
                                                <td>[First Name]</td>
                                                <td>[Last Name]</td>
                                                <td>[Type]</td>
                                                <td><button class="btn btn-default btn-xs" data-toggle="tooltip" data-title="Edit Record"><i class="fa fa-edit"></i></button>
                                                    <button class="btn btn-default btn-xs" data-toggle="tooltip" data-title="Delete Record"><i class="fa fa-trash-o"></i></button></td>
                                            </tr>
                                            <tr>
                                                <td colspan="6" class=" no-padder"><section id="communicationDevices1" class="panel b-a hide">
                                                    <div class="panel-heading b-b bg-light">
                                                        <ul class="nav nav-pills pull-right">
                                                            <li>
                                                                <div class="btn-group padder-v2">
                                                                    <button class="btn btn-default btn-sm" data-toggle="tooltip" data-title="New Person"><i class="fa fa-phone"></i> New</button>
                                                                    <ul class="dropdown-menu pull-right">
                                                                        <li><a href="#">Other menu items</a></li>
                                                                    </ul>
                                                                </div>
                                                            </li>
                                                            <li> <a href="#" class="panel-toggle text-muted"><i class="fa fa-caret-down text-active"></i><i class="fa fa-caret-up text"></i></a> </li>
                                                        </ul>
                                                        <a href="#" class="font-bold">Communication Devices</a> </div>
                                                    <div class="panel-body no-padder">
                                                        <table class="table table-striped th-sortable table-hover" width="100%">
                                                            <thead>
                                                            <tr>
                                                                <th>Type</th>
                                                                <th>Value</th>
                                                                <th>Date Added</th>
                                                                <th>Added By</th>
                                                                <th width="9%">Action</th>
                                                            </tr>
                                                            </thead>
                                                            <tbody>
                                                            <tr class="odd gradeA">
                                                                <td>[Type]</td>
                                                                <td>[Value]</td>
                                                                <td>[Date Added]</td>
                                                                <td>[Added By]</td>
                                                                <td><button class="btn btn-default btn-xs" data-toggle="tooltip" data-title="Edit Record"><i class="fa fa-edit"></i></button>
                                                                    <button class="btn btn-default btn-xs" data-toggle="tooltip" data-title="Delete Record"><i class="fa fa-trash-o"></i></button></td>
                                                            </tr>
                                                            </tbody>
                                                        </table>
                                                    </div>
                                                </section>
                                                    <section id="organizations1" class="panel b-a hide">
                                                        <div class="panel-heading b-b bg-light">
                                                            <ul class="nav nav-pills pull-right">
                                                                <li>
                                                                    <div class="btn-group padder-v2">
                                                                        <button class="btn btn-default btn-sm" data-toggle="tooltip" data-title="New Person"><i class="fa fa-book"></i> New</button>
                                                                        <ul class="dropdown-menu pull-right">
                                                                            <li><a href="#">Other menu items</a></li>
                                                                        </ul>
                                                                    </div>
                                                                </li>
                                                                <li> <a href="#" class="panel-toggle text-muted"><i class="fa fa-caret-down text-active"></i><i class="fa fa-caret-up text"></i></a> </li>
                                                            </ul>
                                                            <a href="#" class="font-bold">Organizations</a> </div>
                                                        <div class="panel-body no-padder">
                                                            <table class="table table-striped th-sortable table-hover" width="100%">
                                                                <thead>
                                                                <tr>
                                                                    <th>Type</th>
                                                                    <th>Value</th>
                                                                    <th>Date Added</th>
                                                                    <th>Added By</th>
                                                                    <th width="9%">Action</th>
                                                                </tr>
                                                                </thead>
                                                                <tbody>
                                                                <tr class="odd gradeA">
                                                                    <td>[Type]</td>
                                                                    <td>[Value]</td>
                                                                    <td>[Date Added]</td>
                                                                    <td>[Added By]</td>
                                                                    <td><button class="btn btn-default btn-xs" data-toggle="tooltip" data-title="Edit Record"><i class="fa fa-edit"></i></button>
                                                                        <button class="btn btn-default btn-xs" data-toggle="tooltip" data-title="Delete Record"><i class="fa fa-trash-o"></i></button></td>
                                                                </tr>
                                                                </tbody>
                                                            </table>
                                                        </div>
                                                    </section>
                                                    <section id="locations1" class="panel b-a hide">
                                                        <div class="panel-heading b-b bg-light">
                                                            <ul class="nav nav-pills pull-right">
                                                                <li>
                                                                    <div class="btn-group padder-v2">
                                                                        <button class="btn btn-default btn-sm" data-toggle="tooltip" data-title="New Person"><i class="fa fa-map-marker"></i> New</button>
                                                                        <ul class="dropdown-menu pull-right">
                                                                            <li><a href="#">Other menu items</a></li>
                                                                        </ul>
                                                                    </div>
                                                                </li>
                                                                <li> <a href="#" class="panel-toggle text-muted"><i class="fa fa-caret-down text-active"></i><i class="fa fa-caret-up text"></i></a> </li>
                                                            </ul>
                                                            <a href="#" class="font-bold">Locations</a> </div>
                                                        <div class="panel-body no-padder">
                                                            <table class="table table-striped th-sortable table-hover" width="100%">
                                                                <thead>
                                                                <tr>
                                                                    <th>Type</th>
                                                                    <th>Address</th>
                                                                    <th>City</th>
                                                                    <th>State</th>
                                                                    <th>ZIP</th>
                                                                    <th>Country</th>
                                                                    <th>Date Added</th>
                                                                    <th>Added By</th>
                                                                    <th width="9%">Action</th>
                                                                </tr>
                                                                </thead>
                                                                <tbody>
                                                                <tr class="odd gradeA">
                                                                    <td>[Type]</td>
                                                                    <td>[Address]</td>
                                                                    <td>[City]</td>
                                                                    <td>[State]</td>
                                                                    <td>[ZIP]</td>
                                                                    <td>[Country]</td>
                                                                    <td>[Date Added]</td>
                                                                    <td>[Added By]</td>
                                                                    <td><button class="btn btn-default btn-xs" data-toggle="tooltip" data-title="Edit Record"><i class="fa fa-edit"></i></button>
                                                                        <button class="btn btn-default btn-xs" data-toggle="tooltip" data-title="Delete Record"><i class="fa fa-trash-o"></i></button></td>
                                                                </tr>
                                                                </tbody>
                                                            </table>
                                                        </div>
                                                    </section>
                                                    <section id="aliases1" class="panel b-a hide">
                                                        <div class="panel-heading b-b bg-light">
                                                            <ul class="nav nav-pills pull-right">
                                                                <li>
                                                                    <div class="btn-group padder-v2">
                                                                        <button class="btn btn-default btn-sm" data-toggle="tooltip" data-title="New Person"><i class="fa fa-users"></i> New</button>
                                                                        <ul class="dropdown-menu pull-right">
                                                                            <li><a href="#">Other menu items</a></li>
                                                                        </ul>
                                                                    </div>
                                                                </li>
                                                                <li> <a href="#" class="panel-toggle text-muted"><i class="fa fa-caret-down text-active"></i><i class="fa fa-caret-up text"></i></a> </li>
                                                            </ul>
                                                            <a href="#" class="font-bold">Aliases</a> </div>
                                                        <div class="panel-body no-padder">
                                                            <table class="table table-striped th-sortable table-hover" width="100%">
                                                                <thead>
                                                                <tr>
                                                                    <th>Type</th>
                                                                    <th>Value</th>
                                                                    <th>Date Added</th>
                                                                    <th>Added By</th>
                                                                    <th width="9%">Action</th>
                                                                </tr>
                                                                </thead>
                                                                <tbody>
                                                                <tr class="odd gradeA">
                                                                    <td>[Type]</td>
                                                                    <td>[Value]</td>
                                                                    <td>[Date Added]</td>
                                                                    <td>[Added By]</td>
                                                                    <td><button class="btn btn-default btn-xs" data-toggle="tooltip" data-title="Edit Record"><i class="fa fa-edit"></i></button>
                                                                        <button class="btn btn-default btn-xs" data-toggle="tooltip" data-title="Delete Record"><i class="fa fa-trash-o"></i></button></td>
                                                                </tr>
                                                                </tbody>
                                                            </table>
                                                        </div>
                                                    </section></td>
                                            </tr>
                                            </tbody>
                                        </table>
                                    </div>
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


                                    <div class="row" id="tabPending" style="display:none;">
                                        <div class="col-md-12">
                                            <section class="panel b-a">
                                                <div class="panel-heading b-b bg-info">
                                                    <ul class="nav nav-pills pull-right">
                                                        <li style="margin-right:5px">
                                                            <div class="btn-group" style="margin-top:4px;">
                                                                <button data-toggle="dropdown" class="btn btn-sm btn-rounded btn-default dropdown-toggle"> <span class="dropdown">Filter</span> <span class="caret"></span> </button>
                                                                <ul class="dropdown-menu dropdown-select">
                                                                    <li><a href="#">
                                                                        <input type="radio" name="b">
                                                                        Filter 1</a></li>
                                                                    <li><a href="#">
                                                                        <input type="radio" name="b">
                                                                        Filter 2</a></li>
                                                                    <li><a href="#">
                                                                        <input type="radio" name="b">
                                                                        Filter 3</a></li>
                                                                </ul>
                                                            </div>
                                                        </li>
                                                        <li>
                                                            <div class="btn-group padder-v2">
                                                                <button class="btn btn-default btn-sm" data-toggle="tooltip" data-title="New Device"><i class="fa fa-file"></i> New</button>
                                                            </div>
                                                        </li>
                                                        <li> <a href="#" class="panel-toggle text-muted"><i class="fa fa-caret-down text-active"></i><i class="fa fa-caret-up text"></i></a> </li>
                                                    </ul>
                                                    <a href="#" class="font-bold">Documents [Status] </a> </div>
                                                <div class="panel-body max-200 no-padder">
                                                    <table class="table table-striped th-sortable table-hover">
                                                        <thead>
                                                        <tr>
                                                            <th>ID</th>
                                                            <th>Title</th>
                                                            <th>Created</th>
                                                            <th>Author</th>
                                                            <th>Priority</th>
                                                            <th>Due</th>
                                                            <th>Status</th>
                                                            <th width="10%">Action</th>
                                                        </tr>
                                                        </thead>
                                                        <tbody>
                                                        <tr class="odd gradeA">
                                                            <td>[ID]</td>
                                                            <td>[Title]</td>
                                                            <td>[Created]</td>
                                                            <td>[Author]</td>
                                                            <td>[Priority]</td>
                                                            <td>[Due]</td>
                                                            <td>[Status]</td>
                                                            <td><select class="input-sm form-control input-s-sm inline v-middle">
                                                                <option value="0">Choose Action</option>
                                                                <option value="1">Delete</option>
                                                                <option value="1">Approve</option>
                                                                <option value="1">Reject</option>
                                                            </select></td>
                                                        </tr>
                                                        </tbody>
                                                    </table>
                                                </div>
                                            </section>
                                        </div>
                                    </div>


                                    <div class="row" id="tabApproved" style="display:none;">
                                        <div class="col-md-12">
                                            <section class="panel b-a">
                                                <div class="panel-heading b-b bg-info">
                                                    <ul class="nav nav-pills pull-right">
                                                        <li style="margin-right:5px">
                                                            <div class="btn-group" style="margin-top:4px;">
                                                                <button data-toggle="dropdown" class="btn btn-sm btn-rounded btn-default dropdown-toggle"> <span class="dropdown">Filter</span> <span class="caret"></span> </button>
                                                                <ul class="dropdown-menu dropdown-select">
                                                                    <li><a href="#">
                                                                        <input type="radio" name="b">
                                                                        Filter 1</a></li>
                                                                    <li><a href="#">
                                                                        <input type="radio" name="b">
                                                                        Filter 2</a></li>
                                                                    <li><a href="#">
                                                                        <input type="radio" name="b">
                                                                        Filter 3</a></li>
                                                                </ul>
                                                            </div>
                                                        </li>
                                                        <li>
                                                            <div class="btn-group padder-v2">
                                                                <button class="btn btn-default btn-sm" data-toggle="tooltip" data-title="New Device"><i class="fa fa-file"></i> New</button>
                                                            </div>
                                                        </li>
                                                        <li> <a href="#" class="panel-toggle text-muted"><i class="fa fa-caret-down text-active"></i><i class="fa fa-caret-up text"></i></a> </li>
                                                    </ul>
                                                    <a href="#" class="font-bold">Documents [Status] </a> </div>
                                                <div class="panel-body max-200 no-padder">
                                                    <table class="table table-striped th-sortable table-hover">
                                                        <thead>
                                                        <tr>
                                                            <th>ID</th>
                                                            <th>Title</th>
                                                            <th>Created</th>
                                                            <th>Author</th>
                                                            <th>Priority</th>
                                                            <th>Due</th>
                                                            <th>Status</th>
                                                            <th width="10%">Action</th>
                                                        </tr>
                                                        </thead>
                                                        <tbody>
                                                        <tr class="odd gradeA">
                                                            <td>[ID]</td>
                                                            <td>[Title]</td>
                                                            <td>[Created]</td>
                                                            <td>[Author]</td>
                                                            <td>[Priority]</td>
                                                            <td>[Due]</td>
                                                            <td>[Status]</td>
                                                            <td><select class="input-sm form-control input-s-sm inline v-middle">
                                                                <option value="0">Choose Action</option>
                                                                <option value="1">Delete</option>
                                                                <option value="1">Approve</option>
                                                                <option value="1">Reject</option>
                                                            </select></td>
                                                        </tr>
                                                        </tbody>
                                                    </table>
                                                </div>
                                            </section>
                                        </div>
                                    </div>


                                    <div class="row" id="tabRejected" style="display:none;">
                                        <div class="col-md-12">
                                            <section class="panel b-a">
                                                <div class="panel-heading b-b bg-info">
                                                    <ul class="nav nav-pills pull-right">
                                                        <li style="margin-right:5px">
                                                            <div class="btn-group" style="margin-top:4px;">
                                                                <button data-toggle="dropdown" class="btn btn-sm btn-rounded btn-default dropdown-toggle"> <span class="dropdown">Filter</span> <span class="caret"></span> </button>
                                                                <ul class="dropdown-menu dropdown-select">
                                                                    <li><a href="#">
                                                                        <input type="radio" name="b">
                                                                        Filter 1</a></li>
                                                                    <li><a href="#">
                                                                        <input type="radio" name="b">
                                                                        Filter 2</a></li>
                                                                    <li><a href="#">
                                                                        <input type="radio" name="b">
                                                                        Filter 3</a></li>
                                                                </ul>
                                                            </div>
                                                        </li>
                                                        <li>
                                                            <div class="btn-group padder-v2">
                                                                <button class="btn btn-default btn-sm" data-toggle="tooltip" data-title="New Device"><i class="fa fa-file"></i> New</button>
                                                            </div>
                                                        </li>
                                                        <li> <a href="#" class="panel-toggle text-muted"><i class="fa fa-caret-down text-active"></i><i class="fa fa-caret-up text"></i></a> </li>
                                                    </ul>
                                                    <a href="#" class="font-bold">Documents [Status] </a> </div>
                                                <div class="panel-body max-200 no-padder">
                                                    <table class="table table-striped th-sortable table-hover">
                                                        <thead>
                                                        <tr>
                                                            <th>ID</th>
                                                            <th>Title</th>
                                                            <th>Created</th>
                                                            <th>Author</th>
                                                            <th>Priority</th>
                                                            <th>Due</th>
                                                            <th>Status</th>
                                                            <th width="10%">Action</th>
                                                        </tr>
                                                        </thead>
                                                        <tbody>
                                                        <tr class="odd gradeA">
                                                            <td>[ID]</td>
                                                            <td>[Title]</td>
                                                            <td>[Created]</td>
                                                            <td>[Author]</td>
                                                            <td>[Priority]</td>
                                                            <td>[Due]</td>
                                                            <td>[Status]</td>
                                                            <td><select class="input-sm form-control input-s-sm inline v-middle">
                                                                <option value="0">Choose Action</option>
                                                                <option value="1">Delete</option>
                                                                <option value="1">Approve</option>
                                                                <option value="1">Reject</option>
                                                            </select></td>
                                                        </tr>
                                                        </tbody>
                                                    </table>
                                                </div>
                                            </section>
                                        </div>
                                    </div>


                                    <div class="row" id="tabUnassigned" style="display:none;">
                                        <div class="col-md-12">
                                            <section class="panel b-a">
                                                <div class="panel-heading b-b bg-info">
                                                    <ul class="nav nav-pills pull-right">
                                                        <li style="margin-right:5px">
                                                            <div class="btn-group" style="margin-top:4px;">
                                                                <button data-toggle="dropdown" class="btn btn-sm btn-rounded btn-default dropdown-toggle"> <span class="dropdown">Filter</span> <span class="caret"></span> </button>
                                                                <ul class="dropdown-menu dropdown-select">
                                                                    <li><a href="#">
                                                                        <input type="radio" name="b">
                                                                        Filter 1</a></li>
                                                                    <li><a href="#">
                                                                        <input type="radio" name="b">
                                                                        Filter 2</a></li>
                                                                    <li><a href="#">
                                                                        <input type="radio" name="b">
                                                                        Filter 3</a></li>
                                                                </ul>
                                                            </div>
                                                        </li>
                                                        <li>
                                                            <div class="btn-group padder-v2">
                                                                <button class="btn btn-default btn-sm" data-toggle="tooltip" data-title="New Device"><i class="i i-checkmark"></i> New</button>
                                                            </div>
                                                        </li>
                                                        <li> <a href="#" class="panel-toggle text-muted"><i class="fa fa-caret-down text-active"></i><i class="fa fa-caret-up text"></i></a> </li>
                                                    </ul>
                                                    <a href="#" class="font-bold">Tasks [Status]</a> </div>
                                                <div class="panel-body max-200 no-padder">
                                                    <table class="table table-striped th-sortable table-hover">
                                                        <thead>
                                                        <tr>
                                                            <th>ID</th>
                                                            <th>Title</th>
                                                            <th>Created</th>
                                                            <th>Priority</th>
                                                            <th>Due</th>
                                                            <th>Status</th>
                                                            <th width="10%">Action</th>
                                                        </tr>
                                                        </thead>
                                                        <tbody>
                                                        <tr class="odd gradeA">
                                                            <td>[ID]</td>
                                                            <td>[Title]</td>
                                                            <td>[Created]</td>
                                                            <td>[Priority]</td>
                                                            <td>[Due]</td>
                                                            <td>[Status]</td>
                                                            <td><select class="input-sm form-control input-s-sm inline v-middle">
                                                                <option value="0">Choose Action</option>
                                                                <option value="1">Assign</option>
                                                                <option value="1">Delete</option>
                                                            </select></td>
                                                        </tr>
                                                        </tbody>
                                                    </table>
                                                </div>

                                                <div class="panel-body max-200 no-padder">
                                                    <div id="divTasks" style="width:100%"></div>
                                                </div>
                                            </section>
                                        </div>
                                    </div>


                                    <div class="row" id="tabAssigned" style="display:none;">
                                        <div class="col-md-12">
                                            <section class="panel b-a">
                                                <div class="panel-heading b-b bg-info">
                                                    <ul class="nav nav-pills pull-right">
                                                        <li style="margin-right:5px">
                                                            <div class="btn-group" style="margin-top:4px;">
                                                                <button data-toggle="dropdown" class="btn btn-sm btn-rounded btn-default dropdown-toggle"> <span class="dropdown">Filter</span> <span class="caret"></span> </button>
                                                                <ul class="dropdown-menu dropdown-select">
                                                                    <li><a href="#">
                                                                        <input type="radio" name="b">
                                                                        Filter 1</a></li>
                                                                    <li><a href="#">
                                                                        <input type="radio" name="b">
                                                                        Filter 2</a></li>
                                                                    <li><a href="#">
                                                                        <input type="radio" name="b">
                                                                        Filter 3</a></li>
                                                                </ul>
                                                            </div>
                                                        </li>
                                                        <li>
                                                            <div class="btn-group padder-v2">
                                                                <button class="btn btn-default btn-sm" data-toggle="tooltip" data-title="New Device"><i class="i i-checkmark"></i> New</button>
                                                            </div>
                                                        </li>
                                                        <li> <a href="#" class="panel-toggle text-muted"><i class="fa fa-caret-down text-active"></i><i class="fa fa-caret-up text"></i></a> </li>
                                                    </ul>
                                                    <a href="#" class="font-bold">Tasks [Status]</a> </div>
                                                <div class="panel-body max-200 no-padder">
                                                    <table class="table table-striped th-sortable table-hover">
                                                        <thead>
                                                        <tr>
                                                            <th>ID</th>
                                                            <th>Title</th>
                                                            <th>Created</th>
                                                            <th>Priority</th>
                                                            <th>Due</th>
                                                            <th>Status</th>
                                                            <th width="10%">Action</th>
                                                        </tr>
                                                        </thead>
                                                        <tbody>
                                                        <tr class="odd gradeA">
                                                            <td>[ID]</td>
                                                            <td>[Title]</td>
                                                            <td>[Created]</td>
                                                            <td>[Priority]</td>
                                                            <td>[Due]</td>
                                                            <td>[Status]</td>
                                                            <td><select class="input-sm form-control input-s-sm inline v-middle">
                                                                <option value="0">Choose Action</option>
                                                                <option value="1">Assign</option>
                                                                <option value="1">Delete</option>
                                                            </select></td>
                                                        </tr>
                                                        </tbody>
                                                    </table>
                                                </div>
                                            </section>
                                        </div>
                                    </div>


                                    <div class="row" id="tabCompleted" style="display:none;">
                                        <div class="col-md-12">
                                            <section class="panel b-a">
                                                <div class="panel-heading b-b bg-info">
                                                    <ul class="nav nav-pills pull-right">
                                                        <li style="margin-right:5px">
                                                            <div class="btn-group" style="margin-top:4px;">
                                                                <button data-toggle="dropdown" class="btn btn-sm btn-rounded btn-default dropdown-toggle"> <span class="dropdown">Filter</span> <span class="caret"></span> </button>
                                                                <ul class="dropdown-menu dropdown-select">
                                                                    <li><a href="#">
                                                                        <input type="radio" name="b">
                                                                        Filter 1</a></li>
                                                                    <li><a href="#">
                                                                        <input type="radio" name="b">
                                                                        Filter 2</a></li>
                                                                    <li><a href="#">
                                                                        <input type="radio" name="b">
                                                                        Filter 3</a></li>
                                                                </ul>
                                                            </div>
                                                        </li>
                                                        <li>
                                                            <div class="btn-group padder-v2">
                                                                <button class="btn btn-default btn-sm" data-toggle="tooltip" data-title="New Device"><i class="i i-checkmark"></i> New</button>
                                                            </div>
                                                        </li>
                                                        <li> <a href="#" class="panel-toggle text-muted"><i class="fa fa-caret-down text-active"></i><i class="fa fa-caret-up text"></i></a> </li>
                                                    </ul>
                                                    <a href="#" class="font-bold">Tasks [Status]</a> </div>
                                                <div class="panel-body max-200 no-padder">
                                                    <table class="table table-striped th-sortable table-hover">
                                                        <thead>
                                                        <tr>
                                                            <th>ID</th>
                                                            <th>Title</th>
                                                            <th>Created</th>
                                                            <th>Priority</th>
                                                            <th>Due</th>
                                                            <th>Status</th>
                                                            <th width="10%">Action</th>
                                                        </tr>
                                                        </thead>
                                                        <tbody>
                                                        <tr class="odd gradeA">
                                                            <td>[ID]</td>
                                                            <td>[Title]</td>
                                                            <td>[Created]</td>
                                                            <td>[Priority]</td>
                                                            <td>[Due]</td>
                                                            <td>[Status]</td>
                                                            <td><select class="input-sm form-control input-s-sm inline v-middle">
                                                                <option value="0">Choose Action</option>
                                                                <option value="1">Assign</option>
                                                                <option value="1">Delete</option>
                                                            </select></td>
                                                        </tr>
                                                        </tbody>
                                                    </table>
                                                </div>
                                            </section>
                                        </div>
                                    </div>



                                    <div class="row" id="tabRefComplaints" style="display:none;">
                                        Other Complaints
                                    </div>

                                    <div class="row" id="tabRefCases" style="display:none;">
                                        Other Cases
                                    </div>

                                    <div class="row" id="tabRefTasks" style="display:none;">
                                        Other Tasks
                                    </div>

                                    <div class="row" id="tabRefDocuments" style="display:none;">
                                        Other Documents
                                    </div>

                                    <div class="row" id="tabApprovers" style="display:none;">
                                        Approvers
                                    </div>

                                    <div class="row" id="tabCollaborators" style="display:none;">
                                        Collaborators
                                    </div>

                                    <div class="row" id="tabWatchers" style="display:none;">
                                        Watchers
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



