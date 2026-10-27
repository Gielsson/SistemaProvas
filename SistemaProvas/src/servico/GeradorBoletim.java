package servico;

import modelo.Notas;

import java.io.IOException;
import java.nio.file.Path;
import java.text.Collator;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

/** Lê os resultados de todas as disciplinas e gera um boletim .txt por aluno. */
public class GeradorBoletim {

    public static final double MEDIA_APROVACAO = 7.0;
    private static final String LINHA = "====================================";

    private final GerenciadorArquivos gerenciador;

    public GeradorBoletim(GerenciadorArquivos gerenciador) {
        this.gerenciador = gerenciador;
    }

    /**
     * Lê os resultados do diretório, agrupa as notas de cada aluno e grava um .txt
     * por aluno em dirSaida. Retorna o texto de cada boletim (para mostrar na tela).
     */
    public List<String> gerar(Path dirResultados, Path dirSaida) throws IOException {
        List<Path> arquivos = gerenciador.listarArquivosResultado(dirResultados);
        if (arquivos.isEmpty()) {
            throw new IOException("Nenhum arquivo de resultado (*" + GerenciadorArquivos.SUFIXO_ALFABETICO
                    + ") encontrado em " + dirResultados);
        }

        Collator collator = Collator.getInstance(Locale.forLanguageTag("pt-BR"));
        collator.setStrength(Collator.PRIMARY);
        Map<String, List<Notas>> notasPorAluno = new TreeMap<>(collator);

        for (Path arquivo : arquivos) {
            String nome = arquivo.getFileName().toString();
            String disciplina = nome.substring(0, nome.length() - GerenciadorArquivos.SUFIXO_ALFABETICO.length());
            for (ResultadoAluno r : gerenciador.lerResultado(arquivo)) {
                notasPorAluno.computeIfAbsent(r.getNome(), k -> new ArrayList<>())
                             .add(new Notas(disciplina, r.getPontos()));
            }
        }

        List<String> boletins = new ArrayList<>();
        for (Map.Entry<String, List<Notas>> e : notasPorAluno.entrySet()) {
            String texto = formatar(e.getKey(), e.getValue());
            gerenciador.salvarBoletim(dirSaida, e.getKey(), texto);
            boletins.add(texto);
        }
        return boletins;
    }

    public double calcularMedia(List<Notas> notas) {
        if (notas.isEmpty()) return 0;
        double soma = 0;
        for (Notas n : notas) soma += n.getPontos();
        return soma / notas.size();
    }

    public String situacao(double media) {
        return media >= MEDIA_APROVACAO ? "APROVADO" : "REPROVADO";
    }

    /** Texto do boletim no formato do enunciado. */
    public String formatar(String nomeAluno, List<Notas> notas) {
        double media = calcularMedia(notas);
        StringBuilder sb = new StringBuilder();
        sb.append(LINHA).append("\n");
        sb.append("BOLETIM GERAL DO ALUNO\n");
        sb.append(LINHA).append("\n");
        sb.append("Aluno: ").append(nomeAluno).append("\n");
        for (Notas n : notas) {
            sb.append("Disciplina: ").append(n.getDisciplina())
              .append("    Nota: ").append(n.getPontos()).append("\n");
        }
        sb.append(String.format(Locale.US, "Média geral: %.1f%n", media));
        sb.append("Situação final: ").append(situacao(media)).append("\n");
        sb.append(LINHA).append("\n");
        return sb.toString();
    }
}