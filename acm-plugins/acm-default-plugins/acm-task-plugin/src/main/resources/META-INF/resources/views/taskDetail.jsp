<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>


<t:layout>
<jsp:attribute name="endOfHead">
    <title>${pageDescriptor.title}</title>

    <div id="detailData" itemscope="true" style="display: none">
        <span itemprop="taskId">${taskId}</span>
    </div>

    <script type="text/javascript">
        $(document).ready(function () {
            Task.initialize();
            TaskDetail.initialize();
        });
    </script>
</jsp:attribute>

<jsp:attribute name="endOfBody">
<script type="text/javascript" src="<c:url value='/resources/js/task/task.js'/>"></script>
<script type="text/javascript" src="<c:url value='/resources/js/task/detail/taskDetail.js'/>"></script>
<script type="text/javascript" src="<c:url value='/resources/js/task/detail/taskDetailObject.js'/>"></script>
<script type="text/javascript" src="<c:url value='/resources/js/task/detail/taskDetailEvent.js'/>"></script>
<script type="text/javascript" src="<c:url value='/resources/js/task/detail/taskDetailPage.js'/>"></script>
<script type="text/javascript" src="<c:url value='/resources/js/task/detail/taskDetailRule.js'/>"></script>
<script type="text/javascript" src="<c:url value='/resources/js/task/detail/taskDetailService.js'/>"></script>
<script type="text/javascript" src="<c:url value='/resources/js/task/detail/taskDetailCallback.js'/>"></script>

<script src="<c:url value='/resources/js/app.js'/>"></script>
<script src="<c:url value='/resources/js/app.plugin.js'/>"></script>
<script type="text/javascript" src="<c:url value='/'/>resources/vendors/${vd_slimscroll}/jquery.slimscroll.min.js"></script>


<!-- Summernote WYSIWYG -->
<link rel="stylesheet" href="<c:url value='/'/>resources/vendors/${vd_summernote}/summernote.css" type="text/css"/>
<script type="text/javascript" src="<c:url value='/'/>resources/vendors/${vd_summernote}/summernote.js"></script>
<script>

    var edit = function() {
        $('.taskDetails').summernote({focus: true});
    };
    var save = function() {
        var aHTML = $('.click2edit').code(); //save HTML If you need(aHTML: array).
        $('.taskDetails').destroy();
    };

</script>

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
<!--
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
            <a href="#" class="btn btn-default btn-md col-lg-12 m-b-xs"><i class="fa fa-repeat"></i> Lead More...</a> </section>
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
-->
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
    <h4 class="m-n"> <a href="#" id="caseTitle" data-type="text" data-pk="1" data-url="/post" data-title="Enter Task Title">...</a> (...)</h4>
</div>
<div>
<div class="wrapper">
<div class="row">

        <div class="col-sm-4">
            <label for="taskId" class="label">Task ID</label>
            <input id="taskId" type="text" class="form-control" placeholder="Task ID">

            <label for="title" class="label">Title</label>
            <input id="title" type="text" class="form-control" placeholder="Title">

            <label for="priority" class="label">Priority</label>
            <input id="priority" type="text" class="form-control" placeholder="Priority">

            <label for="dueDate" class="label">Due Date</label>
            <input id="dueDate" type="text" class="form-control" placeholder="Due Date">

            <label for="assignee" class="label">Assignee</label>
            <input id="assignee" type="text" class="form-control" placeholder="Assignee">

            <label for="adhocTask" class="label">Adhoc Task</label>
            <input id="adhocTask" type="text" class="form-control" placeholder="Is Adhoc Task?">
            </br>
                <%--<label class="checkbox m-n i-checks">--%>
                <%--<input id="adhocTask" type="checkbox" name="adhocTask"><i></i>Adhoc Task--%>
                <%--</label>--%>
        </div>

        <div class="col-sm-4" id="divExtra" style="display:none">
            <label for="businessProcessName" class="label">Business Process Name</label>
            <input id="businessProcessName" type="text" class="form-control" placeholder="Business Process Name">

                <%--<label for="attachedToObjectType" class="label">Attached To Object Type</label>--%>
                <%--<input id="attachedToObjectType" type="text" class="form-control" placeholder="Attached To Object Type">--%>

                <%--<label for="attachedToObjectId" class="label">Attached To Object ID</label>--%>
                <%--<input id="attachedToObjectId" type="text" class="form-control" placeholder="Attached To Object ID">--%>
            <br/>
            <a href="#">&nbsp;&nbsp;Go to: <scan id="attachedToObjectType">COMPLAINT</scan>&nbsp;<scan id="attachedToObjectId">1234</scan></a>
        </div>

</div>
<div class="row">
    <div class="col-sm-4">
        </br>
        <span class="hidden-sm"><button class="btn btn-default btn-sm" data-toggle="tooltip" data-title="Complete"><i class="fa fa-check"></i> Complete</button></span>
    </div>
</div>


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



