package servico;

/** Nome do aluno e quantidade de acertos em uma disciplina. */
public class ResultadoAluno {
    private final String nome;
    private final int pontos;

    public ResultadoAluno(String nome, int pontos) {
        this.nome = nome;
        this.pontos = pontos;
    }

    public String getNome() { return nome; }
    public int getPontos() { return pontos; }
}