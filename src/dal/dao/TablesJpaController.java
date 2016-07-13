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
import dal.entity.Orders;
import dal.entity.Tables;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 *
 * @author Jame Moriarty
 */
public class TablesJpaController implements Serializable {

    public TablesJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Tables tables) throws PreexistingEntityException, Exception {
        if (tables.getOrdersCollection() == null) {
            tables.setOrdersCollection(new ArrayList<Orders>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Collection<Orders> attachedOrdersCollection = new ArrayList<Orders>();
            for (Orders ordersCollectionOrdersToAttach : tables.getOrdersCollection()) {
                ordersCollectionOrdersToAttach = em.getReference(ordersCollectionOrdersToAttach.getClass(), ordersCollectionOrdersToAttach.getOrderID());
                attachedOrdersCollection.add(ordersCollectionOrdersToAttach);
            }
            tables.setOrdersCollection(attachedOrdersCollection);
            em.persist(tables);
            for (Orders ordersCollectionOrders : tables.getOrdersCollection()) {
                Tables oldTableIDOfOrdersCollectionOrders = ordersCollectionOrders.getTableID();
                ordersCollectionOrders.setTableID(tables);
                ordersCollectionOrders = em.merge(ordersCollectionOrders);
                if (oldTableIDOfOrdersCollectionOrders != null) {
                    oldTableIDOfOrdersCollectionOrders.getOrdersCollection().remove(ordersCollectionOrders);
                    oldTableIDOfOrdersCollectionOrders = em.merge(oldTableIDOfOrdersCollectionOrders);
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findTables(tables.getTableID()) != null) {
                throw new PreexistingEntityException("Tables " + tables + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Tables tables) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Tables persistentTables = em.find(Tables.class, tables.getTableID());
            Collection<Orders> ordersCollectionOld = persistentTables.getOrdersCollection();
            Collection<Orders> ordersCollectionNew = tables.getOrdersCollection();
            Collection<Orders> attachedOrdersCollectionNew = new ArrayList<Orders>();
            for (Orders ordersCollectionNewOrdersToAttach : ordersCollectionNew) {
                ordersCollectionNewOrdersToAttach = em.getReference(ordersCollectionNewOrdersToAttach.getClass(), ordersCollectionNewOrdersToAttach.getOrderID());
                attachedOrdersCollectionNew.add(ordersCollectionNewOrdersToAttach);
            }
            ordersCollectionNew = attachedOrdersCollectionNew;
            tables.setOrdersCollection(ordersCollectionNew);
            tables = em.merge(tables);
            for (Orders ordersCollectionOldOrders : ordersCollectionOld) {
                if (!ordersCollectionNew.contains(ordersCollectionOldOrders)) {
                    ordersCollectionOldOrders.setTableID(null);
                    ordersCollectionOldOrders = em.merge(ordersCollectionOldOrders);
                }
            }
            for (Orders ordersCollectionNewOrders : ordersCollectionNew) {
                if (!ordersCollectionOld.contains(ordersCollectionNewOrders)) {
                    Tables oldTableIDOfOrdersCollectionNewOrders = ordersCollectionNewOrders.getTableID();
                    ordersCollectionNewOrders.setTableID(tables);
                    ordersCollectionNewOrders = em.merge(ordersCollectionNewOrders);
                    if (oldTableIDOfOrdersCollectionNewOrders != null && !oldTableIDOfOrdersCollectionNewOrders.equals(tables)) {
                        oldTableIDOfOrdersCollectionNewOrders.getOrdersCollection().remove(ordersCollectionNewOrders);
                        oldTableIDOfOrdersCollectionNewOrders = em.merge(oldTableIDOfOrdersCollectionNewOrders);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = tables.getTableID();
                if (findTables(id) == null) {
                    throw new NonexistentEntityException("The tables with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(Integer id) throws NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Tables tables;
            try {
                tables = em.getReference(Tables.class, id);
                tables.getTableID();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The tables with id " + id + " no longer exists.", enfe);
            }
            Collection<Orders> ordersCollection = tables.getOrdersCollection();
            for (Orders ordersCollectionOrders : ordersCollection) {
                ordersCollectionOrders.setTableID(null);
                ordersCollectionOrders = em.merge(ordersCollectionOrders);
            }
            em.remove(tables);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Tables> findTablesEntities() {
        return findTablesEntities(true, -1, -1);
    }

    public List<Tables> findTablesEntities(int maxResults, int firstResult) {
        return findTablesEntities(false, maxResults, firstResult);
    }

    private List<Tables> findTablesEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Tables.class));
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

    public Tables findTables(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Tables.class, id);
        } finally {
            em.close();
        }
    }

    public int getTablesCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Tables> rt = cq.from(Tables.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
