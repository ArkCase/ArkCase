<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>

<t:layout>
<jsp:attribute name="endOfHead">
    <title>${pageDescriptor.title}</title>
    <div id="searchData" itemscope="true" style="display: none">
        <span itemprop="searchDef">${searchDef}</span>
        <span itemprop="searchEx">${searchEx}</span>
    </div>
</script>
</jsp:attribute>


<jsp:attribute name="endOfBody">
    <script type="text/javascript" src="<c:url value='/resources/js/search/Search.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/resources/js/search/SearchObject.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/resources/js/search/SearchEvent.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/resources/js/search/SearchPage.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/resources/js/search/SearchRule.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/resources/js/search/SearchService.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/resources/js/search/SearchCallback.js'/>"></script>

    <script type="text/javascript" src="<c:url value='/'/>resources/vendors/${vd_slimscroll}/jquery.slimscroll.min.js"></script>

    <!-- JTable -->
    <link rel="stylesheet" href="<c:url value='/'/>resources/vendors/${vd_acm}/themes/basic/${vd_jtable}/blue/jtable.css" type="text/css"/>
    <script type="text/javascript" src="<c:url value='/'/>resources/vendors/${vd_jtable}/${js_jtable}"></script>
</jsp:attribute>

<jsp:body>
<section id="content">
<p>hello, ${prop1.get("search.ex")}</p>
    <section class="hbox stretch">
        <aside class="aside-md bg-light dker b-r" id="subNav">
            <section class="vbox">
                <!--scroll bar -->
                <section class="scrollable">

                    <div class="wrapper b-b header">Advanced Search</div>

                        <div class="wrapper">

                            <div class="input-group">
                                <input type="text" class="input-sm form-control" id="searchQuery" placeholder="Search">
                                    <span class="input-group-btn">
                                      <button class="btn btn-sm btn-default" type="button">Go!</button>
                                    </span>

                            </div>

<!-- ================================================ -->
                            <div class="line line-dashed b-b line-lg pull-in"></div>
                            <div class="form-group">
                                <label class="col-sm-6 control-label">Complaints</label>
                                <div class="col-sm-4">
                                    <label class="switch">
                                        <input type="checkbox" id="chkComplaints">
                                        <span></span>
                                    </label>
                                </div>

                                <div class="col-sm-12" id="complaintFields">
                                    <label class="label">Complaint Title</label>
                                    <input type="text" id="edtComplaintTitle" class="form-control" placeholder="Enter Complaint Title">
                                    <label class="label">Complaint ID</label>
                                    <input type="text" id="edtComplaintID" class="form-control" placeholder="Enter Complaint ID">
                                    <label class="label">Incident Date</label>
                                    <div class="clear"></div>
                                    <label class="label col-sm-3">From</label>
                                    <div class="col-sm-9"><input class="datepicker-input form-control" id="edtComplaintDateStartRange" type="text" value="" data-date-format="dd-mm-yyyy" ></div>
                                    <label class="label col-sm-3">To</label>
                                    <div class="col-sm-9"> <input class="datepicker-input form-control " id="edtComplaintDateEndRange" type="text" value="" data-date-format="dd-mm-yyyy" ></div>
                                    <div class="clear"></div>
                                    <label for="priority"  class="label" >Priority</label>
                                    <select name="priority" class="form-control" id="selComplaintPriority">
                                        <option>Choose Priority</option>
                                        <option selected>Low</option>
                                        <option>Medium</option>
                                        <option>High</option>
                                        <option>Expedited</option>
                                    </select>
                                    <label class="label" >Assigned To</label>
                                    <input type="text" class="form-control" id="edtComplaintAssignee" placeholder="Enter Assigned To">
                                    <label for="subjectType"  class="label">Subject Type</label>
                                    <select name="subjectType" class="form-control" id="selComplaintSubjectType" >
                                        <option>Choose Subject Type</option>
                                    </select>
                                    <label for="complaintStatus"  class="label">Status</label>
                                    <select name="complaintStatus" class="form-control" id="selComplaintStatus" >
                                        <option>Choose Status</option>
                                    </select>
                                </div>

                            </div>
 <!-- ================================================ -->
                            <div class="line line-dashed b-b line-lg pull-in"></div>

                            <div class="form-group">
                                <label class="col-sm-6 control-label">Cases</label>
                                <div class="col-sm-4">
                                    <label class="switch">
                                        <input type="checkbox" id="chkCases">
                                        <span></span>
                                    </label>
                                </div>

                                <div class="col-sm-12" id="caseFields">
                                    <label class="label">Case Title</label>
                                    <input type="text" class="form-control" id="edtCaseTitle" placeholder="Enter Case Title">
                                    <label class="label">Case ID</label>
                                    <input type="text" class="form-control" id="edtCaseID" placeholder="Enter Case ID">
                                    <label class="label">Incident Date</label>
                                    <div class="clear"></div>
                                    <label class="label col-sm-3">From</label>
                                    <div class="col-sm-9"><input class="datepicker-input form-control" id="edtCaseDateStartRange" type="text" value="" data-date-format="dd-mm-yyyy" ></div>
                                    <label class="label col-sm-3">To</label>
                                    <div class="col-sm-9"> <input class="datepicker-input form-control " id="edtCaseDateEndRange" type="text" value="" data-date-format="dd-mm-yyyy" ></div>
                                    <div class="clear"></div>
                                    <label for="priority"  class="label">Priority</label>
                                    <select name="priority" class="form-control" id="selCasePriority">
                                        <option>Choose Priority</option>
                                        <option selected>Low</option>
                                        <option>Medium</option>
                                        <option>High</option>
                                        <option>Expedited</option>
                                    </select>
                                    <label class="label">Assigned To</label>
                                    <input type="text" class="form-control" id="edtCaseAssignee" placeholder="Enter Assigned To">
                                    <label for="subjectType"  class="label">Subject Type</label>
                                    <select name="subjectType" class="form-control" id="selCaseSubjectType">
                                        <option>Choose Subject Type</option>
                                    </select>
                                    <label for="caseStatus"  class="label">Status</label>
                                    <select name="caseStatus" class="form-control" id="selCaseStatus">
                                        <option>Choose Status</option>
                                    </select>
                                </div>

                            </div>

                            <div class="line line-dashed b-b line-lg pull-in"></div>

                            <div class="form-group">
                                <label class="col-sm-6 control-label">Tasks</label>
                                <div class="col-sm-4">
                                    <label class="switch">
                                        <input type="checkbox" id="chkTasks">
                                        <span></span>
                                    </label>
                                </div>

                                <div class="col-sm-12" id="taskFields">
                                    <label class="label">Task Title</label>
                                    <input type="text" class="form-control" id="edtTaskTitle" placeholder="Enter Task Title">
                                    <label class="label">Task ID</label>
                                    <input type="text" class="form-control" id="edtTaskID" placeholder="Enter Task ID">
                                    <label class="label">Due Date</label>
                                    <div class="clear"></div>
                                    <label class="label col-sm-3">From</label>
                                    <div class="col-sm-9"><input class="datepicker-input form-control" id="edtTaskDueDateStartRange" type="text" value="" data-date-format="dd-mm-yyyy" ></div>
                                    <label class="label col-sm-3">To</label>
                                    <div class="col-sm-9"> <input class="datepicker-input form-control " id="edtTaskDueDateEndRange" type="text" value="" data-date-format="dd-mm-yyyy" ></div>
                                    <div class="clear"></div>
                                    <label for="priority"  class="label">Priority</label>
                                    <select name="priority" class="form-control" id="selTaskPriority">
                                        <option>Choose Priority</option>
                                        <option selected>Low</option>
                                        <option>Medium</option>
                                        <option>High</option>
                                        <option>Expedited</option>
                                    </select>
                                    <label class="label">Assigned To</label>
                                    <input type="text" class="form-control" id="edtTaskAssignee" placeholder="Enter Assigned To">
                                    <label for="subjectType"  class="label">Subject Type</label>
                                    <select name="subjectType" class="form-control" id="selTaskSubjectType">
                                        <option>Choose Subject Type</option>
                                    </select>
                                    <label for="taskStatus"  class="label">Status</label>
                                    <select name="taskStatus" class="form-control" id="selTaskStatus">
                                        <option>Choose Status</option>
                                    </select>
                                </div>
                            </div>

                            <div class="line line-dashed b-b line-lg pull-in"></div>


                            <div class="form-group">
                                <label class="col-sm-6 control-label">Documents</label>
                                <div class="col-sm-4">
                                    <label class="switch">
                                        <input type="checkbox" id="chkDocuments">
                                        <span></span>
                                    </label>
                                </div>

                                <div class="col-sm-12" id="docFields">
                                    <label class="label">Document Title</label>
                                    <input type="text" class="form-control" id="edtDocumentTitle" placeholder="Enter Document Title">
                                    <label class="label">Document ID</label>
                                    <input type="text" class="form-control" id="edtDocumentID" placeholder="Enter Document ID">
                                    <label class="label">Due Date</label>
                                    <div class="clear"></div>
                                    <label class="label col-sm-3">From</label>
                                    <div class="col-sm-9"><input class="datepicker-input form-control" id="edtDocumentDateStartRange" type="text" value="" data-date-format="dd-mm-yyyy" ></div>
                                    <label class="label col-sm-3">To</label>
                                    <div class="col-sm-9"> <input class="datepicker-input form-control " id="edtDocumentDateEndRange" type="text" value="" data-date-format="dd-mm-yyyy" ></div>
                                    <div class="clear"></div>
                                    <label for="priority"  class="label">Priority</label>
                                    <select name="priority" class="form-control" id="selDocumentPriority">
                                        <option>Choose Priority</option>
                                        <option selected>Low</option>
                                        <option>Medium</option>
                                        <option>High</option>
                                        <option>Expedited</option>
                                    </select>
                                    <label class="label">Assigned To</label>
                                    <input type="text" class="form-control" id="edtDocumentAssignee" placeholder="Enter Assigned To">
                                    <label for="subjectType"  class="label">Document Type</label>
                                    <select name="subjectType" class="form-control">
                                        <option>Choose Document Type</option>
                                    </select>
                                    <label for="formType"  class="label">Form Type</label>
                                    <select name="formType" class="form-control" id="selDocumentFormType">
                                        <option>Choose Form Type</option>
                                    </select>
                                    <label for="documentStatus"  class="label">Status</label>
                                    <select name="documentStatus" class="form-control" id="selDocumentStatus">
                                        <option>Choose Status</option>
                                    </select>
                                </div>
                            </div>

                            <div class="line line-dashed b-b line-lg pull-in"></div>

                            <div class="line line-dashed b-b line-lg pull-in"></div>


                            <div class="form-group">
                                <label class="col-sm-6 control-label">People</label>
                                <div class="col-sm-4">
                                    <label class="switch">
                                        <input type="checkbox" id="chkPeople">
                                        <span></span>
                                    </label>
                                </div>


                                <div class="col-sm-12" id="peopleFields">
                                    <label class="label">Title</label>
                                    <input type="text" class="form-control" id="edtPeopleTitle" placeholder="Enter Title">
                                    <label class="label">First Name</label>
                                    <input type="text" class="form-control" id="edtPeopleFirstName" placeholder="Enter First Name">
                                    <label class="label">Last Name</label>
                                    <input type="text" class="form-control" id="edtPeopleLastName" placeholder="Enter Last Name">
                                    <label for="priority"  class="label">Type</label>
                                    <select name="priority" class="form-control" id="selPeopleType">
                                        <option>Choose Type</option>
                                    </select>
                                    <label class="label">Phone</label>
                                    <input type="text" class="form-control" id="edtPeoplePhoneNumber" data-type="phone" placeholder="(XXX) XXXX XXX" data-required="true">

                                    <label class="label">Organization</label>
                                    <input type="text" class="form-control" id="edtPeopleOrganization" placeholder="Enter Organization">

                                    <label class="label">Address</label>
                                    <input type="text" class="form-control" id="edtPeopleAddress" placeholder="Enter Address">
                                    <label class="label">City</label>
                                    <input type="text" class="form-control" id="edtPeopleCity" placeholder="Enter City">

                                    <label for="state"  class="label">State</label>
                                    <select name="state" class="form-control" id="selPeopleState">
                                        <option>Choose State</option>
                                    </select>
                                    <label class="label">ZIP</label>
                                    <input type="text" class="form-control" id="edtPeopleZIPCode" placeholder="Enter ZIP">

                                </div>
                            </div>

                        </div>
                </section>
            </section>
        </aside>
        <aside>
            <section class="vbox">
                <header class="header bg-white b-b clearfix">
                    <div class="row m-t-sm">
                        <div class="col-sm-12 m-b-xs">
                            <%--<a href="#subNav" data-toggle="class:hide" class="btn btn-sm btn-default active"><i class="fa fa-caret-right text fa-lg"></i><i class="fa fa-caret-left text-active fa-lg"></i></a>--%>
                            <a href="#subNav" data-toggle="class:hide" class="btn btn-sm btn-default "><i class="fa fa-caret-right text fa-lg"></i><i class="fa fa-caret-left text-active fa-lg"></i></a>
                            <div class="btn-group">
                                <button type="button" class="btn btn-sm btn-default" title="Refresh"><i class="fa fa-refresh"></i></button>
                                <button type="button" class="btn btn-sm btn-default" title="Filter" data-toggle="dropdown"><i class="fa fa-filter"></i> <span class="caret"></span></button>
                                <ul class="dropdown-menu">
                                    <li><a href="#">Filter 1</a></li>
                                    <li><a href="#">Filter 2</a></li>
                                    <li><a href="#">Filter 3</a></li>
                                </ul>
                            </div>

                        </div>

                    </div>
                </header>
                <section class="scrollable wrapper w-f">
                    <section class="panel panel-default">
                        <div id="divResults" style="width:98%"></div>
                        <!--
                        </br></br>
                        <div class="table-responsive">
                            <table class="table table-striped m-b-none">
                                <thead>
                                <tr>
                                    <th width="20"><label class="checkbox m-n i-checks"><input type="checkbox"><i></i></label></th>
                                    <th width="20"></th>
                                    <th width="20">ID</th>
                                    <th class="th-sortable" data-toggle="class">Type
                              <span class="th-sort">
                                <i class="fa fa-sort-down text"></i>
                                <i class="fa fa-sort-up text-active"></i>
                                <i class="fa fa-sort"></i>
                              </span>
                                    </th>
                                    <th class="th-sortable" data-toggle="class">Title
                              <span class="th-sort">
                                <i class="fa fa-sort-down text"></i>
                                <i class="fa fa-sort-up text-active"></i>
                                <i class="fa fa-sort"></i>
                              </span>
                                    <th>Owner</th>
                                    <th>Created</th>
                                </tr>
                                </thead>
                                <tbody>
                                <tr>
                                    <td><label class="checkbox m-n i-checks"><input type="checkbox" name="ids[]"><i></i></label></td>
                                    <td><a href="#modal" data-toggle="modal"><i class="fa fa-search-plus text-muted"></i></a></td>
                                    <td>[ID]</td>
                                    <td>[Type]</td>
                                    <td>[Title]</td>
                                    <td>[Owner]</td>
                                    <td>[Date Created]</td>
                                </tr>
                                <tr class="bg-primary-ltest">
                                    <td><label class="checkbox m-n i-checks"><input type="checkbox" name="ids[]"><i></i></label></td>
                                    <td><a href="#modal" data-toggle="modal"><i class="fa fa-search-plus text-muted"></i></a></td>
                                    <td>[ID]</td>
                                    <td>[Type]</td>
                                    <td>[Title]</td>
                                    <td>[Owner]</td>
                                    <td>[Date Created]</td>
                                </tr>
                                </tbody>
                            </table>
                        </div>
                        -->
                    </section>
                </section>
                <footer class="footer bg-white b-t">
                    <!--
                    <div class="row text-center-xs">
                        <div class="col-md-6 hidden-sm">
                            <p class="text-muted m-t">Showing 20-30 of 50</p>
                        </div>
                        <div class="col-md-6 col-sm-12 text-right text-center-xs">
                            <ul class="pagination pagination-sm m-t-sm m-b-none">
                                <li><a href="#"><i class="fa fa-chevron-left"></i></a></li>
                                <li class="active"><a href="#">1</a></li>
                                <li><a href="#">2</a></li>
                                <li><a href="#">3</a></li>
                                <li><a href="#">4</a></li>
                                <li><a href="#">5</a></li>
                                <li><a href="#"><i class="fa fa-chevron-right"></i></a></li>
                            </ul>
                        </div>
                    </div>
                    -->
                </footer>
            </section>
        </aside>
    </section>
</section>

<script>

    $('#chkComplaints').click(function(){
        var val = $(this).val();
        $('#complaintFields').slideToggle();
    });

    $('#chkCases').click(function(){
        var val = $(this).val();
        $('#caseFields').slideToggle();
    });

    $('#chkTasks').click(function(){
        var val = $(this).val();
        $('#taskFields').slideToggle();
    });

    $('#chkDocuments').click(function(){
        var val = $(this).val();
        $('#docFields').slideToggle();
    });

    $('#chkPeople').click(function(){
        var val = $(this).val();
        $('#peopleFields').slideToggle();
    });

</script>

<style>

    #complaintFields, #caseFields, #taskFields, #docFields, #peopleFields { display:none}

</style>

</jsp:body>
</t:layout>


