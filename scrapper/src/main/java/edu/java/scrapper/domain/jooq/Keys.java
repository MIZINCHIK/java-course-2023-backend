/*
 * This file is generated by jOOQ.
 */
package edu.java.scrapper.domain.jooq;


import edu.java.scrapper.domain.jooq.tables.Databasechangeloglock;
import edu.java.scrapper.domain.jooq.tables.FollowingLinks;
import edu.java.scrapper.domain.jooq.tables.Links;
import edu.java.scrapper.domain.jooq.tables.Users;
import edu.java.scrapper.domain.jooq.tables.records.DatabasechangeloglockRecord;
import edu.java.scrapper.domain.jooq.tables.records.FollowingLinksRecord;
import edu.java.scrapper.domain.jooq.tables.records.LinksRecord;
import edu.java.scrapper.domain.jooq.tables.records.UsersRecord;

import javax.annotation.processing.Generated;

import org.jooq.ForeignKey;
import org.jooq.TableField;
import org.jooq.UniqueKey;
import org.jooq.impl.DSL;
import org.jooq.impl.Internal;


/**
 * A class modelling foreign key relationships and constraints of tables in
 * public.
 */
@Generated(
    value = {
        "https://www.jooq.org",
        "jOOQ version:3.18.9"
    },
    comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes", "this-escape" })
public class Keys {

    // -------------------------------------------------------------------------
    // UNIQUE and PRIMARY KEY definitions
    // -------------------------------------------------------------------------

    public static final UniqueKey<DatabasechangeloglockRecord> DATABASECHANGELOGLOCK_PKEY = Internal.createUniqueKey(Databasechangeloglock.DATABASECHANGELOGLOCK, DSL.name("databasechangeloglock_pkey"), new TableField[] { Databasechangeloglock.DATABASECHANGELOGLOCK.ID }, true);
    public static final UniqueKey<FollowingLinksRecord> FOLLOWING_LINKS_PKEY = Internal.createUniqueKey(FollowingLinks.FOLLOWING_LINKS, DSL.name("following_links_pkey"), new TableField[] { FollowingLinks.FOLLOWING_LINKS.LINK_ID, FollowingLinks.FOLLOWING_LINKS.USER_ID }, true);
    public static final UniqueKey<LinksRecord> LINKS_PKEY = Internal.createUniqueKey(Links.LINKS, DSL.name("links_pkey"), new TableField[] { Links.LINKS.ID }, true);
    public static final UniqueKey<LinksRecord> LINKS_URL_KEY = Internal.createUniqueKey(Links.LINKS, DSL.name("links_url_key"), new TableField[] { Links.LINKS.URL }, true);
    public static final UniqueKey<UsersRecord> USERS_PKEY = Internal.createUniqueKey(Users.USERS, DSL.name("users_pkey"), new TableField[] { Users.USERS.ID }, true);

    // -------------------------------------------------------------------------
    // FOREIGN KEY definitions
    // -------------------------------------------------------------------------

    public static final ForeignKey<FollowingLinksRecord, LinksRecord> FOLLOWING_LINKS__FOLLOWING_LINKS_LINK_ID_FKEY = Internal.createForeignKey(FollowingLinks.FOLLOWING_LINKS, DSL.name("following_links_link_id_fkey"), new TableField[] { FollowingLinks.FOLLOWING_LINKS.LINK_ID }, Keys.LINKS_PKEY, new TableField[] { Links.LINKS.ID }, true);
    public static final ForeignKey<FollowingLinksRecord, UsersRecord> FOLLOWING_LINKS__FOLLOWING_LINKS_USER_ID_FKEY = Internal.createForeignKey(FollowingLinks.FOLLOWING_LINKS, DSL.name("following_links_user_id_fkey"), new TableField[] { FollowingLinks.FOLLOWING_LINKS.USER_ID }, Keys.USERS_PKEY, new TableField[] { Users.USERS.ID }, true);
}
