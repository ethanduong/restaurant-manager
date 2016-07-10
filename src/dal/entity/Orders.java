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
@Table(name = "Orders")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Orders.findAll", query = "SELECT o FROM Orders o"),
    @NamedQuery(name = "Orders.findByOrderID", query = "SELECT o FROM Orders o WHERE o.orderID = :orderID"),
    @NamedQuery(name = "Orders.findByTotalAmount", query = "SELECT o FROM Orders o WHERE o.totalAmount = :totalAmount"),
    @NamedQuery(name = "Orders.findByStatus", query = "SELECT o FROM Orders o WHERE o.status = :status")})
public class Orders implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "orderID")
    private Long orderID;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Basic(optional = false)
    @Column(name = "totalAmount")
    private BigDecimal totalAmount;
    @Basic(optional = false)
    @Column(name = "status")
    private boolean status;
    @JoinColumn(name = "cusID", referencedColumnName = "cusID")
    @ManyToOne(optional = false)
    private Customer cusID;
    @JoinColumn(name = "tableID", referencedColumnName = "tableID")
    @ManyToOne(optional = false)
    private Tables tableID;
    @JoinColumn(name = "userID", referencedColumnName = "userID")
    @ManyToOne(optional = false)
    private Users userID;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "orders")
    private Collection<OrderDetails> orderDetailsCollection;

    public Orders() {
    }

    public Orders(Long orderID) {
        this.orderID = orderID;
    }

    public Orders(Long orderID, BigDecimal totalAmount, boolean status) {
        this.orderID = orderID;
        this.totalAmount = totalAmount;
        this.status = status;
    }

    public Long getOrderID() {
        return orderID;
    }

    public void setOrderID(Long orderID) {
        this.orderID = orderID;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public boolean getStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public Customer getCusID() {
        return cusID;
    }

    public void setCusID(Customer cusID) {
        this.cusID = cusID;
    }

    public Tables getTableID() {
        return tableID;
    }

    public void setTableID(Tables tableID) {
        this.tableID = tableID;
    }

    public Users getUserID() {
        return userID;
    }

    public void setUserID(Users userID) {
        this.userID = userID;
    }

    @XmlTransient
    public Collection<OrderDetails> getOrderDetailsCollection() {
        return orderDetailsCollection;
    }

    public void setOrderDetailsCollection(Collection<OrderDetails> orderDetailsCollection) {
        this.orderDetailsCollection = orderDetailsCollection;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (orderID != null ? orderID.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Orders)) {
            return false;
        }
        Orders other = (Orders) object;
        if ((this.orderID == null && other.orderID != null) || (this.orderID != null && !this.orderID.equals(other.orderID))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "dal.entity.Orders[ orderID=" + orderID + " ]";
    }
    
}
