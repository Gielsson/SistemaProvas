package servico;

import modelo.Aluno;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

/** Correção das provas, ordenação dos resultados e média da turma. */
public class CorretorProva {

    /** Número de acertos. Se o aluno marcou tudo V ou tudo F, a nota é 0. */
    public int corrigir(String respostas, String gabarito) {
        String r = respostas.trim().toUpperCase();
        String g = gabarito.trim().toUpperCase();
        if (!Aluno.respostasValidas(r) || !Aluno.respostasValidas(g)) {
            throw new IllegalArgumentException("Respostas e gabarito devem ter "
                    + Aluno.TOTAL_QUESTOES + " itens V ou F.");
        }
        if (r.matches("V+") || r.matches("F+")) {
            return 0;
        }
        int acertos = 0;
        for (int i = 0; i < Aluno.TOTAL_QUESTOES; i++) {
            if (r.charAt(i) == g.charAt(i)) acertos++;
        }
        return acertos;
    }

    /** Corrige a turma inteira, mantendo a ordem de entrada. */
    public List<ResultadoAluno> corrigirTurma(List<Aluno> alunos, String gabarito) {
        List<ResultadoAluno> resultados = new ArrayList<>();
        for (Aluno a : alunos) {
            resultados.add(new ResultadoAluno(a.getNome(), corrigir(a.getRespostas(), gabarito)));
        }
        return resultados;
    }

    /** Ordem alfabética pelo nome (ignora maiúsculas e acentos). */
    public List<ResultadoAluno> ordenarAlfabetica(List<ResultadoAluno> resultados) {
        Collator collator = Collator.getInstance(Locale.forLanguageTag("pt-BR"));
        collator.setStrength(Collator.PRIMARY);
        List<ResultadoAluno> copia = new ArrayList<>(resultados);
        copia.sort((a, b) -> collator.compare(a.getNome(), b.getNome()));
        return copia;
    }

    /** Ordem decrescente de acertos; empate resolvido pelo nome. */
    public List<ResultadoAluno> ordenarPorNota(List<ResultadoAluno> resultados) {
        Collator collator = Collator.getInstance(Locale.forLanguageTag("pt-BR"));
        collator.setStrength(Collator.PRIMARY);
        List<ResultadoAluno> copia = new ArrayList<>(resultados);
        copia.sort(Comparator.comparingInt(ResultadoAluno::getPontos).reversed()
                .thenComparing((a, b) -> collator.compare(a.getNome(), b.getNome())));
        return copia;
    }

    public double calcularMedia(List<ResultadoAluno> resultados) {
        if (resultados.isEmpty()) return 0;
        double soma = 0;
        for (ResultadoAluno r : resultados) soma += r.getPontos();
        return soma / resultados.size();
    }
}