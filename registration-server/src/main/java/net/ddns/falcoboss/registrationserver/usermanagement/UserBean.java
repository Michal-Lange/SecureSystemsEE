package net.ddns.falcoboss.registrationserver.usermanagement;

import java.util.List;

import javax.ejb.Local;
import javax.ejb.Singleton;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;
import javax.persistence.TypedQuery;

@Local
@Singleton
public class UserBean {
  
    @PersistenceContext(unitName="authPU", type=PersistenceContextType.TRANSACTION)
    public EntityManager em;
     
    public List<User> findAll() {
        TypedQuery<User> query = em.createQuery("SELECT usr FROM User ORDER BY usr.registeredOn ASC", User.class);
        return query.getResultList();
    }
  
    public void save(User user) {
        em.persist(user);
    }
  
    public void update(User user) {
        em.merge(user);
    }
  
    public void remove(String email) {
        User user = find(email);
        if (user != null) {
            em.remove(user);
        }
    }
      
    public void remove(User user) {
        if (user != null && user.getUsername()!=null && em.contains(user)) {
            em.remove(user);
        }
    }
  
    public User find(String email) {
        return em.find(User.class, email);
    }
    
    public User findByServiceKey(String serviceKey) {
    	TypedQuery<User> query = em.createQuery("SELECT usr FROM User usr WHERE usr.serviceKey=:serviceKey", User.class).setParameter("serviceKey", serviceKey);
        return query.getSingleResult();
    }
    
     
    public void detach(User user) {
        em.detach(user);
    }
}
