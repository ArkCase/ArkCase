<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>


<t:layout>
<jsp:attribute name="endOfHead">
    <title><spring:message code="task.page.title" text="Tasks | ACM | Armedia Case Management" /></title>
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
    <link rel="stylesheet" href="<c:url value='/'/>resources/vendors/${vd_acm}/themes/basic/${vd_x_editable}/css/bootstrap-editable.css" type="text/css"/>
    <script src="<c:url value='/'/>resources/vendors/${vd_x_editable}/js/${js_x_editable}"></script>
</jsp:attribute>

<jsp:body>
<section id="content">
<section class="vbox">
<section class="scrollable">
<section class="hbox stretch">
<aside class="aside-lg" id="email-list">
    <section class="vbox">
        <header class="dker header clearfix">
            <h3 class="m-b-xs text-black pull-left"><spring:message code="task.page.descShort" text="Tasks" /></h3>
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
            <!-- Load more tasks action -->
<!--             <a href="#" class="btn btn-default btn-md col-lg-12 m-b-xs"><i class="fa fa-repeat"></i> Load More...</a>
 -->        </section>
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




<aside class="bg-light lter">
    <section class="vbox">
	    <h4 id="noTaskFoundMeassge" class="m-n">No task assigned to you was found.</h4>
        <section id="taskDetailView" class="scrollable">
            <div class="wrapper dk  clearfix">
                <div class="row">
                    <div class="col-xs-12">
                        <div class="">
                            <div class=" clearfix">
<!--                                 <div class="col-xs-4 b-r">
                                    <div class="h4 font-bold"><a href="#" id="caseTitle" data-type="text" data-pk="1" data-url="/post" data-title="Enter Complaint Title"> Sample Complaint Title</a></div>
                                    <small class="text-muted"><a href="#" id="complaintID" >2014-03-12321</a></small></div>
                                <div class="col-xs-2 b-r">
                                    <div class="h4 font-bold"><a href="#" id="incident" data-type="date" data-pk="1" data-url="/post" data-title="Enter Incident Date">MM/DD/YYYY</a></div>
                                    <small class="text-muted">Incident Date</small></div>
                                <div class="col-xs-1 b-r">
                                    <div class="h4 font-bold"><a href="#" id="priority" data-type="select" data-pk="1" data-url="/post" data-title="Enter Priority">High</a></div>
                                    <small class="text-muted">Priority</small></div>
                                <div class="col-xs-2 b-r">
                                    <div class="h4 font-bold"><a href="#" id="assigned" data-type="select" data-pk="1" data-url="/post" data-title="Enter Assignee">AJ McClary</a></div>
                                    <small class="text-muted">Assigned To</small></div>
                                <div class="col-xs-2 b-r">
                                    <div class="h4 font-bold"><a href="#" id="type" data-type="select" data-pk="1" data-url="/post" data-title="Enter Subject Type">CRIMINAL</a></div>
                                    <small class="text-muted">Subject Type</small></div>
                                <div class="col-xs-1">
                                    <div class="h4 font-bold"><a href="#" id="status" >PENDING</a></div>
                                    <small class="text-muted">Status</small></div>
 -->                            </div>
                        </div>
                    </div>
                </div>
            </div>
            <div class="wrapper bg-empty  clearfix">
                <div class="pull-right inline">
                    <div class="btn-group">
			       		<!-- TODO: when data-toggle is modal, the tooltip won't come up 
			       		-->
			            <button class="btn btn-default btn-sm" data-toggle="modal" data-title="Sign" data-target="#signatureModal"><i class="fa fa-certificate"></i></button>
                        <button class="btn btn-default btn-sm" data-toggle="tooltip" data-title="Complete Task"><i class="fa fa-check"></i></button>
                    </div>
                </div>
                <h4 class="m-n"> <a href="#" id="caseTitle" data-type="text" data-title="Enter Case Title"></a></h4>
                <hr/>
                <div class="row">
                    <div class="col-xs-12">
                        <div class="">
                            <div class=" clearfix">
                                <div class="col-xs-2 b-r">
                                    <div class="h4 font-bold"><a href="#" id="percentageCompleted" data-type="text" data-pk="1" data-title="Enter % of Completion"></a></div>
                                    <small class="text-muted">% of Completion</small></div>
                                <div class="col-xs-2 b-r">
                                    <div class="h4 font-bold"><a href="#" id="taskOwner" data-type="text" data-pk="1" data-title="Enter Owner"></a></div>
                                    <small class="text-muted">Owner</small></div>
                                <div class="col-xs-2 b-r">
                                    <div class="h4 font-bold"><a href="#" id="priority" data-type="text" data-pk="1" data-title="Enter priority"></a></div>
                                    <small class="text-muted">Priority</small></div>
                                <div class="col-xs-2 b-r">
                                    <div class="h4 font-bold"><a href="#" id="startDate" data-type="date" data-pk="1" data-title="Enter Start Date"></a></div>
                                    <small class="text-muted">Start Date</small></div>
                                <div class="col-xs-2 b-r">
                                    <div class="h4 font-bold"><a href="#" id="dueDate" data-type="date" data-pk="1" data-title="Enter Due Date"></a></div>
                                    <small class="text-muted">Due Date</small></div>
                                <div class="col-xs-2">
                                    <div class="h4 font-bold"><a href="#" id="status" data-type="text" data-title="Enter Task Status"></a></div>
                                    <small class="text-muted">Status</small></div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
            <div class="wrapper">
                <div class="row">
                    <div class="col-md-12">
                        <section class="panel b-a ">
                            <div class="panel-heading b-b bg-info">
                                <ul class="nav nav-pills pull-right">
                                    <li>
                                        <div class="btn-group padder-v2">
                                            <button class="btn btn-default btn-sm" id="detailEdit" data-toggle="tooltip" data-title="Edit" ><i class="fa fa-pencil"></i></button>
                                           	<button class="btn btn-default btn-sm" id="detailCancel" data-toggle="tooltip" data-title="Cancel" ><i class="fa fa-eject"></i></button>
                                            <button class="btn btn-default btn-sm" id="detailSave" data-toggle="tooltip" data-title="Save" ><i class="fa fa-save"></i></button>
                                            <ul class="dropdown-menu pull-right">
                                                <li><a href="#">Other menu items</a></li>
                                            </ul>
                                        </div>
                                    </li>
                                </ul>
                                <a href="#" class="font-bold">Task Details</a>
                            </div>
                            <div class="panel-body">
                                <div class="complaintDetails" id="details"></div>
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
			                    <a href="#" class="font-bold">Electronic Signatures</a> </div>
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

