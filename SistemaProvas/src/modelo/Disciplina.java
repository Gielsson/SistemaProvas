package modelo;

import java.util.ArrayList;
import java.util.List;

public class Disciplina {

    private String nome;
    private String gabarito;
    private List<Aluno> alunos;

    public Disciplina(String nome) {
        this.nome = nome.trim();
        this.alunos = new ArrayList<>();
    }

    public void adicionarAluno(Aluno aluno) {
        alunos.add(aluno);
    }

    public String getNome() {
        return nome;
    }

    public String getGabarito() {
        return gabarito;
    }

    public void setGabarito(String gabarito) {
        this.gabarito = gabarito.trim().toUpperCase();
    }

    public List<Aluno> getAlunos() {
        return alunos;
    }

    @Override
    public String toString() {
        return nome;
    }
}