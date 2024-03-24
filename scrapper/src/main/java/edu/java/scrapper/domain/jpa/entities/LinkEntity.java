package edu.java.scrapper.domain.jpa.entities;

import edu.java.model.links.Link;
import edu.java.model.links.LinkDomain;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import java.net.URISyntaxException;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcType;
import org.hibernate.dialect.PostgreSQLEnumJdbcType;

@Entity
@NoArgsConstructor
@Getter
@Setter
@Table(name = "links")
public class LinkEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(unique = true)
    private String url;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "external_service")
    @JdbcType(PostgreSQLEnumJdbcType.class)
    private LinkDomain service;

    @Column(name = "last_update")
    private OffsetDateTime lastUpdate;

    @ManyToMany
    @JoinTable(
        name = "following_links",
        joinColumns = @JoinColumn(name = "link_id"),
        inverseJoinColumns = @JoinColumn(name = "user_id"))
    private List<UserEntity> users = new ArrayList<>();

    public LinkEntity(Link link) throws URISyntaxException {
        url = link.getUrl().toString();
        service = link.getDomain();
        lastUpdate = OffsetDateTime.now(ZoneOffset.UTC);
    }
}
