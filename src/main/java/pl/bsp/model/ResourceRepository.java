package pl.bsp.model;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ResourceRepository extends JpaRepository<Resource, Long>{
    Resource findByName(String name);
    
    Resource findBySerialIdAndUser(int serialId, User user);
    
    List<Resource> deleteBySerialIdAndUser(int serialId, User user);
}
