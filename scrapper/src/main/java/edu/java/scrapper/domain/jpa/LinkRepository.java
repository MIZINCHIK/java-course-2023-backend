package edu.java.scrapper.domain.jpa;

import edu.java.scrapper.domain.jpa.entities.LinkEntity;
import java.time.OffsetDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
@SuppressWarnings("MethodName")
public interface LinkRepository extends JpaRepository<LinkEntity, Long> {
    List<LinkEntity> findAllByUsers_Id(long id);

    boolean existsByIdAndUsers_Id(long id, long userId);

    LinkEntity findByUrl(String url);

    List<LinkEntity> findAllByLastUpdateLessThan(OffsetDateTime interval);

    void deleteByUrl(String url);
}
