/*
 * This file is generated by jOOQ.
 */
package edu.java.scrapper.domain.jooq.tables;


import edu.java.scrapper.domain.jooq.Keys;
import edu.java.scrapper.domain.jooq.Public;
import edu.java.scrapper.domain.jooq.tables.records.FollowingLinksRecord;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

import javax.annotation.processing.Generated;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jooq.Field;
import org.jooq.ForeignKey;
import org.jooq.Function2;
import org.jooq.Name;
import org.jooq.Record;
import org.jooq.Records;
import org.jooq.Row2;
import org.jooq.Schema;
import org.jooq.SelectField;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.TableOptions;
import org.jooq.UniqueKey;
import org.jooq.impl.DSL;
import org.jooq.impl.SQLDataType;
import org.jooq.impl.TableImpl;


/**
 * Provides many-to-many relation between links and users
 */
@Generated(
    value = {
        "https://www.jooq.org",
        "jOOQ version:3.18.9"
    },
    comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes", "this-escape" })
public class FollowingLinks extends TableImpl<FollowingLinksRecord> {

    private static final long serialVersionUID = 1L;

    /**
     * The reference instance of <code>public.following_links</code>
     */
    public static final FollowingLinks FOLLOWING_LINKS = new FollowingLinks();

    /**
     * The class holding records for this type
     */
    @Override
    @NotNull
    public Class<FollowingLinksRecord> getRecordType() {
        return FollowingLinksRecord.class;
    }

    /**
     * The column <code>public.following_links.user_id</code>.
     */
    public final TableField<FollowingLinksRecord, Long> USER_ID = createField(DSL.name("user_id"), SQLDataType.BIGINT.nullable(false), this, "");

    /**
     * The column <code>public.following_links.link_id</code>.
     */
    public final TableField<FollowingLinksRecord, Long> LINK_ID = createField(DSL.name("link_id"), SQLDataType.BIGINT.nullable(false), this, "");

    private FollowingLinks(Name alias, Table<FollowingLinksRecord> aliased) {
        this(alias, aliased, null);
    }

    private FollowingLinks(Name alias, Table<FollowingLinksRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment("Provides many-to-many relation between links and users"), TableOptions.table());
    }

    /**
     * Create an aliased <code>public.following_links</code> table reference
     */
    public FollowingLinks(String alias) {
        this(DSL.name(alias), FOLLOWING_LINKS);
    }

    /**
     * Create an aliased <code>public.following_links</code> table reference
     */
    public FollowingLinks(Name alias) {
        this(alias, FOLLOWING_LINKS);
    }

    /**
     * Create a <code>public.following_links</code> table reference
     */
    public FollowingLinks() {
        this(DSL.name("following_links"), null);
    }

    public <O extends Record> FollowingLinks(Table<O> child, ForeignKey<O, FollowingLinksRecord> key) {
        super(child, key, FOLLOWING_LINKS);
    }

    @Override
    @Nullable
    public Schema getSchema() {
        return aliased() ? null : Public.PUBLIC;
    }

    @Override
    @NotNull
    public UniqueKey<FollowingLinksRecord> getPrimaryKey() {
        return Keys.FOLLOWING_LINKS_PKEY;
    }

    @Override
    @NotNull
    public List<ForeignKey<FollowingLinksRecord, ?>> getReferences() {
        return Arrays.asList(Keys.FOLLOWING_LINKS__FOLLOWING_LINKS_USER_ID_FKEY, Keys.FOLLOWING_LINKS__FOLLOWING_LINKS_LINK_ID_FKEY);
    }

    private transient Users _users;
    private transient Links _links;

    /**
     * Get the implicit join path to the <code>public.users</code> table.
     */
    public Users users() {
        if (_users == null)
            _users = new Users(this, Keys.FOLLOWING_LINKS__FOLLOWING_LINKS_USER_ID_FKEY);

        return _users;
    }

    /**
     * Get the implicit join path to the <code>public.links</code> table.
     */
    public Links links() {
        if (_links == null)
            _links = new Links(this, Keys.FOLLOWING_LINKS__FOLLOWING_LINKS_LINK_ID_FKEY);

        return _links;
    }

    @Override
    @NotNull
    public FollowingLinks as(String alias) {
        return new FollowingLinks(DSL.name(alias), this);
    }

    @Override
    @NotNull
    public FollowingLinks as(Name alias) {
        return new FollowingLinks(alias, this);
    }

    @Override
    @NotNull
    public FollowingLinks as(Table<?> alias) {
        return new FollowingLinks(alias.getQualifiedName(), this);
    }

    /**
     * Rename this table
     */
    @Override
    @NotNull
    public FollowingLinks rename(String name) {
        return new FollowingLinks(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    @NotNull
    public FollowingLinks rename(Name name) {
        return new FollowingLinks(name, null);
    }

    /**
     * Rename this table
     */
    @Override
    @NotNull
    public FollowingLinks rename(Table<?> name) {
        return new FollowingLinks(name.getQualifiedName(), null);
    }

    // -------------------------------------------------------------------------
    // Row2 type methods
    // -------------------------------------------------------------------------

    @Override
    @NotNull
    public Row2<Long, Long> fieldsRow() {
        return (Row2) super.fieldsRow();
    }

    /**
     * Convenience mapping calling {@link SelectField#convertFrom(Function)}.
     */
    public <U> SelectField<U> mapping(Function2<? super Long, ? super Long, ? extends U> from) {
        return convertFrom(Records.mapping(from));
    }

    /**
     * Convenience mapping calling {@link SelectField#convertFrom(Class,
     * Function)}.
     */
    public <U> SelectField<U> mapping(Class<U> toType, Function2<? super Long, ? super Long, ? extends U> from) {
        return convertFrom(toType, Records.mapping(from));
    }
}
