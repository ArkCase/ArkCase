<?xml version="1.0" encoding="UTF-8"?>
<beans:beans xmlns="http://www.springframework.org/schema/security"
             xmlns:beans="http://www.springframework.org/schema/beans"
             xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"

             xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans-3.2.xsd
                        http://www.springframework.org/schema/security http://www.springframework.org/schema/security/spring-security-3.1.xsd">

    <beans:bean class="org.springframework.beans.factory.config.PropertyPlaceholderConfigurer">
        <beans:property name="location" value="file:${propertiesFileName}"/>
    </beans:bean>

    <beans:bean id="${id}_userSearch"
                class="org.springframework.security.ldap.search.FilterBasedLdapUserSearch">
        <beans:constructor-arg index="0" value='${r"${ldapConfig.userSearchBase}"}' />
        <beans:constructor-arg index="1" value="sAMAccountName={0}" />
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

</beans:beans>
