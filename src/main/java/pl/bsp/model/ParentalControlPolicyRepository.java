package pl.bsp.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface ParentalControlPolicyRepository extends JpaRepository<ParentalControlPolicy, Long>{
	
	public ParentalControlPolicy findById(long id);
	List<ParentalControlPolicy> findByUser(User user);
	List<ParentalControlPolicy> deleteById(long id);
}
