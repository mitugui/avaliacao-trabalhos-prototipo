package com.mitugui.avaliacaotrabalhos.usuario;

import java.sql.Connection;
import java.sql.SQLException;

import com.mitugui.avaliacaotrabalhos.FabricaDeConexoes;

public class UsuarioService {
    public boolean cadastrarUsuario(DadosCadastroUsuario usuario) {
        String mensagem = validarDadosCadastro(usuario);

        if (!mensagem.isBlank()) {
            throw new IllegalArgumentException(mensagem);
        }

        try (Connection conn = FabricaDeConexoes.getConnection()) {
            return new UsuarioDAO(conn).salvar(usuario);
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao cadastrar usuário no banco de dados.", e);
        }
    }

    private String validarDadosCadastro(DadosCadastroUsuario usuario) {
        String mensagem = "";

        if (usuario.nome() == null || usuario.nome().isBlank()) {
            mensagem +="O nome não pode estar vazio.\n";
        }
        if (usuario.email() == null || usuario.email().isBlank()) {
            mensagem +="O email não pode estar vazio.\n";
        }
        if (usuario.senha() == null || usuario.senha().isBlank()) {
            mensagem +="A senha não pode estar vazia.\n";
        }

        return mensagem;
    }
}
