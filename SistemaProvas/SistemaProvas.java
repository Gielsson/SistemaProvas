package SistemaProvas;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import ui.TelaPrincipal;

/**
 * Ponto de entrada: abre a tela principal.
 * Todos os arquivos ficam na pasta "dados" ao lado do programa
 * (ver GerenciadorArquivos.DIRETORIO_PADRAO).
 */
public class SistemaProvas {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignorada) {
                // segue com o visual padrão
            }
            new TelaPrincipal().setVisible(true);
        });
    }
}