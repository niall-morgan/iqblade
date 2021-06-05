package com.iqblade.portal.service;

import com.iqblade.portal.domain.Organisation;
import com.iqblade.portal.repository.OrganisationRepository;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link Organisation}.
 */
@Service
@Transactional
public class OrganisationService {

    private final Logger log = LoggerFactory.getLogger(OrganisationService.class);

    private final OrganisationRepository organisationRepository;

    public OrganisationService(OrganisationRepository organisationRepository) {
        this.organisationRepository = organisationRepository;
    }

    /**
     * Save a organisation.
     *
     * @param organisation the entity to save.
     * @return the persisted entity.
     */
    public Organisation save(Organisation organisation) {
        log.debug("Request to save Organisation : {}", organisation);
        return organisationRepository.save(organisation);
    }

    /**
     * Partially update a organisation.
     *
     * @param organisation the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<Organisation> partialUpdate(Organisation organisation) {
        log.debug("Request to partially update Organisation : {}", organisation);

        return organisationRepository
            .findById(organisation.getId())
            .map(
                existingOrganisation -> {
                    if (organisation.getCompanyNumber() != null) {
                        existingOrganisation.setCompanyNumber(organisation.getCompanyNumber());
                    }
                    if (organisation.getCompanyName() != null) {
                        existingOrganisation.setCompanyName(organisation.getCompanyName());
                    }
                    if (organisation.getWebsite() != null) {
                        existingOrganisation.setWebsite(organisation.getWebsite());
                    }
                    if (organisation.getStatus() != null) {
                        existingOrganisation.setStatus(organisation.getStatus());
                    }

                    return existingOrganisation;
                }
            )
            .map(organisationRepository::save);
    }

    /**
     * Get all the organisations.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<Organisation> findAll(Pageable pageable) {
        log.debug("Request to get all Organisations");
        return organisationRepository.findAll(pageable);
    }

    /**
     * Get one organisation by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<Organisation> findOne(Long id) {
        log.debug("Request to get Organisation : {}", id);
        return organisationRepository.findById(id);
    }

    /**
     * Delete the organisation by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete Organisation : {}", id);
        organisationRepository.deleteById(id);
    }
}
