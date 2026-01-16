package org.l2explorer.app;

import com.formdev.flatlaf.FlatDarkLaf;
import org.l2explorer.io.UnrealPackage;
import javax.swing.*;
import java.io.File;

/**
 * Main Entry Point for L2Explorer 2026.
 * Specialized for Samurai Crow (Protocol 542).
 */
public class Main {
    public static void main(String[] args) {
        // Configura FlatLaf ANTES de criar qualquer UI
        try {
            UIManager.setLookAndFeel(new FlatDarkLaf());
            UIManager.put("Button.arc", 8);
            UIManager.put("Component.arc", 8);
            UIManager.put("TextComponent.arc", 8);
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> {
            try {
                File packageFile = null;
                
                // 1. Tenta carregar o último arquivo usado
                String lastPackage = GeneralConfig.getLastPackage();
                if (!lastPackage.isEmpty() && new File(lastPackage).exists()) {
                    packageFile = new File(lastPackage);
                } else {
                    // Tenta o arquivo padrão do Samurai Crow
                    File defaultFile = new File("InterfaceSamurai.u");
                    if (defaultFile.exists()) {
                        packageFile = defaultFile;
                    }
                }
                
                // 2. Se não achou nenhum, abre o seletor de arquivos
                if (packageFile == null || !packageFile.exists()) {
                    JFileChooser chooser = new JFileChooser(GeneralConfig.getLastDirectory());
                    chooser.setDialogTitle("Select Lineage II File (.u, .dat, .ini)");
                    chooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter(
                        "L2 Files (*.u, *.utx, *.unr, *.dat, *.ini)", "u", "utx", "unr", "dat", "ini"));
                    
                    if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                        packageFile = chooser.getSelectedFile();
                        GeneralConfig.setLastDirectory(packageFile.getParent());
                        GeneralConfig.setLastPackage(packageFile.getAbsolutePath());
                    } else {
                        System.out.println("No file selected. Exiting.");
                        return;
                    }
                }

                // 3. DESPACHANTE DE CARGA: Identifica se é Unreal ou Data Table
                UnrealPackage up = null;
                String fileName = packageFile.getName().toLowerCase();
                boolean isDatFile = fileName.endsWith(".dat") || fileName.endsWith(".ini");

                if (!isDatFile) {
                    // Só tenta criar UnrealPackage se NÃO for um arquivo de dados (.dat/.ini)
                    // Isso evita o erro "Not a L2 package file" na linha 55
                    up = new UnrealPackage(packageFile, true);
                }

                // 4. Configuração da Janela Principal
                JFrame frame = new JFrame("L2Explorer 2026 - " + packageFile.getName());
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.setSize(1400, 900);
                frame.setLocationRelativeTo(null);

                // Inicializa o painel (up pode ser null se for DAT, o painel deve tratar isso)
                ExplorerPanel panel = new ExplorerPanel(up, packageFile.getParentFile());
                frame.setContentPane(panel);
                frame.setVisible(true);
                
                // 5. CARGA POSTERIOR: Se for DAT, pedimos para o painel decriptar via RSA
                if (isDatFile) {
                    final File finalFile = packageFile;
                    // Pequeno delay para garantir que a UI está visível antes de começar a RSA
                    SwingUtilities.invokeLater(() -> panel.loadDatFile(finalFile));
                }

                System.out.println("✅ L2Explorer started successfully with: " + packageFile.getName());

            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null,
                    "Startup Error:\n" + e.getMessage(),
                    "L2Explorer - Error",
                    JOptionPane.ERROR_MESSAGE);
                System.exit(1);
            }
        });
    }
}