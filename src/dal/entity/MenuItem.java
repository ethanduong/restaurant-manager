/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dal.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Collection;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
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
@Table(name = "MenuItem")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "MenuItem.findAll", query = "SELECT m FROM MenuItem m"),
    @NamedQuery(name = "MenuItem.findByItemID", query = "SELECT m FROM MenuItem m WHERE m.itemID = :itemID"),
    @NamedQuery(name = "MenuItem.findByItemName", query = "SELECT m FROM MenuItem m WHERE m.itemName = :itemName"),
    @NamedQuery(name = "MenuItem.findByItemPrice", query = "SELECT m FROM MenuItem m WHERE m.itemPrice = :itemPrice")})
public class MenuItem implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "itemID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Short itemID;
    @Basic(optional = false)
    @Column(name = "itemName")
    private String itemName;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Basic(optional = false)
    @Column(name = "itemPrice")
    private BigDecimal itemPrice;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "menuItem")
    private Collection<OrderDetails> orderDetailsCollection;
    @JoinColumn(name = "classID", referencedColumnName = "classID")
    @ManyToOne(optional = false)
    private ItemClass classID;

    public MenuItem() {
    }

    public MenuItem(Short itemID) {
        this.itemID = itemID;
    }

    public MenuItem(Short itemID, String itemName, BigDecimal itemPrice) {
        this.itemID = itemID;
        this.itemName = itemName;
        this.itemPrice = itemPrice;
    }

    public Short getItemID() {
        return itemID;
    }

    public void setItemID(Short itemID) {
        this.itemID = itemID;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public BigDecimal getItemPrice() {
        return itemPrice;
    }

    public void setItemPrice(BigDecimal itemPrice) {
        this.itemPrice = itemPrice;
    }

    @XmlTransient
    public Collection<OrderDetails> getOrderDetailsCollection() {
        return orderDetailsCollection;
    }

    public void setOrderDetailsCollection(Collection<OrderDetails> orderDetailsCollection) {
        this.orderDetailsCollection = orderDetailsCollection;
    }

    public ItemClass getClassID() {
        return classID;
    }

    public void setClassID(ItemClass classID) {
        this.classID = classID;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (itemID != null ? itemID.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof MenuItem)) {
            return false;
        }
        MenuItem other = (MenuItem) object;
        if ((this.itemID == null && other.itemID != null) || (this.itemID != null && !this.itemID.equals(other.itemID))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return itemName;
    }
    
}
