package com.iqblade.portal.service.criteria;

import java.io.Serializable;
import java.util.Objects;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.Filter;
import tech.jhipster.service.filter.LongFilter;
import tech.jhipster.service.filter.StringFilter;

/**
 * Criteria class for the {@link com.iqblade.portal.domain.Organisation} entity. This class is used
 * in {@link com.iqblade.portal.web.rest.OrganisationResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /organisations?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
public class OrganisationCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter companyNumber;

    private StringFilter companyName;

    private StringFilter website;

    private StringFilter status;

    public OrganisationCriteria() {}

    public OrganisationCriteria(OrganisationCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.companyNumber = other.companyNumber == null ? null : other.companyNumber.copy();
        this.companyName = other.companyName == null ? null : other.companyName.copy();
        this.website = other.website == null ? null : other.website.copy();
        this.status = other.status == null ? null : other.status.copy();
    }

    @Override
    public OrganisationCriteria copy() {
        return new OrganisationCriteria(this);
    }

    public LongFilter getId() {
        return id;
    }

    public LongFilter id() {
        if (id == null) {
            id = new LongFilter();
        }
        return id;
    }

    public void setId(LongFilter id) {
        this.id = id;
    }

    public StringFilter getCompanyNumber() {
        return companyNumber;
    }

    public StringFilter companyNumber() {
        if (companyNumber == null) {
            companyNumber = new StringFilter();
        }
        return companyNumber;
    }

    public void setCompanyNumber(StringFilter companyNumber) {
        this.companyNumber = companyNumber;
    }

    public StringFilter getCompanyName() {
        return companyName;
    }

    public StringFilter companyName() {
        if (companyName == null) {
            companyName = new StringFilter();
        }
        return companyName;
    }

    public void setCompanyName(StringFilter companyName) {
        this.companyName = companyName;
    }

    public StringFilter getWebsite() {
        return website;
    }

    public StringFilter website() {
        if (website == null) {
            website = new StringFilter();
        }
        return website;
    }

    public void setWebsite(StringFilter website) {
        this.website = website;
    }

    public StringFilter getStatus() {
        return status;
    }

    public StringFilter status() {
        if (status == null) {
            status = new StringFilter();
        }
        return status;
    }

    public void setStatus(StringFilter status) {
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final OrganisationCriteria that = (OrganisationCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(companyNumber, that.companyNumber) &&
            Objects.equals(companyName, that.companyName) &&
            Objects.equals(website, that.website) &&
            Objects.equals(status, that.status)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, companyNumber, companyName, website, status);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "OrganisationCriteria{" +
            (id != null ? "id=" + id + ", " : "") +
            (companyNumber != null ? "companyNumber=" + companyNumber + ", " : "") +
            (companyName != null ? "companyName=" + companyName + ", " : "") +
            (website != null ? "website=" + website + ", " : "") +
            (status != null ? "status=" + status + ", " : "") +
            "}";
    }
}
