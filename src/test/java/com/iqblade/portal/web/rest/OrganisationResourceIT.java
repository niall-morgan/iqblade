package com.iqblade.portal.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.iqblade.portal.IntegrationTest;
import com.iqblade.portal.domain.Organisation;
import com.iqblade.portal.repository.OrganisationRepository;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link OrganisationResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class OrganisationResourceIT {

    private static final String DEFAULT_COMPANY_NUMBER = "AAAAAAAAAA";
    private static final String UPDATED_COMPANY_NUMBER = "BBBBBBBBBB";

    private static final String DEFAULT_COMPANY_NAME = "AAAAAAAAAA";
    private static final String UPDATED_COMPANY_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_WEBSITE = "AAAAAAAAAA";
    private static final String UPDATED_WEBSITE = "BBBBBBBBBB";

    private static final String DEFAULT_STATUS = "AAAAAAAAAA";
    private static final String UPDATED_STATUS = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/organisations";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private OrganisationRepository organisationRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restOrganisationMockMvc;

    private Organisation organisation;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Organisation createEntity(EntityManager em) {
        Organisation organisation = new Organisation()
            .companyNumber(DEFAULT_COMPANY_NUMBER)
            .companyName(DEFAULT_COMPANY_NAME)
            .website(DEFAULT_WEBSITE)
            .status(DEFAULT_STATUS);
        return organisation;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Organisation createUpdatedEntity(EntityManager em) {
        Organisation organisation = new Organisation()
            .companyNumber(UPDATED_COMPANY_NUMBER)
            .companyName(UPDATED_COMPANY_NAME)
            .website(UPDATED_WEBSITE)
            .status(UPDATED_STATUS);
        return organisation;
    }

    @BeforeEach
    public void initTest() {
        organisation = createEntity(em);
    }

    @Test
    @Transactional
    void createOrganisation() throws Exception {
        int databaseSizeBeforeCreate = organisationRepository.findAll().size();
        // Create the Organisation
        restOrganisationMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(organisation)))
            .andExpect(status().isCreated());

        // Validate the Organisation in the database
        List<Organisation> organisationList = organisationRepository.findAll();
        assertThat(organisationList).hasSize(databaseSizeBeforeCreate + 1);
        Organisation testOrganisation = organisationList.get(organisationList.size() - 1);
        assertThat(testOrganisation.getCompanyNumber()).isEqualTo(DEFAULT_COMPANY_NUMBER);
        assertThat(testOrganisation.getCompanyName()).isEqualTo(DEFAULT_COMPANY_NAME);
        assertThat(testOrganisation.getWebsite()).isEqualTo(DEFAULT_WEBSITE);
        assertThat(testOrganisation.getStatus()).isEqualTo(DEFAULT_STATUS);
    }

    @Test
    @Transactional
    void createOrganisationWithExistingId() throws Exception {
        // Create the Organisation with an existing ID
        organisation.setId(1L);

        int databaseSizeBeforeCreate = organisationRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restOrganisationMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(organisation)))
            .andExpect(status().isBadRequest());

        // Validate the Organisation in the database
        List<Organisation> organisationList = organisationRepository.findAll();
        assertThat(organisationList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllOrganisations() throws Exception {
        // Initialize the database
        organisationRepository.saveAndFlush(organisation);

        // Get all the organisationList
        restOrganisationMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(organisation.getId().intValue())))
            .andExpect(jsonPath("$.[*].companyNumber").value(hasItem(DEFAULT_COMPANY_NUMBER)))
            .andExpect(jsonPath("$.[*].companyName").value(hasItem(DEFAULT_COMPANY_NAME)))
            .andExpect(jsonPath("$.[*].website").value(hasItem(DEFAULT_WEBSITE)))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS)));
    }

    @Test
    @Transactional
    void getOrganisation() throws Exception {
        // Initialize the database
        organisationRepository.saveAndFlush(organisation);

        // Get the organisation
        restOrganisationMockMvc
            .perform(get(ENTITY_API_URL_ID, organisation.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(organisation.getId().intValue()))
            .andExpect(jsonPath("$.companyNumber").value(DEFAULT_COMPANY_NUMBER))
            .andExpect(jsonPath("$.companyName").value(DEFAULT_COMPANY_NAME))
            .andExpect(jsonPath("$.website").value(DEFAULT_WEBSITE))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS));
    }

    @Test
    @Transactional
    void getOrganisationsByIdFiltering() throws Exception {
        // Initialize the database
        organisationRepository.saveAndFlush(organisation);

        Long id = organisation.getId();

        defaultOrganisationShouldBeFound("id.equals=" + id);
        defaultOrganisationShouldNotBeFound("id.notEquals=" + id);

        defaultOrganisationShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultOrganisationShouldNotBeFound("id.greaterThan=" + id);

        defaultOrganisationShouldBeFound("id.lessThanOrEqual=" + id);
        defaultOrganisationShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllOrganisationsByCompanyNumberIsEqualToSomething() throws Exception {
        // Initialize the database
        organisationRepository.saveAndFlush(organisation);

        // Get all the organisationList where companyNumber equals to DEFAULT_COMPANY_NUMBER
        defaultOrganisationShouldBeFound("companyNumber.equals=" + DEFAULT_COMPANY_NUMBER);

        // Get all the organisationList where companyNumber equals to UPDATED_COMPANY_NUMBER
        defaultOrganisationShouldNotBeFound("companyNumber.equals=" + UPDATED_COMPANY_NUMBER);
    }

    @Test
    @Transactional
    void getAllOrganisationsByCompanyNumberIsNotEqualToSomething() throws Exception {
        // Initialize the database
        organisationRepository.saveAndFlush(organisation);

        // Get all the organisationList where companyNumber not equals to DEFAULT_COMPANY_NUMBER
        defaultOrganisationShouldNotBeFound("companyNumber.notEquals=" + DEFAULT_COMPANY_NUMBER);

        // Get all the organisationList where companyNumber not equals to UPDATED_COMPANY_NUMBER
        defaultOrganisationShouldBeFound("companyNumber.notEquals=" + UPDATED_COMPANY_NUMBER);
    }

    @Test
    @Transactional
    void getAllOrganisationsByCompanyNumberIsInShouldWork() throws Exception {
        // Initialize the database
        organisationRepository.saveAndFlush(organisation);

        // Get all the organisationList where companyNumber in DEFAULT_COMPANY_NUMBER or UPDATED_COMPANY_NUMBER
        defaultOrganisationShouldBeFound("companyNumber.in=" + DEFAULT_COMPANY_NUMBER + "," + UPDATED_COMPANY_NUMBER);

        // Get all the organisationList where companyNumber equals to UPDATED_COMPANY_NUMBER
        defaultOrganisationShouldNotBeFound("companyNumber.in=" + UPDATED_COMPANY_NUMBER);
    }

    @Test
    @Transactional
    void getAllOrganisationsByCompanyNumberIsNullOrNotNull() throws Exception {
        // Initialize the database
        organisationRepository.saveAndFlush(organisation);

        // Get all the organisationList where companyNumber is not null
        defaultOrganisationShouldBeFound("companyNumber.specified=true");

        // Get all the organisationList where companyNumber is null
        defaultOrganisationShouldNotBeFound("companyNumber.specified=false");
    }

    @Test
    @Transactional
    void getAllOrganisationsByCompanyNumberContainsSomething() throws Exception {
        // Initialize the database
        organisationRepository.saveAndFlush(organisation);

        // Get all the organisationList where companyNumber contains DEFAULT_COMPANY_NUMBER
        defaultOrganisationShouldBeFound("companyNumber.contains=" + DEFAULT_COMPANY_NUMBER);

        // Get all the organisationList where companyNumber contains UPDATED_COMPANY_NUMBER
        defaultOrganisationShouldNotBeFound("companyNumber.contains=" + UPDATED_COMPANY_NUMBER);
    }

    @Test
    @Transactional
    void getAllOrganisationsByCompanyNumberNotContainsSomething() throws Exception {
        // Initialize the database
        organisationRepository.saveAndFlush(organisation);

        // Get all the organisationList where companyNumber does not contain DEFAULT_COMPANY_NUMBER
        defaultOrganisationShouldNotBeFound("companyNumber.doesNotContain=" + DEFAULT_COMPANY_NUMBER);

        // Get all the organisationList where companyNumber does not contain UPDATED_COMPANY_NUMBER
        defaultOrganisationShouldBeFound("companyNumber.doesNotContain=" + UPDATED_COMPANY_NUMBER);
    }

    @Test
    @Transactional
    void getAllOrganisationsByCompanyNameIsEqualToSomething() throws Exception {
        // Initialize the database
        organisationRepository.saveAndFlush(organisation);

        // Get all the organisationList where companyName equals to DEFAULT_COMPANY_NAME
        defaultOrganisationShouldBeFound("companyName.equals=" + DEFAULT_COMPANY_NAME);

        // Get all the organisationList where companyName equals to UPDATED_COMPANY_NAME
        defaultOrganisationShouldNotBeFound("companyName.equals=" + UPDATED_COMPANY_NAME);
    }

    @Test
    @Transactional
    void getAllOrganisationsByCompanyNameIsNotEqualToSomething() throws Exception {
        // Initialize the database
        organisationRepository.saveAndFlush(organisation);

        // Get all the organisationList where companyName not equals to DEFAULT_COMPANY_NAME
        defaultOrganisationShouldNotBeFound("companyName.notEquals=" + DEFAULT_COMPANY_NAME);

        // Get all the organisationList where companyName not equals to UPDATED_COMPANY_NAME
        defaultOrganisationShouldBeFound("companyName.notEquals=" + UPDATED_COMPANY_NAME);
    }

    @Test
    @Transactional
    void getAllOrganisationsByCompanyNameIsInShouldWork() throws Exception {
        // Initialize the database
        organisationRepository.saveAndFlush(organisation);

        // Get all the organisationList where companyName in DEFAULT_COMPANY_NAME or UPDATED_COMPANY_NAME
        defaultOrganisationShouldBeFound("companyName.in=" + DEFAULT_COMPANY_NAME + "," + UPDATED_COMPANY_NAME);

        // Get all the organisationList where companyName equals to UPDATED_COMPANY_NAME
        defaultOrganisationShouldNotBeFound("companyName.in=" + UPDATED_COMPANY_NAME);
    }

    @Test
    @Transactional
    void getAllOrganisationsByCompanyNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        organisationRepository.saveAndFlush(organisation);

        // Get all the organisationList where companyName is not null
        defaultOrganisationShouldBeFound("companyName.specified=true");

        // Get all the organisationList where companyName is null
        defaultOrganisationShouldNotBeFound("companyName.specified=false");
    }

    @Test
    @Transactional
    void getAllOrganisationsByCompanyNameContainsSomething() throws Exception {
        // Initialize the database
        organisationRepository.saveAndFlush(organisation);

        // Get all the organisationList where companyName contains DEFAULT_COMPANY_NAME
        defaultOrganisationShouldBeFound("companyName.contains=" + DEFAULT_COMPANY_NAME);

        // Get all the organisationList where companyName contains UPDATED_COMPANY_NAME
        defaultOrganisationShouldNotBeFound("companyName.contains=" + UPDATED_COMPANY_NAME);
    }

    @Test
    @Transactional
    void getAllOrganisationsByCompanyNameNotContainsSomething() throws Exception {
        // Initialize the database
        organisationRepository.saveAndFlush(organisation);

        // Get all the organisationList where companyName does not contain DEFAULT_COMPANY_NAME
        defaultOrganisationShouldNotBeFound("companyName.doesNotContain=" + DEFAULT_COMPANY_NAME);

        // Get all the organisationList where companyName does not contain UPDATED_COMPANY_NAME
        defaultOrganisationShouldBeFound("companyName.doesNotContain=" + UPDATED_COMPANY_NAME);
    }

    @Test
    @Transactional
    void getAllOrganisationsByWebsiteIsEqualToSomething() throws Exception {
        // Initialize the database
        organisationRepository.saveAndFlush(organisation);

        // Get all the organisationList where website equals to DEFAULT_WEBSITE
        defaultOrganisationShouldBeFound("website.equals=" + DEFAULT_WEBSITE);

        // Get all the organisationList where website equals to UPDATED_WEBSITE
        defaultOrganisationShouldNotBeFound("website.equals=" + UPDATED_WEBSITE);
    }

    @Test
    @Transactional
    void getAllOrganisationsByWebsiteIsNotEqualToSomething() throws Exception {
        // Initialize the database
        organisationRepository.saveAndFlush(organisation);

        // Get all the organisationList where website not equals to DEFAULT_WEBSITE
        defaultOrganisationShouldNotBeFound("website.notEquals=" + DEFAULT_WEBSITE);

        // Get all the organisationList where website not equals to UPDATED_WEBSITE
        defaultOrganisationShouldBeFound("website.notEquals=" + UPDATED_WEBSITE);
    }

    @Test
    @Transactional
    void getAllOrganisationsByWebsiteIsInShouldWork() throws Exception {
        // Initialize the database
        organisationRepository.saveAndFlush(organisation);

        // Get all the organisationList where website in DEFAULT_WEBSITE or UPDATED_WEBSITE
        defaultOrganisationShouldBeFound("website.in=" + DEFAULT_WEBSITE + "," + UPDATED_WEBSITE);

        // Get all the organisationList where website equals to UPDATED_WEBSITE
        defaultOrganisationShouldNotBeFound("website.in=" + UPDATED_WEBSITE);
    }

    @Test
    @Transactional
    void getAllOrganisationsByWebsiteIsNullOrNotNull() throws Exception {
        // Initialize the database
        organisationRepository.saveAndFlush(organisation);

        // Get all the organisationList where website is not null
        defaultOrganisationShouldBeFound("website.specified=true");

        // Get all the organisationList where website is null
        defaultOrganisationShouldNotBeFound("website.specified=false");
    }

    @Test
    @Transactional
    void getAllOrganisationsByWebsiteContainsSomething() throws Exception {
        // Initialize the database
        organisationRepository.saveAndFlush(organisation);

        // Get all the organisationList where website contains DEFAULT_WEBSITE
        defaultOrganisationShouldBeFound("website.contains=" + DEFAULT_WEBSITE);

        // Get all the organisationList where website contains UPDATED_WEBSITE
        defaultOrganisationShouldNotBeFound("website.contains=" + UPDATED_WEBSITE);
    }

    @Test
    @Transactional
    void getAllOrganisationsByWebsiteNotContainsSomething() throws Exception {
        // Initialize the database
        organisationRepository.saveAndFlush(organisation);

        // Get all the organisationList where website does not contain DEFAULT_WEBSITE
        defaultOrganisationShouldNotBeFound("website.doesNotContain=" + DEFAULT_WEBSITE);

        // Get all the organisationList where website does not contain UPDATED_WEBSITE
        defaultOrganisationShouldBeFound("website.doesNotContain=" + UPDATED_WEBSITE);
    }

    @Test
    @Transactional
    void getAllOrganisationsByStatusIsEqualToSomething() throws Exception {
        // Initialize the database
        organisationRepository.saveAndFlush(organisation);

        // Get all the organisationList where status equals to DEFAULT_STATUS
        defaultOrganisationShouldBeFound("status.equals=" + DEFAULT_STATUS);

        // Get all the organisationList where status equals to UPDATED_STATUS
        defaultOrganisationShouldNotBeFound("status.equals=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    void getAllOrganisationsByStatusIsNotEqualToSomething() throws Exception {
        // Initialize the database
        organisationRepository.saveAndFlush(organisation);

        // Get all the organisationList where status not equals to DEFAULT_STATUS
        defaultOrganisationShouldNotBeFound("status.notEquals=" + DEFAULT_STATUS);

        // Get all the organisationList where status not equals to UPDATED_STATUS
        defaultOrganisationShouldBeFound("status.notEquals=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    void getAllOrganisationsByStatusIsInShouldWork() throws Exception {
        // Initialize the database
        organisationRepository.saveAndFlush(organisation);

        // Get all the organisationList where status in DEFAULT_STATUS or UPDATED_STATUS
        defaultOrganisationShouldBeFound("status.in=" + DEFAULT_STATUS + "," + UPDATED_STATUS);

        // Get all the organisationList where status equals to UPDATED_STATUS
        defaultOrganisationShouldNotBeFound("status.in=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    void getAllOrganisationsByStatusIsNullOrNotNull() throws Exception {
        // Initialize the database
        organisationRepository.saveAndFlush(organisation);

        // Get all the organisationList where status is not null
        defaultOrganisationShouldBeFound("status.specified=true");

        // Get all the organisationList where status is null
        defaultOrganisationShouldNotBeFound("status.specified=false");
    }

    @Test
    @Transactional
    void getAllOrganisationsByStatusContainsSomething() throws Exception {
        // Initialize the database
        organisationRepository.saveAndFlush(organisation);

        // Get all the organisationList where status contains DEFAULT_STATUS
        defaultOrganisationShouldBeFound("status.contains=" + DEFAULT_STATUS);

        // Get all the organisationList where status contains UPDATED_STATUS
        defaultOrganisationShouldNotBeFound("status.contains=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    void getAllOrganisationsByStatusNotContainsSomething() throws Exception {
        // Initialize the database
        organisationRepository.saveAndFlush(organisation);

        // Get all the organisationList where status does not contain DEFAULT_STATUS
        defaultOrganisationShouldNotBeFound("status.doesNotContain=" + DEFAULT_STATUS);

        // Get all the organisationList where status does not contain UPDATED_STATUS
        defaultOrganisationShouldBeFound("status.doesNotContain=" + UPDATED_STATUS);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultOrganisationShouldBeFound(String filter) throws Exception {
        restOrganisationMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(organisation.getId().intValue())))
            .andExpect(jsonPath("$.[*].companyNumber").value(hasItem(DEFAULT_COMPANY_NUMBER)))
            .andExpect(jsonPath("$.[*].companyName").value(hasItem(DEFAULT_COMPANY_NAME)))
            .andExpect(jsonPath("$.[*].website").value(hasItem(DEFAULT_WEBSITE)))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS)));

        // Check, that the count call also returns 1
        restOrganisationMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultOrganisationShouldNotBeFound(String filter) throws Exception {
        restOrganisationMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restOrganisationMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingOrganisation() throws Exception {
        // Get the organisation
        restOrganisationMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewOrganisation() throws Exception {
        // Initialize the database
        organisationRepository.saveAndFlush(organisation);

        int databaseSizeBeforeUpdate = organisationRepository.findAll().size();

        // Update the organisation
        Organisation updatedOrganisation = organisationRepository.findById(organisation.getId()).get();
        // Disconnect from session so that the updates on updatedOrganisation are not directly saved in db
        em.detach(updatedOrganisation);
        updatedOrganisation
            .companyNumber(UPDATED_COMPANY_NUMBER)
            .companyName(UPDATED_COMPANY_NAME)
            .website(UPDATED_WEBSITE)
            .status(UPDATED_STATUS);

        restOrganisationMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedOrganisation.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedOrganisation))
            )
            .andExpect(status().isOk());

        // Validate the Organisation in the database
        List<Organisation> organisationList = organisationRepository.findAll();
        assertThat(organisationList).hasSize(databaseSizeBeforeUpdate);
        Organisation testOrganisation = organisationList.get(organisationList.size() - 1);
        assertThat(testOrganisation.getCompanyNumber()).isEqualTo(UPDATED_COMPANY_NUMBER);
        assertThat(testOrganisation.getCompanyName()).isEqualTo(UPDATED_COMPANY_NAME);
        assertThat(testOrganisation.getWebsite()).isEqualTo(UPDATED_WEBSITE);
        assertThat(testOrganisation.getStatus()).isEqualTo(UPDATED_STATUS);
    }

    @Test
    @Transactional
    void putNonExistingOrganisation() throws Exception {
        int databaseSizeBeforeUpdate = organisationRepository.findAll().size();
        organisation.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restOrganisationMockMvc
            .perform(
                put(ENTITY_API_URL_ID, organisation.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(organisation))
            )
            .andExpect(status().isBadRequest());

        // Validate the Organisation in the database
        List<Organisation> organisationList = organisationRepository.findAll();
        assertThat(organisationList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchOrganisation() throws Exception {
        int databaseSizeBeforeUpdate = organisationRepository.findAll().size();
        organisation.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restOrganisationMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(organisation))
            )
            .andExpect(status().isBadRequest());

        // Validate the Organisation in the database
        List<Organisation> organisationList = organisationRepository.findAll();
        assertThat(organisationList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamOrganisation() throws Exception {
        int databaseSizeBeforeUpdate = organisationRepository.findAll().size();
        organisation.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restOrganisationMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(organisation)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Organisation in the database
        List<Organisation> organisationList = organisationRepository.findAll();
        assertThat(organisationList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateOrganisationWithPatch() throws Exception {
        // Initialize the database
        organisationRepository.saveAndFlush(organisation);

        int databaseSizeBeforeUpdate = organisationRepository.findAll().size();

        // Update the organisation using partial update
        Organisation partialUpdatedOrganisation = new Organisation();
        partialUpdatedOrganisation.setId(organisation.getId());

        partialUpdatedOrganisation.companyNumber(UPDATED_COMPANY_NUMBER);

        restOrganisationMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedOrganisation.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedOrganisation))
            )
            .andExpect(status().isOk());

        // Validate the Organisation in the database
        List<Organisation> organisationList = organisationRepository.findAll();
        assertThat(organisationList).hasSize(databaseSizeBeforeUpdate);
        Organisation testOrganisation = organisationList.get(organisationList.size() - 1);
        assertThat(testOrganisation.getCompanyNumber()).isEqualTo(UPDATED_COMPANY_NUMBER);
        assertThat(testOrganisation.getCompanyName()).isEqualTo(DEFAULT_COMPANY_NAME);
        assertThat(testOrganisation.getWebsite()).isEqualTo(DEFAULT_WEBSITE);
        assertThat(testOrganisation.getStatus()).isEqualTo(DEFAULT_STATUS);
    }

    @Test
    @Transactional
    void fullUpdateOrganisationWithPatch() throws Exception {
        // Initialize the database
        organisationRepository.saveAndFlush(organisation);

        int databaseSizeBeforeUpdate = organisationRepository.findAll().size();

        // Update the organisation using partial update
        Organisation partialUpdatedOrganisation = new Organisation();
        partialUpdatedOrganisation.setId(organisation.getId());

        partialUpdatedOrganisation
            .companyNumber(UPDATED_COMPANY_NUMBER)
            .companyName(UPDATED_COMPANY_NAME)
            .website(UPDATED_WEBSITE)
            .status(UPDATED_STATUS);

        restOrganisationMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedOrganisation.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedOrganisation))
            )
            .andExpect(status().isOk());

        // Validate the Organisation in the database
        List<Organisation> organisationList = organisationRepository.findAll();
        assertThat(organisationList).hasSize(databaseSizeBeforeUpdate);
        Organisation testOrganisation = organisationList.get(organisationList.size() - 1);
        assertThat(testOrganisation.getCompanyNumber()).isEqualTo(UPDATED_COMPANY_NUMBER);
        assertThat(testOrganisation.getCompanyName()).isEqualTo(UPDATED_COMPANY_NAME);
        assertThat(testOrganisation.getWebsite()).isEqualTo(UPDATED_WEBSITE);
        assertThat(testOrganisation.getStatus()).isEqualTo(UPDATED_STATUS);
    }

    @Test
    @Transactional
    void patchNonExistingOrganisation() throws Exception {
        int databaseSizeBeforeUpdate = organisationRepository.findAll().size();
        organisation.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restOrganisationMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, organisation.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(organisation))
            )
            .andExpect(status().isBadRequest());

        // Validate the Organisation in the database
        List<Organisation> organisationList = organisationRepository.findAll();
        assertThat(organisationList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchOrganisation() throws Exception {
        int databaseSizeBeforeUpdate = organisationRepository.findAll().size();
        organisation.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restOrganisationMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(organisation))
            )
            .andExpect(status().isBadRequest());

        // Validate the Organisation in the database
        List<Organisation> organisationList = organisationRepository.findAll();
        assertThat(organisationList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamOrganisation() throws Exception {
        int databaseSizeBeforeUpdate = organisationRepository.findAll().size();
        organisation.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restOrganisationMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(organisation))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Organisation in the database
        List<Organisation> organisationList = organisationRepository.findAll();
        assertThat(organisationList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteOrganisation() throws Exception {
        // Initialize the database
        organisationRepository.saveAndFlush(organisation);

        int databaseSizeBeforeDelete = organisationRepository.findAll().size();

        // Delete the organisation
        restOrganisationMockMvc
            .perform(delete(ENTITY_API_URL_ID, organisation.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Organisation> organisationList = organisationRepository.findAll();
        assertThat(organisationList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
