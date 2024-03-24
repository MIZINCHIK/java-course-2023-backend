package edu.java.scrapper.domain.jpa;

import edu.java.scrapper.domain.jpa.entities.UserEntity;
import java.util.List;
import java.util.Set;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {
    List<UserEntity> findAllByLinks_Id(long id);
}
