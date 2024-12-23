package com.pushbait.webapp.entity;

import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "rifa")
public class Rifa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // Identificador único para a rifa

    private String nome;       // Nome do participante
    private String telefone;   // Telefone do participante
    private String descricao;  // Descrição da rifa
    private double preco = 10.00; // Preço da rifa, padrão 10.00

    @OneToMany(mappedBy = "rifa", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<NumeroRifa> numeros; // Lista de números associados a essa rifa

    // Getters e Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public double getPreco() {
        return preco;
    }

    public void setPreco(double preco) {
        this.preco = preco;
    }

    public List<NumeroRifa> getNumeros() {
        return numeros;
    }

    public void setNumeros(List<NumeroRifa> numeros) {
        this.numeros = numeros;
    }
}
