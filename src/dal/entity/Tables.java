/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dal.entity;

import java.io.Serializable;
import java.util.Collection;
import javax.persistence.Basic;
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
@Table(name = "Tables")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Tables.findAll", query = "SELECT t FROM Tables t"),
    @NamedQuery(name = "Tables.findByTableID", query = "SELECT t FROM Tables t WHERE t.tableID = :tableID"),
    @NamedQuery(name = "Tables.findByTableName", query = "SELECT t FROM Tables t WHERE t.tableName = :tableName"),
    @NamedQuery(name = "Tables.findByTableSize", query = "SELECT t FROM Tables t WHERE t.tableSize = :tableSize"),
    @NamedQuery(name = "Tables.findByStatus", query = "SELECT t FROM Tables t WHERE t.status = :status")})
public class Tables implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "tableID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer tableID;
    @Column(name = "tableName")
    private String tableName;
    @Column(name = "tableSize")
    private String tableSize;
    @Column(name = "status")
    private Boolean status;
    @OneToMany(mappedBy = "tableID")
    private Collection<Orders> ordersCollection;

    public Tables() {
    }

    public Tables(Integer tableID) {
        this.tableID = tableID;
    }

    public Integer getTableID() {
        return tableID;
    }

    public void setTableID(Integer tableID) {
        this.tableID = tableID;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getTableSize() {
        return tableSize;
    }

    public void setTableSize(String tableSize) {
        this.tableSize = tableSize;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    @XmlTransient
    public Collection<Orders> getOrdersCollection() {
        return ordersCollection;
    }

    public void setOrdersCollection(Collection<Orders> ordersCollection) {
        this.ordersCollection = ordersCollection;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (tableID != null ? tableID.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Tables)) {
            return false;
        }
        Tables other = (Tables) object;
        if ((this.tableID == null && other.tableID != null) || (this.tableID != null && !this.tableID.equals(other.tableID))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return tableName;
    }
    
}
