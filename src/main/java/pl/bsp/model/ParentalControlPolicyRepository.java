package pl.bsp.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface ParentalControlPolicyRepository extends JpaRepository<ParentalControlPolicy, Long>{

	public User findByUsername(String username);

	public User findById(long id);
}
