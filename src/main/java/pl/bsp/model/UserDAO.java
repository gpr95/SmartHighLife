package pl.bsp.model;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.Transactional;


@Repository
@EnableTransactionManagement
public class UserDAO {
	
	@PersistenceContext
    EntityManager entityManager;

	@Transactional
	public void addUser(User user){
		entityManager.persist(user);
	}
}
