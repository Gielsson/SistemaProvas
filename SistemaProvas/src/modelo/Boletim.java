package modelo;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class Boletim {

    private static final double MEDIA_APROVACAO = 7.0;
    private static final String LINHA = "====================================";

    private String nomeAluno;
    private List<Nota> notas;

    public Boletim(String nomeAluno) {
        this.nomeAluno = nomeAluno;
        this.notas = new ArrayList<>();
    }

    public void adicionarNota(Nota nota) {
        notas.add(nota);
    }

    public double calcularMedia() {
        if (notas.isEmpty()) {
            return 0;
        }
        double soma = 0;
        for (Nota nota : notas) {
            soma += nota.getPontos();
        }
        return soma / notas.size();
    }

    public boolean isAprovado() {
        return calcularMedia() >= MEDIA_APROVACAO;
    }

    public String getNomeAluno() {
        return nomeAluno;
    }

    public List<Nota> getNotas() {
        return notas;
    }

    public String gerarTexto() {
        StringBuilder sb = new StringBuilder();
        sb.append(LINHA).append("\n");
        sb.append("BOLETIM GERAL DO ALUNO\n");
        sb.append(LINHA).append("\n");
        sb.append("Aluno: ").append(nomeAluno).append("\n");
        for (Nota nota : notas) {
            sb.append(String.format("Disciplina: %-12s Nota: %d%n", nota.getDisciplina(), nota.getPontos()));
        }
        sb.append(String.format(Locale.US, "Média geral: %.1f%n", calcularMedia()));
        sb.append("Situação final: ").append(isAprovado() ? "APROVADO" : "REPROVADO").append("\n");
        sb.append(LINHA).append("\n");
        return sb.toString();
    }
}