package com.controleestoque.api_estoque.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.controleestoque.api_estoque.model.Produto;

public interface ProdutoRepository extends JpaRepository<Produto, Long> {
}
