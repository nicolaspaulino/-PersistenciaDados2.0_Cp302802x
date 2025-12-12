package com.controleestoque.api_estoque.controller;

import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import com.controleestoque.api_estoque.model.Produto;
import com.controleestoque.api_estoque.repository.ProdutoRepository;
import com.controleestoque.api_estoque.repository.CategoriaRepository;
import com.controleestoque.api_estoque.repository.FornecedorRepository;

import lombok.RequiredArgsConstructor;
import java.util.List;

@RestController
@RequestMapping("/api/produtos")
@RequiredArgsConstructor
public class ProdutoController {

    private final ProdutoRepository produtoRepository;
    private final CategoriaRepository categoriaRepository;
    private final FornecedorRepository fornecedorRepository;

    // GET /api/produtos
    @GetMapping
    public List<Produto> getAllProdutos() {
        // Retorna todos os produtos cadastrados no banco
        return produtoRepository.findAll();
    }

    // GET /api/produtos/{id}
    @GetMapping("/{id}")
    public ResponseEntity<Produto> getProdutoById(@PathVariable Long id) {
        // Busca o produto pelo ID. Usa orElse para retornar 404 caso não encontre.
        return produtoRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // POST /api/produtos
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED) // Retorna código 201 (Created)
    public ResponseEntity<Produto> createProduto(@RequestBody Produto produto) {

        // 1. Relacionamento N:1 -> Categoria
        // A categoria é obrigatória (FK)
        if (produto.getCategoria() == null || produto.getCategoria().getId() == null) {
            return ResponseEntity.badRequest().build(); // Categoria é obrigatória
        }

        // Busca categoria gerenciada pelo contexto JPA
        categoriaRepository.findById(produto.getCategoria().getId())
                .ifPresent(produto::setCategoria);

        // 2. Relacionamento N:M -> Fornecedores
        // Caso o produto venha com fornecedores, mapeia cada ID para entidade gerenciada
        if (produto.getFornecedores() != null && !produto.getFornecedores().isEmpty()) {

            var fornecedoresIds = produto.getFornecedores(); // IDs vindos no JSON
            produto.getFornecedores().clear(); // limpa para evitar objetos não gerenciados

            fornecedoresIds.forEach(fornecedor ->
                    fornecedorRepository.findById(fornecedor.getId())
                            .ifPresent(produto.getFornecedores()::add)
            );
        }

        // 3. Relacionamento 1:1 -> Estoque
        // O CASCADE.ALL no Produto garante o vínculo automático

        // 4. Salva o produto
        Produto savedProduto = produtoRepository.save(produto);

        return ResponseEntity.status(HttpStatus.CREATED).body(savedProduto);
    }

    // PUT /api/produtos/{id}
    @PutMapping("/{id}")
    public ResponseEntity<Produto> updateProduto(
            @PathVariable Long id,
            @RequestBody Produto produtoDetails) {

        // Busca o produto existente
        return produtoRepository.findById(id)
                .map(produto -> {

                    // Atualiza dados simples
                    produto.setNome(produtoDetails.getNome());
                    produto.setPreco(produtoDetails.getPreco());
                    Produto updatedProduto = produtoRepository.save(produto);
                    return ResponseEntity.ok(updatedProduto);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // DELETE /api/produtos/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduto(@PathVariable Long id) {

        // Verifica se existe
        if (!produtoRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        produtoRepository.deleteById(id);

        return ResponseEntity.noContent().build(); // 204 No Content
    }
}
