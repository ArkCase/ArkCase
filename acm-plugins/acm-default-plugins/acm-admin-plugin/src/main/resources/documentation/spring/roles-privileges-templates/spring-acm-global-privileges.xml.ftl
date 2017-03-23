<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans-4.1.xsd">

    <!-- global privilege for URLs that should be public to anyone -->
    <bean id="acmGlobalAllowPrivilege" class="com.armedia.acm.pluginmanager.model.AcmPluginPrivilege">
        <property name="privilegeName" value="acm-global-allow"/>
        <property name="applicationRolesWithPrivilege">
            <list>
            <#if acmGlobalAllowPrivilege??>
                <#list acmGlobalAllowPrivilege as role>
                    <value>${role}</value>
                </#list>
            </#if>
            </list>
        </property>
    </bean>

    <!--Audit Plugin-->
    <bean id="acmAuditModulePrivilege" class="com.armedia.acm.pluginmanager.model.AcmPluginPrivilege">
        <property name="privilegeName" value="acm-audit-module"/>
        <property name="applicationRolesWithPrivilege">
            <list>
            <#if acmAuditModulePrivilege?? >
                <#list acmAuditModulePrivilege as role>
                    <value>${role}</value>
                </#list>
            </#if>
            </list>
        </property>
    </bean>

    <bean id="auditPrivilege" class="com.armedia.acm.pluginmanager.model.AcmPluginPrivilege">
        <property name="privilegeName" value="acm-audit"/>
        <property name="applicationRolesWithPrivilege">
            <list>
            <#if auditPrivilege?? >
                <#list auditPrivilege as role>
                    <value>${role}</value>
                </#list>
            </#if>
            </list>
        </property>
    </bean>
    <bean id="auditReadEventsForObjectPrivilege" class="com.armedia.acm.pluginmanager.model.AcmPluginPrivilege">
        <property name="privilegeName" value="acm-audit-read-events-for-object"/>
        <property name="applicationRolesWithPrivilege">
            <list>
            <#if auditReadEventsForObjectPrivilege??>
                <#list auditReadEventsForObjectPrivilege as role>
                    <value>${role}</value>
                </#list>
            </#if>
            </list>
        </property>
    </bean>

    <!--Correspondence Generation Plugin-->
    <bean id="acmGenerateCorrespondencePrivilege" class="com.armedia.acm.pluginmanager.model.AcmPluginPrivilege">
        <property name="privilegeName" value="acm-generate-correspondence"/>
        <property name="applicationRolesWithPrivilege">
            <list>
            <#if acmGenerateCorrespondencePrivilege??>
                <#list acmGenerateCorrespondencePrivilege as role>
                    <value>${role}</value>
                </#list>
            </#if>
            </list>
        </property>
    </bean>

    <!--Dashboard Plugin-->
    <bean id="acmDashboardModulePrivilege" class="com.armedia.acm.pluginmanager.model.AcmPluginPrivilege">
        <property name="privilegeName" value="acm-dashboard-module"/>
        <property name="applicationRolesWithPrivilege">
            <list>
            <#if acmDashboardModulePrivilege?? >
                <#list acmDashboardModulePrivilege as role>
                    <value>${role}</value>
                </#list>
            </#if>
            </list>
        </property>
    </bean>

    <bean id="dashboardPrivilege" class="com.armedia.acm.pluginmanager.model.AcmPluginPrivilege">
        <property name="privilegeName" value="acm-dashboard"/>
        <property name="applicationRolesWithPrivilege">
            <list>
            <#if dashboardPrivilege??>
                <#list dashboardPrivilege as role>
                    <value>${role}</value>
                </#list>
            </#if>
            </list>
        </property>
    </bean>


    <!--ECM File Plugin-->
    <bean id="filePrivilege" class="com.armedia.acm.pluginmanager.model.AcmPluginPrivilege">
        <property name="privilegeName" value="acm-file-download"/>
        <property name="applicationRolesWithPrivilege">
            <list>
            <#if filePrivilege??>
                <#list filePrivilege as role>
                    <value>${role}</value>
                </#list>
            </#if>
            </list>
        </property>
    </bean>

    <!--Participant Plugin-->
    <bean id="participantPrivilege" class="com.armedia.acm.pluginmanager.model.AcmPluginPrivilege">
        <property name="privilegeName" value="acm-participant"/>
        <property name="applicationRolesWithPrivilege">
            <list>
            <#if participantPrivilege??>
                <#list participantPrivilege as role>
                    <value>${role}</value>
                </#list>
            </#if>
            </list>
        </property>
    </bean>

    <!--armediaBlogPlugin-->
    <bean id="blogPrivilege" class="com.armedia.acm.pluginmanager.model.AcmPluginPrivilege">
        <property name="privilegeName" value="acm-blog"/>
        <property name="applicationRolesWithPrivilege">
            <list>
            <#if blogPrivilege??>
                <#list blogPrivilege as role>
                    <value>${role}</value>
                </#list>
            </#if>
            </list>
        </property>
    </bean>

    <!--helloWorldPlugin-->
    <bean id="helloWorldPrivilege" class="com.armedia.acm.pluginmanager.model.AcmPluginPrivilege">
        <property name="privilegeName" value="acm-hello-world"/>
        <property name="applicationRolesWithPrivilege">
            <list>
            <#if helloWorldPrivilege??>
                <#list helloWorldPrivilege as role>
                    <value>${role}</value>
                </#list>
            </#if>
            </list>
        </property>
    </bean>

    <!--Profile Plugin-->
    <bean id="profilePrivilege" class="com.armedia.acm.pluginmanager.model.AcmPluginPrivilege">
        <property name="privilegeName" value="acm-profile"/>
        <property name="applicationRolesWithPrivilege">
            <list>
            <#if profilePrivilege??>
                <#list profilePrivilege as role>
                    <value>${role}</value>
                </#list>
            </#if>
            </list>
        </property>
    </bean>

    <!--Report Plugin-->
    <bean id="acmReportModulePrivilege" class="com.armedia.acm.pluginmanager.model.AcmPluginPrivilege">
        <property name="privilegeName" value="acm-report-module"/>
        <property name="applicationRolesWithPrivilege">
            <list>
            <#if acmReportModulePrivilege?? >
                <#list acmReportModulePrivilege as role>
                    <value>${role}</value>
                </#list>
            </#if>
            </list>
        </property>
    </bean>

    <bean id="reportPrivilege" class="com.armedia.acm.pluginmanager.model.AcmPluginPrivilege">
        <property name="privilegeName" value="acm-report"/>
        <property name="applicationRolesWithPrivilege">
            <list>
            <#if reportPrivilege??>
                <#list reportPrivilege as role>
                    <value>${role}</value>
                </#list>
            </#if>
            </list>
        </property>
    </bean>

    <!--Cost Tracking Service Plugin-->
    <bean id="acmCostModulePrivilege" class="com.armedia.acm.pluginmanager.model.AcmPluginPrivilege">
        <property name="privilegeName" value="acm-cost-module"/>
        <property name="applicationRolesWithPrivilege">
            <list>
            <#if acmCostModulePrivilege?? >
                <#list acmCostModulePrivilege as role>
                    <value>${role}</value>
                </#list>
            </#if>
            </list>
        </property>
    </bean>

    <bean id="costsheetCreatePrivilege" class="com.armedia.acm.pluginmanager.model.AcmPluginPrivilege">
        <property name="privilegeName" value="acm-costsheet-create"/>
        <property name="applicationRolesWithPrivilege">
            <list>
            <#if costsheetCreatePrivilege??>
                <#list costsheetCreatePrivilege as role>
                    <value>${role}</value>
                </#list>
            </#if>
            </list>
        </property>
    </bean>
    <bean id="costsheetApprovePrivilege" class="com.armedia.acm.pluginmanager.model.AcmPluginPrivilege">
        <property name="privilegeName" value="acm-costsheet-approve"/>
        <property name="applicationRolesWithPrivilege">
            <list>
            <#if costsheetApprovePrivilege??>
                <#list costsheetApprovePrivilege as role>
                    <value>${role}</value>
                </#list>
            </#if>
            </list>
        </property>
    </bean>
    <bean id="costsheetPrivilege" class="com.armedia.acm.pluginmanager.model.AcmPluginPrivilege">
        <property name="privilegeName" value="acm-costsheet"/>
        <property name="applicationRolesWithPrivilege">
            <list>
            <#if costsheetPrivilege??>
                <#list costsheetPrivilege as role>
                    <value>${role}</value>
                </#list>
            </#if>
            </list>
        </property>
    </bean>

    <!--Time Tracking Service Plugin-->
    <bean id="acmTimeModulePrivilege" class="com.armedia.acm.pluginmanager.model.AcmPluginPrivilege">
        <property name="privilegeName" value="acm-time-module"/>
        <property name="applicationRolesWithPrivilege">
            <list>
            <#if acmTimeModulePrivilege?? >
                <#list acmTimeModulePrivilege as role>
                    <value>${role}</value>
                </#list>
            </#if>
            </list>
        </property>
    </bean>

    <bean id="timesheetCreatePrivilege" class="com.armedia.acm.pluginmanager.model.AcmPluginPrivilege">
        <property name="privilegeName" value="acm-timesheet-create"/>
        <property name="applicationRolesWithPrivilege">
            <list>
            <#if timesheetCreatePrivilege??>
                <#list timesheetCreatePrivilege as role>
                    <value>${role}</value>
                </#list>
            </#if>
            </list>
        </property>
    </bean>
    <bean id="timesheetApprovePrivilege" class="com.armedia.acm.pluginmanager.model.AcmPluginPrivilege">
        <property name="privilegeName" value="acm-timesheet-approve"/>
        <property name="applicationRolesWithPrivilege">
            <list>
            <#if timesheetApprovePrivilege??>
                <#list timesheetApprovePrivilege as role>
                    <value>${role}</value>
                </#list>
            </#if>
            </list>
        </property>
    </bean>
    <bean id="timesheetPrivilege" class="com.armedia.acm.pluginmanager.model.AcmPluginPrivilege">
        <property name="privilegeName" value="acm-timesheet"/>
        <property name="applicationRolesWithPrivilege">
            <list>
            <#if timesheetPrivilege??>
                <#list timesheetPrivilege as role>
                    <value>${role}</value>
                </#list>
            </#if>
            </list>
        </property>
    </bean>

    <!--Tag Plugin-->
    <bean id="acmTagModulePrivilege" class="com.armedia.acm.pluginmanager.model.AcmPluginPrivilege">
        <property name="privilegeName" value="acm-tag-module"/>
        <property name="applicationRolesWithPrivilege">
            <list>
            <#if acmTagModulePrivilege?? >
                <#list acmTagModulePrivilege as role>
                    <value>${role}</value>
                </#list>
            </#if>
            </list>
        </property>
    </bean>

    <bean id="tagPrivilege" class="com.armedia.acm.pluginmanager.model.AcmPluginPrivilege">
        <property name="privilegeName" value="acm-tag"/>
        <property name="applicationRolesWithPrivilege">
            <list>
            <#if tagPrivilege??>
                <#list tagPrivilege as role>
                    <value>${role}</value>
                </#list>
            </#if>
            </list>
        </property>
    </bean>

    <!--Task Plugin-->
    <bean id="acmTaskModulePrivilege" class="com.armedia.acm.pluginmanager.model.AcmPluginPrivilege">
        <property name="privilegeName" value="acm-task-module"/>
        <property name="applicationRolesWithPrivilege">
            <list>
            <#if acmTaskModulePrivilege?? >
                <#list acmTaskModulePrivilege as role>
                    <value>${role}</value>
                </#list>
            </#if>
            </list>
        </property>
    </bean>

    <bean id="acmTaskListPrivilege" class="com.armedia.acm.pluginmanager.model.AcmPluginPrivilege">
        <property name="privilegeName" value="acm-task-list"/>
        <property name="applicationRolesWithPrivilege">
            <list>
            <#if acmTaskListPrivilege??>
                <#list acmTaskListPrivilege as role>
                    <value>${role}</value>
                </#list>
            </#if>
            </list>
        </property>
    </bean>
    <bean id="acmTaskSavePrivilege" class="com.armedia.acm.pluginmanager.model.AcmPluginPrivilege">
        <property name="privilegeName" value="acm-task-save"/>
        <property name="applicationRolesWithPrivilege">
            <list>
            <#if acmTaskSavePrivilege??>
                <#list acmTaskSavePrivilege as role>
                    <value>${role}</value>
                </#list>
            </#if>
            </list>
        </property>
    </bean>
    <bean id="acmTaskCreatePrivilege" class="com.armedia.acm.pluginmanager.model.AcmPluginPrivilege">
        <property name="privilegeName" value="acm-task-create"/>
        <property name="applicationRolesWithPrivilege">
            <list>
            <#if acmTaskCreatePrivilege??>
                <#list acmTaskCreatePrivilege as role>
                    <value>${role}</value>
                </#list>
            </#if>
            </list>
        </property>
    </bean>
    <bean id="acmTaskCompletePrivilege" class="com.armedia.acm.pluginmanager.model.AcmPluginPrivilege">
        <property name="privilegeName" value="acm-task-complete"/>
        <property name="applicationRolesWithPrivilege">
            <list>
            <#if acmTaskCompletePrivilege??>
                <#list acmTaskCompletePrivilege as role>
                    <value>${role}</value>
                </#list>
            </#if>
            </list>
        </property>
    </bean>
    <bean id="acmTaskDeletePrivilege" class="com.armedia.acm.pluginmanager.model.AcmPluginPrivilege">
        <property name="privilegeName" value="acm-task-delete"/>
        <property name="applicationRolesWithPrivilege">
            <list>
            <#if acmTaskDeletePrivilege??>
                <#list acmTaskDeletePrivilege as role>
                    <value>${role}</value>
                </#list>
            </#if>
            </list>
        </property>
    </bean>
    <bean id="acmTaskClaimPrivilege" class="com.armedia.acm.pluginmanager.model.AcmPluginPrivilege">
        <property name="privilegeName" value="acm-task-claim"/>
        <property name="applicationRolesWithPrivilege">
            <list>
            <#if acmTaskClaimPrivilege??>
                <#list acmTaskClaimPrivilege as role>
                    <value>${role}</value>
                </#list>
            </#if>
            </list>
        </property>
    </bean>

    <!--Save Notification Service Plugin-->
    <bean id="acmNotificationModulePrivilege" class="com.armedia.acm.pluginmanager.model.AcmPluginPrivilege">
        <property name="privilegeName" value="acm-notification-module"/>
        <property name="applicationRolesWithPrivilege">
            <list>
            <#if acmNotificationModulePrivilege?? >
                <#list acmNotificationModulePrivilege as role>
                    <value>${role}</value>
                </#list>
            </#if>
            </list>
        </property>
    </bean>

    <bean id="acmNotificationPrivilege" class="com.armedia.acm.pluginmanager.model.AcmPluginPrivilege">
        <property name="privilegeName" value="acm-notification"/>
        <property name="applicationRolesWithPrivilege">
            <list>
            <#if acmNotificationPrivilege??>
                <#list acmNotificationPrivilege as role>
                    <value>${role}</value>
                </#list>
            </#if>
            </list>
        </property>
    </bean>

    <!--Subscription Plugin-->
    <bean id="acmSubscriptionModulePrivilege" class="com.armedia.acm.pluginmanager.model.AcmPluginPrivilege">
        <property name="privilegeName" value="acm-subscription-module"/>
        <property name="applicationRolesWithPrivilege">
            <list>
            <#if acmSubscriptionModulePrivilege?? >
                <#list acmSubscriptionModulePrivilege as role>
                    <value>${role}</value>
                </#list>
            </#if>
            </list>
        </property>
    </bean>

    <bean id="subscribePrivilege" class="com.armedia.acm.pluginmanager.model.AcmPluginPrivilege">
        <property name="privilegeName" value="acm-subscription"/>
        <property name="applicationRolesWithPrivilege">
            <list>
            <#if subscribePrivilege??>
                <#list subscribePrivilege as role>
                    <value>${role}</value>
                </#list>
            </#if>
            </list>
        </property>
    </bean>

    <!--Complaint Plugin-->
    <bean id="acmComplaintModulePrivilege" class="com.armedia.acm.pluginmanager.model.AcmPluginPrivilege">
        <property name="privilegeName" value="acm-complaint-module"/>
        <property name="applicationRolesWithPrivilege">
            <list>
            <#if acmComplaintModulePrivilege?? >
                <#list acmComplaintModulePrivilege as role>
                    <value>${role}</value>
                </#list>
            </#if>
            </list>
        </property>
    </bean>

    <bean id="acmComplaintListPrivilege" class="com.armedia.acm.pluginmanager.model.AcmPluginPrivilege">
        <property name="privilegeName" value="acm-complaint-list"/>
        <property name="applicationRolesWithPrivilege">
            <list>
            <#if acmComplaintListPrivilege??>
                <#list acmComplaintListPrivilege as role>
                    <value>${role}</value>
                </#list>
            </#if>
            </list>
        </property>
    </bean>
    <bean id="acmComplaintSavePrivilege" class="com.armedia.acm.pluginmanager.model.AcmPluginPrivilege">
        <property name="privilegeName" value="acm-complaint-save"/>
        <property name="applicationRolesWithPrivilege">
            <list>
            <#if acmComplaintSavePrivilegeas??>
                <#list acmComplaintSavePrivilegeas as role>
                    <value>${role}</value>
                </#list>
            </#if>
            </list>
        </property>
    </bean>
    <bean id="acmComplaintCreatePrivilege" class="com.armedia.acm.pluginmanager.model.AcmPluginPrivilege">
        <property name="privilegeName" value="acm-complaint-create"/>
        <property name="applicationRolesWithPrivilege">
            <list>
            <#if acmComplaintCreatePrivilege??>
                <#list acmComplaintCreatePrivilege as role>
                    <value>${role}</value>
                </#list>
            </#if>
            </list>
        </property>
    </bean>
    <bean id="acmComplaintApprovePrivilege" class="com.armedia.acm.pluginmanager.model.AcmPluginPrivilege">
        <property name="privilegeName" value="acm-complaint-approve"/>
        <property name="applicationRolesWithPrivilege">
            <list>
            <#if acmComplaintApprovePrivilege??>
                <#list acmComplaintApprovePrivilege as role>
                    <value>${role}</value>
                </#list>
            </#if>
            </list>
        </property>
    </bean>
    <bean id="acmSearchPrivilege" class="com.armedia.acm.pluginmanager.model.AcmPluginPrivilege">
        <property name="privilegeName" value="acm-search"/>
        <property name="applicationRolesWithPrivilege">
            <list>
            <#if acmSearchPrivilege??>
                <#list acmSearchPrivilege as role>
                    <value>${role}</value>
                </#list>
            </#if>
            </list>
        </property>
    </bean>

    <!--Search Plugin-->
    <bean id="acmSearchModulePrivilege" class="com.armedia.acm.pluginmanager.model.AcmPluginPrivilege">
        <property name="privilegeName" value="acm-search-module"/>
        <property name="applicationRolesWithPrivilege">
            <list>
            <#if acmSearchModulePrivilege?? >
                <#list acmSearchModulePrivilege as role>
                    <value>${role}</value>
                </#list>
            </#if>
            </list>
        </property>
    </bean>

    <bean id="searchPrivilege" class="com.armedia.acm.pluginmanager.model.AcmPluginPrivilege">
        <property name="privilegeName" value="acm-search"/>
        <property name="applicationRolesWithPrivilege">
            <list>
            <#if searchPrivilege??>
                <#list searchPrivilege as role>
                    <value>${role}</value>
                </#list>
            </#if>
            </list>
        </property>
    </bean>

    <!--ACM Case Files-->
    <bean id="acmCaseModulePrivilege" class="com.armedia.acm.pluginmanager.model.AcmPluginPrivilege">
        <property name="privilegeName" value="acm-case-module"/>
        <property name="applicationRolesWithPrivilege">
            <list>
            <#if acmCaseModulePrivilege?? >
                <#list acmCaseModulePrivilege as role>
                    <value>${role}</value>
                </#list>
            </#if>
            </list>
        </property>
    </bean>


    <bean id="acmCaseApprovePrivilege" class="com.armedia.acm.pluginmanager.model.AcmPluginPrivilege">
        <property name="privilegeName" value="acm-case-approve"/>
        <property name="applicationRolesWithPrivilege">
            <list>
            <#if acmCaseApprovePrivilege??>
                <#list acmCaseApprovePrivilege as role>
                    <value>${role}</value>
                </#list>
            </#if>
            </list>
        </property>
    </bean>
    <bean id="saveCasePrivilege" class="com.armedia.acm.pluginmanager.model.AcmPluginPrivilege">
        <property name="privilegeName" value="fis-save-case"/>
        <property name="applicationRolesWithPrivilege">
            <list>
            <#if saveCasePrivilege??>
                <#list saveCasePrivilege as role>
                    <value>${role}</value>
                </#list>
            </#if>
            </list>
        </property>
    </bean>
    <bean id="caseFileListPrivilege" class="com.armedia.acm.pluginmanager.model.AcmPluginPrivilege">
        <property name="privilegeName" value="case-file-list"/>
        <property name="applicationRolesWithPrivilege">
            <list>
            <#if caseFileListPrivilege??>
                <#list caseFileListPrivilege as role>
                    <value>${role}</value>
                </#list>
            </#if>
            </list>
        </property>
    </bean>
    <bean id="caseFileCreatePrivilege" class="com.armedia.acm.pluginmanager.model.AcmPluginPrivilege">
        <property name="privilegeName" value="case-file-create"/>
        <property name="applicationRolesWithPrivilege">
            <list>
            <#if caseFileCreatePrivilege??>
                <#list caseFileCreatePrivilege as role>
                    <value>${role}</value>
                </#list>
            </#if>
            </list>
        </property>
    </bean>

    <!--Milestone Plugin-->
    <bean id="listMilestonesGroupedByDate" class="com.armedia.acm.pluginmanager.model.AcmPluginPrivilege">
        <property name="privilegeName" value="acm-milestone-service"/>
        <property name="applicationRolesWithPrivilege">
            <list>
            <#if listMilestonesGroupedByDate??>
                <#list listMilestonesGroupedByDate as role>
                    <value>${role}</value>
                </#list>
            </#if>
            </list>
        </property>
    </bean>

    <!--Signature Confirm Service Plugin-->
    <bean id="acmSignatureConfirmPrivilege" class="com.armedia.acm.pluginmanager.model.AcmPluginPrivilege">
        <property name="privilegeName" value="acm-signature-confirm"/>
        <property name="applicationRolesWithPrivilege">
            <list>
            <#if acmSignatureConfirmPrivilege??>
                <#list acmSignatureConfirmPrivilege as role>
                    <value>${role}</value>
                </#list>
            </#if>
            </list>
        </property>
    </bean>
    <bean id="acmSignatureFindPrivilege" class="com.armedia.acm.pluginmanager.model.AcmPluginPrivilege">
        <property name="privilegeName" value="acm-signature-find"/>
        <property name="applicationRolesWithPrivilege">
            <list>
            <#if acmSignatureFindPrivilege??>
                <#list acmSignatureFindPrivilege as role>
                    <value>${role}</value>
                </#list>
            </#if>
            </list>
        </property>
    </bean>

    <!--Person Plugin-->
    <bean id="acmPersonSavePrivilege" class="com.armedia.acm.pluginmanager.model.AcmPluginPrivilege">
        <property name="privilegeName" value="acm-person-save"/>
        <property name="applicationRolesWithPrivilege">
            <list>
            <#if acmPersonSavePrivilege??>
                <#list acmPersonSavePrivilege as role>
                    <value>${role}</value>
                </#list>
            </#if>
            </list>
        </property>
    </bean>
    <bean id="acmPersonFindPrivilege" class="com.armedia.acm.pluginmanager.model.AcmPluginPrivilege">
        <property name="privilegeName" value="acm-person-find"/>
        <property name="applicationRolesWithPrivilege">
            <list>
            <#if acmPersonFindPrivilege??>
                <#list acmPersonFindPrivilege as role>
                    <value>${role}</value>
                </#list>
            </#if>
            </list>
        </property>
    </bean>
    <bean id="acmPersonDeletePrivilege" class="com.armedia.acm.pluginmanager.model.AcmPluginPrivilege">
        <property name="privilegeName" value="acm-person-delete"/>
        <property name="applicationRolesWithPrivilege">
            <list>
            <#if acmPersonDeletePrivilege??>
                <#list acmPersonDeletePrivilege as role>
                    <value>${role}</value>
                </#list>
            </#if>
            </list>
        </property>
    </bean>
    <bean id="acmPersonAssociationDeletePrivilege" class="com.armedia.acm.pluginmanager.model.AcmPluginPrivilege">
        <property name="privilegeName" value="acm-person-association-delete"/>
        <property name="applicationRolesWithPrivilege">
            <list>
            <#if acmPersonAssociationDeletePrivilege??>
                <#list acmPersonAssociationDeletePrivilege as role>
                    <value>${role}</value>
                </#list>
            </#if>
            </list>
        </property>
    </bean>
    <bean id="acmPersonListPrivilege" class="com.armedia.acm.pluginmanager.model.AcmPluginPrivilege">
        <property name="privilegeName" value="acm-person-List"/>
        <property name="applicationRolesWithPrivilege">
            <list>
            <#if acmPersonListPrivilege??>
                <#list acmPersonListPrivilege as role>
                    <value>${role}</value>
                </#list>
            </#if>
            </list>
        </property>
    </bean>
    <bean id="acmPersonTypesPrivilege" class="com.armedia.acm.pluginmanager.model.AcmPluginPrivilege">
        <property name="privilegeName" value="acm-person-values"/>
        <property name="applicationRolesWithPrivilege">
            <list>
            <#if acmPersonTypesPrivilege??>
                <#list acmPersonTypesPrivilege as role>
                    <value>${role}</value>
                </#list>
            </#if>
            </list>
        </property>
    </bean>
    <bean id="acmPersonAssociationSavePrivilege" class="com.armedia.acm.pluginmanager.model.AcmPluginPrivilege">
        <property name="privilegeName" value="acm-personAssociation-save"/>
        <property name="applicationRolesWithPrivilege">
            <list>
            <#if acmPersonAssociationSavePrivilege??>
                <#list acmPersonAssociationSavePrivilege as role>
                    <value>${role}</value>
                </#list>
            </#if>
            </list>
        </property>
    </bean>
    <bean id="acmOrganizationSavePrivilege" class="com.armedia.acm.pluginmanager.model.AcmPluginPrivilege">
        <property name="privilegeName" value="acm-organization-save"/>
        <property name="applicationRolesWithPrivilege">
            <list>
            <#if acmOrganizationSavePrivilege??>
                <#list acmOrganizationSavePrivilege as role>
                    <value>${role}</value>
                </#list>
            </#if>
            </list>
        </property>
    </bean>
    <bean id="acmOrganizationListPrivilege" class="com.armedia.acm.pluginmanager.model.AcmPluginPrivilege">
        <property name="privilegeName" value="acm-organization-list"/>
        <property name="applicationRolesWithPrivilege">
            <list>
            <#if acmOrganizationListPrivilege??>
                <#list acmOrganizationListPrivilege as role>
                    <value>${role}</value>
                </#list>
            </#if>
            </list>
        </property>
    </bean>
    <bean id="acmOrganizationDeletePrivilege" class="com.armedia.acm.pluginmanager.model.AcmPluginPrivilege">
        <property name="privilegeName" value="acm-organization-delete"/>
        <property name="applicationRolesWithPrivilege">
            <list>
            <#if acmOrganizationDeletePrivilege??>
                <#list acmOrganizationDeletePrivilege as role>
                    <value>${role}</value>
                </#list>
            </#if>
            </list>
        </property>
    </bean>
    <bean id="acmOrganizationDetailsPrivilege" class="com.armedia.acm.pluginmanager.model.AcmPluginPrivilege">
        <property name="privilegeName" value="acm-organization-details"/>
        <property name="applicationRolesWithPrivilege">
            <list>
            <#if acmOrganizationDetailsPrivilege??>
                <#list acmOrganizationDetailsPrivilege as role>
                    <value>${role}</value>
                </#list>
            </#if>
            </list>
        </property>
    </bean>
    <!--Admin Plugin-->
    <bean id="acmAdminModulePrivilege" class="com.armedia.acm.pluginmanager.model.AcmPluginPrivilege">
        <property name="privilegeName" value="acm-admin-module"/>
        <property name="applicationRolesWithPrivilege">
            <list>
            <#if acmAdminModulePrivilege?? >
                <#list acmAdminModulePrivilege as role>
                    <value>${role}</value>
                </#list>
            </#if>
            </list>
        </property>
    </bean>

    <bean id="adminPrivilege" class="com.armedia.acm.pluginmanager.model.AcmPluginPrivilege">
        <property name="privilegeName" value="acm-admin"/>
        <property name="applicationRolesWithPrivilege">
            <list>
            <#if adminPrivilege??>
                <#list adminPrivilege as role>
                    <value>${role}</value>
                </#list>
            </#if>
            </list>
        </property>
    </bean>

    <!--Save Note Service Plugin-->
    <bean id="acmSaveNotePrivilege" class="com.armedia.acm.pluginmanager.model.AcmPluginPrivilege">
        <property name="privilegeName" value="acm-save-note"/>
        <property name="applicationRolesWithPrivilege">
            <list>
            <#if acmSaveNotePrivilege??>
                <#list acmSaveNotePrivilege as role>
                    <value>${role}</value>
                </#list>
            </#if>
            </list>
        </property>
    </bean>
    <bean id="acmDeleteNoteByIdPrivilege" class="com.armedia.acm.pluginmanager.model.AcmPluginPrivilege">
        <property name="privilegeName" value="acm-note-delete"/>
        <property name="applicationRolesWithPrivilege">
            <list>
            <#if acmDeleteNoteByIdPrivilege??>
                <#list acmDeleteNoteByIdPrivilege as role>
                    <value>${role}</value>
                </#list>
            </#if>
            </list>
        </property>
    </bean>
    <bean id="acmListNotesPrivilege" class="com.armedia.acm.pluginmanager.model.AcmPluginPrivilege">
        <property name="privilegeName" value="acm-note-delete"/>
        <property name="applicationRolesWithPrivilege">
            <list>
            <#if acmListNotesPrivilege??>
                <#list acmListNotesPrivilege as role>
                    <value>${role}</value>
                </#list>
            </#if>
            </list>
        </property>
    </bean>

    <!--Microsoft Outlook Plugin-->
    <bean id="acmOutlookListObjectsPrivilege" class="com.armedia.acm.pluginmanager.model.AcmPluginPrivilege">
        <property name="privilegeName" value="acm-outlook-list-items"/>
        <property name="applicationRolesWithPrivilege">
            <list>
            <#if acmOutlookListObjectsPrivilege??>
                <#list acmOutlookListObjectsPrivilege as role>
                    <value>${role}</value>
                </#list>
            </#if>
            </list>
        </property>
    </bean>

    <!--Split Case Plugin-->
    <bean id="splitCaseFilePrivilege" class="com.armedia.acm.pluginmanager.model.AcmPluginPrivilege">
        <property name="privilegeName" value="split-case-privilege"/>
        <property name="applicationRolesWithPrivilege">
            <list>
            <#if splitCaseFilePrivilege??>
                <#list splitCaseFilePrivilege as role>
                    <value>${role}</value>
                </#list>
            </#if>
            </list>
        </property>
    </bean>
    <!--Lock Plugin-->
    <bean id="objectLockPrivilege" class="com.armedia.acm.pluginmanager.model.AcmPluginPrivilege">
        <property name="privilegeName" value="object-lock-privilege"/>
        <property name="applicationRolesWithPrivilege">
            <list>
            <#if objectLockPrivilege??>
                <#list objectLockPrivilege as role>
                    <value>${role}</value>
                </#list>
            </#if>
            </list>
        </property>
    </bean>
    <bean id="objectUnLockPrivilege" class="com.armedia.acm.pluginmanager.model.AcmPluginPrivilege">
        <property name="privilegeName" value="object-unlock-privilege"/>
        <property name="applicationRolesWithPrivilege">
            <list>
            <#if objectUnLockPrivilege??>
                <#list objectUnLockPrivilege as role>
                    <value>${role}</value>
                </#list>
            </#if>
            </list>
        </property>
    </bean>
    <bean id="listLocksDetailsPrivilege" class="com.armedia.acm.pluginmanager.model.AcmPluginPrivilege">
        <property name="privilegeName" value="list-locks-details-privilege"/>
        <property name="applicationRolesWithPrivilege">
            <list>
            <#if listLocksDetailsPrivilege??>
                <#list listLocksDetailsPrivilege as role>
                    <value>${role}</value>
                </#list>
            </#if>
            </list>
        </property>
    </bean>
    <bean id="listLockedObjectsPrivilege" class="com.armedia.acm.pluginmanager.model.AcmPluginPrivilege">
        <property name="privilegeName" value="list-locked-objects-privilege"/>
        <property name="applicationRolesWithPrivilege">
            <list>
            <#if listLockedObjectsPrivilege??>
                <#list listLockedObjectsPrivilege as role>
                    <value>${role}</value>
                </#list>
            </#if>
            </list>
        </property>
    </bean>
</beans>