/*
 * Copyright (c) 2026 Galagard/L2Explorer
 * Project: Lineage II Interface.u Explorer
 */
package org.l2explorer.app;

import javax.swing.*;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeWillExpandListener;
import javax.swing.tree.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import org.l2explorer.io.UnrealPackage;
import org.l2explorer.io.UnrealPackage.ExportEntry;
import org.l2explorer.utils.unreal.UnrealDecompiler;

/**
 * GUI principal do Explorer com suporte a Console de Debug, 
 * Hierarquia de Pastas e Lazy Loading.
 */
public class ExplorerPanel extends JPanel {
    private static final long serialVersionUID = 1L;

    private JTree objectTree;
    private JTextArea infoArea;
    private JTextArea codeArea;
    private DebugConsole debugConsole; 
    private UnrealPackage currentPackage;
    private UnrealDecompiler decompiler;

 // Adicione este campo
    private File systemDir;

    public ExplorerPanel(UnrealPackage up, File baseDir) {
        this.currentPackage = up;
        this.systemDir = baseDir; // <--- Guarda o caminho aqui
        this.debugConsole = new DebugConsole();
        
        // O decompiler agora é instanciado aqui
        this.decompiler = new UnrealDecompiler();
        
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(1100, 800));

        // Split Principal: Árvore (Esquerda) vs Conteúdo (Direita)
        JSplitPane mainSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        mainSplit.setDividerLocation(350);
        mainSplit.setResizeWeight(0.3);
        mainSplit.setLeftComponent(createTreePanel());
        
        // Split Direito: Abas de Código (Cima) vs Console (Baixo)
        JSplitPane rightSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        rightSplit.setDividerLocation(550);
        rightSplit.setResizeWeight(0.7);
        rightSplit.setTopComponent(createContentTabs());
        rightSplit.setBottomComponent(createConsolePanel());
        
        mainSplit.setRightComponent(rightSplit);

        add(mainSplit, BorderLayout.CENTER);
        add(createStatusBar(), BorderLayout.SOUTH);
    }

    @SuppressWarnings("unused")
	private JPanel createTreePanel() {
        JPanel treePanel = new JPanel(new BorderLayout()); 
        treePanel.setBackground(new Color(45, 45, 45));

        DefaultMutableTreeNode root = new DefaultMutableTreeNode(currentPackage.getPackageName());
        
        // Popula as classes raiz com ordenação alfabética
        currentPackage.getExportTable().stream()
                .filter(e -> e.getObjectPackage() == null)
                .sorted((e1, e2) -> e1.getObjectName().getName().compareToIgnoreCase(e2.getObjectName().getName()))
                .forEach(e -> {
                    DefaultMutableTreeNode node = new DefaultMutableTreeNode(e);
                    node.add(new DefaultMutableTreeNode("Loading...")); // Dummy para lazy loading
                    root.add(node);
                });

        objectTree = new JTree(root);
        objectTree.setBackground(new Color(40, 40, 40));
        objectTree.setForeground(Color.WHITE);
        
        // Lazy Loading: Carrega os filhos apenas quando o usuário expande a classe
        objectTree.addTreeWillExpandListener(new TreeWillExpandListener() {
            @Override
            public void treeWillExpand(TreeExpansionEvent event) {
                DefaultMutableTreeNode node = (DefaultMutableTreeNode) event.getPath().getLastPathComponent();
                if (node.getChildCount() == 1 && node.getChildAt(0).toString().equals("Loading...")) {
                    node.removeAllChildren();
                    if (node.getUserObject() instanceof ExportEntry entry) {
                        buildClassHierarchy(node, entry);
                    }
                    ((DefaultTreeModel) objectTree.getModel()).nodeStructureChanged(node);
                }
            }
            @Override
            public void treeWillCollapse(TreeExpansionEvent event) {}
        });

        // Estilização dos ícones e cores por tipo de objeto
        objectTree.setCellRenderer(new DefaultTreeCellRenderer() {
            @Override
            public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean exp, boolean leaf, int row, boolean focus) {
                super.getTreeCellRendererComponent(tree, value, sel, exp, leaf, row, focus);
                setBackgroundNonSelectionColor(new Color(40, 40, 40));
                
                Object userObject = ((DefaultMutableTreeNode) value).getUserObject();
                if (userObject instanceof ExportEntry entry) {
                    String className = entry.getFullClassName();
                    setText(entry.getObjectName().getName() + " (" + className + ")");
                    
                    if (className.contains("Class")) setForeground(new Color(150, 200, 255));
                    else if (className.contains("Function")) setForeground(new Color(150, 255, 150));
                    else setForeground(Color.LIGHT_GRAY);
                } else {
                    setForeground(new Color(255, 200, 100)); // Cor das pastas virtuais
                }
                return this;
            }
        });

        objectTree.addTreeSelectionListener(e -> {
			try {
				handleSelection();
			} catch (IOException e3) {
				// TODO Auto-generated catch block
				e3.printStackTrace();
			}
		});

        JScrollPane scroll = new JScrollPane(objectTree);
        scroll.setBorder(null);
        treePanel.add(scroll, BorderLayout.CENTER); 
        return treePanel;
    }

    private void buildClassHierarchy(DefaultMutableTreeNode parentNode, ExportEntry parentEntry) {
        int parentIndex = parentEntry.getIndex() + 1;
        
        List<ExportEntry> children = currentPackage.getExportTable().stream()
                .filter(e -> e.getObjectPackage() != null && e.getObjectPackage().getIndex() + 1 == parentIndex)
                .sorted((e1, e2) -> e1.getObjectName().getName().compareToIgnoreCase(e2.getObjectName().getName()))
                .collect(Collectors.toList());

        DefaultMutableTreeNode folderVars = new DefaultMutableTreeNode("Variables");
        DefaultMutableTreeNode folderFuncs = new DefaultMutableTreeNode("Functions");
        DefaultMutableTreeNode folderStructs = new DefaultMutableTreeNode("Structs");
        DefaultMutableTreeNode folderOther = new DefaultMutableTreeNode("Other Content");

        for (ExportEntry child : children) {
            String className = child.getFullClassName();
            DefaultMutableTreeNode childNode = new DefaultMutableTreeNode(child);
            
            if (className.contains("Struct") || className.contains("State")) {
                childNode.add(new DefaultMutableTreeNode("Loading..."));
            }

            if (className.contains("Property")) folderVars.add(childNode);
            else if (className.contains("Function")) folderFuncs.add(childNode);
            else if (className.contains("Struct")) folderStructs.add(childNode);
            else folderOther.add(childNode);
        }

        if (folderVars.getChildCount() > 0) parentNode.add(folderVars);
        if (folderFuncs.getChildCount() > 0) parentNode.add(folderFuncs);
        if (folderStructs.getChildCount() > 0) parentNode.add(folderStructs);
        if (folderOther.getChildCount() > 0) parentNode.add(folderOther);
    }

    private JTabbedPane createContentTabs() {
        JTabbedPane tabs = new JTabbedPane();
        infoArea = createTextArea(new Color(25, 25, 25), new Color(200, 200, 200));
        codeArea = createTextArea(new Color(20, 20, 25), new Color(170, 255, 170));

        tabs.addTab("Binary Details", new JScrollPane(infoArea));
        tabs.addTab("Source Code", new JScrollPane(codeArea));
        return tabs;
    }

    private JPanel createConsolePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(80, 80, 80)), 
            "Debug Console", 0, 0, null, Color.GRAY));
        
        panel.add(new JScrollPane(debugConsole), BorderLayout.CENTER);
        return panel;
    }

    private JTextArea createTextArea(Color bg, Color fg) {
        JTextArea area = new JTextArea();
        area.setEditable(false);
        area.setBackground(bg);
        area.setForeground(fg);
        area.setFont(new Font("Monospaced", Font.PLAIN, 12));
        area.setMargin(new Insets(10, 10, 10, 10));
        return area;
    }

    private JPanel createStatusBar() {
        JPanel status = new JPanel(new FlowLayout(FlowLayout.LEFT));
        status.setBackground(new Color(60, 60, 60));
        JLabel lbl = new JLabel("L2Explorer 2026 | " + currentPackage.getExportTable().size() + " Objects");
        lbl.setForeground(Color.WHITE);
        status.add(lbl);
        return status;
    }

    private void handleSelection() throws IOException {
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) objectTree.getLastSelectedPathComponent();
        
        if (node == null || !(node.getUserObject() instanceof ExportEntry entry)) return;

        debugConsole.clear();
        debugConsole.log("Analyzing: " + entry.getObjectName().getName());

        StringBuilder sb = new StringBuilder();
        sb.append("=== UNREAL EXPORT ENTRY ===\n");
        sb.append("Object Name : ").append(entry.getObjectName().getName()).append("\n");
        sb.append("Class Name  : ").append(entry.getFullClassName()).append("\n");
        sb.append("Size        : ").append(entry.getSize()).append(" bytes\n");
        sb.append("Offset      : 0x").append(Integer.toHexString(entry.getOffset()).toUpperCase()).append("\n");
        infoArea.setText(sb.toString());

        codeArea.setText("// Decompiling " + entry.getObjectName().getName() + "...\n");

        try {
            String source;
            
            // Se for uma Class completa
            if (entry.getFullClassName().equals("Core.Class")) {
                debugConsole.log("Decompiling full class with all members...");
                source = decompiler.decompileClassComplete(entry);
            }
            // Se for uma Function individual
            else if (entry.getFullClassName().equals("Core.Function")) {
                debugConsole.log("Decompiling function bytecode...");
                source = decompiler.decompile(entry);
            }
            // Outros tipos (Struct, Property, etc)
            else {
                debugConsole.log("Type not fully supported: " + entry.getFullClassName());
                source = "// Type: " + entry.getFullClassName() + "\n";
                source += "// Name: " + entry.getObjectName().getName() + "\n";
                source += "// Decompilation not yet implemented for this type\n";
            }
            
            codeArea.setText(source);
            codeArea.setCaretPosition(0);
            debugConsole.log("Decompilation completed!");
            
        } catch (Exception e) {
            codeArea.setText("// ERROR during decompilation:\n// " + e.getMessage());
            debugConsole.log("ERROR: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
	 * @return the systemDir
	 */
	public File getSystemDir() {
		return systemDir;
	}

	/**
	 * @param systemDir the systemDir to set
	 */
	public void setSystemDir(File systemDir) {
		this.systemDir = systemDir;
	}

	/**
     * Componente interno para logs de debug.
     */
    public class DebugConsole extends JTextArea {
        private static final long serialVersionUID = 1L;

		public DebugConsole() {
            setEditable(false);
            setBackground(new Color(15, 15, 15));
            setForeground(new Color(100, 255, 100));
            setFont(new Font("Monospaced", Font.PLAIN, 11));
        }

        public void log(String msg) {
            SwingUtilities.invokeLater(() -> {
                append("[DEBUG] " + msg + "\n");
                setCaretPosition(getDocument().getLength());
            });
        }
        
        public void clear() {
            setText("");
        }
    }
}