package modelo;

public class Aluno {
    public static final int TOTAL_QUESTOES = 10;
private String nome;
private String respostas;

   public Aluno(String nome, String respostas) {
        this.nome = nome.trim();
        this.respostas = respostas.trim().toUpperCase();
    }

    public static boolean respostasValidas(String respostas) {
        return respostas != null && respostas.trim().toUpperCase().matches("[VF]{" + TOTAL_QUESTOES + "}");
    }

    public String getNome() {
        return nome;
    }

    public String getRespostas() {
        return respostas;
    }

    @Override
    public String toString() {
        return respostas + "\t" + nome; //separação por tab
    }
}