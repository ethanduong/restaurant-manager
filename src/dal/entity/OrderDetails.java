/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dal.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Jame Moriarty
 */
@Entity
@Table(name = "OrderDetails")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "OrderDetails.findAll", query = "SELECT o FROM OrderDetails o"),
    @NamedQuery(name = "OrderDetails.findByOrderID", query = "SELECT o FROM OrderDetails o WHERE o.orderDetailsPK.orderID = :orderID"),
    @NamedQuery(name = "OrderDetails.findByItemID", query = "SELECT o FROM OrderDetails o WHERE o.orderDetailsPK.itemID = :itemID"),
    @NamedQuery(name = "OrderDetails.findByItemQty", query = "SELECT o FROM OrderDetails o WHERE o.itemQty = :itemQty"),
    @NamedQuery(name = "OrderDetails.findByAmount", query = "SELECT o FROM OrderDetails o WHERE o.amount = :amount"),
    @NamedQuery(name = "OrderDetails.findByDatetime", query = "SELECT o FROM OrderDetails o WHERE o.datetime = :datetime")})
public class OrderDetails implements Serializable {
    private static final long serialVersionUID = 1L;
    @EmbeddedId
    protected OrderDetailsPK orderDetailsPK;
    @Basic(optional = false)
    @Column(name = "itemQty")
    private short itemQty;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Basic(optional = false)
    @Column(name = "amount")
    private BigDecimal amount;
    @Basic(optional = false)
    @Column(name = "datetime")
    @Temporal(TemporalType.TIMESTAMP)
    private Date datetime;
    @JoinColumn(name = "itemID", referencedColumnName = "itemID", insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private MenuItem menuItem;
    @JoinColumn(name = "orderID", referencedColumnName = "orderID", insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private Orders orders;

    public OrderDetails() {
    }

    public OrderDetails(OrderDetailsPK orderDetailsPK) {
        this.orderDetailsPK = orderDetailsPK;
    }

    public OrderDetails(OrderDetailsPK orderDetailsPK, short itemQty, BigDecimal amount, Date datetime) {
        this.orderDetailsPK = orderDetailsPK;
        this.itemQty = itemQty;
        this.amount = amount;
        this.datetime = datetime;
    }

    public OrderDetails(long orderID, short itemID) {
        this.orderDetailsPK = new OrderDetailsPK(orderID, itemID);
    }

    public OrderDetailsPK getOrderDetailsPK() {
        return orderDetailsPK;
    }

    public void setOrderDetailsPK(OrderDetailsPK orderDetailsPK) {
        this.orderDetailsPK = orderDetailsPK;
    }

    public short getItemQty() {
        return itemQty;
    }

    public void setItemQty(short itemQty) {
        this.itemQty = itemQty;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public Date getDatetime() {
        return datetime;
    }

    public void setDatetime(Date datetime) {
        this.datetime = datetime;
    }

    public MenuItem getMenuItem() {
        return menuItem;
    }

    public void setMenuItem(MenuItem menuItem) {
        this.menuItem = menuItem;
    }

    public Orders getOrders() {
        return orders;
    }

    public void setOrders(Orders orders) {
        this.orders = orders;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (orderDetailsPK != null ? orderDetailsPK.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof OrderDetails)) {
            return false;
        }
        OrderDetails other = (OrderDetails) object;
        if ((this.orderDetailsPK == null && other.orderDetailsPK != null) || (this.orderDetailsPK != null && !this.orderDetailsPK.equals(other.orderDetailsPK))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "dal.entity.OrderDetails[ orderDetailsPK=" + orderDetailsPK + " ]";
    }
    
}
