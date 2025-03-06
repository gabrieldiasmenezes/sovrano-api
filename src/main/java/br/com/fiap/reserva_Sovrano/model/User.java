package br.com.fiap.reserva_Sovrano.model;

import java.util.Random;

public class User{
    private Long id;
    private String name;
    private String email;
    private String senha;
    private String telefone;
    public User(Long id, String name, String email, String senha,String telefone) {
        this.id = Math.abs(new Random().nextLong());
        this.name = name;
        this.email = email;
        this.senha = senha;
        this.telefone=telefone;
    }
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public String getSenha() {
        return senha;
    }
    public void setSenha(String senha) {
        this.senha = senha;
    }
    public String getTelefone() {
        return telefone;
    }
    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }
    
    
    
}