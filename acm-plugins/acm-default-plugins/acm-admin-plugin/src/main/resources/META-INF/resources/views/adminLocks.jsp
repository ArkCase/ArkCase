<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<t:layout>
    <jsp:attribute name="endOfHead">
        <title><spring:message code="adminLocks.page.title" text="Admin | ACM | Armedia Case Management" /></title>
    </jsp:attribute>


    <jsp:attribute name="endOfBody">
        <script type="text/javascript" src="<c:url value='/resources/js/admin/access/adminAccess.js'/>"></script>
        <script type="text/javascript" src="<c:url value='/resources/js/admin/access/adminAccessObject.js'/>"></script>
        <script type="text/javascript" src="<c:url value='/resources/js/admin/access/adminAccessEvent.js'/>"></script>
        <script type="text/javascript" src="<c:url value='/resources/js/admin/access/adminAccessPage.js'/>"></script>
        <script type="text/javascript" src="<c:url value='/resources/js/admin/access/adminAccessRule.js'/>"></script>
        <script type="text/javascript" src="<c:url value='/resources/js/admin/access/adminAccessService.js'/>"></script>
        <script type="text/javascript" src="<c:url value='/resources/js/admin/access/adminAccessCallback.js'/>"></script>

        <script type="text/javascript" src="<c:url value='/'/>resources/vendors/${vd_slimscroll}/${js_slimscroll}"></script>

        <!-- JTable -->
        <link rel="stylesheet" href="<c:url value='/'/>resources/vendors/${vd_acm}/themes/basic/${vd_jtable}/blue/jtable.css" type="text/css"/>
        <script type="text/javascript" src="<c:url value='/'/>resources/vendors/${vd_jtable}/${js_jtable}"></script>

        <%--Fancy Tree--%>
        <link href="<c:url value='/'/>resources/vendors/${vd_fancytree}/skin-win8/ui.fancytree.css" rel="stylesheet">
        <script src="<c:url value='/'/>resources/vendors/${vd_fancytree}/${js_fancytree}"></script>
        <script src="<c:url value='/'/>resources/vendors/${vd_contextmenu}/${js_contextmenu}"></script>

    </jsp:attribute>

    <jsp:body>
        <section class="vbox">
            <section class="scrollable">
                <section class="hbox stretch"><!-- /.aside -->

                    <aside class="aside-lg bg-light lt">
                        <section class="vbox animated fadeInLeft">
                            <section class="scrollable">
                                <header class="dk header">
                                    <h3 class="m-b-xs text-black pull-left"><spring:message code="admin.page.descLong" text="Admin Locks" /></h3>
                                </header>

                                <div class="row m-b">
                                    <div class="col-sm-12">
                                        <div id="tree"></div>
                                    </div>
                                </div>

                            </section>
                        </section>
                    </aside>

                    <aside id="email-content" class="bg-light lter">
                        <section class="vbox">

                            <%--Title--%>

                            <section class="scrollable padder">
                                <section class="row m-b-md">
                                    <div class="col-sm-12">
                                        <h3 class="m-b-xs text-black"><spring:message code="adminLocks.page.descShort" text="Admin Locks" /></h3>
                                    </div>
                                </section>



                                <%--Upper table--%>


                                <section>
                                    <section class="panel panel-default">
                                        <div class="row wrapper">
                                            <div class="col-sm-4">
                                                <label id="subjectType"  class="label">Type</label>
                                                <select name="subjectType" class="form-control">
                                                    <option>Choose Type</option>
                                                </select>
                                                <label class="label">ID</label>
                                                <input type="text" class="form-control" placeholder="Enter ID">
                                                <br/>
                                                <button class="btn btn-sm btn-default" type="button">Search</button>
                                            </div>
                                            <div class="col-sm-4">
                                                <label class="label">Title</label>
                                                <input type="text" class="form-control" placeholder="Enter Title">
                                                <label class="label">Lock Owner</label>
                                                <input type="text" class="form-control" placeholder="Enter Username">
                                            </div>
                                            <div class="col-sm-4">
                                                <label class="label">Date Locked</label>
                                                <div class="clear"></div>
                                                <label class="label col-sm-2">From</label>
                                                <div class="col-sm-10">
                                                    <input class="datepicker-input form-control" type="text" value="12-02-2013" data-date-format="dd-mm-yyyy" >
                                                </div>
                                                <label class="label col-sm-2">To</label>
                                                <div class="col-sm-10">
                                                    <input class="datepicker-input form-control " type="text" value="12-02-2013" data-date-format="dd-mm-yyyy" >
                                                </div>
                                            </div>
                                        </div>
                                    </section>
                                </section>

                                <%--End of upper table--%>

                                <%--Lower table--%>

                                <section class="panel panel-default">
                                    <div class="row wrapper">
                                        <div class="col-sm-4 m-b-xs">
                                            <select class="input-sm form-control input-s-sm inline v-middle">
                                                <option value="0">Action</option>
                                                <option value="1">Release Locks</option>
                                            </select>
                                            <button class="btn btn-sm btn-default">Apply</button>
                                        </div>
                                        <div class="col-sm-5 m-b-xs"> </div>
                                        <div class="col-sm-3"> </div>
                                    </div>
                                    <div class="table-responsive">
                                        <table class="table table-striped b-t b-light">
                                            <thead>
                                            <tr>
                                                <th width="20"><label class="checkbox m-n i-checks">
                                                    <input type="checkbox">
                                                    <i></i></label></th>
                                                <th>Type</th>
                                                <th class="th-sortable" data-toggle="class">ID <span class="th-sort"> <i class="fa fa-sort-down text"></i> <i class="fa fa-sort-up text-active"></i> <i class="fa fa-sort"></i> </span> </th>
                                                <th class="th-sortable" data-toggle="class">Title <span class="th-sort"> <i class="fa fa-sort-down text"></i> <i class="fa fa-sort-up text-active"></i> <i class="fa fa-sort"></i> </span> </th>
                                                <th class="th-sortable" data-toggle="class">User <span class="th-sort"> <i class="fa fa-sort-down text"></i> <i class="fa fa-sort-up text-active"></i> <i class="fa fa-sort"></i> </span> </th>
                                                <th>Locked On</th>
                                                <th width="120">Release Lock</th>
                                            </tr>
                                            </thead>
                                            <tbody>
                                            <tr>
                                                <td><label class="checkbox m-n i-checks">
                                                    <input type="checkbox" name="post[]">
                                                    <i></i></label></td>
                                                <td>Complaint</td>
                                                <td>123456</td>
                                                <td>Title example</td>
                                                <td>Username</td>
                                                <td>4/23/2014 12:00:00</td>
                                                <td><a href="#" class="active" data-toggle="class"><i class="fa fa-lock text-danger text-active"> Unlock</i><i class="fa fa-unlock text-success text"> Lock</i> </a></td>
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

                                <%--End of lower table--%>

                            </section>
                        </section>
                    </aside>
                </section>
            </section>
        </section>
    </jsp:body>
</t:layout>


