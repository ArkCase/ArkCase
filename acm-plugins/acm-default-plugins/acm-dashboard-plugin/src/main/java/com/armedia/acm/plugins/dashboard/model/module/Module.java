package com.armedia.acm.plugins.dashboard.model.module;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.TableGenerator;

/**
 * Created by marjan.stefanoski on 14.01.2016.
 */

@Entity
@Table(name = "acm_module")
public class Module
{
    private static final long serialVersionUID = -1122137631123433851L;

    @Id
    @TableGenerator(name = "acm_module_gen", table = "acm_module_id", pkColumnName = "cm_seq_name", valueColumnName = "cm_seq_num", pkColumnValue = "acm_module", initialValue = 100, allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "acm_module_gen")
    @Column(name = "cm_module_id")
    private Long moduleId;

    @Column(name = "cm_module_name")
    private String moduleName;

    public Long getModuleId()
    {
        return moduleId;
    }

    public void setModuleId(Long moduleId)
    {
        this.moduleId = moduleId;
    }

    public String getModuleName()
    {
        return moduleName;
    }

    public void setModuleName(String moduleName)
    {
        this.moduleName = moduleName;
    }
}
