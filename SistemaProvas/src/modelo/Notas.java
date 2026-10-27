package modelo;

public class Notas {

    private String disciplina;
    private int pontos;

    public Notas(String disciplina, int pontos) {
        this.disciplina = disciplina;
        this.pontos = pontos;
    }
    public String getDisciplina() {
        return disciplina;
    }
    public int getPontos() {
        return pontos;
    }
}
