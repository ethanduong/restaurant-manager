/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dal.dao;

import dal.dao.exceptions.IllegalOrphanException;
import dal.dao.exceptions.NonexistentEntityException;
import dal.dao.exceptions.PreexistingEntityException;
import dal.entity.Customer;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
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
public class CustomerJpaController implements Serializable {

    public CustomerJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Customer customer) throws PreexistingEntityException, Exception {
        if (customer.getOrdersCollection() == null) {
            customer.setOrdersCollection(new ArrayList<Orders>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Collection<Orders> attachedOrdersCollection = new ArrayList<Orders>();
            for (Orders ordersCollectionOrdersToAttach : customer.getOrdersCollection()) {
                ordersCollectionOrdersToAttach = em.getReference(ordersCollectionOrdersToAttach.getClass(), ordersCollectionOrdersToAttach.getOrderID());
                attachedOrdersCollection.add(ordersCollectionOrdersToAttach);
            }
            customer.setOrdersCollection(attachedOrdersCollection);
            em.persist(customer);
            for (Orders ordersCollectionOrders : customer.getOrdersCollection()) {
                Customer oldCusIDOfOrdersCollectionOrders = ordersCollectionOrders.getCusID();
                ordersCollectionOrders.setCusID(customer);
                ordersCollectionOrders = em.merge(ordersCollectionOrders);
                if (oldCusIDOfOrdersCollectionOrders != null) {
                    oldCusIDOfOrdersCollectionOrders.getOrdersCollection().remove(ordersCollectionOrders);
                    oldCusIDOfOrdersCollectionOrders = em.merge(oldCusIDOfOrdersCollectionOrders);
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findCustomer(customer.getCusID()) != null) {
                throw new PreexistingEntityException("Customer " + customer + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Customer customer) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Customer persistentCustomer = em.find(Customer.class, customer.getCusID());
            Collection<Orders> ordersCollectionOld = persistentCustomer.getOrdersCollection();
            Collection<Orders> ordersCollectionNew = customer.getOrdersCollection();
            List<String> illegalOrphanMessages = null;
            for (Orders ordersCollectionOldOrders : ordersCollectionOld) {
                if (!ordersCollectionNew.contains(ordersCollectionOldOrders)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Orders " + ordersCollectionOldOrders + " since its cusID field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            Collection<Orders> attachedOrdersCollectionNew = new ArrayList<Orders>();
            for (Orders ordersCollectionNewOrdersToAttach : ordersCollectionNew) {
                ordersCollectionNewOrdersToAttach = em.getReference(ordersCollectionNewOrdersToAttach.getClass(), ordersCollectionNewOrdersToAttach.getOrderID());
                attachedOrdersCollectionNew.add(ordersCollectionNewOrdersToAttach);
            }
            ordersCollectionNew = attachedOrdersCollectionNew;
            customer.setOrdersCollection(ordersCollectionNew);
            customer = em.merge(customer);
            for (Orders ordersCollectionNewOrders : ordersCollectionNew) {
                if (!ordersCollectionOld.contains(ordersCollectionNewOrders)) {
                    Customer oldCusIDOfOrdersCollectionNewOrders = ordersCollectionNewOrders.getCusID();
                    ordersCollectionNewOrders.setCusID(customer);
                    ordersCollectionNewOrders = em.merge(ordersCollectionNewOrders);
                    if (oldCusIDOfOrdersCollectionNewOrders != null && !oldCusIDOfOrdersCollectionNewOrders.equals(customer)) {
                        oldCusIDOfOrdersCollectionNewOrders.getOrdersCollection().remove(ordersCollectionNewOrders);
                        oldCusIDOfOrdersCollectionNewOrders = em.merge(oldCusIDOfOrdersCollectionNewOrders);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = customer.getCusID();
                if (findCustomer(id) == null) {
                    throw new NonexistentEntityException("The customer with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(Integer id) throws IllegalOrphanException, NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Customer customer;
            try {
                customer = em.getReference(Customer.class, id);
                customer.getCusID();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The customer with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            Collection<Orders> ordersCollectionOrphanCheck = customer.getOrdersCollection();
            for (Orders ordersCollectionOrphanCheckOrders : ordersCollectionOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Customer (" + customer + ") cannot be destroyed since the Orders " + ordersCollectionOrphanCheckOrders + " in its ordersCollection field has a non-nullable cusID field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            em.remove(customer);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Customer> findCustomerEntities() {
        return findCustomerEntities(true, -1, -1);
    }

    public List<Customer> findCustomerEntities(int maxResults, int firstResult) {
        return findCustomerEntities(false, maxResults, firstResult);
    }

    private List<Customer> findCustomerEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Customer.class));
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

    public Customer findCustomer(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Customer.class, id);
        } finally {
            em.close();
        }
    }

    public int getCustomerCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Customer> rt = cq.from(Customer.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
