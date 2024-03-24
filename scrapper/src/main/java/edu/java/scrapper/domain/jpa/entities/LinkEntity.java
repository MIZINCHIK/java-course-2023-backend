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
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
    private URI url;

    @Enumerated(EnumType.STRING)
    private LinkDomain service;

    @Column(name = "last_update")
    private OffsetDateTime lastUpdate;

    @ManyToMany(mappedBy = "links")
    private List<UserEntity> users;

    public LinkEntity(Link link) throws URISyntaxException {
        url = link.getUrl().toURI();
        service = link.getDomain();
        lastUpdate = OffsetDateTime.now(ZoneOffset.UTC);
    }
}
