<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>


<t:layout>
<jsp:attribute name="endOfHead">
    <title>${pageDescriptor.title}</title>
    <div id="detailData" itemscope="true" style="display: none">
        <span itemprop="taskId">${taskId}</span>
    </div>
</jsp:attribute>

<jsp:attribute name="endOfBody">
    <script type="text/javascript" src="<c:url value='/resources/js/task/task.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/resources/js/task/list/taskList.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/resources/js/task/list/taskListObject.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/resources/js/task/list/taskListEvent.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/resources/js/task/list/taskListPage.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/resources/js/task/list/taskListRule.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/resources/js/task/list/taskListService.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/resources/js/task/list/taskListCallback.js'/>"></script>

    <script type="text/javascript" src="<c:url value='/'/>resources/vendors/${vd_slimscroll}/jquery.slimscroll.min.js"></script>


    <!-- Summernote WYSIWYG -->
    <link rel="stylesheet" href="<c:url value='/'/>resources/vendors/${vd_summernote}/summernote.css" type="text/css"/>
    <script type="text/javascript" src="<c:url value='/'/>resources/vendors/${vd_summernote}/summernote.js"></script>
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
    <link href="<c:url value='/'/>resources/vendors/${vd_x_editable}/css/bootstrap-editable.css" rel="stylesheet">
    <script src="<c:url value='/'/>resources/vendors/${vd_x_editable}/js/bootstrap-editable.min.js"></script>
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
                        <li><a href="#">All Open Tasks</a></li>
                        <li><a href="#">Tasks I've Opened</a></li>
                        <li><a href="#">Unapproved Tasks</a></li>
                        <li><a href="#">Approved Tasks</a></li>
                        <li><a href="#">Tasks From Group</a></li>
                        <li><a href="#">Closed or Expired Tasks</a></li>
                        <li><a href="#">New Tasks</a></li>
                    </ul>
                </div>
            </div>
        </header>
        <section class="scrollable hover">
            <ul class="list-group auto no-radius m-b-none m-t-n-xxs list-group-lg" id="ulTasks">
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
       		<!-- TODO: AJ TO HELP, when data-toggle is modal, the tooltip won't come up 
       		TODO:  Only display the sign when the object hasn't been signed yet
       		TODO:  Show signature panel somewhere
       		-->
            <button class="btn btn-default btn-sm" data-toggle="modal" data-title="Sign" data-target="#signatureModal"><i class="fa fa-certificate"></i></button>
            <button class="btn btn-default btn-sm" data-toggle="tooltip" data-title="Complete"><i class="i i-checkmark"></i></button>
            <button class="btn btn-default btn-sm" data-toggle="tooltip" data-title="Reject"><i class="i i-cancel"></i></button>
            <button class="btn btn-default btn-sm dropdown-toggle" data-toggle="dropdown"><span class="caret"></span></button>
            <ul class="dropdown-menu pull-right">
                <li><a href="#">Other menu items</a></li>
            </ul>
        </div>
        <a href="#nav, #chat" class="inline animated btn btn-default btn-sm " data-toggle="class:nav-xs, show"><i class="fa  fa-columns"></i></a> </div>
    <h4 class="m-n"> <a href="#" id="caseTitle" data-type="text" data-pk="1" data-url="/post" data-title="Enter Task Title"> Sample Task Title</a> (6423) <em> from (2014-03-12321)</em></h4>
</div>
<div>
<div class="wrapper">
<div class="row">
    <div class="col-xs-6">
        <div class="panel b-a  bg-gradient">
            <div class="padder-v text-center clearfix">
                <div class="col-xs-4 b-r">
                    <div class="h4 font-bold"><a href="#" id="incident" data-type="date" data-pk="1" data-url="/post" data-title="Enter Incident Date"></a></div>
                    <small class="text-muted">Due Date</small> </div>
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
                    <div class="h4 font-bold"><a href="#" id="status" >PENDING</a></div>
                    <small class="text-muted">Status</small> </div>
            </div>
        </div>
    </div>
</div>
<section class="panel panel-default">
<header class="panel-heading bg-light">
    <ul class="nav nav-tabs nav-justified">
        <li class="active"><a href="#details" data-toggle="tab">Details</a></li>
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
                    <div class="complaintDetails">
                        <p>Task description</p>
                    </div>
                </div>
            </section>
        </div>
    </div>
    
    <div class="row">
        <div class="col-md-12">
            <section class="panel b-a ">
                <div class="panel-heading b-b bg-info">
                    <ul class="nav nav-pills pull-right">
                        <li> <a href="#" class="panel-toggle text-muted"><i class="fa fa-caret-down text-active"></i><i class="fa fa-caret-up text"></i></a> </li>
                    </ul>
                    </span> <a href="#" class="font-bold">Electronic Signatures</a> </div>
                <div class="panel-body max-200 no-padder">
                    <table class="table table-striped th-sortable table-hover">
                        <thead>
                        <tr>
                            <th>Signed By</th>
                            <th>Date</th>
                        </tr>
                        </thead>
                        <tbody id="signatureList">
                        </tbody>
                    </table>
                </div>
            </section>
        </div>
    </div>

    <div class="row">
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
                    <a href="#" class="font-bold">Notes</a> 
                </div>
                <div class="panel-body max-200">
                    <ul class="list-group list-group-lg no-bg auto">
                        <a href="#" class="list-group-item clearfix"> <span class="pull-left thumb-sm avatar m-r"> <img src="<c:url value='/'/>resources/vendors/${acm_theme}/images/a4.png" alt="John said"> <i class="on b-white bottom"></i> </span> <span class="clear"> <small class="text-muted pull-right">5m ago</small> <span>Judy Hsu</span> <small class="text-muted clear text-ellipsis">Sample notes go here.</small> </span> </a>
                    </ul>
                </div>
            </section>
        </div>
    </div>
</div>

<div class="tab-pane" id="references">
    <div class="row">
        <div class="col-md-12">
            <section class="panel b-a">
                <div class="panel-heading b-b bg-info">
                    <ul class="nav nav-pills pull-right">
                        <li>
                            <div class="btn-group padder-v2">
                                <button class="btn btn-default btn-sm" data-toggle="tooltip" data-title="New Reference"><i class="i i-notice"></i> New</button>
                            </div>
                        </li>
                        <li> <a href="#" class="panel-toggle text-muted"><i class="fa fa-caret-down text-active"></i><i class="fa fa-caret-up text"></i></a> </li>
                    </ul>
                    <a href="#" class="font-bold">Complaints</a> </div>
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
                            <th width="9%">Action</th>
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
                            <td><button class="btn btn-default btn-xs" data-toggle="tooltip" data-title="Delete Record"><i class="fa fa-trash-o"></i></button></td>
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
                <div class="panel-heading b-b bg-info">
                    <ul class="nav nav-pills pull-right">
                        <li>
                            <div class="btn-group padder-v2">
                                <button class="btn btn-default btn-sm" data-toggle="tooltip" data-title="New Reference"><i class="i i-folder"></i> New</button>
                            </div>
                        </li>
                        <li> <a href="#" class="panel-toggle text-muted"><i class="fa fa-caret-down text-active"></i><i class="fa fa-caret-up text"></i></a> </li>
                    </ul>
                    <a href="#" class="font-bold">Cases</a> </div>
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
                            <th width="9%">Action</th>
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
                            <td><button class="btn btn-default btn-xs" data-toggle="tooltip" data-title="Delete Record"><i class="fa fa-trash-o"></i></button></td>
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
                <div class="panel-heading b-b bg-info">
                    <ul class="nav nav-pills pull-right">
                        <li>
                            <div class="btn-group padder-v2">
                                <button class="btn btn-default btn-sm" data-toggle="tooltip" data-title="New Reference"><i class="i i-checkmark"></i> New</button>
                            </div>
                        </li>
                        <li> <a href="#" class="panel-toggle text-muted"><i class="fa fa-caret-down text-active"></i><i class="fa fa-caret-up text"></i></a> </li>
                    </ul>
                    <a href="#" class="font-bold">Tasks</a> </div>
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
                            <th width="9%">Action</th>
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
                            <td><button class="btn btn-default btn-xs" data-toggle="tooltip" data-title="Delete Record"><i class="fa fa-trash-o"></i></button></td>
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
                <div class="panel-heading b-b bg-info">
                    <ul class="nav nav-pills pull-right">
                        <li>
                            <div class="btn-group padder-v2">
                                <button class="btn btn-default btn-sm" data-toggle="tooltip" data-title="New Reference"><i class="i i-file"></i> New</button>
                            </div>
                        </li>
                        <li> <a href="#" class="panel-toggle text-muted"><i class="fa fa-caret-down text-active"></i><i class="fa fa-caret-up text"></i></a> </li>
                    </ul>
                    <a href="#" class="font-bold">Documents</a> </div>
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
                            <th width="9%">Action</th>
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
                            <td><button class="btn btn-default btn-xs" data-toggle="tooltip" data-title="Delete Record"><i class="fa fa-trash-o"></i></button></td>
                        </tr>
                        </tbody>
                    </table>
                </div>
            </section>
        </div>
    </div>
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
                    <th>User</th>
                    <th>Role</th>
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
                    <td>David Miller</td>
                    <td>Supervisor</td>
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
	        Lorem ipsum dolor sit amet, consectetur adipiscing elit. Integer blandit pellentesque tincidunt. Ut tristique sed augue non mollis. Praesent luctus massa nisl, eu iaculis felis mollis sed. Nullam sit amet urna at nisi lobortis pharetra a vitae diam.  	      	
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


