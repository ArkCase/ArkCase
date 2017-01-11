package com.armedia.acm.plugins.category.model;

import static com.armedia.acm.plugins.category.model.Category.FIND_ROOT_CATEGORIES;

import com.armedia.acm.core.AcmObject;
import com.armedia.acm.data.AcmEntity;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author Lazo Lazarev a.k.a. Lazarius Borg @ zerogravity Jan 9, 2017
 *
 */
@Entity
@Table(name = "acm_category")
@NamedQueries({@NamedQuery(name = FIND_ROOT_CATEGORIES, query = "Select c FROM Category c WHERE c.parent IS NULL")})
public class Category implements Serializable, AcmObject, AcmEntity
{

    public static final String FIND_ROOT_CATEGORIES = "Category.findRootCategories";

    private static final long serialVersionUID = -2857432909290052195L;

    private static final String OBJECT_TYPE = "CATEGORY";

    @Id
    @TableGenerator(name = "category_gen", table = "acm_category_id", pkColumnName = "cm_seq_name", valueColumnName = "cm_seq_num", pkColumnValue = "acm_category", initialValue = 100, allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "category_gen")
    @Column(name = "cm_category_id")
    private Long id;

    @Column(name = "cm_category_name", nullable = false)
    private String name;

    @Column(name = "cm_category_description")
    private String description;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "parent_id")
    private Category parent;

    @OneToMany(mappedBy = "parent")
    private List<Category> children;

    @Column(name = "cm_category_creator", nullable = false, updatable = false)
    private String creator;

    @Column(name = "cm_category_created", nullable = false, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date created;

    @Column(name = "cm_category_modifier", nullable = false)
    private String modifier;

    @Column(name = "cm_category_modified", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date modified;

    @Column(name = "cm_category_status", nullable = false)
    @Enumerated(EnumType.STRING)
    private CategoryStatus status;

    /**
     * @return the id
     */
    @Override
    public Long getId()
    {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(Long id)
    {
        this.id = id;
    }

    @Override
    public String getObjectType()
    {
        return OBJECT_TYPE;
    }

    /**
     * @return the name
     */
    public String getName()
    {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name)
    {
        this.name = name;
    }

    /**
     * @return the description
     */
    public String getDescription()
    {
        return description;
    }

    /**
     * @param description the description to set
     */
    public void setDescription(String description)
    {
        this.description = description;
    }

    /**
     * @return the parent
     */
    public Category getParent()
    {
        return parent;
    }

    /**
     * @param parent the parent to set
     */
    public void setParent(Category parent)
    {
        this.parent = parent;
    }

    /**
     * @return the children
     */
    public List<Category> getChildren()
    {
        return new ArrayList<>(children);
    }

    /**
     * @param children the children to set
     */
    public void setChildren(List<Category> children)
    {
        this.children = new ArrayList<>(children);
    }

    /**
     * @return the creator
     */
    @Override
    public String getCreator()
    {
        return creator;
    }

    /**
     * @param creator the creator to set
     */
    @Override
    public void setCreator(String creator)
    {
        this.creator = creator;
    }

    /**
     * @return the created
     */
    @Override
    public Date getCreated()
    {
        return created;
    }

    /**
     * @param created the created to set
     */
    @Override
    public void setCreated(Date created)
    {
        this.created = created;
    }

    /**
     * @return the modifier
     */
    @Override
    public String getModifier()
    {
        return modifier;
    }

    /**
     * @param modifier the modifier to set
     */
    @Override
    public void setModifier(String modifier)
    {
        this.modifier = modifier;
    }

    /**
     * @return the modified
     */
    @Override
    public Date getModified()
    {
        return modified;
    }

    /**
     * @param modified the modified to set
     */
    @Override
    public void setModified(Date modified)
    {
        this.modified = modified;
    }

    /**
     * @return the status
     */
    public CategoryStatus getStatus()
    {
        return status;
    }

    /**
     * @param status the status to set
     */
    public void setStatus(CategoryStatus status)
    {
        this.status = status;
    }

}
