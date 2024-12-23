package com.pushbait.webapp.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.pushbait.webapp.entity.NumeroRifa;
import com.pushbait.webapp.entity.Rifa;

@Repository
public interface NumeroRifaRepository extends JpaRepository<NumeroRifa, Long> {

    List<NumeroRifa> findByRifa(Rifa rifa);

    Optional<NumeroRifa> findByRifaIdAndNumero(Long rifaId, Integer numero);
}
