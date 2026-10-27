package servico;

import modelo.Aluno;
import modelo.Disciplina;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Leitura e escrita de todos os arquivos do sistema.
 *
 * Estrutura sob o diretório base:
 *   disciplinas/<DISCIPLINA>.txt          RESPOSTAS \t NOME
 *   resultados/<DISCIPLINA>_alfabetico.txt NOME \t PONTOS
 *   resultados/<DISCIPLINA>_notas.txt      NOME \t PONTOS ... e a média da turma no final
 *   boletins/<ALUNO>.txt
 */
public class GerenciadorArquivos {

    public static final String SUFIXO_ALFABETICO = "_alfabetico.txt";
    public static final String SUFIXO_NOTAS = "_notas.txt";

    private final Path dirDisciplinas;
    private final Path dirResultados;
    private final Path dirBoletins;

    /** Pasta padrão do sistema: "dados", ao lado do programa. */
    public static final Path DIRETORIO_PADRAO = Paths.get("dados");

    /** Usa a pasta padrão. É o construtor usado pelas telas. */
    public GerenciadorArquivos() {
        this(DIRETORIO_PADRAO);
    }

    public GerenciadorArquivos(Path diretorioBase) {
        this.dirDisciplinas = diretorioBase.resolve("disciplinas");
        this.dirResultados = diretorioBase.resolve("resultados");
        this.dirBoletins = diretorioBase.resolve("boletins");
    }

    public Path getDirDisciplinas() { return dirDisciplinas; }
    public Path getDirResultados() { return dirResultados; }
    public Path getDirBoletins() { return dirBoletins; }

    // ---------- Disciplinas (requisito 1) ----------

    /** Cria (ou substitui) o arquivo da disciplina com as respostas de todos os alunos. */
    public void salvarDisciplina(Disciplina disciplina) throws IOException {
        List<String> linhas = new ArrayList<>();
        for (Aluno a : disciplina.getAlunos()) {
            if (a.getNome().isEmpty()) {
                throw new IllegalArgumentException("Nome do aluno não pode ser vazio.");
            }
            if (!Aluno.respostasValidas(a.getRespostas())) {
                throw new IllegalArgumentException("Respostas inválidas para " + a.getNome() + ": use 10 letras V ou F.");
            }
            linhas.add(a.getRespostas() + "\t" + a.getNome());
        }
        Files.createDirectories(dirDisciplinas);
        Files.write(dirDisciplinas.resolve(nomeArquivo(disciplina.getNome()) + ".txt"), linhas, StandardCharsets.UTF_8);
    }

    /** Nomes das disciplinas já criadas, em ordem alfabética. */
    public List<String> listarDisciplinas() throws IOException {
        if (!Files.isDirectory(dirDisciplinas)) return new ArrayList<>();
        try (Stream<Path> s = Files.list(dirDisciplinas)) {
            return s.map(p -> p.getFileName().toString())
                    .filter(n -> n.endsWith(".txt"))
                    .map(n -> n.substring(0, n.length() - 4))
                    .sorted(String.CASE_INSENSITIVE_ORDER)
                    .collect(Collectors.toList());
        }
    }

    /** Lê o arquivo da disciplina e devolve a Disciplina com seus alunos (sem gabarito). */
    public Disciplina lerDisciplina(String nomeDisciplina) throws IOException {
        Path arquivo = dirDisciplinas.resolve(nomeArquivo(nomeDisciplina) + ".txt");
        Disciplina disciplina = new Disciplina(nomeDisciplina);
        for (String linha : Files.readAllLines(arquivo, StandardCharsets.UTF_8)) {
            if (linha.trim().isEmpty()) continue;
            String[] partes = linha.split("\t", 2);
            if (partes.length < 2) {
                throw new IOException("Linha inválida em " + arquivo.getFileName() + ": " + linha);
            }
            disciplina.adicionarAluno(new Aluno(partes[1], partes[0]));
        }
        return disciplina;
    }

    // ---------- Gabarito e resultados (requisito 2) ----------

    /** Lê a primeira linha não vazia do arquivo de gabarito (10 respostas V/F). */
    public String lerGabarito(Path arquivoGabarito) throws IOException {
        for (String linha : Files.readAllLines(arquivoGabarito, StandardCharsets.UTF_8)) {
            String g = linha.trim().toUpperCase();
            if (g.isEmpty()) continue;
            if (!Aluno.respostasValidas(g)) {
                throw new IOException("Gabarito inválido: deve ter 10 letras V ou F.");
            }
            return g;
        }
        throw new IOException("Arquivo de gabarito vazio.");
    }

    /** Grava os dois arquivos de resultado. Retorna [alfabético, por nota]. */
    public Path[] salvarResultados(String nomeDisciplina, List<ResultadoAluno> alfabetica,
                                   List<ResultadoAluno> porNota, double mediaTurma) throws IOException {
        Files.createDirectories(dirResultados);
        String base = nomeArquivo(nomeDisciplina);
        Path arqAlfa = dirResultados.resolve(base + SUFIXO_ALFABETICO);
        Path arqNota = dirResultados.resolve(base + SUFIXO_NOTAS);

        Files.write(arqAlfa, formatar(alfabetica), StandardCharsets.UTF_8);

        List<String> linhasNota = formatar(porNota);
        linhasNota.add("");
        linhasNota.add(String.format(Locale.US, "MEDIA DA TURMA\t%.2f", mediaTurma));
        Files.write(arqNota, linhasNota, StandardCharsets.UTF_8);
        return new Path[]{arqAlfa, arqNota};
    }

    /** Conteúdo de um arquivo de resultado, para exibir na tela. */
    public String lerTexto(Path arquivo) throws IOException {
        return new String(Files.readAllBytes(arquivo), StandardCharsets.UTF_8);
    }

    /** Lê um arquivo de resultado alfabético (NOME \t PONTOS). */
    public List<ResultadoAluno> lerResultado(Path arquivo) throws IOException {
        List<ResultadoAluno> lista = new ArrayList<>();
        for (String linha : Files.readAllLines(arquivo, StandardCharsets.UTF_8)) {
            if (linha.trim().isEmpty()) continue;
            String[] partes = linha.split("\t");
            if (partes.length != 2) {
                throw new IOException("Linha inválida em " + arquivo.getFileName() + ": " + linha);
            }
            try {
                lista.add(new ResultadoAluno(partes[0].trim(), Integer.parseInt(partes[1].trim())));
            } catch (NumberFormatException e) {
                throw new IOException("Nota inválida em " + arquivo.getFileName() + ": " + linha);
            }
        }
        return lista;
    }

    /** Arquivos de resultado alfabético de todas as disciplinas de um diretório. */
    public List<Path> listarArquivosResultado(Path diretorio) throws IOException {
        if (!Files.isDirectory(diretorio)) {
            throw new IOException("Diretório não encontrado: " + diretorio);
        }
        try (Stream<Path> s = Files.list(diretorio)) {
            return s.filter(p -> p.getFileName().toString().endsWith(SUFIXO_ALFABETICO))
                    .sorted()
                    .collect(Collectors.toList());
        }
    }

    // ---------- Boletins (requisito 3) ----------

    /** Grava o boletim de um aluno em <diretorio>/<NOME DO ALUNO>.txt. */
    public Path salvarBoletim(Path diretorio, String nomeAluno, String conteudo) throws IOException {
        Files.createDirectories(diretorio);
        Path arquivo = diretorio.resolve(nomeArquivo(nomeAluno) + ".txt");
        Files.write(arquivo, conteudo.getBytes(StandardCharsets.UTF_8));
        return arquivo;
    }

    // ---------- Auxiliares ----------

    private List<String> formatar(List<ResultadoAluno> lista) {
        List<String> linhas = new ArrayList<>();
        for (ResultadoAluno r : lista) linhas.add(r.getNome() + "\t" + r.getPontos());
        return linhas;
    }

    /** Remove caracteres proibidos em nomes de arquivo. */
    public static String nomeArquivo(String nome) {
        String limpo = nome.trim().replaceAll("[\\\\/:*?\"<>|\\t]", "_");
        if (limpo.isEmpty()) throw new IllegalArgumentException("Nome inválido.");
        return limpo;
    }
}