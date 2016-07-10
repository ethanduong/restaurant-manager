/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dal.dao;

import dal.dao.exceptions.IllegalOrphanException;
import dal.dao.exceptions.NonexistentEntityException;
import dal.dao.exceptions.PreexistingEntityException;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import dal.entity.Customer;
import dal.entity.Tables;
import dal.entity.Users;
import dal.entity.OrderDetails;
import dal.entity.Orders;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 *
 * @author Jame Moriarty
 */
public class OrdersJpaController implements Serializable {

    public OrdersJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Orders orders) throws PreexistingEntityException, Exception {
        if (orders.getOrderDetailsCollection() == null) {
            orders.setOrderDetailsCollection(new ArrayList<OrderDetails>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Customer cusID = orders.getCusID();
            if (cusID != null) {
                cusID = em.getReference(cusID.getClass(), cusID.getCusID());
                orders.setCusID(cusID);
            }
            Tables tableID = orders.getTableID();
            if (tableID != null) {
                tableID = em.getReference(tableID.getClass(), tableID.getTableID());
                orders.setTableID(tableID);
            }
            Users userID = orders.getUserID();
            if (userID != null) {
                userID = em.getReference(userID.getClass(), userID.getUserID());
                orders.setUserID(userID);
            }
            Collection<OrderDetails> attachedOrderDetailsCollection = new ArrayList<OrderDetails>();
            for (OrderDetails orderDetailsCollectionOrderDetailsToAttach : orders.getOrderDetailsCollection()) {
                orderDetailsCollectionOrderDetailsToAttach = em.getReference(orderDetailsCollectionOrderDetailsToAttach.getClass(), orderDetailsCollectionOrderDetailsToAttach.getOrderDetailsPK());
                attachedOrderDetailsCollection.add(orderDetailsCollectionOrderDetailsToAttach);
            }
            orders.setOrderDetailsCollection(attachedOrderDetailsCollection);
            em.persist(orders);
            if (cusID != null) {
                cusID.getOrdersCollection().add(orders);
                cusID = em.merge(cusID);
            }
            if (tableID != null) {
                tableID.getOrdersCollection().add(orders);
                tableID = em.merge(tableID);
            }
            if (userID != null) {
                userID.getOrdersCollection().add(orders);
                userID = em.merge(userID);
            }
            for (OrderDetails orderDetailsCollectionOrderDetails : orders.getOrderDetailsCollection()) {
                Orders oldOrdersOfOrderDetailsCollectionOrderDetails = orderDetailsCollectionOrderDetails.getOrders();
                orderDetailsCollectionOrderDetails.setOrders(orders);
                orderDetailsCollectionOrderDetails = em.merge(orderDetailsCollectionOrderDetails);
                if (oldOrdersOfOrderDetailsCollectionOrderDetails != null) {
                    oldOrdersOfOrderDetailsCollectionOrderDetails.getOrderDetailsCollection().remove(orderDetailsCollectionOrderDetails);
                    oldOrdersOfOrderDetailsCollectionOrderDetails = em.merge(oldOrdersOfOrderDetailsCollectionOrderDetails);
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findOrders(orders.getOrderID()) != null) {
                throw new PreexistingEntityException("Orders " + orders + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Orders orders) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Orders persistentOrders = em.find(Orders.class, orders.getOrderID());
            Customer cusIDOld = persistentOrders.getCusID();
            Customer cusIDNew = orders.getCusID();
            Tables tableIDOld = persistentOrders.getTableID();
            Tables tableIDNew = orders.getTableID();
            Users userIDOld = persistentOrders.getUserID();
            Users userIDNew = orders.getUserID();
            Collection<OrderDetails> orderDetailsCollectionOld = persistentOrders.getOrderDetailsCollection();
            Collection<OrderDetails> orderDetailsCollectionNew = orders.getOrderDetailsCollection();
            List<String> illegalOrphanMessages = null;
            for (OrderDetails orderDetailsCollectionOldOrderDetails : orderDetailsCollectionOld) {
                if (!orderDetailsCollectionNew.contains(orderDetailsCollectionOldOrderDetails)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain OrderDetails " + orderDetailsCollectionOldOrderDetails + " since its orders field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            if (cusIDNew != null) {
                cusIDNew = em.getReference(cusIDNew.getClass(), cusIDNew.getCusID());
                orders.setCusID(cusIDNew);
            }
            if (tableIDNew != null) {
                tableIDNew = em.getReference(tableIDNew.getClass(), tableIDNew.getTableID());
                orders.setTableID(tableIDNew);
            }
            if (userIDNew != null) {
                userIDNew = em.getReference(userIDNew.getClass(), userIDNew.getUserID());
                orders.setUserID(userIDNew);
            }
            Collection<OrderDetails> attachedOrderDetailsCollectionNew = new ArrayList<OrderDetails>();
            for (OrderDetails orderDetailsCollectionNewOrderDetailsToAttach : orderDetailsCollectionNew) {
                orderDetailsCollectionNewOrderDetailsToAttach = em.getReference(orderDetailsCollectionNewOrderDetailsToAttach.getClass(), orderDetailsCollectionNewOrderDetailsToAttach.getOrderDetailsPK());
                attachedOrderDetailsCollectionNew.add(orderDetailsCollectionNewOrderDetailsToAttach);
            }
            orderDetailsCollectionNew = attachedOrderDetailsCollectionNew;
            orders.setOrderDetailsCollection(orderDetailsCollectionNew);
            orders = em.merge(orders);
            if (cusIDOld != null && !cusIDOld.equals(cusIDNew)) {
                cusIDOld.getOrdersCollection().remove(orders);
                cusIDOld = em.merge(cusIDOld);
            }
            if (cusIDNew != null && !cusIDNew.equals(cusIDOld)) {
                cusIDNew.getOrdersCollection().add(orders);
                cusIDNew = em.merge(cusIDNew);
            }
            if (tableIDOld != null && !tableIDOld.equals(tableIDNew)) {
                tableIDOld.getOrdersCollection().remove(orders);
                tableIDOld = em.merge(tableIDOld);
            }
            if (tableIDNew != null && !tableIDNew.equals(tableIDOld)) {
                tableIDNew.getOrdersCollection().add(orders);
                tableIDNew = em.merge(tableIDNew);
            }
            if (userIDOld != null && !userIDOld.equals(userIDNew)) {
                userIDOld.getOrdersCollection().remove(orders);
                userIDOld = em.merge(userIDOld);
            }
            if (userIDNew != null && !userIDNew.equals(userIDOld)) {
                userIDNew.getOrdersCollection().add(orders);
                userIDNew = em.merge(userIDNew);
            }
            for (OrderDetails orderDetailsCollectionNewOrderDetails : orderDetailsCollectionNew) {
                if (!orderDetailsCollectionOld.contains(orderDetailsCollectionNewOrderDetails)) {
                    Orders oldOrdersOfOrderDetailsCollectionNewOrderDetails = orderDetailsCollectionNewOrderDetails.getOrders();
                    orderDetailsCollectionNewOrderDetails.setOrders(orders);
                    orderDetailsCollectionNewOrderDetails = em.merge(orderDetailsCollectionNewOrderDetails);
                    if (oldOrdersOfOrderDetailsCollectionNewOrderDetails != null && !oldOrdersOfOrderDetailsCollectionNewOrderDetails.equals(orders)) {
                        oldOrdersOfOrderDetailsCollectionNewOrderDetails.getOrderDetailsCollection().remove(orderDetailsCollectionNewOrderDetails);
                        oldOrdersOfOrderDetailsCollectionNewOrderDetails = em.merge(oldOrdersOfOrderDetailsCollectionNewOrderDetails);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Long id = orders.getOrderID();
                if (findOrders(id) == null) {
                    throw new NonexistentEntityException("The orders with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(Long id) throws IllegalOrphanException, NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Orders orders;
            try {
                orders = em.getReference(Orders.class, id);
                orders.getOrderID();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The orders with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            Collection<OrderDetails> orderDetailsCollectionOrphanCheck = orders.getOrderDetailsCollection();
            for (OrderDetails orderDetailsCollectionOrphanCheckOrderDetails : orderDetailsCollectionOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Orders (" + orders + ") cannot be destroyed since the OrderDetails " + orderDetailsCollectionOrphanCheckOrderDetails + " in its orderDetailsCollection field has a non-nullable orders field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            Customer cusID = orders.getCusID();
            if (cusID != null) {
                cusID.getOrdersCollection().remove(orders);
                cusID = em.merge(cusID);
            }
            Tables tableID = orders.getTableID();
            if (tableID != null) {
                tableID.getOrdersCollection().remove(orders);
                tableID = em.merge(tableID);
            }
            Users userID = orders.getUserID();
            if (userID != null) {
                userID.getOrdersCollection().remove(orders);
                userID = em.merge(userID);
            }
            em.remove(orders);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Orders> findOrdersEntities() {
        return findOrdersEntities(true, -1, -1);
    }

    public List<Orders> findOrdersEntities(int maxResults, int firstResult) {
        return findOrdersEntities(false, maxResults, firstResult);
    }

    private List<Orders> findOrdersEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Orders.class));
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

    public Orders findOrders(Long id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Orders.class, id);
        } finally {
            em.close();
        }
    }

    public int getOrdersCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Orders> rt = cq.from(Orders.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
