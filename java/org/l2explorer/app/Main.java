package org.l2explorer.app;

import com.formdev.flatlaf.FlatDarkLaf;
import org.l2explorer.io.UnrealPackage;
import javax.swing.*;
import java.io.File;

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
                
                // Tenta carregar o último package
                String lastPackage = GeneralConfig.getLastPackage();
                if (!lastPackage.isEmpty() && new File(lastPackage).exists()) {
                    packageFile = new File(lastPackage);
                } else {
                    // Tenta o default
                    File defaultFile = new File("InterfaceSamurai.u");
                    if (defaultFile.exists()) {
                        packageFile = defaultFile;
                    }
                }
                
                // Se não achou nenhum, abre o seletor
                if (packageFile == null || !packageFile.exists()) {
                    JFileChooser chooser = new JFileChooser(GeneralConfig.getLastDirectory());
                    chooser.setDialogTitle("Select Unreal Package");
                    chooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter(
                        "Unreal Package (*.u, *.utx, *.unr)", "u", "utx", "unr"));
                    
                    if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                        packageFile = chooser.getSelectedFile();
                        GeneralConfig.setLastDirectory(packageFile.getParent());
                        GeneralConfig.setLastPackage(packageFile.getAbsolutePath());
                    } else {
                        // Usuário cancelou
                        System.out.println("No package selected. Exiting.");
                        return;
                    }
                }

                // Carrega o package
                UnrealPackage up = new UnrealPackage(packageFile, true);

                // Cria a janela
                JFrame frame = new JFrame("L2Explorer 2026 - " + packageFile.getName());
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.setSize(1400, 900);
                frame.setLocationRelativeTo(null);

                ExplorerPanel panel = new ExplorerPanel(up, packageFile.getParentFile());
                frame.setContentPane(panel);

                frame.setVisible(true);
                
                System.out.println("✅ L2Explorer started with: " + packageFile.getName());

            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null,
                    "Failed to load package:\n" + e.getMessage(),
                    "Startup Error",
                    JOptionPane.ERROR_MESSAGE);
                System.exit(1);
            }
        });
    }
}