package com.pushbait.webapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import com.pushbait.webapp.entity.Rifa;

@EnableJpaRepositories(basePackages = "com.pushbait.webapp.repository")
public interface RifaRepository extends JpaRepository<Rifa, Long> {
}
