/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dal.dao;

import dal.dao.exceptions.NonexistentEntityException;
import dal.dao.exceptions.PreexistingEntityException;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import dal.entity.MenuItem;
import dal.entity.OrderDetails;
import dal.entity.OrderDetailsPK;
import dal.entity.Orders;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 *
 * @author Jame Moriarty
 */
public class OrderDetailsJpaController implements Serializable {

    public OrderDetailsJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(OrderDetails orderDetails) throws PreexistingEntityException, Exception {
        if (orderDetails.getOrderDetailsPK() == null) {
            orderDetails.setOrderDetailsPK(new OrderDetailsPK());
        }
        orderDetails.getOrderDetailsPK().setOrderID(orderDetails.getOrders().getOrderID());
        orderDetails.getOrderDetailsPK().setItemID(orderDetails.getMenuItem().getItemID());
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            MenuItem menuItem = orderDetails.getMenuItem();
            if (menuItem != null) {
                menuItem = em.getReference(menuItem.getClass(), menuItem.getItemID());
                orderDetails.setMenuItem(menuItem);
            }
            Orders orders = orderDetails.getOrders();
            if (orders != null) {
                orders = em.getReference(orders.getClass(), orders.getOrderID());
                orderDetails.setOrders(orders);
            }
            em.persist(orderDetails);
            if (menuItem != null) {
                menuItem.getOrderDetailsCollection().add(orderDetails);
                menuItem = em.merge(menuItem);
            }
            if (orders != null) {
                orders.getOrderDetailsCollection().add(orderDetails);
                orders = em.merge(orders);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findOrderDetails(orderDetails.getOrderDetailsPK()) != null) {
                throw new PreexistingEntityException("OrderDetails " + orderDetails + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(OrderDetails orderDetails) throws NonexistentEntityException, Exception {
        orderDetails.getOrderDetailsPK().setOrderID(orderDetails.getOrders().getOrderID());
        orderDetails.getOrderDetailsPK().setItemID(orderDetails.getMenuItem().getItemID());
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            OrderDetails persistentOrderDetails = em.find(OrderDetails.class, orderDetails.getOrderDetailsPK());
            MenuItem menuItemOld = persistentOrderDetails.getMenuItem();
            MenuItem menuItemNew = orderDetails.getMenuItem();
            Orders ordersOld = persistentOrderDetails.getOrders();
            Orders ordersNew = orderDetails.getOrders();
            if (menuItemNew != null) {
                menuItemNew = em.getReference(menuItemNew.getClass(), menuItemNew.getItemID());
                orderDetails.setMenuItem(menuItemNew);
            }
            if (ordersNew != null) {
                ordersNew = em.getReference(ordersNew.getClass(), ordersNew.getOrderID());
                orderDetails.setOrders(ordersNew);
            }
            orderDetails = em.merge(orderDetails);
            if (menuItemOld != null && !menuItemOld.equals(menuItemNew)) {
                menuItemOld.getOrderDetailsCollection().remove(orderDetails);
                menuItemOld = em.merge(menuItemOld);
            }
            if (menuItemNew != null && !menuItemNew.equals(menuItemOld)) {
                menuItemNew.getOrderDetailsCollection().add(orderDetails);
                menuItemNew = em.merge(menuItemNew);
            }
            if (ordersOld != null && !ordersOld.equals(ordersNew)) {
                ordersOld.getOrderDetailsCollection().remove(orderDetails);
                ordersOld = em.merge(ordersOld);
            }
            if (ordersNew != null && !ordersNew.equals(ordersOld)) {
                ordersNew.getOrderDetailsCollection().add(orderDetails);
                ordersNew = em.merge(ordersNew);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                OrderDetailsPK id = orderDetails.getOrderDetailsPK();
                if (findOrderDetails(id) == null) {
                    throw new NonexistentEntityException("The orderDetails with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(OrderDetailsPK id) throws NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            OrderDetails orderDetails;
            try {
                orderDetails = em.getReference(OrderDetails.class, id);
                orderDetails.getOrderDetailsPK();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The orderDetails with id " + id + " no longer exists.", enfe);
            }
            MenuItem menuItem = orderDetails.getMenuItem();
            if (menuItem != null) {
                menuItem.getOrderDetailsCollection().remove(orderDetails);
                menuItem = em.merge(menuItem);
            }
            Orders orders = orderDetails.getOrders();
            if (orders != null) {
                orders.getOrderDetailsCollection().remove(orderDetails);
                orders = em.merge(orders);
            }
            em.remove(orderDetails);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<OrderDetails> findOrderDetailsEntities() {
        return findOrderDetailsEntities(true, -1, -1);
    }

    public List<OrderDetails> findOrderDetailsEntities(int maxResults, int firstResult) {
        return findOrderDetailsEntities(false, maxResults, firstResult);
    }

    private List<OrderDetails> findOrderDetailsEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(OrderDetails.class));
            Query q = em.createQuery(cq);
            if (!all) {
                q.setMaxResults(maxResults);
                q.setFirstResult(firstResult);
            }
            return q.getResultList();
        } finally {
            em.close();
        }
    }

    public OrderDetails findOrderDetails(OrderDetailsPK id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(OrderDetails.class, id);
        } finally {
            em.close();
        }
    }

    public int getOrderDetailsCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<OrderDetails> rt = cq.from(OrderDetails.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
