package org.l2explorer.app;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Insets;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.border.EmptyBorder;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeWillExpandListener;
import javax.swing.text.DefaultHighlighter;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;

import org.l2explorer.io.UnrealPackage;
import org.l2explorer.io.UnrealPackage.ExportEntry;
import org.l2explorer.utils.crypt.rsa.L2Ver41x;
import org.l2explorer.utils.crypt.rsa.L2Ver41xInputStream;
import org.l2explorer.utils.enums.UnrealOpcode;
import org.l2explorer.utils.unreal.UnrealDecompiler;

public class ExplorerPanel extends JPanel {
    private static final long serialVersionUID = 1L;
    
    // Theme Colors
    private static final Color SIDEBAR_BG = new Color(30, 31, 34);
    private static final Color MAIN_BG = new Color(43, 43, 43);
    private static final Color ACCENT = new Color(99, 102, 241);
    @SuppressWarnings("unused")
	private static final Color ACCENT_HOVER = new Color(124, 127, 247);
    private static final Color SUCCESS = new Color(52, 211, 153);
    private static final Color WARNING = new Color(251, 191, 36);
    private static final Color INSPECT = new Color(139, 92, 246);
    
    private JTree objectTree;
    private JTextArea codeArea, infoArea;
    public DebugConsole debugConsole;
    private UnrealDecompiler decompiler;
    private UnrealPackage currentPackage;
    @SuppressWarnings("unused")
	private File systemDir;
    private JLabel statusLabel, packageNameLabel;
    private JButton openBtn, reloadBtn, exportBtn, inspectBtn;
	private String chronicle_name;

    public ExplorerPanel(UnrealPackage up, File baseDir) {
        this.currentPackage = up;
        this.systemDir = baseDir;
        this.debugConsole = new DebugConsole();
        this.decompiler = new UnrealDecompiler(up, baseDir);

        setLayout(new BorderLayout());
        setBackground(MAIN_BG);

        add(createTopBar(), BorderLayout.NORTH);
        add(createMainContent(), BorderLayout.CENTER);
        add(createStatusBar(), BorderLayout.SOUTH);
        
        updatePackageInfo();
    }

    @SuppressWarnings("unused")
	private JPanel createTopBar() {
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(SIDEBAR_BG);
        topBar.setBorder(new EmptyBorder(10, 15, 10, 15));
        topBar.setPreferredSize(new Dimension(0, 60));

        // Left side - Logo and Package name
        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        leftPanel.setOpaque(false);
        
        JLabel logo = new JLabel("⚡ L2Explorer");
        logo.setFont(new Font("Segoe UI", Font.BOLD, 20));
        logo.setForeground(ACCENT);
        
        packageNameLabel = new JLabel("");
        packageNameLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        packageNameLabel.setForeground(Color.LIGHT_GRAY);
        
        leftPanel.add(logo);
        leftPanel.add(createSeparator());
        leftPanel.add(packageNameLabel);

        // Right side - Action buttons
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        rightPanel.setOpaque(false);
        
        openBtn = createTopBarButton("📁 Open", ACCENT);
        openBtn.addActionListener(e -> openPackageAction());
        
        reloadBtn = createTopBarButton("🔄 Reload", SUCCESS);
        reloadBtn.addActionListener(e -> reloadPackageAction());
        reloadBtn.setEnabled(false);
        
        exportBtn = createTopBarButton("💾 Export", WARNING);
        exportBtn.addActionListener(e -> exportCodeAction());
        exportBtn.setEnabled(false);
        
        inspectBtn = createTopBarButton("🔍 Inspect", INSPECT);
        inspectBtn.addActionListener(e -> InspectAction());
       
        rightPanel.add(openBtn);
        rightPanel.add(reloadBtn);
        rightPanel.add(exportBtn);
        rightPanel.add(inspectBtn);

        topBar.add(leftPanel, BorderLayout.WEST);
        topBar.add(rightPanel, BorderLayout.EAST);
        
        return topBar;
    }
    
    private void InspectAction() {
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) objectTree.getLastSelectedPathComponent();
        if (node == null || !(node.getUserObject() instanceof ExportEntry entry)) {
            debugConsole.log("⚠️ Selecione um objeto válido na árvore para inspecionar.");
            return;
        }

        JDialog inspector = new JDialog((java.awt.Frame) SwingUtilities.getWindowAncestor(this), 
            "Hex Inspector: " + entry.getObjectName().getName(), true);
        inspector.setSize(1000, 650);
        inspector.setLocationRelativeTo(this);
        inspector.setLayout(new BorderLayout());
        
        JTextArea hexArea = createStyledTextArea(new Color(15, 15, 15), Color.CYAN);
        hexArea.setFont(new Font("JetBrains Mono", Font.PLAIN, 12));
        hexArea.setEditable(false); // Importante para não bagunçar o dump

        try {
            // Lemos os bytes do Samurai Crow via motor de Raio-X
            final byte[] rawData = currentPackage.readRawData(entry);
            
            hexArea.setText(convertToHexDump(rawData));
            applyOpcodeHighlighting(hexArea, rawData, entry.getFullClassName());
            hexArea.setCaretPosition(0);

            hexArea.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
                @Override
                public void mouseMoved(java.awt.event.MouseEvent e) {
                    try {
                        int pos = hexArea.viewToModel2D(e.getPoint());
                        int line = hexArea.getLineOfOffset(pos);
                        int col = pos - hexArea.getLineStartOffset(line);

                        if (line >= 2 && col >= 10 && col <= 58) {
                            int byteCol = (col - 10) / 3;
                            int byteIndex = ((line - 2) * 16) + byteCol;

                            if (byteIndex >= 0 && byteIndex < rawData.length) {
                                int opcodeValue = rawData[byteIndex] & 0xFF;
                                
                                // BUSCA DINÂMICA NO SEU ENUM
                                UnrealOpcode.Main op = UnrealOpcode.Main.fromInt(opcodeValue);
                                String opName = (op != null) ? op.getName() : "UNKNOWN_TOKEN";
                                
                                hexArea.setToolTipText(String.format("Offset: 0x%X | Token: %s (0x%02X)", 
                                                       byteIndex, opName, opcodeValue));
                                return;
                            }
                        }
                        hexArea.setToolTipText(null);
                    } catch (Exception ex) {
                        hexArea.setToolTipText(null);
                    }
                }
            });

        } catch (Exception e) {
            hexArea.setText("❌ Erro ao ler dados brutos: " + e.getMessage());
            e.printStackTrace();
        }

        inspector.add(new JScrollPane(hexArea), BorderLayout.CENTER);
        inspector.add(createHexLegendPanel(), BorderLayout.SOUTH);

        inspector.setVisible(true);
    }

    /**
     * Converte bytes para o formato clássico de HexDump (Offset | Hex | ASCII)
     */
    private String convertToHexDump(byte[] data) {
        StringBuilder sb = new StringBuilder();
        // Cabeçalho (Linha 0)
        sb.append("OFFSET    00 01 02 03 04 05 06 07  08 09 0A 0B 0C 0D 0E 0F   ASCII\n");
        // Separador (Linha 1)
        sb.append("--------------------------------------------------------------------------\n");
        
        for (int i = 0; i < data.length; i += 16) {
            // Offset: 8 chars + 2 espaços = 10
            sb.append(String.format("%08X  ", i));
            
            for (int j = 0; j < 16; j++) {
                if (i + j < data.length) {
                    sb.append(String.format("%02X ", data[i + j]));
                } else {
                    sb.append("   ");
                }
                if (j == 7) sb.append(" "); // Espaço extra no meio
            }
            
            sb.append(" "); // Espaço antes do ASCII
            for (int j = 0; j < 16; j++) {
                if (i + j < data.length) {
                    int b = data[i + j] & 0xFF;
                    sb.append((b >= 32 && b <= 126) ? (char) b : ".");
                }
            }
            sb.append("\n");
        }
        return sb.toString();
    }
    
    /**
     * Analisa os bytes e define as cores baseadas no tipo de dado.
     * @param data Os bytes brutos do objeto.
     * @param fullClass A classe do objeto (ex: Core.Function).
     */

    private void markByteInHex(JTextArea area, int byteIndex, Color color) {
        try {
            int line = byteIndex / 16;
            int col = byteIndex % 16;
            
            // Pulamos as 2 linhas do cabeçalho
            int headerLines = 2;
            
            // Descobre onde a linha real começa no JTextArea
            int lineStart = area.getLineStartOffset(line + headerLines);
            
            // Cálculo da Coluna:
            // 10 caracteres do offset ("00000000  ")
            // Cada byte anterior ocupa 3 caracteres ("FF ")
            int posInLine = 10 + (col * 3);
            
            // Se passar da metade da linha (col > 7), tem o espaço extra de respiro
            if (col > 7) {
                posInLine++;
            }

            int finalStart = lineStart + posInLine;
            
            // Pinta apenas os 2 caracteres do Hex (sem o espaço)
            area.getHighlighter().addHighlight(finalStart, finalStart + 2, 
                new DefaultHighlighter.DefaultHighlightPainter(color));
                
        } catch (Exception e) {
            // Ignora se o byte estiver fora da área visível
        }
    }
    
    /**
     * Traduz o token para uma cor específica de debug.
     * Use as cores para identificar rapidamente a estrutura do código.
     */
    private Color getColorForOpcode(int opcodeValue) {
        // Busca o objeto do seu Enum pelo valor numérico
        UnrealOpcode.Main op = UnrealOpcode.Main.fromInt(opcodeValue);
        if (op == null) return null;

        return switch (op) {
            // Fluxo e Jumps (Amarelo)
            case JUMP, JUMP_IF_NOT, SWITCH, CASE, GOTO_LABEL -> new Color(251, 191, 36, 120);
            
            // Chamadas de Funções (Verde)
            case FINAL_FUNCTION, VIRTUAL_FUNCTION, GLOBAL_FUNCTION -> new Color(52, 211, 153, 120);
            
            // Retorno e Parada (Vermelho)
            case RETURN, STOP -> new Color(239, 68, 68, 120);
            
            // Variáveis e Contexto (Roxo)
            case LOCAL_VARIABLE, INSTANCE_VARIABLE, DEFAULT_VARIABLE, CLASS_CONTEXT, CONTEXT -> new Color(99, 102, 241, 120);
            
            // Estruturas (Violeta)
            case STRUCT_MEMBER, STRUCT_CONST -> new Color(167, 139, 250, 120);
            
            // Atribuição (Ciano)
            case LET, LET_BOOL -> new Color(34, 211, 238, 120);
            
            default -> null; 
        };
    }
    
    private void applyOpcodeHighlighting(JTextArea area, byte[] data, String fullClass) {
        if (!fullClass.equals("Core.Function") && !fullClass.equals("Core.State")) return;

        // Começamos o scan. 
        // Em funções L2, os bytes iniciais são flags. O bytecode real 
        // costuma começar após o ScriptSize (4 bytes).
        int startOffset = 0; 
        
        for (int i = startOffset; i < data.length; i++) {
            int opcode = data[i] & 0xFF;
            Color c = getColorForOpcode(opcode);
            
            if (c != null) {
                try {
                    markByteInHex(area, i, c);
                    
                    // Lógica de pulo: Se for Native (0x1C), os próximos 2 bytes são o ID
                    if (opcode == 0x1C) {
                        markByteInHex(area, i + 1, c);
                        markByteInHex(area, i + 2, c);
                        i += 2;
                    }
                    // Se for Jump (0x06), os próximos 2 bytes são o endereço
                    else if (opcode == 0x06 || opcode == 0x07) {
                        markByteInHex(area, i + 1, c);
                        markByteInHex(area, i + 2, c);
                        i += 2;
                    }
                } catch (Exception e) {
                    // Silencioso para não travar a visualização
                }
            }
        }
    }

    /**
     * Cria um painel visual para explicar o que cada cor no Hex significa.
     */
    private JPanel createHexLegendPanel() {
        JPanel legendPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 5));
        legendPanel.setBackground(new Color(25, 25, 25)); // Um pouco mais escuro que o fundo
        legendPanel.setBorder(new EmptyBorder(5, 10, 5, 10));

        // Adicionamos os itens da legenda
        legendPanel.add(createLegendItem("⚡ Native Call", new Color(52, 211, 153)));
        legendPanel.add(createLegendItem("🔄 Jump/Loop", new Color(251, 191, 36)));
        legendPanel.add(createLegendItem("🎯 Return", new Color(239, 68, 68)));
        legendPanel.add(createLegendItem("📦 Local Var", new Color(99, 102, 241)));
        legendPanel.add(createLegendItem("📋 Struct/Member", new Color(167, 139, 250)));

        return legendPanel;
    }

    private JPanel createLegendItem(String text, Color color) {
        JPanel item = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        item.setOpaque(false);

        JPanel colorBox = new JPanel();
        colorBox.setPreferredSize(new Dimension(12, 12));
        colorBox.setBackground(color);
        colorBox.setBorder(BorderFactory.createLineBorder(Color.WHITE, 1));

        JLabel label = new JLabel(text);
        label.setForeground(new Color(200, 200, 200));
        label.setFont(new Font("Segoe UI", Font.BOLD, 10));

        item.add(colorBox);
        item.add(label);
        return item;
    }
    
    private JButton createTopBarButton(String text, Color color) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setForeground(Color.WHITE);
        btn.setBackground(color);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(110, 35));
        
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn.setBackground(color.brighter());
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn.setBackground(color);
            }
        });
        
        return btn;
    }

    private JSeparator createSeparator() {
        JSeparator sep = new JSeparator(JSeparator.VERTICAL);
        sep.setPreferredSize(new Dimension(2, 30));
        sep.setForeground(new Color(60, 60, 60));
        return sep;
    }

    private JPanel createMainContent() {
        JPanel mainContent = new JPanel(new BorderLayout());
        mainContent.setBackground(MAIN_BG);
        mainContent.setBorder(new EmptyBorder(5, 10, 5, 10));

        JSplitPane mainSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        mainSplit.setDividerLocation(320);
        mainSplit.setDividerSize(8);
        mainSplit.setBorder(null);
        mainSplit.setOpaque(false);

        mainSplit.setLeftComponent(createTreePanel());
        
        JSplitPane rightSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        rightSplit.setDividerLocation(500);
        rightSplit.setDividerSize(8);
        rightSplit.setResizeWeight(0.7);
        rightSplit.setBorder(null);
        rightSplit.setOpaque(false);
        
        rightSplit.setTopComponent(createContentTabs());
        rightSplit.setBottomComponent(createConsolePanel());

        mainSplit.setRightComponent(rightSplit);
        mainContent.add(mainSplit);
        
        return mainContent;
    }

    private JPanel createTreePanel() {
        JPanel treePanel = new JPanel(new BorderLayout());
        treePanel.setBackground(MAIN_BG);
        treePanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(60, 60, 60)), 
            " Package Structure ", 0, 0, 
            new Font("Segoe UI", Font.BOLD, 12), 
            Color.LIGHT_GRAY));

        // --- PROTEÇÃO PARA ARQUIVOS DAT (currentPackage == null) ---
        DefaultMutableTreeNode root;
        if (currentPackage != null) {
            /* Context: English comments as requested */
            // Initialize root with the Unreal Package name
            root = new DefaultMutableTreeNode(currentPackage.getPackageName());
            
            // Build the first level of the tree from the Export Table
            currentPackage.getExportTable().stream()
                    .filter(e -> e.getObjectPackage() == null)
                    .sorted((e1, e2) -> e1.getObjectName().getName().compareToIgnoreCase(e2.getObjectName().getName()))
                    .forEach(e -> {
                        DefaultMutableTreeNode node = new DefaultMutableTreeNode(e);
                        node.add(new DefaultMutableTreeNode("Loading..."));
                        root.add(node);
                    });
        } else {
            /* Context: English comments as requested */
            // Fallback for Data Files (.dat, .ini) that don't have an Unreal structure
            root = new DefaultMutableTreeNode("Data / Logic Content");
        }

        objectTree = new JTree(root);
        objectTree.setBackground(new Color(50, 50, 50));
        objectTree.setForeground(Color.WHITE);
        objectTree.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        objectTree.setRowHeight(22);
        
        // Add the expansion listener (it already handles null currentPackage internally)
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
            @Override public void treeWillCollapse(TreeExpansionEvent event) {}
        });

        objectTree.setCellRenderer(new ModernTreeCellRenderer());
        objectTree.addTreeSelectionListener(_ -> {
            try { handleSelection(); } catch (IOException ex) { showError("Selection Error", ex.getMessage()); }
        });

        JScrollPane scroll = new JScrollPane(objectTree);
        scroll.setBorder(null);
        treePanel.add(scroll);
        
        return treePanel;
    }

    private class ModernTreeCellRenderer extends DefaultTreeCellRenderer {
        private static final long serialVersionUID = -7762182474560384946L;

		@Override
        public Component getTreeCellRendererComponent(JTree tree, Object value, 
                boolean sel, boolean exp, boolean leaf, int row, boolean focus) {
            super.getTreeCellRendererComponent(tree, value, sel, exp, leaf, row, focus);
            
            setBackgroundNonSelectionColor(new Color(50, 50, 50));
            setBackgroundSelectionColor(ACCENT);
            setBorderSelectionColor(ACCENT);
            
            Object userObject = ((DefaultMutableTreeNode) value).getUserObject();
            if (userObject instanceof ExportEntry entry) {
                String className = entry.getFullClassName();
                String icon = getIconForClass(className);
                setText(icon + " " + entry.getObjectName().getName());
                
                if (className.contains("Class")) setForeground(new Color(129, 140, 248));
                else if (className.contains("Function")) setForeground(new Color(52, 211, 153));
                else if (className.contains("Property")) setForeground(new Color(251, 191, 36));
                else setForeground(Color.LIGHT_GRAY);
            } else {
                setForeground(new Color(147, 197, 253));
            }
            
            return this;
        }
        
        private String getIconForClass(String className) {
            if (className.contains("Class")) return "📦";
            if (className.contains("Function")) return "⚡";
            if (className.contains("Property")) return "🔧";
            if (className.contains("Struct")) return "📋";
            if (className.contains("Enum")) return "🔢";
            if (className.contains("Const")) return "🔒";
            return "📄";
        }        
    }
    
    private void buildClassHierarchy(DefaultMutableTreeNode parentNode, ExportEntry parentEntry) {
        int parentIndex = parentEntry.getIndex() + 1;
        
        List<ExportEntry> children = currentPackage.getExportTable().stream()
                .filter(e -> e.getObjectPackage() != null && 
                            e.getObjectPackage().getIndex() + 1 == parentIndex)
                .sorted((e1, e2) -> e1.getObjectName().getName()
                        .compareToIgnoreCase(e2.getObjectName().getName()))
                .collect(Collectors.toList());

        // 1. Criamos um mapa para organizar as categorias dinamicamente
        java.util.Map<String, DefaultMutableTreeNode> categoryNodes = new java.util.LinkedHashMap<>();

        for (ExportEntry child : children) {
            String fullClass = child.getFullClassName();
            // Remove o "Core." para ficar mais limpo (ex: ObjectProperty)
            String baseClass = fullClass.substring(fullClass.lastIndexOf('.') + 1);
            
            // 2. Definimos o nome da "Pasta Pai"
            String folderName;
            if (baseClass.endsWith("Property")) {
                folderName = "📁 Properties";
            } else {
                folderName = "📁 " + baseClass + "s"; // Pluraliza: Function -> Functions, Enum -> Enums
            }

            // 3. Pegamos ou criamos o nó da categoria principal
            DefaultMutableTreeNode mainCategory = categoryNodes.computeIfAbsent(folderName, DefaultMutableTreeNode::new);
            
            DefaultMutableTreeNode targetNode = mainCategory;

            // 4. Se for Property, criamos uma subpasta com o tipo real (Ouro puro para o WindowHandle!)
            if (baseClass.endsWith("Property")) {
                targetNode = getOrCreateSubNode(mainCategory, baseClass);
            }

            DefaultMutableTreeNode childNode = new DefaultMutableTreeNode(child);
            
            // Se for algo que pode ter filhos (Struct, State, Function), adiciona o Loading...
            if (baseClass.contains("Struct") || baseClass.contains("State") || baseClass.contains("Function")) {
                childNode.add(new DefaultMutableTreeNode("Loading..."));
            }

            targetNode.add(childNode);
        }

        // 5. Adicionamos as pastas ao nó pai na ordem que elas foram criadas
        categoryNodes.values().forEach(parentNode::add);
    }

    /**
     * Método auxiliar para criar subpastas (ex: Properties -> ObjectProperty)
     */
    private DefaultMutableTreeNode getOrCreateSubNode(DefaultMutableTreeNode parent, String name) {
        for (int i = 0; i < parent.getChildCount(); i++) {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) parent.getChildAt(i);
            if (node.getUserObject().equals(name)) return node;
        }
        DefaultMutableTreeNode newNode = new DefaultMutableTreeNode(name);
        parent.add(newNode);
        return newNode;
    }

    private JTabbedPane createContentTabs() {
        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(new Font("Segoe UI", Font.BOLD, 11));
        tabs.setBackground(MAIN_BG);
        
        infoArea = createStyledTextArea(new Color(30, 30, 30), new Color(200, 200, 200));
        codeArea = createStyledTextArea(new Color(30, 30, 30), new Color(171, 178, 191));

        JScrollPane infoScroll = new JScrollPane(infoArea);
        JScrollPane codeScroll = new JScrollPane(codeArea);
        
        infoScroll.setBorder(null);
        codeScroll.setBorder(null);
        
        tabs.addTab("📊 Details", infoScroll);
        tabs.addTab("💻 Source Code", codeScroll);
        
        return tabs;
    }

    private JPanel createConsolePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(MAIN_BG);
        panel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(60, 60, 60)), 
            " 🖥️ Debug Console ", 0, 0,
            new Font("Segoe UI", Font.BOLD, 12), 
            Color.LIGHT_GRAY));
        
        JScrollPane scroll = new JScrollPane(debugConsole);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        panel.add(scroll);
        
        return panel;
    }

    private JTextArea createStyledTextArea(Color bg, Color fg) {
        JTextArea area = new JTextArea();
        area.setEditable(false);
        area.setBackground(bg);
        area.setForeground(fg);
        area.setFont(new Font("JetBrains Mono", Font.PLAIN, 12));
        area.setMargin(new Insets(15, 15, 15, 15));
        area.setLineWrap(false);
        area.setTabSize(4);
        return area;
    }

    private JPanel createStatusBar() {
        JPanel status = new JPanel(new BorderLayout());
        status.setBackground(SIDEBAR_BG);
        status.setBorder(new EmptyBorder(8, 15, 8, 15));
        status.setPreferredSize(new Dimension(0, 35));
        
        statusLabel = new JLabel("Ready");
        statusLabel.setForeground(SUCCESS);
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        
        JLabel versionLabel = new JLabel("v2.0.0 | 2026");
        versionLabel.setForeground(Color.GRAY);
        versionLabel.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        
        status.add(statusLabel, BorderLayout.WEST);
        status.add(versionLabel, BorderLayout.EAST);
        
        return status;
    }

    // ===== ACTIONS =====

    private void openPackageAction() {
        JFileChooser chooser = new JFileChooser(GeneralConfig.getLastDirectory());
        chooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter(
            "Unreal Package (*.u, *.utx, *.unr)", "u", "utx", "unr"));
        
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File selected = chooser.getSelectedFile();
            GeneralConfig.setLastDirectory(selected.getParent());
            GeneralConfig.setLastPackage(selected.getAbsolutePath());
            
            loadPackage(selected);
        }
    }

    private void reloadPackageAction() {
        String lastPackage = GeneralConfig.getLastPackage();
        if (!lastPackage.isEmpty()) {
            loadPackage(new File(lastPackage));
        }
    }

    private void loadPackage(File file) {
        String fileName = file.getName().toLowerCase();

        // --- INTERCEPTADOR PARA DAT/INI (SAMURAI CROW 413) ---
        if (fileName.endsWith(".dat") || fileName.endsWith(".ini")) {
            loadDatFile(file); // Chama o método que decripta via RSA + Zlib
            return;
        }

        // --- FLUXO ORIGINAL PARA PACOTES UNREAL (.u, .utx, .unr, .ukx) ---
        SwingWorker<Void, String> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() {
                publish("Loading: " + file.getName());
                try {
                    // Aqui o motor ACMI decripta o Header (XOR/111/121) automaticamente
                    UnrealPackage newPackage = new UnrealPackage(file, true);
                    
                    SwingUtilities.invokeLater(() -> {
                        currentPackage = newPackage;
                        systemDir = file.getParentFile();
                        decompiler = new UnrealDecompiler(newPackage, file);
                        
                        // Rebuild tree
                        DefaultMutableTreeNode root = new DefaultMutableTreeNode(newPackage.getPackageName());
                        newPackage.getExportTable().stream()
                            .filter(e -> e.getObjectPackage() == null)
                            .sorted((e1, e2) -> e1.getObjectName().getName()
                                    .compareToIgnoreCase(e2.getObjectName().getName()))
                            .forEach(e -> {
                                DefaultMutableTreeNode node = new DefaultMutableTreeNode(e);
                                node.add(new DefaultMutableTreeNode("Loading..."));
                                root.add(node);
                            });
                        
                        objectTree.setModel(new DefaultTreeModel(root));
                        updatePackageInfo(); // Mostra Versão, Licença e GUID no console
                        
                        reloadBtn.setEnabled(true);
                        exportBtn.setEnabled(true);
                        
                        debugConsole.clear();
                        debugConsole.log("✅ Package loaded: " + file.getName());
                        setStatus("Loaded: " + file.getName(), SUCCESS);
                    });
                    
                } catch (Exception ex) {
                    ex.printStackTrace(); // Log completo no console da IDE
                    SwingUtilities.invokeLater(() -> {
                        showError("Load Error", ex.getMessage());
                        setStatus("Error loading package", new Color(239, 68, 68));
                    });
                }
                return null;
            }
            
            @Override
            protected void process(List<String> chunks) {
                chunks.forEach(msg -> setStatus(msg, WARNING));
            }
        };
        
        worker.execute();
    }

    private void exportCodeAction() {
        JFileChooser chooser = new JFileChooser(GeneralConfig.getLastDirectory());
        chooser.setDialogTitle("Export Source Code");
        chooser.setSelectedFile(new File(currentPackage.getPackageName() + "_export.uc"));
        
        if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            // TODO: Implement export logic
            debugConsole.log("Export to: " + chooser.getSelectedFile().getAbsolutePath());
        }
    }

    private void handleSelection() throws IOException {
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) objectTree.getLastSelectedPathComponent();
        
        if (node == null || !(node.getUserObject() instanceof ExportEntry entry)) {
            return;
        }

        debugConsole.clear();
        debugConsole.log("📂 Selected: " + entry.getObjectName().getName());
        setStatus("Analyzing: " + entry.getObjectName().getName(), ACCENT);

        // Update info Panel (Aquele quadrado com Detalhes)
        StringBuilder info = new StringBuilder();
        info.append("╔══════════════════════════════════════╗\n");
        info.append("║       UNREAL EXPORT ENTRY           ║\n");
        info.append("╠══════════════════════════════════════╣\n");
        info.append("  Object Name  : ").append(entry.getObjectName().getName()).append("\n");
        info.append("  Class Name   : ").append(entry.getFullClassName()).append("\n");
        info.append("  Data Size    : ").append(entry.getSize()).append(" bytes\n");
        info.append("  File Offset  : 0x").append(Integer.toHexString(entry.getOffset()).toUpperCase()).append("\n");
        info.append("╚══════════════════════════════════════╝\n");
        infoArea.setText(info.toString());

        // Decompile - AQUI ESTÁ A MUDANÇA REAL
        codeArea.setText("// Decompiling " + entry.getObjectName().getName() + "...\n");

        try {
            String source;
            String fullClass = entry.getFullClassName();

            // Despachante: Manda para o método correto do decompiler baseado na classe
            if (fullClass.equals("Core.Class")) {
                debugConsole.log("🔄 Decompiling class...");
                source = decompiler.decompileClassComplete(entry);
            } else if (fullClass.equals("Core.Function")) {
                debugConsole.log("⚡ Decompiling function...");
                source = decompiler.decompileFunction(entry);
            } else if (fullClass.equals("Core.Const")) {
                debugConsole.log("🔒 Decompiling constant...");
                source = decompiler.decompileConst(entry); // Adicione esta linha
            } else if (fullClass.equals("Core.Struct")) {
                debugConsole.log("📋 Decompiling struct...");
                source = decompiler.decompileStruct(entry); // <--- Chamando o exorcista de structs
            } else if (fullClass.equals("Core.Enum")) {
                debugConsole.log("🔢 Decompiling enum...");
                source = decompiler.decompileEnum(entry);   // <--- Chamando o leitor de enums
            } else if (fullClass.endsWith("Property")) {
                debugConsole.log("🔧 Decompiling property...");
                source = decompiler.getFullPropertyLine(entry); // <--- Chamando o identificador de tipos
            } else if (fullClass.equals("Core.TextBuffer")) {
                debugConsole.log("📄 Reading TextBuffer...");
                source = decompiler.decompileTextBuffer(entry);            
            } else {
                source = "// Type: " + fullClass + "\n// Not yet supported\n";
            }
            
            codeArea.setText(source);
            codeArea.setCaretPosition(0);
            debugConsole.log("✅ Decompilation complete!");
            setStatus("Ready", SUCCESS);
            
        } catch (Exception e) {
            codeArea.setText("// ❌ ERROR: " + e.getMessage());
            debugConsole.log("❌ ERROR: " + e.getMessage());
            setStatus("Decompilation failed", new Color(239, 68, 68));
            e.printStackTrace();
        }
    }

    private void updatePackageInfo() {
        if (currentPackage == null) return;

        // Pega os dados direto do objeto que o app já decriptou
        int ver = currentPackage.getVersion();
        int lic = currentPackage.getLicense();
        String guid = currentPackage.getGUID().toString().toUpperCase().replace("-", "");
        int exports = currentPackage.getExportTable().size();
        int names = currentPackage.getNameTable().size();
        
        switch (guid) {
        case "FB5A049677054A12856CA726A1B964F9":
            chronicle_name = "Lindvior";
            break;
        case "10F5A7D67D994E949F305C985D45C026":
            chronicle_name = "Samurai Crow (Main)";
            break;
        case "4B1F4CDC62174079905B8B274FE3B422":
        	chronicle_name = "Samurai Crow (Essence)";
        	break;
        case "576B076E627145D8B5EFA8C7792893F0":
        	chronicle_name = "Superion";
        	break;
        case "B634DA4591DF4ED5A2E2B65316406D12":
        	chronicle_name = "Guardians";
        	break;
        default:
            chronicle_name = "Unknown";
            break;
    }

        // Atualiza a Label principal
        packageNameLabel.setText(String.format("📦 %s | %s | %d Objects", 
            currentPackage.getPackageName(), chronicle_name, exports));

        // Opcional: Log detalhado no console de debug que você criou
        debugConsole.log("--- Package Metadata ---");
        debugConsole.log("File Version: " + ver);
        debugConsole.log("Licensee: " + lic + " (" + chronicle_name + ")");
        debugConsole.log("GUID: " + guid);
        debugConsole.log("Names: " + names);
        debugConsole.log("------------------------");
    }
    private void setStatus(String message, Color color) {
        statusLabel.setText(message);
        statusLabel.setForeground(color);
    }

    /**
     * Decrypts and displays Lineage II .dat files (Crypt 413 - RSA + Zlib).
     * * @param file The encrypted .dat file
     */
    public void loadDatFile(File file) {
        SwingWorker<Void, String> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() {
                publish("Decrypting: " + file.getName() + " (RSA 413)...");
                
                // Usamos um FileInputStream puro para evitar o erro na linha 43 do seu RandomAccessFile
                try (FileInputStream fis = new FileInputStream(file)) {
                    
                    // 1. Pular o header "Lineage2Ver413" (28 bytes em UTF-16LE)
                    long skipped = fis.skip(28);
                    if (skipped < 28) throw new IOException("Invalid DAT header.");

                    // 2. Inicializar o stream de decriptação RSA + Descompressão Zlib
                    // Certifique-se que MODULUS_413 e PRIVATE_EXPONENT_413 estão acessíveis
                    try (L2Ver41xInputStream l2in = new L2Ver41xInputStream(fis, L2Ver41x.MODULUS_413, L2Ver41x.PRIVATE_EXPONENT_413)) {
                        
                        // 3. Ler os dados decriptados
                        byte[] decryptedData = l2in.readAllBytes();
                        
                        // No Samurai Crow, a maioria dos DATs usa UTF-16LE para as strings
                        String content = new String(decryptedData, java.nio.charset.StandardCharsets.UTF_16LE);

                        SwingUtilities.invokeLater(() -> {
                            codeArea.setText(content);
                            codeArea.setCaretPosition(0);
                            
                            // Limpa a árvore já que DAT não tem estrutura de pacotes Unreal
                            objectTree.setModel(new DefaultTreeModel(new DefaultMutableTreeNode(file.getName())));
                            
                            debugConsole.log("✅ DAT Decrypted successfully!");
                            setStatus("DAT Loaded", SUCCESS);
                        });
                    }
                } catch (Exception e) {
                    // Log detalhado para o seu DebugConsole
                    String errorMsg = "❌ Crypto Error: " + e.getMessage();
                    publish(errorMsg);
                    e.printStackTrace();
                    
                    SwingUtilities.invokeLater(() -> {
                        codeArea.setText("// Failed to decrypt DAT file.\n// Error: " + e.getMessage());
                        showError("Decryption Failed", e.getMessage());
                    });
                }
                return null;
            }

            @Override
            protected void process(java.util.List<String> chunks) {
                chunks.forEach(msg -> debugConsole.log(msg));
            }
        };
        worker.execute();
    }
    
    private void showError(String title, String message) {
        JOptionPane.showMessageDialog(this, message, title, JOptionPane.ERROR_MESSAGE);
    }

    // ===== DEBUG CONSOLE =====
    
    public class DebugConsole extends JTextArea {
        private static final long serialVersionUID = 1L;

        public DebugConsole() {
            setEditable(false);
            setBackground(new Color(20, 20, 20));
            setForeground(new Color(52, 211, 153));
            setFont(new Font("JetBrains Mono", Font.PLAIN, 11));
            setMargin(new Insets(10, 10, 10, 10));
        }

        public void log(String msg) {
            SwingUtilities.invokeLater(() -> {
                append(msg + "\n");
                setCaretPosition(getDocument().getLength());
            });
        }
        
        public void clear() {
            setText("");
        }
    }
}