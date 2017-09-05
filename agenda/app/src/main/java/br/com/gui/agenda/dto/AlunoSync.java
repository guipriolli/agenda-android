package br.com.gui.agenda.dto;

import java.util.List;

import br.com.gui.agenda.model.Aluno;

//Classe pra facilitar a troca de dados entre o servidor e o app. Possui apenas a lista de alunos e a última modificação
public class AlunoSync {

    private List<Aluno> alunos;
    private String momentoDaUltimaModificacao;

    public String getMomentoDaUltimaModificacao() {
        return momentoDaUltimaModificacao;
    }

    public List<Aluno> getAlunos() {
        return alunos;
    }
}
