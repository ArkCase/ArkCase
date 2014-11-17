<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<t:layout>
<jsp:attribute name="endOfHead">

    <title><spring:message code="audit.page.title" text="Audit | ACM | Armedia Case Management" /></title>

</jsp:attribute>


<jsp:attribute name="endOfBody">

    <%--<script type="text/javascript" src="<c:url value='/resources/js/audit/Audit.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/resources/js/audit/AuditObject.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/resources/js/audit/AuditEvent.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/resources/js/audit/AuditPage.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/resources/js/audit/AuditRule.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/resources/js/audit/AuditService.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/resources/js/audit/AuditCallback.js'/>"></script>--%>

<%--
    <script type="text/javascript" src="<c:url value='/'/>resources/vendors/${vd_slimscroll}/jquery.slimscroll.min.js"></script>
--%>
    <link rel="stylesheet" href="<c:url value='/'/>resources/vendors/${vd_acm}/themes/basic/${vd_x_editable}/css/bootstrap-editable.css" type="text/css"/>

</jsp:attribute>

    <jsp:body>
        <section id="content">
            <section class="vbox">
                <section class="scrollable padder">
                    <section class="row m-b-md">
                        <div class="col-sm-12">
                            <h3 class="m-b-xs text-black">Audit</h3>
                        </div>
                    </section>
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
                                        <input type="radio" name="options" id="option3">
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
                                    <th>Date/Time</th>
                                    <th>User</th>
                                    <th>Name</th>
                                    <th>Result</th>
                                    <th>IP Address</th>
                                    <th>Object ID</th>
                                </tr>
                                </thead>
                                <tbody>
                                <tr>
                                    <td><label class="checkbox m-n i-checks">
                                        <input type="checkbox" name="post[]">
                                        <i></i></label></td>
                                    <td>[Date/Time]</td>
                                    <td>[User]</td>
                                    <td>[Name]</td>
                                    <td>[Result]</td>
                                    <td>[IP Address]</td>
                                    <td>[Object ID]</td>
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
                </section>
            </section>
        </section>

    </jsp:body>
</t:layout>


