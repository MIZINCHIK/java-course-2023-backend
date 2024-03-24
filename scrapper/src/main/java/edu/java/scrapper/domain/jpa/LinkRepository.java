package edu.java.scrapper.domain.jpa;

import edu.java.scrapper.domain.jpa.entities.LinkEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface LinkRepository extends JpaRepository<LinkEntity, Long> {
    List<LinkEntity> findAllByUsers_Id(long id);

    boolean existsByUsers_Id(long id);

    LinkEntity findByUrl(String url);
}
