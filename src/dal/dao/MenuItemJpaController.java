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
import dal.entity.ItemClass;
import dal.entity.MenuItem;
import dal.entity.OrderDetails;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 *
 * @author Jame Moriarty
 */
public class MenuItemJpaController implements Serializable {

    public MenuItemJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(MenuItem menuItem) throws PreexistingEntityException, Exception {
        if (menuItem.getOrderDetailsCollection() == null) {
            menuItem.setOrderDetailsCollection(new ArrayList<OrderDetails>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            ItemClass classID = menuItem.getClassID();
            if (classID != null) {
                classID = em.getReference(classID.getClass(), classID.getClassID());
                menuItem.setClassID(classID);
            }
            Collection<OrderDetails> attachedOrderDetailsCollection = new ArrayList<OrderDetails>();
            for (OrderDetails orderDetailsCollectionOrderDetailsToAttach : menuItem.getOrderDetailsCollection()) {
                orderDetailsCollectionOrderDetailsToAttach = em.getReference(orderDetailsCollectionOrderDetailsToAttach.getClass(), orderDetailsCollectionOrderDetailsToAttach.getOrderDetailsPK());
                attachedOrderDetailsCollection.add(orderDetailsCollectionOrderDetailsToAttach);
            }
            menuItem.setOrderDetailsCollection(attachedOrderDetailsCollection);
            em.persist(menuItem);
            if (classID != null) {
                classID.getMenuItemCollection().add(menuItem);
                classID = em.merge(classID);
            }
            for (OrderDetails orderDetailsCollectionOrderDetails : menuItem.getOrderDetailsCollection()) {
                MenuItem oldMenuItemOfOrderDetailsCollectionOrderDetails = orderDetailsCollectionOrderDetails.getMenuItem();
                orderDetailsCollectionOrderDetails.setMenuItem(menuItem);
                orderDetailsCollectionOrderDetails = em.merge(orderDetailsCollectionOrderDetails);
                if (oldMenuItemOfOrderDetailsCollectionOrderDetails != null) {
                    oldMenuItemOfOrderDetailsCollectionOrderDetails.getOrderDetailsCollection().remove(orderDetailsCollectionOrderDetails);
                    oldMenuItemOfOrderDetailsCollectionOrderDetails = em.merge(oldMenuItemOfOrderDetailsCollectionOrderDetails);
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findMenuItem(menuItem.getItemID()) != null) {
                throw new PreexistingEntityException("MenuItem " + menuItem + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(MenuItem menuItem) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            MenuItem persistentMenuItem = em.find(MenuItem.class, menuItem.getItemID());
            ItemClass classIDOld = persistentMenuItem.getClassID();
            ItemClass classIDNew = menuItem.getClassID();
            Collection<OrderDetails> orderDetailsCollectionOld = persistentMenuItem.getOrderDetailsCollection();
            Collection<OrderDetails> orderDetailsCollectionNew = menuItem.getOrderDetailsCollection();
            List<String> illegalOrphanMessages = null;
            for (OrderDetails orderDetailsCollectionOldOrderDetails : orderDetailsCollectionOld) {
                if (!orderDetailsCollectionNew.contains(orderDetailsCollectionOldOrderDetails)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain OrderDetails " + orderDetailsCollectionOldOrderDetails + " since its menuItem field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            if (classIDNew != null) {
                classIDNew = em.getReference(classIDNew.getClass(), classIDNew.getClassID());
                menuItem.setClassID(classIDNew);
            }
            Collection<OrderDetails> attachedOrderDetailsCollectionNew = new ArrayList<OrderDetails>();
            for (OrderDetails orderDetailsCollectionNewOrderDetailsToAttach : orderDetailsCollectionNew) {
                orderDetailsCollectionNewOrderDetailsToAttach = em.getReference(orderDetailsCollectionNewOrderDetailsToAttach.getClass(), orderDetailsCollectionNewOrderDetailsToAttach.getOrderDetailsPK());
                attachedOrderDetailsCollectionNew.add(orderDetailsCollectionNewOrderDetailsToAttach);
            }
            orderDetailsCollectionNew = attachedOrderDetailsCollectionNew;
            menuItem.setOrderDetailsCollection(orderDetailsCollectionNew);
            menuItem = em.merge(menuItem);
            if (classIDOld != null && !classIDOld.equals(classIDNew)) {
                classIDOld.getMenuItemCollection().remove(menuItem);
                classIDOld = em.merge(classIDOld);
            }
            if (classIDNew != null && !classIDNew.equals(classIDOld)) {
                classIDNew.getMenuItemCollection().add(menuItem);
                classIDNew = em.merge(classIDNew);
            }
            for (OrderDetails orderDetailsCollectionNewOrderDetails : orderDetailsCollectionNew) {
                if (!orderDetailsCollectionOld.contains(orderDetailsCollectionNewOrderDetails)) {
                    MenuItem oldMenuItemOfOrderDetailsCollectionNewOrderDetails = orderDetailsCollectionNewOrderDetails.getMenuItem();
                    orderDetailsCollectionNewOrderDetails.setMenuItem(menuItem);
                    orderDetailsCollectionNewOrderDetails = em.merge(orderDetailsCollectionNewOrderDetails);
                    if (oldMenuItemOfOrderDetailsCollectionNewOrderDetails != null && !oldMenuItemOfOrderDetailsCollectionNewOrderDetails.equals(menuItem)) {
                        oldMenuItemOfOrderDetailsCollectionNewOrderDetails.getOrderDetailsCollection().remove(orderDetailsCollectionNewOrderDetails);
                        oldMenuItemOfOrderDetailsCollectionNewOrderDetails = em.merge(oldMenuItemOfOrderDetailsCollectionNewOrderDetails);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Short id = menuItem.getItemID();
                if (findMenuItem(id) == null) {
                    throw new NonexistentEntityException("The menuItem with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(Short id) throws IllegalOrphanException, NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            MenuItem menuItem;
            try {
                menuItem = em.getReference(MenuItem.class, id);
                menuItem.getItemID();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The menuItem with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            Collection<OrderDetails> orderDetailsCollectionOrphanCheck = menuItem.getOrderDetailsCollection();
            for (OrderDetails orderDetailsCollectionOrphanCheckOrderDetails : orderDetailsCollectionOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This MenuItem (" + menuItem + ") cannot be destroyed since the OrderDetails " + orderDetailsCollectionOrphanCheckOrderDetails + " in its orderDetailsCollection field has a non-nullable menuItem field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            ItemClass classID = menuItem.getClassID();
            if (classID != null) {
                classID.getMenuItemCollection().remove(menuItem);
                classID = em.merge(classID);
            }
            em.remove(menuItem);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<MenuItem> findMenuItemEntities() {
        return findMenuItemEntities(true, -1, -1);
    }

    public List<MenuItem> findMenuItemEntities(int maxResults, int firstResult) {
        return findMenuItemEntities(false, maxResults, firstResult);
    }

    private List<MenuItem> findMenuItemEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(MenuItem.class));
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

    public MenuItem findMenuItem(Short id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(MenuItem.class, id);
        } finally {
            em.close();
        }
    }

    public int getMenuItemCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<MenuItem> rt = cq.from(MenuItem.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
