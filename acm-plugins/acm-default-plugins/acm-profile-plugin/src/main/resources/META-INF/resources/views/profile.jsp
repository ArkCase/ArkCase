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
    <script type="text/javascript" src="<c:url value='/resources/js/profile/profileObject.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/resources/js/profile/profileEvent.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/resources/js/profile/profilePage.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/resources/js/profile/profileRule.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/resources/js/profile/profileService.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/resources/js/profile/profileCallback.js'/>"></script>

    <script type="text/javascript" src="<c:url value='/'/>resources/vendors/${vd_slimscroll}/${js_slimscroll}"></script>
</jsp:attribute>

<jsp:body>
<section id="content">
<section class="vbox">

<header class="header m-b-xs b-light hidden-print">
    <a href="editProfile.html" class="btn btn-sm btn-info pull-right">Edit</a>
    <h3 class="m-b-xs text-black"><spring:message code="profile.page.descShort" text="User Profile" /></h3>
</header>


<section class="scrollable">












<section class="hbox stretch">
<aside class="aside-lg bg-light lter b-r">
    <section class="vbox">



        <section class="scrollable">
            <div class="wrapper">
                <section class="panel no-border bg-gradient lt">
                    <div class="panel-body">
                        <div class="row m-t-xl">
                            <div class="col-xs-3 text-right padder-v">
                                <a href="#" class="btn btn-primary btn-icon btn-rounded m-t-xl"><i class="i i-mail2"></i></a>
                            </div>
                            <div class="col-xs-6 text-center">
                                <div class="inline">
                                    <div class="thumb-lg avatar">
                                        <img src="<c:url value='/'/>resources/vendors/${acm_theme}/images/ann.jpg" class="dker">
                                    </div>
                                    <div class="h4 m-t m-b-xs font-bold text-lt">Ann Administrator</div>
                                    <small class="text-muted m-b">Agent</small>

                                </div>
                            </div>






                            <div class="col-xs-3 padder-v">
                                <a href="#" class="btn btn-primary btn-icon btn-rounded m-t-xl" data-toggle="class:btn-danger">
                                    <i class="i i-phone text"></i>
                                    <i class="i i-phone2 text-active"></i>
                                </a>
                            </div>



                        </div>
                    </div>
                    <footer class="panel-footer dk text-center no-border">
                        <div class="row pull-out">
                            <div class="col-xs-3 dker">
                                <div class="padder-v">
                                    <span class="m-b-xs h3 block text-black">0</span>
                                    <small class="text-muted">Complaints</small>
                                </div>
                            </div>
                            <div class="col-xs-3">
                                <div class="padder-v">
                                    <span class="m-b-xs h3 block text-black">0</span>
                                    <small class="text-muted">Cases</small>
                                </div>
                            </div>
                            <div class="col-xs-3 dker">
                                <div class="padder-v">
                                    <span class="m-b-xs h3 block text-black">0</span>
                                    <small class="text-muted">Documents</small>
                                </div>
                            </div>
                            <div class="col-xs-3">
                                <div class="padder-v">
                                    <span class="m-b-xs h3 block text-black">0</span>
                                    <small class="text-muted">Tasks</small>
                                </div>
                            </div>
                        </div>
                    </footer>
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
                    <h4 class="padder">About</h4>



                    <ul class="list-group">
                        <li class="list-group-item">

                            <div class="row">
                                <div class="col-xs-6">

                                    <h4>Ann Administrator</h4>
                                    <small>
                                        Agent
                                    </small>
                                </div>
                                <div class="col-xs-6">
                                    <h4>Pentagon</h4>

                                </div>
                            </div>








                        </li>
                    </ul>
                </section>

                <section class="panel panel-default">
                    <h4 class="padder">Contact Information</h4>
                    <ul class="list-group">
                        <li class="list-group-item">




                            <div class="row">
                                <div class="col-xs-6">

                                    <h4>ann-acm@armedia.com</h4>
                                    <small>
                                        E-mail Address
                                    </small>
                                    <h4>ann-acm</h4>
                                    <small>
                                        Skype
                                    </small>
                                </div>
                                <div class="col-xs-6">
                                    <h4>555-555-5555</h4>
                                    <small>
                                        Office Phone
                                    </small>
                                    <h4>555-555-5555</h4>
                                    <small>
                                        Mobile Phone
                                    </small>
                                </div>
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

                                    <h4>Armedia</h4>
                                    <small>
                                        Company Name
                                    </small>
                                    <h4>1234 Street Name</h4>
                                    <small>
                                        Address 1
                                    </small>
                                    <h4>Suite 123</h4>
                                    <small>
                                        Address 2
                                    </small>
                                    <h4>Vienna</h4>
                                    <small>
                                        City
                                    </small>
                                    <h4>VA</h4>
                                    <small>
                                        State
                                    </small>
                                    <h4>12345</h4>
                                    <small>
                                        ZIP
                                    </small>

                                </div>
                                <div class="col-xs-6">
                                    <h4>555-555-5555</h4>
                                    <small>
                                        Main Office Phone
                                    </small>
                                    <h4>555-555-5555</h4>
                                    <small>
                                        Fax
                                    </small>

                                    <h4><a href="http://www.armedia.com">www.armedia.com</a></h4>
                                    <small>
                                        Website
                                    </small>
                                </div>
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


