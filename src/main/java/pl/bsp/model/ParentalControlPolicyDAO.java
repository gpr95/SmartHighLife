package pl.bsp.model;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Repository
@EnableTransactionManagement
public class ParentalControlPolicyDAO {

	@PersistenceContext
    EntityManager entityManager;

	@Transactional
	public void addParentalControlPolicy(ParentalControlPolicy parentalControlPolicy){
		entityManager.persist(parentalControlPolicy);
	}
}
