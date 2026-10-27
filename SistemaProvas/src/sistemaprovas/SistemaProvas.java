package sistemaprovas;

import ui.TelaPrincipal;

import java.nio.file.Paths;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import servico.CorretorProva;
import servico.GeradorBoletim;
import servico.GerenciadorArquivos;

/** Ponto de entrada: cria os serviços e abre a tela principal. */
public class SistemaProvas {

    public static void main(String[] args) {
        // Todos os arquivos ficam na pasta "dados" ao lado do programa.
        GerenciadorArquivos gerenciador = new GerenciadorArquivos(Paths.get("dados"));
        CorretorProva corretor = new CorretorProva();
        GeradorBoletim gerador = new GeradorBoletim(gerenciador);

        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignorada) {
                // segue com o visual padrão
            }
            new TelaPrincipal(gerenciador, corretor, gerador).setVisible(true);
        });
    }
}