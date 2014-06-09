<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>


<t:layout>
<jsp:attribute name="endOfHead">
    <title>${pageDescriptor.title}</title>

    <script type="text/javascript">
        $(document).ready(function () {
            Task.initialize();
            TaskWizard.initialize();
        });
    </script>
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

    <script type="text/javascript" src="<c:url value='/resources/js/datepicker/bootstrap-datepicker.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/resources/js/app.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/resources/js/slimscroll/jquery.slimscroll.min.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/resources/js/parsley/parsley.min.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/resources/js/wizard/jquery.bootstrap.wizard.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/resources/js/wizard/demo.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/resources/js/app.plugin.js'/>"></script>

    <!-- File Manager -->
    <script type="text/javascript" src="<c:url value='/resources/js/knob1.2.8/js/jquery.knob.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/resources/js/upload5.40.1/js/jquery.fileupload.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/resources/js/upload5.40.1/js/jquery.iframe-transport.js'/>"></script>
<!--    <script type="text/javascript" src="<c:url value='/resources/js/upload5.40.1/js/file-uploads-custom.js'/>"></script>     -->

    <!-- Summernote WYSIWYG -->
    <link rel="stylesheet" href="<c:url value='/resources/js/summernote0.5.1/summernote.css'/>" type="text/css"/>
    <script type="text/javascript" src="<c:url value='/resources/js/summernote0.5.1/summernote.js'/>"></script>

    <!-- Multi-Select Field WYSIWYG -->
    <script type="text/javascript" charset="utf-8" src="<c:url value='/resources/js/chosen1.1.0/chosen.js'/>"></script>


    <!-- JTable -->
    <link rel="stylesheet" href="<c:url value='/resources/js/jtable2.4.0/themes/lightcolor/blue/jtable.css" type="text/css'/>"/>
    <script type="text/javascript" src="<c:url value='/resources/js/jtable2.4.0/jquery.jtable.js'/>"></script>

</jsp:attribute>

<jsp:body>
    <section id="content">
        <section class="vbox">
            <section class="scrollable padder">
                <section class="row m-b-md">
                    <div class="col-sm-12">
                        <h3 class="m-b-xs text-black">${pageDescriptor.descShort}</h3>
                    </div>
                </section>
                <div class="row">
                    <div class="col-sm-12">
                        <form id="wizardform" method="get" action="">
                            <div class="panel panel-default">
                                <div class="panel-heading text-right">
                                    <span class="hidden-sm"><button class="btn btn-default btn-sm" data-toggle="tooltip" data-title="Save"><i class="fa fa-save"></i> Create Task</button></span>
                                </div>
                                <div class="panel-body">
                                    <div class="tab-content">
                                        <div class="row">
                                            <div class="col-sm-4">
                                                <label for="title" class="label">Title</label>
                                                <input id="title" type="text" class="form-control" placeholder="Title">

                                                <label for="priority" class="label">Priority</label>
                                                <input id="priority" type="text" class="form-control" placeholder="Priority">

                                                <label for="dueDate" class="label">Due Date</label>
                                                <input id="dueDate" type="text" class="datepicker-input form-control" placeholder="Due Date" value="" data-date-format="yyyy-mm-dd" style="display:none">

                                                <label for="assignees"  class="label">Assignee</label>
                                                <select id="assignees" name="assignees" class="form-control m-b">
                                                    <option value=""></option>
                                                </select>
                                            </div>
                                        </div>

                                    </div>
                                </div>
                            </div>
                        </form>
                    </div>
                </div>
            </section>
        </section>
    </section>
</jsp:body>
</t:layout>



