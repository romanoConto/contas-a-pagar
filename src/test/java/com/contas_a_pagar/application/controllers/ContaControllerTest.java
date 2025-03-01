package com.contas_a_pagar.application.controllers;

import com.contas_a_pagar.application.dto.conta.ContaResponse;
import com.contas_a_pagar.application.dto.conta.CreateRequest;
import com.contas_a_pagar.application.dto.conta.UpdateRequest;
import com.contas_a_pagar.domain.enums.Situacao;
import com.contas_a_pagar.domain.services.ContaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class ContaControllerTest {

    @Mock
    private ContaService contaService;

    @InjectMocks
    private ContaController contaController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(contaController).build();
    }

    @Test
    void getAll() throws Exception {
        ContaResponse contaResponse = new ContaResponse();
        contaResponse.setId(1);
        contaResponse.setDescricao("Conta Teste");
        contaResponse.setValor(new BigDecimal(100.0));
        contaResponse.setSituacao(Situacao.Pendente);

        List<ContaResponse> contaList = new ArrayList<>();
        contaList.add(contaResponse);
        Pageable pageable = PageRequest.of(0, 10);
        PageImpl page = new PageImpl<>(contaList, pageable, contaList.size());
        when(contaService.getAll(1, 10)).thenReturn(page);

        mockMvc.perform(get("/api/v1/contas")
                        .param("pagina", "1")
                        .param("itens", "10"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        verify(contaService, times(1)).getAll(1, 10);
    }

    @Test
    void getById() throws Exception {
        ContaResponse contaResponse = new ContaResponse();
        contaResponse.setId(1);
        contaResponse.setDescricao("Conta Teste");
        contaResponse.setValor(new BigDecimal(100.0));
        contaResponse.setSituacao(Situacao.Pendente);

        when(contaService.get(1)).thenReturn(contaResponse);

        mockMvc.perform(get("/api/v1/contas/{id}", 1))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.descricao").value("Conta Teste"));

        verify(contaService, times(1)).get(1);
    }

    @Test
    void getContasAPagar() throws Exception {
        ContaResponse contaResponse = new ContaResponse();
        contaResponse.setId(1);
        contaResponse.setDescricao("Conta Teste");
        contaResponse.setValor(new BigDecimal(100.0));
        contaResponse.setSituacao(Situacao.Pendente);

        List<ContaResponse> contaList = new ArrayList<>();
        contaList.add(contaResponse);
        Pageable pageable = PageRequest.of(0, 10);
        PageImpl page = new PageImpl<>(contaList, pageable, contaList.size());
        when(contaService.getContasAPagar(1, 10, null, null, null)).thenReturn(page);

        mockMvc.perform(get("/api/v1/contas/contas-a-pagar")
                        .param("pagina", "1")
                        .param("itens", "10"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content[0].descricao").value("Conta Teste"));

        verify(contaService, times(1)).getContasAPagar(1, 10, null, null, null);

    }

    @Test
    void getValorTotalPeriodo() throws Exception {
        BigDecimal total = new BigDecimal(200.0);

        when(contaService.getValorTotalPeriodo(any(), any())).thenReturn(total);

        mockMvc.perform(get("/api/v1/contas/valor-total-periodo")
                        .param("dataPagamentoInicial", "2023-01-01")
                        .param("dataPagamentoFinal", "2023-12-31"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string(total.toString()));

        verify(contaService, times(1)).getValorTotalPeriodo(any(), any());
    }

    @Test
    void testPost() throws Exception {
        CreateRequest createRequest = new CreateRequest();
        createRequest.setDescricao("Nova Conta");
        createRequest.setValor(new BigDecimal(100.0));

        doNothing().when(contaService).post(any(CreateRequest.class));

        mockMvc.perform(post("/api/v1/contas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"descricao\":\"Nova Conta\",\"valor\":100.0}"))
                .andExpect(status().isNoContent());

        verify(contaService, times(1)).post(any(CreateRequest.class));
        ArgumentCaptor<CreateRequest> captor = ArgumentCaptor.forClass(CreateRequest.class);
        verify(contaService).post(captor.capture());

        CreateRequest capturedRequest = captor.getValue();
        assertEquals("Nova Conta", capturedRequest.getDescricao());
        assertEquals(new BigDecimal("100.0"), capturedRequest.getValor());
    }

    @Test
    void testPut() throws Exception {
        UpdateRequest updateRequest = new UpdateRequest();
        updateRequest.setDescricao("Conta Atualizada");
        updateRequest.setValor(new BigDecimal(200.0));

        doNothing().when(contaService).put(eq(1), any(UpdateRequest.class));

        mockMvc.perform(put("/api/v1/contas/{id}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"descricao\":\"Conta Atualizada\",\"valor\":200.0}"))
                .andExpect(status().isNoContent());

        verify(contaService, times(1)).put(eq(1), any(UpdateRequest.class));
        ArgumentCaptor<UpdateRequest> captor = ArgumentCaptor.forClass(UpdateRequest.class);
        verify(contaService).put(eq(1), captor.capture());
    }

    @Test
    void atualizarSituacao() throws Exception {
        Situacao situacao = Situacao.Pago;

        doNothing().when(contaService).atualizarSituacao(eq(1), any(Situacao.class));

        mockMvc.perform(put("/api/v1/contas/{id}/atualizar-situacao", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("1"))
                .andExpect(status().isNoContent());

        verify(contaService, times(1)).atualizarSituacao(1, situacao);
    }


    @Test
    void getModeloCsv() throws Exception {
        String content = "nome,valor\nConta 1,100\nConta 2,200";
        InputStream inputStream = new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8));
        InputStreamResource resource = new InputStreamResource(inputStream);

        when(contaService.getModeloCsv()).thenReturn(resource);

        mockMvc.perform(get("/api/v1/contas/baixar-modelo-csv"))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Disposition", "attachment; filename=contas.csv"))
                .andExpect(content().contentType("application/json"));

        verify(contaService, times(1)).getModeloCsv();
    }

    @Test
    void importarPorCsv() throws Exception {
        MultipartFile mockFile = new MockMultipartFile("file", "test.csv", "text/csv", "test,csv,file".getBytes());

        mockMvc.perform(multipart("/api/v1/contas/importar-por-csv")
                        .file("file", "test,csv,file".getBytes()))
                .andExpect(status().isOk())
                .andExpect(content().string("Dados importados com sucesso!"));

        ArgumentCaptor<MultipartFile> captor = ArgumentCaptor.forClass(MultipartFile.class);
        verify(contaService, times(1)).importarPorCsv(captor.capture());

        assertArrayEquals(mockFile.getBytes(), captor.getValue().getBytes());
    }

}
