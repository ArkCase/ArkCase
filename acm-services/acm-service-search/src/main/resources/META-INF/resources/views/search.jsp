<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<t:layout>
<jsp:attribute name="endOfHead">
    <title><spring:message code="search.page.title" text="Search | ACM | ArkCase" /></title>
    <div id="searchData" itemscope="true" style="display: none">
        <span itemprop="resourceNamespace">search</span>
        <%--<span itemprop="objectTypes">${objectTypes}</span>--%>
    </div>
</script>
</jsp:attribute>


<jsp:attribute name="endOfBody">
    <script type="text/javascript" src="<c:url value='/resources/js/search/search.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/resources/js/search/searchModel.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/resources/js/search/searchService.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/resources/js/search/searchView.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/resources/js/search/searchController.js'/>"></script>

    <script type="text/javascript" src="<c:url value='/resources/vendors/${vd_slimscroll}/jquery.slimscroll.min.js'/>"></script>

    <!-- JTable -->
    <link rel="stylesheet" href="<c:url value='/resources/vendors/${vd_acm}/themes/basic/${vd_jtable}/blue/jtable.css'/>" type="text/css"></link>
    <script type="text/javascript" src="<c:url value='/resources/vendors/${vd_jtable}/${js_jtable}'/>"></script>
</jsp:attribute>

<jsp:body>
    <header class="header bg-gradient b-b clearfix">
        <div class="row m-t-sm">
            <div class="col-md-12 m-b-sm">
                <div class="input-group">
                    <input type="text" class="input-md form-control" id="searchQuery" data-i18n="[placeholder]search:label.input-search" placeholder="Type in your search query to find complaints, cases, tasks, and documents.">
                  <span class="input-group-btn">
                  <button class="btn btn-md btn-default" type="button" data-i18n="search:button.go">Go!</button>
                  </span> </div>
            </div>
        </div>
    </header>

    <section class="hbox stretchSearch">
        <aside class="aside-md bg-light dker b-r" id="subNav">
            <section class="vbox">
                <section class="scrollable">



                    <div class="wrapper facets" id="divFacets">

                    <%--<div name="facet_fields">--%>



                        <%--<h6>Create User</h6>--%>
                        <%--<div class="list-group" name="Create User">--%>
                            <%--<label class="list-group-item"><input type="checkbox" />--%>
                                <%--<span class="badge bg-info">5</span>--%>
                                <%--sally-acm--%>
                            <%--</label>--%>
                            <%--<label class="list-group-item"><input type="checkbox" />--%>
                                <%--<span class="badge bg-info">2</span>--%>
                                <%--ann-acm--%>
                            <%--</label>--%>

                        <%--</div>--%>



                        <%--<h6>Person, Organization Type</h6>--%>
                        <%--<div class="list-group auto">--%>
                            <%--<label class="list-group-item"><input type="checkbox" />--%>
                                <%--<span class="badge bg-info">2</span>--%>
                                <%--initiator--%>
                            <%--</label>--%>


                        <%--</div>--%>



                    <%--</div>--%>
                        <%--<div name="facet_dates">--%>




                        <%--<h6>Object Type</h6>--%>
                        <%--<div class="list-group auto">--%>
                            <%--<label class="list-group-item"><input type="checkbox" />--%>
                                <%--<span class="badge bg-info">6</span>--%>
                                <%--TASK--%>
                            <%--</label>--%>

                            <%--<label class="list-group-item"><input type="checkbox" />--%>
                                <%--<span class="badge bg-info">2</span>--%>
                                <%--PERSON-ASSOCIATION--%>
                            <%--</label>--%>
                        <%--</div>--%>



                        <%--<h6>Assignee Full Name</h6>--%>
                        <%--<div class="list-group auto">--%>
                            <%--<label class="list-group-item"><input type="checkbox" />--%>
                                <%--<span class="badge bg-info">6</span>--%>
                                <%--sally supervisor--%>
                            <%--</label>--%>

                            <%--<label class="list-group-item"><input type="checkbox" />--%>
                                <%--<span class="badge bg-info">2</span>--%>
                                <%--ann administrator--%>
                            <%--</label>--%>
                        <%--</div>--%>



                        <%--<h6>Priority</h6>--%>
                        <%--<div class="list-group ">--%>
                            <%--<label class="list-group-item"><input type="checkbox" />--%>
                                <%--<span class="badge bg-info">6</span>--%>
                                <%--medium--%>
                            <%--</label>--%>


                        <%--</div>--%>
                    <%--</div>--%>

                    <%--<div>--%>


                        <%--<h6>Create User</h6>--%>
                        <%--<div class="list-group ">--%>
                            <%--<label class="list-group-item"><input type="checkbox" />--%>
                                <%--<span class="badge bg-info">5</span>--%>
                                <%--sally-acm--%>
                            <%--</label>--%>
                            <%--<label class="list-group-item"><input type="checkbox" />--%>
                                <%--<span class="badge bg-info">2</span>--%>
                                <%--ann-acm--%>
                            <%--</label>--%>

                        <%--</div>--%>






                        <%--<h6>Person, Organization Type</h6>--%>
                        <%--<div class="list-group auto">--%>
                            <%--<label class="list-group-item"><input type="checkbox" />--%>
                                <%--<span class="badge bg-info">2</span>--%>
                                <%--initiator--%>
                            <%--</label>--%>


                        <%--</div>--%>







                        <%--<h6>Object Type</h6>--%>
                        <%--<div class="list-group auto">--%>
                            <%--<label class="list-group-item"><input type="checkbox" />--%>
                                <%--<span class="badge bg-info">6</span>--%>
                                <%--TASK--%>
                            <%--</label>--%>

                            <%--<label class="list-group-item"><input type="checkbox" />--%>
                                <%--<span class="badge bg-info">2</span>--%>
                                <%--PERSON-ASSOCIATION--%>
                            <%--</label>--%>
                        <%--</div>--%>



                        <%--<h6>Assignee Full Name</h6>--%>
                        <%--<div class="list-group auto">--%>
                            <%--<label class="list-group-item"><input type="checkbox" />--%>
                                <%--<span class="badge bg-info">6</span>--%>
                                <%--sally supervisor--%>
                            <%--</label>--%>

                            <%--<label class="list-group-item"><input type="checkbox" />--%>
                                <%--<span class="badge bg-info">2</span>--%>
                                <%--ann administrator--%>
                            <%--</label>--%>
                        <%--</div>--%>



                        <%--<h6>Priority</h6>--%>
                        <%--<div class="list-group ">--%>
                            <%--<label class="list-group-item"><input type="checkbox" />--%>
                                <%--<span class="badge bg-info">6</span>--%>
                                <%--medium--%>
                            <%--</label>--%>


                        <%--</div>--%>
                    <%--</div>--%>

                    </div>




                </section>
            </section>
        </aside>


        <aside>
            <section class="vbox">
                <section class="scrollable wrapper w-f">
                    <section class="panel panel-default">
                        <div id="divResults" style="width:100%"></div>
                    </section>
                </section>
            </section>
        </aside>


        <%--<aside>--%>
            <%--<section class="vbox">--%>

                <%--<section class="scrollable wrapper w-f">--%>


                    <%--<section class="panel panel-default">--%>
                        <%--<div class="row wrapper">--%>
                            <%--<div class="col-md-10">--%>
                                <%--<p>Showing 1-14 of 34,835 results for "Search Query"</p>--%>
                            <%--</div>--%>

                            <%--<div class="col-md-2 text-right">--%>
                                <%--<a href="#" class="btn btn-primary">Export</a>--%>
                            <%--</div>--%>
                        <%--</div>--%>
                        <%--<div class="table-responsive">--%>
                            <%--<table class="table table-striped b-t b-light">--%>
                                <%--<thead>--%>
                                <%--<tr>--%>
                                    <%--<th class="th-sortable" data-toggle="class">Type <span class="th-sort"> <i class="fa fa-sort-down text"></i> <i class="fa fa-sort-up text-active"></i> <i class="fa fa-sort"></i> </span> </th>--%>
                                    <%--<th class="th-sortable" data-toggle="class">ID <span class="th-sort"> <i class="fa fa-sort-down text"></i> <i class="fa fa-sort-up text-active"></i> <i class="fa fa-sort"></i> </span> </th>--%>
                                    <%--<th class="th-sortable" data-toggle="class">Title <span class="th-sort"> <i class="fa fa-sort-down text"></i> <i class="fa fa-sort-up text-active"></i> <i class="fa fa-sort"></i> </span> </th>--%>
                                    <%--<th class="th-sortable" data-toggle="class">Created <span class="th-sort"> <i class="fa fa-sort-down text"></i> <i class="fa fa-sort-up text-active"></i> <i class="fa fa-sort"></i> </span> </th>--%>
                                    <%--<th class="th-sortable" data-toggle="class">Status <span class="th-sort"> <i class="fa fa-sort-down text"></i> <i class="fa fa-sort-up text-active"></i> <i class="fa fa-sort"></i> </span> </th>--%>
                                <%--</tr>--%>
                                <%--</thead>--%>
                                <%--<tbody>--%>
                                <%--<tr>--%>
                                    <%--<td>[Type]</td>--%>
                                    <%--<td>[ID]</td>--%>
                                    <%--<td>[Title]</td>--%>
                                    <%--<td>[Created]</td>--%>
                                    <%--<td>[Status]</td>--%>
                                <%--</tr>--%>

                                <%--</tbody>--%>
                            <%--</table>--%>
                        <%--</div>--%>

                    <%--</section>--%>
                <%--</section>--%>
                <%--<footer class="footer bg-white b-t">--%>
                    <%--<div class="row text-center-xs">--%>
                        <%--<div class="col-md-6 hidden-sm">--%>
                            <%--<p class="text-muted m-t">Showing 20-30 of 50</p>--%>
                        <%--</div>--%>
                        <%--<div class="col-md-6 col-sm-12 text-right text-center-xs">--%>
                            <%--<ul class="pagination pagination-sm m-t-sm m-b-none">--%>
                                <%--<li><a href="#"><i class="fa fa-chevron-left"></i></a></li>--%>
                                <%--<li class="active"><a href="#">1</a></li>--%>
                                <%--<li><a href="#">2</a></li>--%>
                                <%--<li><a href="#">3</a></li>--%>
                                <%--<li><a href="#">4</a></li>--%>
                                <%--<li><a href="#">5</a></li>--%>
                                <%--<li><a href="#"><i class="fa fa-chevron-right"></i></a></li>--%>
                            <%--</ul>--%>
                        <%--</div>--%>
                    <%--</div>--%>
                <%--</footer>--%>
            <%--</section>--%>
        <%--</aside>--%>
    </section>
</jsp:body>
</t:layout>


