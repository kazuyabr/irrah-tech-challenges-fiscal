package com.erpvarejo.service;

import com.erpvarejo.enums.CategoriaProduto;
import com.erpvarejo.enums.Uf;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.erpvarejo.model.Produto;
import com.erpvarejo.repository.ProdutoRepository;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FiscalServiceTest {

    @Mock
    private ProdutoRepository produtoRepository;

    @InjectMocks
    private FiscalService fiscalService;

    @BeforeEach
    void setUp() {
    }

    @Test
    void deveCalcularBaseCalculoCorretamente() {
        BigDecimal resultado = fiscalService.calcularBaseCalculo(2, new BigDecimal("10.00"), new BigDecimal("1.00"));

        assertEquals(new BigDecimal("19.00"), resultado);
    }

    @Test
    void deveCalcularBaseCalculoSemDesconto() {
        BigDecimal resultado = fiscalService.calcularBaseCalculo(3, new BigDecimal("10.00"), BigDecimal.ZERO);

        assertEquals(new BigDecimal("30.00"), resultado);
    }

    @Test
    void deveCalcularIcmsInternoCorretamente() {
        BigDecimal baseCalculo = new BigDecimal("100.00");
        BigDecimal resultado = fiscalService.calcularIcms(baseCalculo, Uf.PR, Uf.PR, CategoriaProduto.ELETRONICOS);

        assertEquals(new BigDecimal("18.00"), resultado);
    }

    @Test
    void deveCalcularIcmsInterestadualCorretamente() {
        BigDecimal baseCalculo = new BigDecimal("100.00");
        BigDecimal resultado = fiscalService.calcularIcms(baseCalculo, Uf.PR, Uf.RJ, CategoriaProduto.ELETRONICOS);

        assertEquals(new BigDecimal("12.00"), resultado);
    }

    @Test
    void deveCalcularIcmsCestaBasicaIsento() {
        BigDecimal baseCalculo = new BigDecimal("100.00");
        BigDecimal resultado = fiscalService.calcularIcms(baseCalculo, Uf.PR, Uf.PR, CategoriaProduto.CESTA_BASICA);

        assertEquals(new BigDecimal("0.00"), resultado);
    }

    @Test
    void deveCalcularPisCorretamente() {
        BigDecimal baseCalculo = new BigDecimal("100.00");
        BigDecimal resultado = fiscalService.calcularPis(baseCalculo, CategoriaProduto.ELETRONICOS);

        assertEquals(new BigDecimal("1.65"), resultado);
    }

    @Test
    void deveCalcularPisBebidaAlcoolicaZerado() {
        BigDecimal baseCalculo = new BigDecimal("100.00");
        BigDecimal resultado = fiscalService.calcularPis(baseCalculo, CategoriaProduto.BEBIDAS_ALCOOLICAS);

        assertEquals(new BigDecimal("0.00"), resultado);
    }

    @Test
    void deveCalcularCofinsCorretamente() {
        BigDecimal baseCalculo = new BigDecimal("100.00");
        BigDecimal resultado = fiscalService.calcularCofins(baseCalculo, CategoriaProduto.ELETRONICOS);

        assertEquals(new BigDecimal("7.60"), resultado);
    }

    @Test
    void deveCalcularCofinsBebidaAlcoolicaZerado() {
        BigDecimal baseCalculo = new BigDecimal("100.00");
        BigDecimal resultado = fiscalService.calcularCofins(baseCalculo, CategoriaProduto.BEBIDAS_ALCOOLICAS);

        assertEquals(new BigDecimal("0.00"), resultado);
    }

    @Test
    void deveCalcularImpostosProdutoCorreto() {
        Produto produto = new Produto("PROD-001", "Mouse USB", CategoriaProduto.ELETRONICOS, new BigDecimal("10.00"));
        when(produtoRepository.findByCodigo("PROD-001")).thenReturn(Optional.of(produto));

        com.erpvarejo.dto.NotaFiscalRequest request = new com.erpvarejo.dto.NotaFiscalRequest();
        request.setNumeroNota("NF-TESTE");
        request.setUfOrigem("PR");
        request.setUfDestino("RJ");

        com.erpvarejo.dto.ItemNotaRequest item = new com.erpvarejo.dto.ItemNotaRequest();
        item.setCodigoProduto("PROD-001");
        item.setNome("Mouse USB");
        item.setCategoria("ELETRONICOS");
        item.setQuantidade(1);
        item.setValorUnitario(new BigDecimal("10.00"));
        item.setDesconto(BigDecimal.ZERO);

        com.erpvarejo.dto.ImpostosInformados impostos = new com.erpvarejo.dto.ImpostosInformados();
        impostos.setIcms(new BigDecimal("1.20"));
        impostos.setPis(new BigDecimal("0.17"));
        impostos.setCofins(new BigDecimal("0.76"));
        item.setImpostosInformados(impostos);

        request.setItens(java.util.List.of(item));

        com.erpvarejo.dto.NotaFiscalResponse response = fiscalService.validarNota(request);

        assertEquals(com.erpvarejo.enums.StatusNota.APROVADA, response.getStatus());
        assertTrue(response.getDivergencias().isEmpty());
    }

    @Test
    void deveDetectarDivergenciaIcms() {
        Produto produto = new Produto("PROD-004", "Teclado Mecanico", CategoriaProduto.ELETRONICOS, new BigDecimal("100.00"));
        when(produtoRepository.findByCodigo("PROD-004")).thenReturn(Optional.of(produto));

        com.erpvarejo.dto.NotaFiscalRequest request = new com.erpvarejo.dto.NotaFiscalRequest();
        request.setNumeroNota("NF-TESTE");
        request.setUfOrigem("PR");
        request.setUfDestino("RJ");

        com.erpvarejo.dto.ItemNotaRequest item = new com.erpvarejo.dto.ItemNotaRequest();
        item.setCodigoProduto("PROD-004");
        item.setNome("Teclado Mecanico");
        item.setCategoria("ELETRONICOS");
        item.setQuantidade(1);
        item.setValorUnitario(new BigDecimal("100.00"));
        item.setDesconto(new BigDecimal("10.00"));

        com.erpvarejo.dto.ImpostosInformados impostos = new com.erpvarejo.dto.ImpostosInformados();
        impostos.setIcms(new BigDecimal("16.20"));
        impostos.setPis(new BigDecimal("1.49"));
        impostos.setCofins(new BigDecimal("6.84"));
        item.setImpostosInformados(impostos);

        request.setItens(java.util.List.of(item));

        com.erpvarejo.dto.NotaFiscalResponse response = fiscalService.validarNota(request);

        assertEquals(com.erpvarejo.enums.StatusNota.DIVERGENTE, response.getStatus());
        assertFalse(response.getDivergencias().isEmpty());
        assertEquals("ICMS", response.getDivergencias().get(0).getImposto());
    }
}
