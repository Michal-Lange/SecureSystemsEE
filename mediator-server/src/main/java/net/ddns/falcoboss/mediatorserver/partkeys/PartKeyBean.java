package net.ddns.falcoboss.mediatorserver.partkeys;

import java.util.List;

import javax.ejb.Local;
import javax.ejb.Singleton;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;
import javax.persistence.TypedQuery;

@Local
@Singleton
public class PartKeyBean {

	@PersistenceContext(unitName="mediatorPU", type=PersistenceContextType.TRANSACTION)
    public EntityManager em;
	
	public List<PartKey> findAll() {
        TypedQuery<PartKey> query = em.createQuery("SELECT key FROM PartKey ORDER BY key.createdOn ASC", PartKey.class);
        return query.getResultList();
    }
	
	public void save(PartKey partKey) {
        em.persist(partKey);
    }
  
    public void update(PartKey partKey) {
        em.merge(partKey);
    }
  
    public void remove(String serviceKey) {
    	PartKey partKey = find(serviceKey);
        if (partKey != null) {
            em.remove(partKey);
        }
    }
      
    public void remove(PartKey partKey) {
        if (partKey != null && partKey.getServiceKey()!=null && em.contains(partKey)) {
            em.remove(partKey);
        }
    }
  
    public PartKey find(String serviceKey) {
        return em.find(PartKey.class, serviceKey);
    }
    
    public void detach(PartKey partKey) {
        em.detach(partKey);
    }
    
//    public PartKey findByServiceKey(String serviceKey) {
//    	TypedQuery<PartKey> query = em.createQuery("SELECT key FROM PartKey key WHERE key.serviceKey=:serviceKey", PartKey.class).setParameter("serviceKey", serviceKey);
//        return query.getSingleResult();
//    }
}