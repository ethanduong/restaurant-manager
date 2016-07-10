/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dal.dao;

import dal.dao.exceptions.IllegalOrphanException;
import dal.dao.exceptions.NonexistentEntityException;
import dal.dao.exceptions.PreexistingEntityException;
import dal.entity.ItemClass;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import dal.entity.MenuItem;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 *
 * @author Jame Moriarty
 */
public class ItemClassJpaController implements Serializable {

    public ItemClassJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(ItemClass itemClass) throws PreexistingEntityException, Exception {
        if (itemClass.getMenuItemCollection() == null) {
            itemClass.setMenuItemCollection(new ArrayList<MenuItem>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Collection<MenuItem> attachedMenuItemCollection = new ArrayList<MenuItem>();
            for (MenuItem menuItemCollectionMenuItemToAttach : itemClass.getMenuItemCollection()) {
                menuItemCollectionMenuItemToAttach = em.getReference(menuItemCollectionMenuItemToAttach.getClass(), menuItemCollectionMenuItemToAttach.getItemID());
                attachedMenuItemCollection.add(menuItemCollectionMenuItemToAttach);
            }
            itemClass.setMenuItemCollection(attachedMenuItemCollection);
            em.persist(itemClass);
            for (MenuItem menuItemCollectionMenuItem : itemClass.getMenuItemCollection()) {
                ItemClass oldClassIDOfMenuItemCollectionMenuItem = menuItemCollectionMenuItem.getClassID();
                menuItemCollectionMenuItem.setClassID(itemClass);
                menuItemCollectionMenuItem = em.merge(menuItemCollectionMenuItem);
                if (oldClassIDOfMenuItemCollectionMenuItem != null) {
                    oldClassIDOfMenuItemCollectionMenuItem.getMenuItemCollection().remove(menuItemCollectionMenuItem);
                    oldClassIDOfMenuItemCollectionMenuItem = em.merge(oldClassIDOfMenuItemCollectionMenuItem);
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findItemClass(itemClass.getClassID()) != null) {
                throw new PreexistingEntityException("ItemClass " + itemClass + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(ItemClass itemClass) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            ItemClass persistentItemClass = em.find(ItemClass.class, itemClass.getClassID());
            Collection<MenuItem> menuItemCollectionOld = persistentItemClass.getMenuItemCollection();
            Collection<MenuItem> menuItemCollectionNew = itemClass.getMenuItemCollection();
            List<String> illegalOrphanMessages = null;
            for (MenuItem menuItemCollectionOldMenuItem : menuItemCollectionOld) {
                if (!menuItemCollectionNew.contains(menuItemCollectionOldMenuItem)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain MenuItem " + menuItemCollectionOldMenuItem + " since its classID field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            Collection<MenuItem> attachedMenuItemCollectionNew = new ArrayList<MenuItem>();
            for (MenuItem menuItemCollectionNewMenuItemToAttach : menuItemCollectionNew) {
                menuItemCollectionNewMenuItemToAttach = em.getReference(menuItemCollectionNewMenuItemToAttach.getClass(), menuItemCollectionNewMenuItemToAttach.getItemID());
                attachedMenuItemCollectionNew.add(menuItemCollectionNewMenuItemToAttach);
            }
            menuItemCollectionNew = attachedMenuItemCollectionNew;
            itemClass.setMenuItemCollection(menuItemCollectionNew);
            itemClass = em.merge(itemClass);
            for (MenuItem menuItemCollectionNewMenuItem : menuItemCollectionNew) {
                if (!menuItemCollectionOld.contains(menuItemCollectionNewMenuItem)) {
                    ItemClass oldClassIDOfMenuItemCollectionNewMenuItem = menuItemCollectionNewMenuItem.getClassID();
                    menuItemCollectionNewMenuItem.setClassID(itemClass);
                    menuItemCollectionNewMenuItem = em.merge(menuItemCollectionNewMenuItem);
                    if (oldClassIDOfMenuItemCollectionNewMenuItem != null && !oldClassIDOfMenuItemCollectionNewMenuItem.equals(itemClass)) {
                        oldClassIDOfMenuItemCollectionNewMenuItem.getMenuItemCollection().remove(menuItemCollectionNewMenuItem);
                        oldClassIDOfMenuItemCollectionNewMenuItem = em.merge(oldClassIDOfMenuItemCollectionNewMenuItem);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Short id = itemClass.getClassID();
                if (findItemClass(id) == null) {
                    throw new NonexistentEntityException("The itemClass with id " + id + " no longer exists.");
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
            ItemClass itemClass;
            try {
                itemClass = em.getReference(ItemClass.class, id);
                itemClass.getClassID();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The itemClass with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            Collection<MenuItem> menuItemCollectionOrphanCheck = itemClass.getMenuItemCollection();
            for (MenuItem menuItemCollectionOrphanCheckMenuItem : menuItemCollectionOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This ItemClass (" + itemClass + ") cannot be destroyed since the MenuItem " + menuItemCollectionOrphanCheckMenuItem + " in its menuItemCollection field has a non-nullable classID field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            em.remove(itemClass);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<ItemClass> findItemClassEntities() {
        return findItemClassEntities(true, -1, -1);
    }

    public List<ItemClass> findItemClassEntities(int maxResults, int firstResult) {
        return findItemClassEntities(false, maxResults, firstResult);
    }

    private List<ItemClass> findItemClassEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(ItemClass.class));
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

    public ItemClass findItemClass(Short id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(ItemClass.class, id);
        } finally {
            em.close();
        }
    }

    public int getItemClassCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<ItemClass> rt = cq.from(ItemClass.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
