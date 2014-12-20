<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<t:layout>
<jsp:attribute name="endOfHead">
    <title><spring:message code="profile.page.title" text="Profile | ACM | Armedia Case Management" /></title>
</jsp:attribute>


<jsp:attribute name="endOfBody">
    <script type="text/javascript" src="<c:url value='/resources/js/profile/profile.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/resources/js/profile/profileModel.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/resources/js/profile/profileView.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/resources/js/profile/profileController.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/resources/js/profile/profileService.js'/>"></script>

    <script type="text/javascript" src="<c:url value='/'/>resources/vendors/${vd_slimscroll}/${js_slimscroll}"></script>

    <link rel="stylesheet" href="<c:url value='/'/>resources/vendors/${vd_acm}/themes/basic/${vd_x_editable}/css/bootstrap-editable.css" type="text/css"/>
    <script src="<c:url value='/'/>resources/vendors/${vd_x_editable}/js/${js_x_editable}"></script>

    <link rel="stylesheet" href="<c:url value='/'/>resources/vendors/${vd_acm}/themes/basic/${vd_jtable}/blue/jtable.css" type="text/css"/>
    <script type="text/javascript" src="<c:url value='/'/>resources/vendors/${vd_jtable}/${js_jtable}"></script>

</jsp:attribute>

<jsp:body>
    <section id="content">
        <section class="vbox">
            <header class="header m-b-xs b-light hidden-print">
                <h3 class="m-b-xs text-black">User Profile</h3>
            </header>
            <section class="scrollable">
                <section class="hbox stretch">
                    <aside class="aside-lg bg-light lter b-r">
                        <section class="vbox">
                            <section class="scrollable">
                                <div class="wrapper">
                                    <section class="panel no-border bg-gradient lt">
                                        <div class="panel-body">
                                            <div class="row">

                                                <div class="col-xs-6 text-center">

                                                    <div class="thumb-lg avatar"> <img id="picture" src="" default="<c:url value='/'/>resources/vendors/${acm_theme}/images/nopic.png" class="dker"></div>

                                                    <div>
                                                        <a id="lnkChangePicture" href="#">&nbsp;&nbsp;&nbsp;&nbsp;<u>Change Picture</u></a>
                                                        <img id="picLoading" src="<c:url value='/'/>resources/vendors/${acm_theme}/images/ajax-loader.gif" class="dker" style="display:none;">
                                                        <form id="formPicture" style="display:none;">
                                                            <input type="file" id="file" name="file">
                                                                <%--<input type="submit">--%>
                                                        </form>
                                                    </div>
                                                </div>
                                                <div class="col-xs-6">

                                                    <h4 id="fullName"></h4>
                                                    <h4><a href="#" id="title" data-type="text" data-pk="1" data-title="Enter Title"></a></h4>
                                                        <%--<small> Agent </small>--%>


                                                    <h4 id="email"></h4>
                                                    <small> E-mail Address </small>
                                                </div>
                                            </div>
                                        </div>
                                    </section>

                                    <section class="panel panel-default bg-gradient">
                                        <h4 class="padder">Groups</h4>
                                        <ul class="list-group">
                                            <li class="list-group-item">
                                                <div class="row">
                                                    <div class="col-xs-12" id="groups">

                                                            <%--<span class="btn-rounded btn-sm btn-info"> Richmond Field Office</span>--%>
                                                            <%--<span class="btn-rounded btn-sm btn-info"> Internal Affairs</span>--%>
                                                            <%--<span class="btn-rounded btn-sm btn-info"> Forensic Accountants</span>--%>
                                                            <%--<span class="btn-rounded btn-sm btn-info"> Group 4</span>--%>
                                                            <%--<span class="btn-rounded btn-sm btn-info"> Group 5</span>--%>



                                                    </div>
                                                </div>
                                            </li>
                                        </ul>
                                    </section>


                                    <section class="panel panel-default bg-gradient">


                                            <%--<h4 class="padder">Subscriptions</h4>--%>

                                            <%--<table class="table table-striped b-light">--%>
                                            <%--<thead>--%>
                                            <%--<tr>--%>

                                            <%--<th class="th-sortable" data-toggle="class">Type</th>--%>
                                            <%--<th>ID</th>--%>
                                            <%--<th>Title</th>--%>
                                            <%--<th>Date</th>--%>
                                            <%--<th width="30"></th>--%>
                                            <%--</tr>--%>
                                            <%--</thead>--%>
                                            <%--<tbody>--%>
                                            <%--<tr>--%>

                                            <%--<td>[Object]</td>--%>
                                            <%--<td>[ID]</td>--%>
                                            <%--<td>[Title]</td>--%>
                                            <%--<td>MM/DD/YYYY</td>--%>

                                            <%--<td><a href="#" class="active" data-toggle="class"><i class="fa fa-times text-danger text-active"></i><i class="fa  fa-check-circle text-success text"></i></a></td>--%>
                                            <%--</tr>--%>
                                            <%--</tr>--%>

                                            <%--</tbody>--%>
                                            <%--</table>--%>


                                            <%--<footer class="panel-footer">--%>
                                            <%--<div class="row">--%>
                                            <%--<div class="col-sm-5 hidden-xs"> <small class="text-muted inline m-t-sm m-b-sm">Showing 1-50 of 50 items</small> </div>--%>
                                            <%--<div class="col-sm-7 text-right text-center-xs">--%>
                                            <%--<ul class="pagination pagination-sm m-t-none m-b-none">--%>
                                            <%--<li><a href="#"><i class="fa fa-chevron-left"></i></a></li>--%>
                                            <%--<li><a href="#">1</a></li>--%>
                                            <%--<li><a href="#">2</a></li>--%>
                                            <%--<li><a href="#">3</a></li>--%>
                                            <%--<li><a href="#"><i class="fa fa-chevron-right"></i></a></li>--%>
                                            <%--</ul>--%>
                                            <%--</div>--%>
                                            <%--</div>--%>
                                            <%--</footer>--%>

                                        <div id="divSubscriptions" style="width:100%"></div>
                                    </section>


                                </div>
                            </section>
                        </section>
                    </aside>
                    <aside class="col-lg-4 b-l no-padder">
                        <section class="vbox">
                            <section class="scrollable">
                                <div class="wrapper">
                                    <section class="panel panel-default">
                                        <h4 class="padder">Contact Information</h4>
                                        <ul class="list-group">
                                            <li class="list-group-item">
                                                <div class="row">
                                                    <div class="col-xs-6">
                                                        <h4><a href="#" id="location" data-type="text" data-pk="1" data-title="Enter Location"></a></h4>
                                                        <small>Location </small>

                                                        <h4><a href="#" id="imaccount" data-type="text" data-pk="1" data-title="Enter IM Account"></a> (<a href="#" id="imsystem" data-type="text" data-pk="1" data-title="Enter IM System"></a>)</h4>
                                                        <small> IM Account </small> </div>
                                                    <div class="col-xs-6">
                                                        <h4><a href="#" id="officephone" data-type="text" data-pk="1" data-title="Enter Office Phone Number"></a></h4>
                                                        <small> Office Phone </small>
                                                        <h4><a href="#" id="mobilephone" data-type="text" data-pk="1" data-title="Enter Mobile Phone Number"></a></h4>
                                                        <small> Mobile Phone </small> </div>
                                                </div>
                                            </li>
                                        </ul>
                                    </section>
                                    <section class="panel panel-default">
                                        <h4 class="padder">Company Details</h4>
                                        <ul class="list-group">
                                            <li class="list-group-item">
                                                <div class="row">
                                                    <div class="col-xs-6">
                                                        <h4><a href="#" id="company" data-type="text" data-pk="1" data-title="Enter Company Name"></a></h4>
                                                        <small> Company Name </small>
                                                        <h4><a href="#" id="street" data-type="text" data-pk="1" data-title="Enter Street"></a></h4>
                                                        <small> Address 1 </small>
                                                        <h4><a href="#" id="address2" data-type="text" data-pk="1" data-title="Enter Address 2"></a></h4>
                                                        <small> Address 2 </small>
                                                        <h4><a href="#" id="city" data-type="text" data-pk="1" data-title="Enter City"></a></h4>
                                                        <small> City </small>
                                                        <h4><a href="#" id="state" data-type="text" data-pk="1" data-title="Enter State"></a></h4>
                                                        <small> State </small>
                                                        <h4><a href="#" id="zip" data-type="text" data-pk="1" data-title="Enter ZIP"></a></h4>
                                                        <small> ZIP </small> </div>
                                                    <div class="col-xs-6">
                                                        <h4><a href="#" id="mainphone" data-type="text" data-pk="1" data-title="Enter Main Office Phone"></a></h4>
                                                        <small> Main Office Phone </small>
                                                        <h4><a href="#" id="fax" data-type="text" data-pk="1" data-title="Enter Main Office Phone"></a></h4>
                                                        <small> Fax </small>
                                                        <h4><a href="#" id="website" data-type="text" data-pk="1" data-title="Enter Website Address"></a></h4>
                                                        <small> Website </small> </div>
                                                </div>
                                            </li>
                                        </ul>
                                    </section>
                                    <section class="panel panel-default"></section>
                                </div>
                            </section>
                        </section>
                    </aside>
                </section>
            </section>
        </section>
    </section>
</jsp:body>
</t:layout>


