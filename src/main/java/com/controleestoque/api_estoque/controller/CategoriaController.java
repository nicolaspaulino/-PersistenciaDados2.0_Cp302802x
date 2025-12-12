package com.controleestoque.api_estoque.controller;

import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import com.controleestoque.api_estoque.model.Categoria;
import com.controleestoque.api_estoque.repository.CategoriaRepository;
import lombok.RequiredArgsConstructor;
import java.util.List;

@RestController
@RequestMapping("/api/categorias")
@RequiredArgsConstructor
public class CategoriaController {

    private final CategoriaRepository categoriaRepository;

    // GET /api/categorias
    @GetMapping
    public List<Categoria> getAllCategorias() {
        // Retorna todas as categorias do banco de dados
        return categoriaRepository.findAll();
    }

    // GET /api/categorias/{id}
    @GetMapping("/{id}")
    public ResponseEntity<Categoria> getCategoriaById(@PathVariable Long id) {
        // Busca a categoria pelo ID. Usa orElse para retornar 404 se não encontrar.
        return categoriaRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // POST /api/categorias
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED) // Retorna código 201 (Created)
    public Categoria createCategoria(@RequestBody Categoria categoria) {
        // Salva uma nova categoria no banco de dados
        return categoriaRepository.save(categoria);
    }

    // PUT /api/categorias/{id}
    @PutMapping("/{id}")
    public ResponseEntity<Categoria> updateCategoria(
            @PathVariable Long id,
            @RequestBody Categoria categoriaDetails) {

        // Tenta encontrar a categoria existente
        return categoriaRepository.findById(id)
                .map(categoria -> {
                    // Atualiza os dados da categoria encontrada
                    categoria.setNome(categoriaDetails.getNome());
                    Categoria updatedCategoria = categoriaRepository.save(categoria);
                    return ResponseEntity.ok(updatedCategoria);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // DELETE /api/categorias/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategoria(@PathVariable Long id) {

        // Tenta encontrar e deletar
        if (!categoriaRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        categoriaRepository.deleteById(id);
        return ResponseEntity.noContent().build(); // Retorna código 204 (No Content)
    }
}
