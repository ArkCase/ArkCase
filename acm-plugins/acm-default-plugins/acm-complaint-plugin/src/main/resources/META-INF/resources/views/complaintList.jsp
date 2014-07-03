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
    <script type="text/javascript" src="<c:url value='/resources/js/complaint/list/complaintList.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/resources/js/complaint/list/complaintListObject.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/resources/js/complaint/list/complaintListEvent.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/resources/js/complaint/list/complaintListPage.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/resources/js/complaint/list/complaintListRule.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/resources/js/complaint/list/complaintListService.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/resources/js/complaint/list/complaintListCallback.js'/>"></script>

    <script type="text/javascript" src="<c:url value='/'/>resources/vendors/${vd_slimscroll}/jquery.slimscroll.min.js"></script>

    <!-- File Manager -->
    <script type="text/javascript" src="<c:url value='/'/>resources/vendors/${vd_knob}/js/jquery.knob.js"></script>
    <script type="text/javascript" src="<c:url value='/'/>resources/vendors/${vd_upload}/js/jquery.fileupload.js"></script>
    <script type="text/javascript" src="<c:url value='/'/>resources/vendors/${vd_upload}/js/jquery.iframe-transport.js"></script>

    <!-- Summernote WYSIWYG -->
    <link rel="stylesheet" href="<c:url value='/'/>resources/vendors/${vd_summernote}/summernote.css" type="text/css"/>
    <script type="text/javascript" src="<c:url value='/'/>resources/vendors/${vd_summernote}/summernote.js"></script>

    <!-- JTable -->
    <link rel="stylesheet" href="<c:url value='/'/>resources/vendors/${vd_jtable}/themes/lightcolor/blue/jtable.css" type="text/css"/>
    <script type="text/javascript" src="<c:url value='/'/>resources/vendors/${vd_jtable}/jquery.jtable.js"></script>

<!-- X-Editable -->
<link href="<c:url value='/'/>resources/vendors/${vd_x_editable}/css/bootstrap-editable.css" rel="stylesheet">
<script src="<c:url value='/'/>resources/vendors/${vd_x_editable}/js/bootstrap-editable.min.js"></script>
<script>
    $.fn.editable.defaults.url = '/post';
    $(document).ready(function() { $('#caseTitle').editable({placement: 'right'}); });
    $(document).ready(function() { $('#priority').editable({placement: 'left', value: 2,
        source: [
            {value: 1, text: 'Low'},
            {value: 2, text: 'Medium'},
            {value: 3, text: 'High'},
            {value: 3, text: 'Expedited'}
        ]}); });
    $(document).ready(function() { $('#type').editable({placement: 'bottom', value: 2,
        source: [
            {value: 1, text: 'Type 1'},
            {value: 2, text: 'Type 2'},
            {value: 3, text: 'Type 3'},
            {value: 3, text: 'Type 4'}
        ]}); });

    $(document).ready(function() { $('#assigned').editable({placement: 'bottom', value: 2,
        source: [
            {value: 1, text: 'David Miller'},
            {value: 2, text: 'Judy Hsu'},
            {value: 3, text: 'Ronda Ringo'},
            {value: 3, text: 'AJ McClary'}
        ]}); });

    $(document).ready(function() { $('#incident').editable({
        format: 'yyyy-mm-dd',
        placement: 'bottom',
        viewformat: 'dd/mm/yyyy',
        datepicker: {
            weekStart: 1
        }
    }); });

</script>
</jsp:attribute>

<jsp:body>
<section id="content">
<section class="vbox">
<section class="scrollable">
<section class="hbox stretch">
<aside class="aside-lg" id="email-list">
    <section class="vbox">
        <header class="dker header clearfix">
            <h3 class="m-b-xs text-black pull-left">${pageDescriptor.descShort}</h3>
            <div class="btn-toolbar">
                <div class="btn-group inline select pull-right">
                    <button class="btn btn-default btn-sm  dropdown-toggle" data-toggle="dropdown"> <span class="dropdown-label" style="width: 65px;"><i class="fa fa-sort"></i></span> <span class="caret"></span> </button>
                    <ul class="dropdown-menu text-left text-sm">
                        <li><a href="#">Sort Date Ascending</a></li>
                        <li><a href="#">Sort Date Descending</a></li>
                        <li><a href="#">Sort Title Ascending</a></li>
                        <li><a href="#">Sort Title Ascending</a></li>
                    </ul>
                </div>
                <div class="btn-group select pull-right">
                    <button class="btn btn-default btn-sm  dropdown-toggle" data-toggle="dropdown"> <span class="dropdown-label" style="width: 65px;"><i class="fa fa-filter"></i></span> <span class="caret"></span> </button>
                    <ul class="dropdown-menu text-left text-sm">
                        <li><a href="#">All Open Complaints</a></li>
                        <li><a href="#">Complaints I've Opened</a></li>
                        <li><a href="#">Unapproved Complaints</a></li>
                        <li><a href="#">Approved Complaints</a></li>
                        <li><a href="#">Complaints From Group</a></li>
                        <li><a href="#">Closed or Expired Complaints</a></li>
                        <li><a href="#">New Complaints</a></li>
                    </ul>
                </div>
            </div>
        </header>
        <section class="scrollable hover">
            <ul class="list-group auto no-radius m-b-none m-t-n-xxs list-group-lg" id="ulComplaints">
            </ul>
            <a href="#" class="btn btn-default btn-md col-lg-12 m-b-xs"><i class="fa fa-repeat"></i> Load More...</a> </section>
        <footer class="footer dk clearfix">
            <form class="m-t-sm">
                <div class="input-group">
                    <input type="text" class="input-sm form-control input-s-sm" placeholder="Search">
                    <div class="input-group-btn">
                        <button class="btn btn-sm btn-default"><i class="fa fa-search"></i></button>
                    </div>
                </div>
            </form>
        </footer>
    </section>
</aside>
<!-- /.aside -->
<!-- .aside -->
<aside id="email-content" class="bg-light lter">
<section class="vbox">
<section class="scrollable">
<div class="wrapper dk  clearfix">
    <div class="pull-right inline">
        <div class="btn-group">
            <button class="btn btn-default btn-sm" data-toggle="tooltip" data-title="Refer"><i class="fa fa-share"></i></button>
            <button class="btn btn-default btn-sm" data-toggle="tooltip" data-title="Lock"><i class="fa fa-lock"></i></button>
            <button class="btn btn-default btn-sm" data-toggle="tooltip" data-title="Close"><i class="fa fa-archive"></i></button>
            <button class="btn btn-default btn-sm" data-toggle="tooltip" data-title="Convert to Case"><i class="fa fa-folder"></i></button>
            <button class="btn btn-default btn-sm dropdown-toggle" data-toggle="dropdown"><span class="caret"></span></button>
            <ul class="dropdown-menu pull-right">
                <li><a href="#">Other menu items</a></li>
            </ul>
        </div>
        <a href="#nav, #chat" class="inline animated btn btn-default btn-sm " data-toggle="class:nav-xs, show"><i class="fa  fa-columns"></i></a> </div>
    <h4 class="m-n"> <a href="#" id="caseTitle" data-type="text" data-pk="1" data-url="/post" data-title="Enter Complaint Title">...</a> (...)</h4>
</div>
<div>
<div class="wrapper">
<div class="row">
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
<section class="panel panel-default">
<header class="panel-heading bg-light">
    <ul class="nav nav-tabs nav-justified">
        <li class="active"><a href="#details" data-toggle="tab">Incident</a></li>
        <li><a href="#documents" data-toggle="tab">Documents</a></li>
        <li><a href="#tasks" data-toggle="tab">Tasks</a></li>
        <li><a href="#maps" data-toggle="tab">Maps</a></li>
        <li><a href="#references" data-toggle="tab">References</a></li>
        <li><a href="#history" data-toggle="tab">History</a></li>
    </ul>
</header>
<div class="panel-body">
<div class="tab-content">
<div class="tab-pane active" id="details">
    <div class="row">
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
    <div class="row">
        <div class="col-md-12">
            <section class="panel b-a" id="secIncident">
                <div id="divInitiator" style="width:100%"></div>
            </section>
        </div>
    </div>
    <div class="row">
        <div class="col-md-12">
            <section class="panel b-a">
                <div class="panel-heading b-b bg-info"> <span class="pull-right">New</span> <a href="#" class="font-bold">People</a> </div>
                <div class="panel-body max-200 no-padder">
                    <table class="table table-striped th-sortable table-hover" >
                        <thead>
                        <tr>
                            <th>First Name</th>
                            <th>Last Name</th>
                            <th>Type</th>
                            <th>Phone</th>
                            <th>Address</th>
                            <th>City</th>
                            <th>State</th>
                            <th>ZIP</th>
                            <th width="3%">Edit</th>
                            <th width="4%">Delete</th>
                        </tr>
                        </thead>
                        <tbody>
                        <tr class="odd gradeA">
                            <td>[First]</td>
                            <td>[Last]</td>
                            <td>[Type]</td>
                            <td>[Phone]</td>
                            <td>[Address]</td>
                            <td>[City]</td>
                            <td>[State]</td>
                            <td>[ZIP]</td>
                            <td><a href="javascript:;" class="edit">Edit</a></td>
                            <td><a href="javascript:;" class="delete">Delete</a></td>
                        </tr>
                        </tbody>
                    </table>
                </div>
            </section>
        </div>
    </div>
    <div class="row">
        <div class="col-md-12">
            <section class="panel b-a">
                <div class="panel-heading b-b bg-info"> <span class="pull-right">New</span> <a href="#" class="font-bold">Notes</a> </div>
                <div class="panel-body max-200">
                    <ul class="list-group list-group-lg no-bg auto">
                        <a href="#" class="list-group-item clearfix"> <span class="pull-left thumb-sm avatar m-r"> <img src="<c:url value='/'/>resources/vendors/${acm_theme}/images/a4.png" alt="John said"> <i class="on b-white bottom"></i> </span> <span class="clear"> <small class="text-muted pull-right">5m ago</small> <span>Judy Hsu</span> <small class="text-muted clear text-ellipsis">Sample notes go here.</small> </span> </a>
                    </ul>
                </div>
            </section>
        </div>
    </div>
</div>

<div class="tab-pane " id="documents">
    <div class="row">
        <div class="col-md-12">
            <section class="panel b-a" id='secDocDocuments'>
                <div class="panel-heading b-b bg-info"> <span class="pull-right">New</span>
                    <a href="#" class="font-bold">Documents</a>
                    <input type="file" name="files[]" multiple style="display:none;"/>
                </div>
                <div class="panel-body max-200 no-padder">
                    <table class="table table-striped th-sortable table-hover">
                        <thead>
                        <tr>
                            <th>ID</th>
                            <th>Title</th>
                            <th>Created</th>
                            <th>Creator</th>
                            <th>Status</th>
                        </tr>
                        </thead>
                        <tbody>
                        <tr class="odd gradeA">
                        </tr>
                        </tbody>
                    </table>
                    <%--<ul/>--%>
                </div>
                <div id="upload">
                    <ul/>
                </div>
            </section>
        </div>
    </div>
<!--
    <div class="row">
        <div class="col-md-12">
            <section class="panel b-a">
                <div class="panel-heading b-b bg-info"><a href="#" class="font-bold">Pending</a> </div>
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
                        </tr>
                        </tbody>
                    </table>
                </div>
            </section>
        </div>
    </div>
    <div class="row">
        <div class="col-md-12">
            <section class="panel b-a">
                <div class="panel-heading b-b bg-info"> <a href="#" class="font-bold">Approved</a> </div>
                <div class="panel-body max-200 no-padder">
                    <table class="table table-striped th-sortable table-hover">
                        <thead>
                        <tr>
                            <th>ID</th>
                            <th>Title</th>
                            <th>Created</th>
                            <th>Priority</th>
                            <th>Approved By</th>
                            <th>Approved Date</th>
                        </tr>
                        </thead>
                        <tbody>
                        <tr class="odd gradeA">
                            <td>[ID]</td>
                            <td>[Title]</td>
                            <td>[Created]</td>
                            <td>[Priority]</td>
                            <td>[Approved By]</td>
                            <td>[Approved Date]</td>
                        </tr>
                        </tbody>
                    </table>
                </div>
            </section>
        </div>
    </div>
-->
</div>
<div class="tab-pane" id="tasks">
    <div class="row">
        <div class="col-md-12">
            <section class="panel b-a">
                <div class="panel-heading b-b bg-info"> <a href="#" class="font-bold">Unassigned</a> </div>
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
                            </select></td>
                        </tr>
                        </tbody>
                    </table>
                </div>
            </section>
        </div>
    </div>
    <div class="row">
        <div class="col-md-12">
            <section class="panel b-a">
                <div class="panel-heading b-b bg-info"> <a href="#" class="font-bold">Assigned</a> </div>
                <div class="panel-body max-200 no-padder">
                    <table class="table table-striped th-sortable table-hover">
                        <thead>
                        <tr>
                        <th>ID</th>
                        <th>Title</th>
                        <th>Created</th>
                        <th>Priority</th>
                        <th>Due</th>
                        <th>Assigned To</th>
                        <th>Status</th>
                        <th width="10%">Action</th>
                        </thead>
                        <tbody>
                        <tr class="odd gradeA">
                            <td>[ID]</td>
                            <td>[Title]</td>
                            <td>[Created]</td>
                            <td>[Priority]</td>
                            <td>[Due]</td>
                            <td>[Assigned To]</td>
                            <td>[Status]</td>
                            <td><select class="input-sm form-control input-s-sm inline v-middle">
                                <option value="0">Choose Action</option>
                                <option value="1">Unassign</option>
                            </select></td>
                        </tr>
                        </tbody>
                    </table>
                </div>
            </section>
        </div>
    </div>
</div>
<div class="tab-pane" id="references">
    <div class="row">
        <div class="col-md-12">
            <section class="panel b-a">
                <div class="panel-heading b-b bg-info"> <span class="pull-right">New</span> <a href="#" class="font-bold">Complaints</a> </div>
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
                            <th>Direction</th>
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
                            <td>[Direction]</td>
                        </tr>
                        </tbody>
                    </table>
                </div>
            </section>
        </div>
    </div>
    <div class="row">
        <div class="col-md-12">
            <section class="panel b-a">
                <div class="panel-heading b-b bg-info"> <span class="pull-right">New</span> <a href="#" class="font-bold">Cases</a> </div>
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
                            <th>Direction</th>
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
                            <td>[Direction]</td>
                        </tr>
                        </tbody>
                    </table>
                </div>
            </section>
        </div>
    </div>
    <div class="row">
        <div class="col-md-12">
            <section class="panel b-a">
                <div class="panel-heading b-b bg-info"> <span class="pull-right">New</span> <a href="#" class="font-bold">Tasks</a> </div>
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
                            <th>Direction</th>
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
                            <td>[Direction]</td>
                        </tr>
                        </tbody>
                    </table>
                </div>
            </section>
        </div>
    </div>
    <div class="row">
        <div class="col-md-12">
            <section class="panel b-a" id='secRefDocuments'>
                <div class="panel-heading b-b bg-info"> <span class="pull-right">New</span> <a href="#" class="font-bold">Documents</a> </div>
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
                            <th>Direction</th>
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
                            <td>[Direction]</td>
                        </tr>
                        </tbody>
                    </table>
                </div>
            </section>
        </div>
    </div>
</div>
<div class="tab-pane" id="maps">
    <iframe class="col-xs-12" src="https://maps.google.com/maps?client=safari&amp;q=2000+Riveredge+Parkway+NW+Suite+775+Atlanta,+Georgia+30328&amp;ie=UTF8&amp;hq=&amp;hnear=2000+Riveredge+Pkwy+NW+%23775,+Atlanta,+Georgia+30328&amp;gl=us&amp;t=m&amp;z=13&amp;output=embed"></iframe>
    <table class="table table-striped th-sortable table-hover">
        <thead>
        <tr>
            <th>Name</th>
            <th>Address</th>
            <th>City</th>
            <th>State</th>
            <th>ZIP</th>
        </tr>
        </thead>
        <tbody>
        <tr class="odd gradeA">
            <td>[Name]</td>
            <td>[Address]</td>
            <td>[City]</td>
            <td>[State]</td>
            <td>[ZIP]</td>
        </tr>
        </tbody>
    </table>
</div>
<div class="tab-pane" id="history">
    <section class="panel panel-default">
        <div class="row wrapper">
            <div class="col-sm-4 m-b-xs">
                <select class="input-sm form-control input-s-sm inline v-middle">
                    <option value="0">Action</option>
                    <option value="1">Export to Excel</option>
                </select>
                <button class="btn btn-sm btn-default">Apply</button>
            </div>
            <div class="col-sm-5 m-b-xs">
                <div class="btn-group" data-toggle="buttons">
                    <label class="btn btn-sm btn-default active">
                        <input type="radio" name="options" id="option1">
                        Today </label>
                    <label class="btn btn-sm btn-default">
                        <input type="radio" name="options" id="option2">
                        This Week </label>
                    <label class="btn btn-sm btn-default">
                        <input type="radio" name="options" id="option2">
                        This Month </label>
                </div>
            </div>
            <div class="col-sm-3">
                <div class="input-group">
                    <input type="text" class="input-sm form-control" placeholder="Search">
                                        <span class="input-group-btn">
                                        <button class="btn btn-sm btn-default" type="button">Go!</button>
                                        </span> </div>
            </div>
        </div>
        <div class="table-responsive">
            <table class="table table-striped b-t b-light">
                <thead>
                <tr>
                    <th width="20"><label class="checkbox m-n i-checks">
                        <input type="checkbox">
                        <i></i></label></th>
                    <th class="th-sortable" data-toggle="class">Event <span class="th-sort"> <i class="fa fa-sort-down text"></i> <i class="fa fa-sort-up text-active"></i> <i class="fa fa-sort"></i> </span> </th>
                    <th>Date/Time</th>
                    <th>Type</th>
                    <th width="30">Read?</th>
                </tr>
                </thead>
                <tbody>
                <tr>
                    <td><label class="checkbox m-n i-checks">
                        <input type="checkbox" name="post[]">
                        <i></i></label></td>
                    <td>David Miller created a document and assigned it to you.</td>
                    <td>4/23/2014 12:00:00</td>
                    <td>Document</td>
                    <td><a href="#" class="active" data-toggle="class"><i class="fa fa-check text-success text-active"></i><i class="fa fa-times text-danger text"></i></a></td>
                </tr>
                </tr>

                </tbody>
            </table>
        </div>
        <footer class="panel-footer">
            <div class="row">
                <div class="col-sm-8 hidden-xs"> <small class="text-muted inline m-t-sm m-b-sm">Showing 1-50 of 50 items</small> </div>
                <div class="col-sm-4 text-right text-center-xs">
                    <ul class="pagination pagination-sm m-t-none m-b-none">
                        <li><a href="#"><i class="fa fa-chevron-left"></i></a></li>
                        <li><a href="#">1</a></li>
                        <li><a href="#">2</a></li>
                        <li><a href="#">3</a></li>
                        <li><a href="#">4</a></li>
                        <li><a href="#">5</a></li>
                        <li><a href="#"><i class="fa fa-chevron-right"></i></a></li>
                    </ul>
                </div>
            </div>
        </footer>
    </section>
</div>
</div>
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



