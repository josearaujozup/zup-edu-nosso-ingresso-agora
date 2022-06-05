package br.com.zup.edu.ingressoagora.controller;

import br.com.zup.edu.ingressoagora.model.EstadoIngresso;
import br.com.zup.edu.ingressoagora.model.Evento;
import br.com.zup.edu.ingressoagora.model.Ingresso;
import br.com.zup.edu.ingressoagora.repository.EventoRepository;
import br.com.zup.edu.ingressoagora.repository.IngressoRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureMockMvc(printOnlyOnFailure = false)
@ActiveProfiles("test")
class CancelarIngressoControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private IngressoRepository ingressoRepository;

    @Autowired
    private EventoRepository eventoRepository;

    @BeforeEach
    void setUp() {
        ingressoRepository.deleteAll();
        eventoRepository.deleteAll();
    }

    @Test
    @DisplayName("Não deve cancelar ingresso não cadastrado")
    void naoDeveCancelarIngressoNaoCadastrado() throws Exception {
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.patch("/ingressos/{id}/cancelamento",
                Integer.MAX_VALUE).contentType(MediaType.APPLICATION_JSON);

        Exception resolvedException = mockMvc.perform(request)
                .andExpect(
                        MockMvcResultMatchers.status().isNotFound()
                )
                .andReturn().getResolvedException();

        assertNotNull(resolvedException);
        assertEquals(ResponseStatusException.class,resolvedException.getClass());
        assertEquals("Este ingresso não existe.", ((ResponseStatusException) resolvedException).getReason());
    }

    @Test
    @DisplayName("Não deve cancelar ingresso com menos de um dia para o evento")
    void naoDeveCancelarIngressoComMenosDeUmDiaParaEvento() throws Exception {
        Evento zupCon = new Evento("ZupCon", LocalDate.now(), new BigDecimal("100"));
        eventoRepository.save(zupCon);

        Ingresso ingresso = new Ingresso(zupCon);
        ingressoRepository.save(ingresso);

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.patch("/ingressos/{id}/cancelamento",
                ingresso.getId()).contentType(MediaType.APPLICATION_JSON);

        Exception resolvedException = mockMvc.perform(request)
                .andExpect(
                        MockMvcResultMatchers.status().isUnprocessableEntity()
                )
                .andReturn().getResolvedException();

        assertNotNull(resolvedException);
        assertEquals(ResponseStatusException.class,resolvedException.getClass());
        assertEquals("Não é possivel cancelar faltando menos de 1 dia para data do evento", ((ResponseStatusException) resolvedException).getReason());
    }

    @Test
    @DisplayName("Não deve cancelar ingresso já consumido")
    void naoDeveCancelarIngressoJaConsumido() throws Exception {
        Evento zupCon = new Evento("ZupCon", LocalDate.of(2022,6,26), new BigDecimal("100"));
        eventoRepository.save(zupCon);

        Ingresso ingresso = new Ingresso(zupCon);
        ingresso.consumir();
        ingressoRepository.save(ingresso);

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.patch("/ingressos/{id}/cancelamento",
                ingresso.getId()).contentType(MediaType.APPLICATION_JSON);

        Exception resolvedException = mockMvc.perform(request)
                .andExpect(
                        MockMvcResultMatchers.status().isUnprocessableEntity()
                )
                .andReturn().getResolvedException();

        assertNotNull(resolvedException);
        assertEquals(ResponseStatusException.class,resolvedException.getClass());
        assertEquals("Impossivel cancelar um Ingresso já consumido.", ((ResponseStatusException) resolvedException).getReason());
    }

    @Test
    @DisplayName("Deve cancelar ingresso")
    void deveCancelarIngresso() throws Exception {
        Evento zupCon = new Evento("ZupCon", LocalDate.of(2022, 6, 26), new BigDecimal("100"));
        eventoRepository.save(zupCon);

        Ingresso ingresso = new Ingresso(zupCon);
        ingressoRepository.save(ingresso);

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.patch("/ingressos/{id}/cancelamento",
                ingresso.getId()).contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(request)
                .andExpect(
                        MockMvcResultMatchers.status().isNoContent()
                );

        Optional<Ingresso> possivelIngresso = ingressoRepository.findById(ingresso.getId());
        assertTrue(possivelIngresso.isPresent());
        assertEquals(EstadoIngresso.CANCELADO,possivelIngresso.get().getEstado());
    }
}