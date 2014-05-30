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
    <script type="text/javascript" src="<c:url value='/resources/js/task/taskObject.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/resources/js/task/taskEvent.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/resources/js/task/taskPage.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/resources/js/task/taskRule.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/resources/js/task/taskService.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/resources/js/task/taskCallback.js'/>"></script>

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
                                    <ul class="nav nav-tabs pull-left font-bold">
                                        <li><a href="#step1" data-toggle="tab">Step 1: Contact</a></li>
                                        <li><a href="#step2" data-toggle="tab">Step 2: Incident</a></li>
                                        <li><a href="#step3" data-toggle="tab">Step 3: People</a></li>
                                        <li><a href="#step4" data-toggle="tab">Step 4: Attachments</a></li>
                                        <li><a href="#step5" data-toggle="tab">Step 5: Assignment</a></li>
                                    </ul>
                                    <span class="hidden-sm"><button class="btn btn-default btn-sm" data-toggle="tooltip" data-title="Save"><i class="fa fa-save"></i> Save As Draft</button> <button class="btn btn-default btn-sm" data-toggle="tooltip" data-title="Submit"><i class="fa fa-check"></i> Submit</button></span>
                                </div>
                                <div class="panel-body">
                                    <div class="progress progress-xs m-t-sm">
                                        <div class="progress-bar bg-success"></div>
                                    </div>
                                    <div class="tab-content">
                                        <div class="tab-pane" id="step1">
                                            <h4>Contact Information</h4>
                                            <p>Enter the contact information of the initiator.</p>
                                            <section class="row m-b-md">
                                                <div id="divInitiator" style="width:98%"></div>
                                            </section>
                                            <hr />

                                            <label for="intiatorFlags" class="label">Initiator Flags</label>

                                            <select data-placeholder="Choose Initiator Flags..." id="intiatorFlags" class="choose-intitiatorFlags form-control" multiple >
                                                <option value=""></option>
                                                <option value="Protected Source">Protected Source</option>
                                            </select>

                                        </div>
                                        <div class="tab-pane" id="step2">
                                            <h4>Incident Information</h4>
                                            <p>Enter in your overall concern and task. Please be very specific as to names, places, times, dates, reasons and outcomes.</p>
                                            <section class="row m-b-md">
                                                <div class="col-sm-3">
                                                    <label for="incidentDate" class="label">Incident Date</label>
                                                    <input id="incidentDate" type="text" class="datepicker-input form-control" placeholder="Incident Date"  value="12-02-2013" data-date-format="dd-mm-yyyy" >

                                                </div>

                                                <div class="col-sm-3">
                                                    <label for="duration" class="label">Duration</label>
                                                    <input id="duration" type="text" class="form-control" placeholder="Duration"   >

                                                </div>

                                                <div class="col-sm-3">

                                                    <label for="taskType"  class="label">Subject Type</label>
                                                    <select name="taskType" class="form-control m-b">
                                                        <option>Choose Subject Type</option>
                                                        <option>Domestic Dispute</option>
                                                        <option>Arson</option>
                                                        <option>Better Business Dispute</option>
                                                        <option>Government</option>
                                                        <option>Local</option>
                                                        <option>Agricultural</option>
                                                        <option>Pollution </option>
                                                    </select>
                                                </div>


                                                <div class="col-sm-3">
                                                    <label for="priority"  class="label">Priority</label>
                                                    <select name="priority" class="form-control m-b">
                                                        <option>Choose Priority</option>
                                                        <option selected>Low</option>
                                                        <option>Medium</option>
                                                        <option>High</option>
                                                        <option>Expedited</option>
                                                    </select>
                                                </div>
                                            </section>
                                            <hr/>

                                            <label for="title"  class="label">Task Title</label>
                                            <input id="edtTaskTitle" type="text" class="form-control" placeholder="Task Title">
                                            <hr />
                                            <div class="taskDetails"></div>

                                            <hr />
                                            <label for="taskFlags" class="label">Task Flags</label>

                                            <select data-placeholder="Task Flags..." id="taskFlags" class="choose-taskFlags form-control" multiple >
                                                <option value=""></option>
                                                <option value="Trade Secret">Trade Secret</option>
                                                <option value="Confidential">Confidential</option>
                                            </select>





                                        </div>
                                        <div class="tab-pane" id="step3">
                                            <h4>People</h4>
                                            <section class="row">
                                                <div id="divPeople" style="width:98%"></div>
                                            </section>

                                        </div>
                                        <div class="tab-pane" id="step4">
                                            <h4>Attachments</h4>
                                            <p>Please drag/drop any attachments that you find pertinent to your task.  Examples could included, scanned documents, notes, photographs, videos, emails, etc.</p>
                                            <div id="upload">
                                                <div id="drop"> Drop Here <br />
                                                    <a>Browse</a>
                                                    <input type="file" name="files[]" multiple />
                                                </div>
                                                <ul/>
                                            </div>
                                        </div>
                                        <div class="tab-pane" id="step5">
                                            <h4>Assignment</h4>
                                            <p>Associate users to this task.</p>
                                            <label for="approvers" class="label">Approvers</label>

                                            <select data-placeholder="Choose Approvers..." id="approvers" class="choose-approvers form-control" multiple style="width:350px;" >
                                                <option value=""></option>
                                                <!--
                                                <option value="David Miller">David Miller</option>
                                                <option value="James Bailey">James Bailey</option>
                                                <option value="Judy Hsu">Judy Hsu</option>
                                                <option value="Ronda Ringo">Ronda Ringo</option>
                                                <option value="AJ McClary">AJ McClary</option>
                                                <option value="Jim Nasr">Jim Nasr</option>
                                                -->
                                            </select>



                                            <hr/>
                                            <label for="notifications" class="label">Notifications</label>

                                            <select data-placeholder="Choose Notifications..." id="notifications" class="choose-notifications form-control" multiple style="width:350px;" >
                                                <option value=""></option>
                                                <option value="David Miller">David Miller</option>
                                                <option value="James Bailey">James Bailey</option>
                                                <option value="Judy Hsu">Judy Hsu</option>
                                                <option value="Ronda Ringo">Ronda Ringo</option>
                                                <option value="AJ McClary">AJ McClary</option>
                                                <option value="Jim Nasr">Jim Nasr</option>
                                            </select>

                                            <hr/>
                                            <label for="notifications" class="label">Alerts</label>
                                            <section id="communicationDevices4" class="panel b-a">
                                                <div id="divDevices" style="width:98%"></div>
                                            </section>


                                        </div>
                                        <ul class="pager wizard m-b-sm">
                                            <li class="previous"><a href="#"><i class="fa fa-arrow-left"></i> Previous</a></li>

                                            <li class="next"><a href="#">Next <i class="fa fa-arrow-right"></i> </a></li>



                                        </ul>
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



