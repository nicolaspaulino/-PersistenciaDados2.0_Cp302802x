package com.controleestoque.api_estoque.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.controleestoque.api_estoque.model.Categoria;


public interface CategoriaRepository extends JpaRepository<Categoria, Long> {

}
