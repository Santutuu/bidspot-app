package com.subastas.subastas_api.repository;

import com.subastas.subastas_api.model.*;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ItemRepository extends JpaRepository<Item, Long> {
}