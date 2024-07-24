package com.shopme.admin.state;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shopme.admin.repository.CountryRepository;
import com.shopme.admin.repository.StateRepository;
import com.shopme.common.entity.Country;
import com.shopme.common.entity.State;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class StateRestControllerTests {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    CountryRepository countryRepo;

    @Autowired
    StateRepository stateRepo;
    @Autowired
    private PasswordEncoder passwordEncoder;

    private String encodedPassword;

    @BeforeEach
    public void setup() {
        encodedPassword = passwordEncoder.encode("nam2020");
    }


    @Test
    @WithMockUser(username = "nam@codejava.net", password = "nam2020", authorities = {"Admin"})
    public void testListByCountries() throws Exception {
        Integer countryId = 46;
        String url = "/states/list_by_country/" + countryId;

        MvcResult result = mockMvc.perform(get(url))
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn();

        String jsonResponse = result.getResponse().getContentAsString();
        State[] states = objectMapper.readValue(jsonResponse, State[].class);

        assertThat(states).hasSizeGreaterThan(1);
    }

    @Test
    @WithMockUser(username = "nam@codejava.net", password = "nam2020", authorities = {"Admin"})
    public void testCreateState() throws Exception {
        String url = "/states/save";
        Integer countryId = 960;
        Country country = countryRepo.findById(countryId).get();
        State state = new State("Arizona", country);

        MvcResult result = mockMvc.perform(post(url).contentType("application/json")
                        .content(objectMapper.writeValueAsString(state))
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        String response = result.getResponse().getContentAsString();
        Integer stateId = Integer.parseInt(response);
        Optional<State> findById = stateRepo.findById(stateId);

        assertThat(findById.isPresent());
    }

    @Test
    @WithMockUser(username = "nam@codejava.net", password = "nam2020", authorities = {"Admin"})
    public void testUpdateState() throws Exception {
        String url = "/states/save";
        Integer stateId = 948;
        String stateName = "Alaska";

        State state = stateRepo.findById(stateId).get();
        state.setName(stateName);

        mockMvc.perform(post(url).contentType("application/json")
                        .content(objectMapper.writeValueAsString(state))
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(String.valueOf(stateId)));

        Optional<State> findById = stateRepo.findById(stateId);
        assertThat(findById.isPresent());

        State updatedState = findById.get();
        assertThat(updatedState.getName()).isEqualTo(stateName);

    }

    @Test
    @WithMockUser(username = "nam@codejava.net", password = "nam2020", authorities = {"Admin"})
    public void testDeleteState() throws Exception {
        Integer countryId = 943;
        Country country = countryRepo.findById(countryId).get();
        State state = new State("TemporaryState", country);
        state = stateRepo.save(state);  // Save the state to get an ID

        Integer stateId = state.getId();
        String uri = "/states/delete/" + stateId;


        mockMvc.perform(delete(uri).with(csrf())).andExpect(status().isOk());

        Optional<State> findById = stateRepo.findById(stateId);
        assertThat(findById).isNotPresent();
    }
}