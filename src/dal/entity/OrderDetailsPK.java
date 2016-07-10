/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dal.entity;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 *
 * @author Jame Moriarty
 */
@Embeddable
public class OrderDetailsPK implements Serializable {
    @Basic(optional = false)
    @Column(name = "orderID")
    private long orderID;
    @Basic(optional = false)
    @Column(name = "itemID")
    private short itemID;

    public OrderDetailsPK() {
    }

    public OrderDetailsPK(long orderID, short itemID) {
        this.orderID = orderID;
        this.itemID = itemID;
    }

    public long getOrderID() {
        return orderID;
    }

    public void setOrderID(long orderID) {
        this.orderID = orderID;
    }

    public short getItemID() {
        return itemID;
    }

    public void setItemID(short itemID) {
        this.itemID = itemID;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (int) orderID;
        hash += (int) itemID;
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof OrderDetailsPK)) {
            return false;
        }
        OrderDetailsPK other = (OrderDetailsPK) object;
        if (this.orderID != other.orderID) {
            return false;
        }
        if (this.itemID != other.itemID) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "dal.entity.OrderDetailsPK[ orderID=" + orderID + ", itemID=" + itemID + " ]";
    }
    
}
