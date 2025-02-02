package com.mitugui.avaliacaotrabalhos.modulos.professor;

import com.mitugui.avaliacaotrabalhos.config.FabricaDeConexoes;
import com.mitugui.avaliacaotrabalhos.exceptions.ConexaoBancoException;
import com.mitugui.avaliacaotrabalhos.interfaces.ProfessorDAO;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class ProfessorService {
    private final ProfessorDAO professorDAO;

    public ProfessorService(ProfessorDAO professorDAO) {
        this.professorDAO = professorDAO;
    }

    public boolean cadastrarProfessor(DadosCadastroProfessor professor){
        String mensagem = validarDadosCadastro(professor);

        if (!mensagem.isBlank()) {
            throw new IllegalArgumentException(mensagem);
        }

        try (Connection conn = FabricaDeConexoes.getConnection()) {
            return professorDAO.salvar(conn, professor);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Erro na validação dos dados: " + e.getMessage());
        } catch (ConexaoBancoException e) {
            throw new RuntimeException("Erro inesperado ao cadastrar professor. " + e.getMessage());
        } catch (SQLException e) {
            throw new RuntimeException("Erro na conexão com o banco de dados ao cadastrar professor.", e);
        }
    }

    private String validarDadosCadastro(DadosCadastroProfessor professor) {
        String mensagem = "";

        if (professor.email() == null || professor.email().isBlank()) {
            mensagem +="O email não pode estar vazio.\n";
        }
        if (professor.senha() == null || professor.senha().isBlank()) {
            mensagem +="O senha não pode estar vazio.\n";
        }
        if (professor.siape() == null || professor.siape().toString().isBlank()) {
            mensagem +="A siape não pode estar vazia.\n";
        }

        return mensagem;
    }
    
    public List<DadosListagemProfessor> listar() {
        try (Connection conn = FabricaDeConexoes.getConnection()) {
            return professorDAO.listar(conn);
        } catch (SQLException e) {
            throw new RuntimeException("Erro no banco ao listar professores.", e);
        }
    }

    public boolean atualizar(DadosAtualizarProfessor dados) {
        try (Connection conn = FabricaDeConexoes.getConnection()) {
            return professorDAO.atualizar(conn, dados);
        } catch (SQLException e) {
            throw new RuntimeException("Erro no banco ao atualizar professores.", e);            
        }
    }
}
