<?xml version="1.0" encoding="UTF-8"?>
<beans:beans xmlns="http://www.springframework.org/schema/security"
        xmlns:beans="http://www.springframework.org/schema/beans"
        xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
        xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans-4.1.xsd
                            http://www.springframework.org/schema/security http://www.springframework.org/schema/security/spring-security-3.2.xsd">

    <beans:beans profile="ldap">
        <beans:bean class="org.springframework.beans.factory.config.PropertyPlaceholderConfigurer">
            <beans:property name="location" value="file:${r'${user.home}'}/.arkcase/acm/spring/spring-config-${id}-ldap.properties"/>
        </beans:bean>

        <beans:bean id="${id}_userSearch"
                class="org.springframework.security.ldap.search.FilterBasedLdapUserSearch">
            <beans:constructor-arg index="0" value='${r"${ldapConfig.userSearchBase}"}' />
            <beans:constructor-arg index="1" value='${r"${ldapConfig.userIdAttributeName}={0}"}' />
            <beans:constructor-arg index="2" ref="${id}_contextSource" />
        </beans:bean>

        <beans:bean id="${id}_authenticationProvider"
                class="org.springframework.security.ldap.authentication.LdapAuthenticationProvider">
            <beans:constructor-arg>
                <beans:bean
                        class="org.springframework.security.ldap.authentication.BindAuthenticator">
                    <beans:constructor-arg ref="${id}_contextSource" />
                    <beans:property name="userSearch" ref="${id}_userSearch"/>
                </beans:bean>
            </beans:constructor-arg>
            <beans:constructor-arg>
                <beans:bean
                        class="org.springframework.security.ldap.userdetails.DefaultLdapAuthoritiesPopulator">
                    <beans:constructor-arg ref="${id}_contextSource" />
                    <beans:constructor-arg value='${r"${ldapConfig.groupSearchBaseOU}"}'/>
                    <beans:property name="groupSearchFilter" value="member={0}"/>
                    <beans:property name="rolePrefix" value=""/>
                    <beans:property name="searchSubtree" value="true"/>
                    <beans:property name="convertToUpperCase" value="true"/>
                    <beans:property name="ignorePartialResultException" value="true"/>
                </beans:bean>
            </beans:constructor-arg>
        </beans:bean>

        <beans:bean id="${id}_contextSource"
                class="org.springframework.ldap.core.support.LdapContextSource">
            <beans:property name="url" value='${r"${ldapConfig.ldapUrl}"}' />
            <beans:property name="base" value='${r"${ldapConfig.base}"}' />
            <beans:property name="userDn" value='${r"${ldapConfig.authUserDn}"}' />
            <beans:property name="password" value='${r"${ldapConfig.authUserPassword}"}' />
            <beans:property name="pooled" value="true" />
            <!-- AD Specific Setting for avoiding the partial exception error -->
            <beans:property name="referral" value="follow" />
        </beans:bean>

        <!--
		Authenticates a user id and password against LDAP directory.  To support multiple LDAP configurations, create multiple Spring 
		beans, each with its own LdapAuthenticateService.
		-->
        <beans:bean id="${id}_ldapAuthenticateService" class="com.armedia.acm.services.users.service.ldap.LdapAuthenticateService">
            <!-- ldapAuthenticateConfig: ref must match an AcmLdapAuthenticateConfig bean, which should be defined below. -->
            <beans:property name="ldapAuthenticateConfig" ref="${id}_authenticate"/>

            <!-- do not change ldapDao properties. -->
            <beans:property name="ldapDao" ref="springLdapDao"/>
        </beans:bean>

        <beans:bean id="${id}_authenticate" class="com.armedia.acm.services.users.model.ldap.AcmLdapAuthenticateConfig">
            <!-- only specify authUserDn if your LDAP server requires user authentication (do not specify if you
				 are using anonymous authentication -->
            <beans:property name="authUserDn" value='${r"${ldapConfig.authUserDn}"}'/>
            <!-- only specify authUserPassword if you also specify authUserDn -->
            <beans:property name="authUserPassword" value='${r"${ldapConfig.authUserPassword}"}'/>
            <!-- groupSearchBase is the full tree under which groups are found (e.g. ou=groups,dc=armedia,dc=com).  -->
            <beans:property name="searchBase" value='${r"${ldapConfig.groupSearchBase}"}'/>
            <!-- groupSearchFilter is an LDAP filter to restrict which entries under the groupSearchBase are processsed -->
            <beans:property name="ignorePartialResultException" value="true"/>
            <!-- ldapUrl: URL of the ldap instance (e.g. ldap://armedia.com:389) -->
            <beans:property name="ldapUrl" value='${r"${ldapConfig.ldapUrl}"}'/>
            <!-- referral: "follow" if you want to follow LDAP referrals, "ignore" otherwise (search "ldap referral" for more info). -->
            <beans:property name="referral" value="follow"/>
            <!-- userIdAttributeName: use "samAccountName" if your LDAP server is Active Directory.  Most other LDAP
				 servers use "uid". -->
            <beans:property name="userIdAttributeName" value='${r"${ldapConfig.userIdAttributeName}"}'/>
        </beans:bean>
    </beans:beans>
</beans:beans>
