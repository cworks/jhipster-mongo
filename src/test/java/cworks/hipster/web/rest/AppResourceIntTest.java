package cworks.hipster.web.rest;

import cworks.hipster.Application;
import cworks.hipster.domain.App;
import cworks.hipster.repository.AppRepository;
import cworks.hipster.service.AppService;
import cworks.hipster.web.rest.dto.AppDTO;
import cworks.hipster.web.rest.mapper.AppMapper;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.hamcrest.Matchers.hasItem;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


/**
 * Test class for the AppResource REST controller.
 *
 * @see AppResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@IntegrationTest
public class AppResourceIntTest {

    private static final String DEFAULT_NAME = "AAAAA";
    private static final String UPDATED_NAME = "BBBBB";

    private static final LocalDate DEFAULT_CREATE_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_CREATE_DATE = LocalDate.now(ZoneId.systemDefault());
    private static final String DEFAULT_URL = "AAAAA";
    private static final String UPDATED_URL = "BBBBB";

    @Inject
    private AppRepository appRepository;

    @Inject
    private AppMapper appMapper;

    @Inject
    private AppService appService;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    private MockMvc restAppMockMvc;

    private App app;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        AppResource appResource = new AppResource();
        ReflectionTestUtils.setField(appResource, "appService", appService);
        ReflectionTestUtils.setField(appResource, "appMapper", appMapper);
        this.restAppMockMvc = MockMvcBuilders.standaloneSetup(appResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    @Before
    public void initTest() {
        appRepository.deleteAll();
        app = new App();
        app.setName(DEFAULT_NAME);
        app.setCreateDate(DEFAULT_CREATE_DATE);
        app.setUrl(DEFAULT_URL);
    }

    @Test
    public void createApp() throws Exception {
        int databaseSizeBeforeCreate = appRepository.findAll().size();

        // Create the App
        AppDTO appDTO = appMapper.appToAppDTO(app);

        restAppMockMvc.perform(post("/api/apps")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(appDTO)))
                .andExpect(status().isCreated());

        // Validate the App in the database
        List<App> apps = appRepository.findAll();
        assertThat(apps).hasSize(databaseSizeBeforeCreate + 1);
        App testApp = apps.get(apps.size() - 1);
        assertThat(testApp.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testApp.getCreateDate()).isEqualTo(DEFAULT_CREATE_DATE);
        assertThat(testApp.getUrl()).isEqualTo(DEFAULT_URL);
    }

    @Test
    public void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = appRepository.findAll().size();
        // set the field null
        app.setName(null);

        // Create the App, which fails.
        AppDTO appDTO = appMapper.appToAppDTO(app);

        restAppMockMvc.perform(post("/api/apps")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(appDTO)))
                .andExpect(status().isBadRequest());

        List<App> apps = appRepository.findAll();
        assertThat(apps).hasSize(databaseSizeBeforeTest);
    }

    @Test
    public void checkCreateDateIsRequired() throws Exception {
        int databaseSizeBeforeTest = appRepository.findAll().size();
        // set the field null
        app.setCreateDate(null);

        // Create the App, which fails.
        AppDTO appDTO = appMapper.appToAppDTO(app);

        restAppMockMvc.perform(post("/api/apps")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(appDTO)))
                .andExpect(status().isBadRequest());

        List<App> apps = appRepository.findAll();
        assertThat(apps).hasSize(databaseSizeBeforeTest);
    }

    @Test
    public void getAllApps() throws Exception {
        // Initialize the database
        appRepository.save(app);

        // Get all the apps
        restAppMockMvc.perform(get("/api/apps?sort=id,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[*].id").value(hasItem(app.getId())))
                .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
                .andExpect(jsonPath("$.[*].createDate").value(hasItem(DEFAULT_CREATE_DATE.toString())))
                .andExpect(jsonPath("$.[*].url").value(hasItem(DEFAULT_URL.toString())));
    }

    @Test
    public void getApp() throws Exception {
        // Initialize the database
        appRepository.save(app);

        // Get the app
        restAppMockMvc.perform(get("/api/apps/{id}", app.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(app.getId()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME.toString()))
            .andExpect(jsonPath("$.createDate").value(DEFAULT_CREATE_DATE.toString()))
            .andExpect(jsonPath("$.url").value(DEFAULT_URL.toString()));
    }

    @Test
    public void getNonExistingApp() throws Exception {
        // Get the app
        restAppMockMvc.perform(get("/api/apps/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    public void updateApp() throws Exception {
        // Initialize the database
        appRepository.save(app);

		int databaseSizeBeforeUpdate = appRepository.findAll().size();

        // Update the app
        app.setName(UPDATED_NAME);
        app.setCreateDate(UPDATED_CREATE_DATE);
        app.setUrl(UPDATED_URL);
        AppDTO appDTO = appMapper.appToAppDTO(app);

        restAppMockMvc.perform(put("/api/apps")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(appDTO)))
                .andExpect(status().isOk());

        // Validate the App in the database
        List<App> apps = appRepository.findAll();
        assertThat(apps).hasSize(databaseSizeBeforeUpdate);
        App testApp = apps.get(apps.size() - 1);
        assertThat(testApp.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testApp.getCreateDate()).isEqualTo(UPDATED_CREATE_DATE);
        assertThat(testApp.getUrl()).isEqualTo(UPDATED_URL);
    }

    @Test
    public void deleteApp() throws Exception {
        // Initialize the database
        appRepository.save(app);

		int databaseSizeBeforeDelete = appRepository.findAll().size();

        // Get the app
        restAppMockMvc.perform(delete("/api/apps/{id}", app.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate the database is empty
        List<App> apps = appRepository.findAll();
        assertThat(apps).hasSize(databaseSizeBeforeDelete - 1);
    }
}
