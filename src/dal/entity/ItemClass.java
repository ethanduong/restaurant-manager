/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dal.entity;

import java.io.Serializable;
import java.util.Collection;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author Jame Moriarty
 */
@Entity
@Table(name = "ItemClass")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "ItemClass.findAll", query = "SELECT i FROM ItemClass i"),
    @NamedQuery(name = "ItemClass.findByClassID", query = "SELECT i FROM ItemClass i WHERE i.classID = :classID"),
    @NamedQuery(name = "ItemClass.findByClassName", query = "SELECT i FROM ItemClass i WHERE i.className = :className")})
public class ItemClass implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "classID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Short classID;
    @Basic(optional = false)
    @Column(name = "className")
    private String className;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "classID")
    private Collection<MenuItem> menuItemCollection;

    public ItemClass() {
    }

    public ItemClass(Short classID) {
        this.classID = classID;
    }

    public ItemClass(Short classID, String className) {
        this.classID = classID;
        this.className = className;
    }

    public Short getClassID() {
        return classID;
    }

    public void setClassID(Short classID) {
        this.classID = classID;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    @XmlTransient
    public Collection<MenuItem> getMenuItemCollection() {
        return menuItemCollection;
    }

    public void setMenuItemCollection(Collection<MenuItem> menuItemCollection) {
        this.menuItemCollection = menuItemCollection;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (classID != null ? classID.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof ItemClass)) {
            return false;
        }
        ItemClass other = (ItemClass) object;
        if ((this.classID == null && other.classID != null) || (this.classID != null && !this.classID.equals(other.classID))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return className;
    }
    
}
