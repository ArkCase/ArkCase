<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<t:layout>
<jsp:attribute name="endOfHead">
    <title><spring:message code="adminAccess.page.title" text="Admin | ACM | Armedia Case Management" /></title>
</jsp:attribute>


<jsp:attribute name="endOfBody">
    <script type="text/javascript" src="<c:url value='/resources/js/admin/access/adminAccess.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/resources/js/admin/access/adminAccessObject.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/resources/js/admin/access/adminAccessEvent.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/resources/js/admin/access/adminAccessPage.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/resources/js/admin/access/adminAccessRule.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/resources/js/admin/access/adminAccessService.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/resources/js/admin/access/adminAccessCallback.js'/>"></script>

    <script type="text/javascript" src="<c:url value='/'/>resources/vendors/${vd_slimscroll}/jquery.slimscroll.min.js"></script>
    
    <!-- JTable -->
    <link rel="stylesheet" href="<c:url value='/'/>resources/vendors/${vd_acm}/themes/basic/${vd_jtable}/blue/jtable.css" type="text/css"/>
    <script type="text/javascript" src="<c:url value='/'/>resources/vendors/${vd_jtable}/jquery.jtable.js"></script>

</jsp:attribute>

    <jsp:body>
        <section id="content">
            <section class="vbox">
                <section class="scrollable padder">
                    <section class="row m-b-md">
                        <div class="col-sm-12">
                            <h3 class="m-b-xs text-black"><spring:message code="adminAccess.page.descShort" text="Access Control Policy" /></h3>
                        </div>
                    </section>

                    <div class="row">
                        <div class="col-md-12">
                            <section class="panel panel-default" id="secIncident">
                                <div id="divAdminAccessControlPolicy" style="width:100%"></div>
                            </section>
                        </div>
                    </div>
<!--
                    <section class="panel panel-default">

                        <div class="table-responsive">
                            <table class="table table-striped b-t b-light">
                                <thead>
                                <tr>
                                    <th  class="th-sortable" data-toggle="class">Object Type <span class="th-sort"> <i class="fa fa-sort-down text"></i> <i class="fa fa-sort-up text-active"></i> <i class="fa fa-sort"></i> </span></th>
                                    <th class="th-sortable" data-toggle="class">State <span class="th-sort"> <i class="fa fa-sort-down text"></i> <i class="fa fa-sort-up text-active"></i> <i class="fa fa-sort"></i> </span> </th>
                                    <th class="th-sortable" data-toggle="class">Access Level <span class="th-sort"> <i class="fa fa-sort-down text"></i> <i class="fa fa-sort-up text-active"></i> <i class="fa fa-sort"></i> </span> </th>
                                    <th class="th-sortable" data-toggle="class">Accessor Type <span class="th-sort"> <i class="fa fa-sort-down text"></i> <i class="fa fa-sort-up text-active"></i> <i class="fa fa-sort"></i> </span> </th>
                                    <th  class="th-sortable" data-toggle="class">Access Decision <span class="th-sort"> <i class="fa fa-sort-down text"></i> <i class="fa fa-sort-up text-active"></i> <i class="fa fa-sort"></i> </span></th>
                                    <th  class="th-sortable" data-toggle="class">Allow Discretionary Update <span class="th-sort"> <i class="fa fa-sort-down text"></i> <i class="fa fa-sort-up text-active"></i> <i class="fa fa-sort"></i> </span></th>
                                </tr>
                                </thead>
                                <tbody>
                                <tr>
                                    <td width="89">Complaint</td>
                                    <td width="53">DRAFT</td>
                                    <td width="53">read</td>
                                    <td width="53">*</td>
                                    <td><select name="Decision" class="form-control">
                                        <option>Choose Decision</option>
                                        <option selected>DENY</option>
                                        <option>GRANT</option>
                                    </select></td>
                                    <td><select name="Update" class="form-control">
                                        <option>Choose Update</option>
                                        <option selected>true</option>
                                        <option>false</option>
                                    </select></td>
                                </tr>
                                <tr>
                                    <td>Complaint</td>
                                    <td>DRAFT</td>
                                    <td>update</td>
                                    <td>*</td>
                                    <td><select name="Decision" class="form-control">
                                        <option>Choose Decision</option>
                                        <option selected>DENY</option>
                                        <option>GRANT</option>
                                    </select></td>
                                    <td><select name="Update" class="form-control">
                                        <option>Choose Update</option>
                                        <option selected>true</option>
                                        <option>false</option>
                                    </select></td>
                                </tr>
                                </tbody>
                            </table>
                        </div>
                        <br/><br/><br/>

                    </section>
-->

                </section>
                <!-- <footer class="footer bg-info clearfix">
                    <form class="m-t-sm pull-right">
                        <div class="input-group">
                            <button class="btn btn-sm btn-default" type="button">Save Changes</button>
                        </div>
                    </form>
                </footer> -->
            </section>
        </section>
    </jsp:body>
</t:layout>


