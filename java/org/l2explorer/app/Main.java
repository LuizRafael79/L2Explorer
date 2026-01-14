package org.l2explorer.app;

import javax.swing.*;
import org.l2explorer.io.UnrealPackage;
import org.l2explorer.io.RandomAccessFile;
import java.io.File;
import java.nio.charset.StandardCharsets;

public class Main {
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}

        SwingUtilities.invokeLater(() -> {
            // 1. JFileChooser para selecionar o arquivo .u
            JFileChooser chooser = new JFileChooser(new File("."));
            chooser.setDialogTitle("L2Explorer - Selecione um pacote Unreal (.u)");
            
            if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                File selectedFile = chooser.getSelectedFile();
                
                try {
                    // 2. Motor de leitura do ACMI
                    RandomAccessFile ra = new RandomAccessFile(selectedFile, true, StandardCharsets.ISO_8859_1);
                    UnrealPackage up = new UnrealPackage(ra);

                    // 3. Janela Principal
                    JFrame frame = new JFrame("L2Explorer 2026 - [" + selectedFile.getName() + "]");
                    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                    
                    // 4. Injeta o pacote no painel
                    // IMPORTANTE: Precisamos passar a pasta base para o descompilador!
                    ExplorerPanel panel = new ExplorerPanel(up, selectedFile.getParentFile());
                    
                    frame.setContentPane(panel);
                    frame.pack();
                    frame.setSize(1200, 850);
                    frame.setLocationRelativeTo(null);
                    frame.setVisible(true);

                    System.out.println("L2Explorer: " + selectedFile.getName() + " carregado!");
                    
                } catch (Exception e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(null, "Erro: " + e.getMessage());
                }
            } else {
                System.exit(0); // Usuário cancelou
            }
        });
    }
}