package com.armedia.acm.services.search.model;

import com.armedia.acm.data.AcmEntity;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * Created by marjan.stefanoski on 06.01.2015.
 */
@Entity
@Table(name = "acm_solr_filter")
public class SolrFilter implements Serializable, AcmEntity {

    private static final long serialVersionUID = -2960319466522863767L;

    @Id
    @Column(name = "cm_filter_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "cm_filter_type")
    private String filterType;

    @Column(name = "cm_filter_name")
    private String filterName;

    @Column(name = "cm_filter_title")
    private String filterTitle;

    @Column(name = "cm_filter_created")
    @Temporal(TemporalType.TIMESTAMP)
    private Date created;

    @Column(name = "cm_filter_creator", insertable = true, updatable = false)
    private String creator;


    @Column(name = "cm_filter_modified", nullable = false, insertable = true, updatable = true)
    @Temporal(TemporalType.TIMESTAMP)
    private Date modified;

    @Column(name = "cm_filter_modifier", insertable = true, updatable = true)
    private String modifier;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFilterType() {
        return filterType;
    }

    public void setFilterType(String filterType) {
        this.filterType = filterType;
    }

    public String getFilterName() {
        return filterName;
    }

    public void setFilterName(String filterName) {
        this.filterName = filterName;
    }

    public String getFilterTitle() {
        return filterTitle;
    }

    public void setFilterTitle(String filterTitle) {
        this.filterTitle = filterTitle;
    }

    @Override
    public Date getCreated() {
        return created;
    }

    @Override
    public void setCreated(Date created) {
        this.created = created;
    }

    @Override
    public String getCreator() {
        return creator;
    }

    @Override
    public void setCreator(String creator) {
        this.creator = creator;
    }

    @Override
    public Date getModified() {
        return modified;
    }

    @Override
    public void setModified(Date modified) {
        this.modified = modified;
    }

    @Override
    public String getModifier() {
        return modifier;
    }

    @Override
    public void setModifier(String modifier) {
        this.modifier = modifier;
    }
}
