/*
 * Copyright (c) 2026 Galagard/L2Explorer
 */
package org.l2explorer.app;

import javax.swing.*;
import org.l2explorer.io.UnrealPackage;
import org.l2explorer.io.RandomAccessFile; // Importante: o leitor que o UnrealPackage usa
import java.io.File;
import java.nio.charset.StandardCharsets;

public class Main {
    public static void main(String[] args) {
        // Define o LookAndFeel do sistema para não ficar com cara de Windows 95
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}

        SwingUtilities.invokeLater(() -> {
            try {
                // 1. Verificar se o arquivo existe antes de tentar abrir
                File interfaceFile = new File("InterfaceSamurai.u");
                if (!interfaceFile.exists()) {
                    throw new Exception("Arquivo 'Interface.u' não encontrado na pasta raiz!");
                }

                // 2. Instanciar o RandomAccessFile (o motor de leitura do ACMI)
                // O segundo parâmetro 'true' indica que é Read-Only
                RandomAccessFile ra = new RandomAccessFile(interfaceFile, true, StandardCharsets.ISO_8859_1);

                // 3. Carrega o UnrealPackage usando o RandomAccess
                UnrealPackage up = new UnrealPackage(ra);

                // 4. Configura a Janela Principal
                JFrame frame = new JFrame("L2Explorer 2026 - [" + interfaceFile.getName() + "]");
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.setSize(1200, 850); 

                // 5. Injeta o pacote no ExplorerPanel
                //ExplorerPanel panel = new ExplorerPanel(up);
                //frame.setContentPane(panel);

                frame.setLocationRelativeTo(null);
                frame.setVisible(true);
                
             // No seu Main.java, logo antes do frame.setVisible(true):
                System.out.println("Montando componentes...");
                ExplorerPanel panel = new ExplorerPanel(up);
                frame.setContentPane(panel);

                frame.pack(); // Faz o frame assumir o tamanho necessário para os componentes
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);
                frame.setContentPane(panel);

                System.out.println("Janela visível: " + frame.isVisible());
                
                System.out.println("L2Explorer: " + interfaceFile.getName() + " carregado com sucesso!");
                
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, 
                    "Erro ao carregar pacote: " + e.getMessage(), 
                    "Erro de Inicialização", 
                    JOptionPane.ERROR_MESSAGE);
            }
        });
    }
}