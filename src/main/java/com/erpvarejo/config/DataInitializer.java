package com.erpvarejo.config;

import com.erpvarejo.enums.CategoriaProduto;
import com.erpvarejo.enums.RoleUsuario;
import com.erpvarejo.model.Produto;
import com.erpvarejo.model.Usuario;
import com.erpvarejo.repository.ProdutoRepository;
import com.erpvarejo.repository.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UsuarioRepository usuarioRepository;
    private final ProdutoRepository produtoRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UsuarioRepository usuarioRepository,
                           ProdutoRepository produtoRepository,
                           PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.produtoRepository = produtoRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        if (usuarioRepository.count() == 0) {
            cadastrarUsuarios();
        }
        if (produtoRepository.count() == 0) {
            cadastrarProdutos();
        }
    }

    private void cadastrarUsuarios() {
        usuarioRepository.save(new Usuario(
                "Admin ERP",
                "admin@erpvarejo.com",
                passwordEncoder.encode("Admin@123"),
                RoleUsuario.ADMIN
        ));

        usuarioRepository.save(new Usuario(
                "Operador Caixa 01",
                "caixa01@erpvarejo.com",
                passwordEncoder.encode("User@123"),
                RoleUsuario.OPERADOR_CAIXA
        ));

        usuarioRepository.save(new Usuario(
                "Operador Caixa 02",
                "caixa02@erpvarejo.com",
                passwordEncoder.encode("User@123"),
                RoleUsuario.OPERADOR_CAIXA
        ));
    }

    private void cadastrarProdutos() {
        produtoRepository.save(new Produto(
                "PROD-001",
                "Mouse USB Optico",
                CategoriaProduto.ELETRONICOS,
                new BigDecimal("10.00")
        ));

        produtoRepository.save(new Produto(
                "PROD-002",
                "Feijao Carioca 1kg",
                CategoriaProduto.CESTA_BASICA,
                new BigDecimal("8.00")
        ));

        produtoRepository.save(new Produto(
                "PROD-003",
                "Cerveja IPA 500ml",
                CategoriaProduto.BEBIDAS_ALCOOLICAS,
                new BigDecimal("12.00")
        ));

        produtoRepository.save(new Produto(
                "PROD-004",
                "Teclado Mecanico",
                CategoriaProduto.ELETRONICOS,
                new BigDecimal("100.00")
        ));

        produtoRepository.save(new Produto(
                "PROD-005",
                "Arroz Integral 1kg",
                CategoriaProduto.CESTA_BASICA,
                new BigDecimal("20.00")
        ));

        produtoRepository.save(new Produto(
                "PROD-006",
                "Vinho Tinto 750ml",
                CategoriaProduto.BEBIDAS_ALCOOLICAS,
                new BigDecimal("50.00")
        ));
    }
}
