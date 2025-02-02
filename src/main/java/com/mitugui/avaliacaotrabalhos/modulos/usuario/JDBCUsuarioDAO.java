package com.mitugui.avaliacaotrabalhos.modulos.usuario;

import com.mitugui.avaliacaotrabalhos.exceptions.ConexaoBancoException;
import com.mitugui.avaliacaotrabalhos.exceptions.UsuarioNaoEncontradoException;
import com.mitugui.avaliacaotrabalhos.interfaces.UsuarioDAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class JDBCUsuarioDAO implements UsuarioDAO {
    @Override
    public boolean salvar(Connection conn, DadosCadastroUsuario usuario) throws SQLException {
        String sql = "INSERT INTO usuario(nome, email, senha) VALUES (?, ?, ?)";

        try (PreparedStatement pstm = conn.prepareStatement(sql)) {
            pstm.setString(1, usuario.nome());
            pstm.setString(2, usuario.email());
            pstm.setString(3, usuario.senha());

            return pstm.executeUpdate() == 1;
        }
    }

    @Override
    public List<DadosListagemUsuario> listar(Connection conn) throws SQLException {
        String sql = "SELECT u.nome, u.email FROM usuario u WHERE ativo = 1;";

        List<DadosListagemUsuario> usuarios = new ArrayList<>();

        try (
            PreparedStatement pstm = conn.prepareStatement(sql);
            ResultSet rs = pstm.executeQuery()
        ) {
            while (rs.next()) {
                String nome = rs.getString("nome");
                String email = rs.getString("email");

                usuarios.add(new DadosListagemUsuario(nome, email));
            }
        }

        return usuarios;
    }

    @Override
    public boolean atualizar(Connection conn, DadosAtualizarUsuario usuario, Integer id) throws SQLException {
        String sql = "UPDATE usuario SET nome = ?, email = ?, senha = ? WHERE id = ? AND ativo = 1;";

        try (PreparedStatement pstm = conn.prepareStatement(sql)) {
            pstm.setString(1, usuario.nome());
            pstm.setString(2, usuario.email());
            pstm.setString(3, usuario.senha());
            pstm.setInt(4, id);

            return pstm.executeUpdate() == 1;
        }
    }

    @Override
    public boolean deletar(Connection conn, Integer id) throws SQLException {
        String sql = "UPDATE usuario SET ativo = false WHERE id = ?";

        try (PreparedStatement pstm = conn.prepareStatement(sql)) {
            pstm.setInt(1, id);

            return pstm.executeUpdate() == 1;
        }
    }

    @Override
    public Integer validar(Connection conn, DadosValidacaoUsuario usuario) throws UsuarioNaoEncontradoException {
        String sql = "SELECT id FROM usuario WHERE ativo = 1 AND email = ? AND senha = ?;";

        try (PreparedStatement pstm = conn.prepareStatement(sql)) {
            pstm.setString(1, usuario.email());
            pstm.setString(2, usuario.senha());

            try (ResultSet rs = pstm.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("id");
                } else {
                    throw new UsuarioNaoEncontradoException("O usuário não foi encontrado!!");
                }
            }
        } catch (SQLException e) {
            throw new ConexaoBancoException("Erro na conexão com o banco de dados ao validar usuário.", e);
        }
    }
}
