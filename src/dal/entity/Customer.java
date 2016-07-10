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
@Table(name = "Customer")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Customer.findAll", query = "SELECT c FROM Customer c"),
    @NamedQuery(name = "Customer.findByCusID", query = "SELECT c FROM Customer c WHERE c.cusID = :cusID"),
    @NamedQuery(name = "Customer.findByCusName", query = "SELECT c FROM Customer c WHERE c.cusName = :cusName"),
    @NamedQuery(name = "Customer.findByCusAddress", query = "SELECT c FROM Customer c WHERE c.cusAddress = :cusAddress"),
    @NamedQuery(name = "Customer.findByCusPhone", query = "SELECT c FROM Customer c WHERE c.cusPhone = :cusPhone")})
public class Customer implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "cusID")
    private Integer cusID;
    @Basic(optional = false)
    @Column(name = "CusName")
    private String cusName;
    @Basic(optional = false)
    @Column(name = "cusAddress")
    private String cusAddress;
    @Basic(optional = false)
    @Column(name = "cusPhone")
    private String cusPhone;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "cusID")
    private Collection<Orders> ordersCollection;

    public Customer() {
    }

    public Customer(Integer cusID) {
        this.cusID = cusID;
    }

    public Customer(Integer cusID, String cusName, String cusAddress, String cusPhone) {
        this.cusID = cusID;
        this.cusName = cusName;
        this.cusAddress = cusAddress;
        this.cusPhone = cusPhone;
    }

    public Integer getCusID() {
        return cusID;
    }

    public void setCusID(Integer cusID) {
        this.cusID = cusID;
    }

    public String getCusName() {
        return cusName;
    }

    public void setCusName(String cusName) {
        this.cusName = cusName;
    }

    public String getCusAddress() {
        return cusAddress;
    }

    public void setCusAddress(String cusAddress) {
        this.cusAddress = cusAddress;
    }

    public String getCusPhone() {
        return cusPhone;
    }

    public void setCusPhone(String cusPhone) {
        this.cusPhone = cusPhone;
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
        hash += (cusID != null ? cusID.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Customer)) {
            return false;
        }
        Customer other = (Customer) object;
        if ((this.cusID == null && other.cusID != null) || (this.cusID != null && !this.cusID.equals(other.cusID))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "dal.entity.Customer[ cusID=" + cusID + " ]";
    }
    
}
