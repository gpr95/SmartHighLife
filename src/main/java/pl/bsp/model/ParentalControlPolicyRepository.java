package pl.bsp.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface ParentalControlPolicyRepository extends JpaRepository<ParentalControlPolicy, Long>{

	public User findByName(String name);
	
	public User findById(long id);
}
