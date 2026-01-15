package org.l2explorer.app;

import com.formdev.flatlaf.FlatDarkLaf;
import org.l2explorer.io.UnrealPackage;
import org.l2explorer.io.UnrealPackage.ExportEntry;
import org.l2explorer.utils.unreal.UnrealDecompiler;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeWillExpandListener;
import javax.swing.tree.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class ExplorerPanel extends JPanel {
    private static final long serialVersionUID = 1L;
    
    // Theme Colors
    private static final Color SIDEBAR_BG = new Color(30, 31, 34);
    private static final Color MAIN_BG = new Color(43, 43, 43);
    private static final Color ACCENT = new Color(99, 102, 241);
    private static final Color ACCENT_HOVER = new Color(124, 127, 247);
    private static final Color SUCCESS = new Color(52, 211, 153);
    private static final Color WARNING = new Color(251, 191, 36);
    
    private JTree objectTree;
    private JTextArea codeArea, infoArea;
    public DebugConsole debugConsole;
    private UnrealDecompiler decompiler;
    private UnrealPackage currentPackage;
    private File systemDir;
    private JLabel statusLabel, packageNameLabel;
    private JButton openBtn, reloadBtn, exportBtn;

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
        
        rightPanel.add(openBtn);
        rightPanel.add(reloadBtn);
        rightPanel.add(exportBtn);

        topBar.add(leftPanel, BorderLayout.WEST);
        topBar.add(rightPanel, BorderLayout.EAST);
        
        return topBar;
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

        DefaultMutableTreeNode root = new DefaultMutableTreeNode(currentPackage.getPackageName());
        
        currentPackage.getExportTable().stream()
                .filter(e -> e.getObjectPackage() == null)
                .sorted((e1, e2) -> e1.getObjectName().getName().compareToIgnoreCase(e2.getObjectName().getName()))
                .forEach(e -> {
                    DefaultMutableTreeNode node = new DefaultMutableTreeNode(e);
                    node.add(new DefaultMutableTreeNode("Loading..."));
                    root.add(node);
                });

        objectTree = new JTree(root);
        objectTree.setBackground(new Color(50, 50, 50));
        objectTree.setForeground(Color.WHITE);
        objectTree.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        objectTree.setRowHeight(22);
        
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

        objectTree.setCellRenderer(new ModernTreeCellRenderer());
        objectTree.addTreeSelectionListener(e -> {
            try {
                handleSelection();
            } catch (IOException ex) {
                showError("Selection Error", ex.getMessage());
            }
        });

        JScrollPane scroll = new JScrollPane(objectTree);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        treePanel.add(scroll);
        
        return treePanel;
    }

    private class ModernTreeCellRenderer extends DefaultTreeCellRenderer {
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

        DefaultMutableTreeNode folderVars = new DefaultMutableTreeNode("📁 Variables");
        DefaultMutableTreeNode folderFuncs = new DefaultMutableTreeNode("📁 Functions");
        DefaultMutableTreeNode folderStructs = new DefaultMutableTreeNode("📁 Structs");
        DefaultMutableTreeNode folderOther = new DefaultMutableTreeNode("📁 Other");

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
        SwingWorker<Void, String> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() {
                publish("Loading: " + file.getName());
                try {
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
                        updatePackageInfo();
                        
                        reloadBtn.setEnabled(true);
                        exportBtn.setEnabled(true);
                        
                        debugConsole.clear();
                        debugConsole.log("✅ Package loaded: " + file.getName());
                        setStatus("Loaded: " + file.getName(), SUCCESS);
                    });
                    
                } catch (Exception ex) {
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

        // Update info
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

        // Decompile
        codeArea.setText("// Decompiling " + entry.getObjectName().getName() + "...\n");

        try {
            String source;
            if (entry.getFullClassName().equals("Core.Class")) {
                debugConsole.log("🔄 Decompiling class...");
                source = decompiler.decompileClassComplete(entry);
            } else if (entry.getFullClassName().equals("Core.Function")) {
                debugConsole.log("⚡ Decompiling function...");
                source = decompiler.decompileFunction(entry);
            } else {
                source = "// Type: " + entry.getFullClassName() + "\n// Not yet supported\n";
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
        packageNameLabel.setText("📦 " + currentPackage.getPackageName() + 
                                " (" + currentPackage.getExportTable().size() + " objects)");
    }

    private void setStatus(String message, Color color) {
        statusLabel.setText(message);
        statusLabel.setForeground(color);
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