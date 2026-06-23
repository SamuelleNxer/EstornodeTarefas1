package com.nexum.estorno_tarifas.ui.screens;

import com.nexum.estorno_tarifas.configuration.EstornoTarifasConfiguration;
import com.nexum.estorno_tarifas.configuration.EstornoTarifasConfigurationStore;
import com.nexum.estorno_tarifas.directories.SisbrLauncher;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Window;
import java.io.IOException;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileNameExtensionFilter;

public class EstornoTarifasConfigurationScreen extends JDialog {
    private final EstornoTarifasConfigurationStore store = new EstornoTarifasConfigurationStore();
    private final JTextField cooperativa = new JTextField(20);
    private final JComboBox<String> loginManual = new JComboBox<>(new String[]{"Sim", "Nao"});
    private final JTextField usuarioSisbr = new JTextField(20);
    private final JPasswordField senhaSisbr = new JPasswordField(20);
    private final JTextField usuarioNxCoop = new JTextField(20);
    private final JPasswordField senhaNxCoop = new JPasswordField(20);
    private final JTextField nxCoopApiUrl = new JTextField(20);
    private final JCheckBox schedulerHabilitado = new JCheckBox("Habilitar Scheduler");
    private final JTextField janelaAgendamento = new JTextField(20);
    private final JTextField caminhoPlanilha = new JTextField(30);
    private final JTextField executavelSisbr = new JTextField(30);
    private final JTextField moduloSisbr = new JTextField(20);
    private final JTextField menuSisbr = new JTextField(20);
    private final JTextField submenuSisbr = new JTextField(20);
    private final JTextField rotinaSisbr = new JTextField(20);
    private final JTextField documentoPadrao = new JTextField(20);
    private final JCheckBox usarDataAtual = new JCheckBox("Usar a data atual");
    private final JCheckBox marcarEstornoTarifa = new JCheckBox("Marcar Estorno de Tarifa");

    public static void open(Window owner) {
        EstornoTarifasConfigurationScreen dialog = new EstornoTarifasConfigurationScreen(owner);
        dialog.setVisible(true);
    }

    private EstornoTarifasConfigurationScreen(Window owner) {
        super(owner, "Configuracoes - Estorno de Tarifas", ModalityType.APPLICATION_MODAL);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));
        add(createTabs(), BorderLayout.CENTER);
        add(createActions(), BorderLayout.SOUTH);
        loginManual.addActionListener(event -> updateCredentialState());
        loadConfiguration();
        pack();
        setMinimumSize(new Dimension(620, 500));
        setSize(680, 560);
        setLocationRelativeTo(owner);
    }

    private JTabbedPane createTabs() {
        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Campos", new JScrollPane(createFieldsTab()));
        tabs.addTab("Tabelas", new JScrollPane(createTablesTab()));
        return tabs;
    }

    private JPanel createFieldsTab() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(12, 16, 12, 16));
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = 0;
        constraints.weightx = 1;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.anchor = GridBagConstraints.NORTHWEST;
        constraints.insets = new Insets(0, 0, 12, 0);

        int row = 0;
        constraints.gridy = row++;
        panel.add(createSisbrSection(), constraints);
        constraints.gridy = row++;
        panel.add(createNxCoopSection(), constraints);
        constraints.gridy = row++;
        panel.add(createSchedulingSection(), constraints);

        constraints.gridy = row;
        constraints.weighty = 1;
        panel.add(new JPanel(), constraints);
        return panel;
    }

    private JPanel createSisbrSection() {
        JPanel panel = createSectionPanel("Sisbr");
        GridBagConstraints constraints = formConstraints();
        int row = 0;
        row = field(panel, constraints, row, "Usuario Sisbr", usuarioSisbr);
        row = field(panel, constraints, row, "Senha Sisbr", senhaSisbr);
        row = field(panel, constraints, row, "Login Manual", loginManual);
        field(panel, constraints, row, "Nome modulo cobranca", moduloSisbr);
        return panel;
    }

    private JPanel createNxCoopSection() {
        JPanel panel = createSectionPanel("NxCoop");
        GridBagConstraints constraints = formConstraints();
        int row = 0;
        row = field(panel, constraints, row, "Usuario NxCoop", usuarioNxCoop);
        row = field(panel, constraints, row, "Senha NxCoop", senhaNxCoop);
        field(panel, constraints, row, "NxCoop Api Url", nxCoopApiUrl);
        return panel;
    }

    private JPanel createSchedulingSection() {
        JPanel panel = createSectionPanel("Agendamento");
        GridBagConstraints constraints = formConstraints();
        int row = 0;
        row = field(panel, constraints, row, "", schedulerHabilitado);
        field(panel, constraints, row, "Janela agendamento", janelaAgendamento);
        return panel;
    }

    private JPanel createTablesTab() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(12, 16, 12, 16));
        GridBagConstraints constraints = formConstraints();

        int row = 0;
        row = field(panel, constraints, row, "Cooperativa", cooperativa);

        JPanel filePanel = new JPanel(new BorderLayout(6, 0));
        filePanel.add(caminhoPlanilha, BorderLayout.CENTER);
        JButton browse = new JButton("Selecionar...");
        browse.addActionListener(event -> selectSpreadsheet());
        filePanel.add(browse, BorderLayout.EAST);
        row = field(panel, constraints, row, "Planilha XLSX", filePanel);

        JPanel sisbrFilePanel = new JPanel(new BorderLayout(6, 0));
        sisbrFilePanel.add(executavelSisbr, BorderLayout.CENTER);
        JButton browseSisbr = new JButton("Selecionar...");
        browseSisbr.addActionListener(event -> selectSisbrExecutable());
        sisbrFilePanel.add(browseSisbr, BorderLayout.EAST);
        row = field(panel, constraints, row, "Executavel do SISBR", sisbrFilePanel);
        row = field(panel, constraints, row, "Menu", menuSisbr);
        row = field(panel, constraints, row, "Submenu", submenuSisbr);
        row = field(panel, constraints, row, "Rotina", rotinaSisbr);

        row = field(panel, constraints, row, "Documento padrao", documentoPadrao);
        row = field(panel, constraints, row, "Data", usarDataAtual);
        field(panel, constraints, row, "Tipo", marcarEstornoTarifa);
        return panel;
    }

    private JPanel createActions() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 8));
        JButton restore = new JButton("Restaurar PDD");
        restore.addActionListener(event -> populate(EstornoTarifasConfiguration.defaults()));
        JButton save = new JButton("Salvar");
        save.addActionListener(event -> saveConfiguration());
        JButton close = new JButton("Fechar");
        close.addActionListener(event -> dispose());
        panel.add(restore);
        panel.add(save);
        panel.add(close);
        getRootPane().setDefaultButton(save);
        return panel;
    }

    private void loadConfiguration() {
        try {
            populate(store.load());
        } catch (IOException exception) {
            showError("Nao foi possivel carregar as configuracoes.", exception);
            populate(EstornoTarifasConfiguration.defaults());
        }
    }

    private void populate(EstornoTarifasConfiguration configuration) {
        cooperativa.setText(configuration.cooperativa());
        loginManual.setSelectedItem(configuration.loginManual() ? "Sim" : "Nao");
        usuarioSisbr.setText(configuration.usuarioSisbr());
        senhaSisbr.setText(configuration.senhaSisbr());
        usuarioNxCoop.setText(configuration.usuarioNxCoop());
        senhaNxCoop.setText(configuration.senhaNxCoop());
        nxCoopApiUrl.setText(configuration.nxCoopApiUrl());
        schedulerHabilitado.setSelected(configuration.schedulerHabilitado());
        janelaAgendamento.setText(configuration.primeiroDiaUtil() + "-" + configuration.ultimoDiaUtil());
        caminhoPlanilha.setText(configuration.caminhoPlanilha());
        executavelSisbr.setText(SisbrLauncher.normalizeExecutablePath(configuration.executavelSisbr()));
        moduloSisbr.setText(configuration.moduloSisbr());
        menuSisbr.setText(configuration.menuSisbr());
        submenuSisbr.setText(configuration.submenuSisbr());
        rotinaSisbr.setText(configuration.rotinaSisbr());
        documentoPadrao.setText(configuration.documentoPadrao());
        usarDataAtual.setSelected(configuration.usarDataAtual());
        marcarEstornoTarifa.setSelected(configuration.marcarEstornoTarifa());
        updateCredentialState();
    }

    private void saveConfiguration() {
        int[] window = parseSchedulingWindow();
        if (window == null) {
            JOptionPane.showMessageDialog(this, "Informe a janela de agendamento no formato 1-5.",
                    "Configuracao invalida", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int firstDay = window[0];
        int lastDay = window[1];
        if (firstDay > lastDay) {
            JOptionPane.showMessageDialog(this, "O primeiro dia util nao pode ser maior que o ultimo.",
                    "Configuracao invalida", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (required(cooperativa).isEmpty() || required(moduloSisbr).isEmpty()
                || required(rotinaSisbr).isEmpty() || required(documentoPadrao).isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Preencha cooperativa, modulo, rotina e documento padrao.",
                    "Configuracao invalida", JOptionPane.WARNING_MESSAGE);
            return;
        }

        EstornoTarifasConfiguration configuration = new EstornoTarifasConfiguration(
                required(cooperativa), isManualLogin(), usuarioSisbr.getText().trim(),
                new String(senhaSisbr.getPassword()), usuarioNxCoop.getText().trim(),
                new String(senhaNxCoop.getPassword()), nxCoopApiUrl.getText().trim(),
                schedulerHabilitado.isSelected(), firstDay, lastDay,
                caminhoPlanilha.getText().trim(), SisbrLauncher.normalizeExecutablePath(executavelSisbr.getText()),
                required(moduloSisbr),
                menuSisbr.getText().trim(),
                submenuSisbr.getText().trim(), required(rotinaSisbr), required(documentoPadrao),
                usarDataAtual.isSelected(), marcarEstornoTarifa.isSelected());
        try {
            store.save(configuration);
            JOptionPane.showMessageDialog(this,
                    "Configuracoes salvas em:\n" + store.getPath().toAbsolutePath(),
                    "Configuracoes", JOptionPane.INFORMATION_MESSAGE);
            dispose();
        } catch (IOException exception) {
            showError("Nao foi possivel salvar as configuracoes.", exception);
        }
    }

    private void selectSpreadsheet() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Selecionar base de dados");
        chooser.setFileFilter(new FileNameExtensionFilter("Planilhas Excel (*.xlsx)", "xlsx"));
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            caminhoPlanilha.setText(chooser.getSelectedFile().getAbsolutePath());
        }
    }

    private void selectSisbrExecutable() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Selecionar executavel do SISBR");
        chooser.setFileFilter(new FileNameExtensionFilter("Aplicativos Windows (*.exe)", "exe"));
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            executavelSisbr.setText(chooser.getSelectedFile().getAbsolutePath());
        }
    }

    private void updateCredentialState() {
        boolean automaticLogin = !isManualLogin();
        usuarioSisbr.setEnabled(automaticLogin);
        senhaSisbr.setEnabled(automaticLogin);
    }

    private boolean isManualLogin() {
        return "Sim".equals(loginManual.getSelectedItem());
    }

    private int[] parseSchedulingWindow() {
        String text = janelaAgendamento.getText().trim();
        if (text.isEmpty()) {
            return null;
        }

        String[] parts = text.split("\\D+");
        if (parts.length == 1) {
            return dayWindow(parts[0], parts[0]);
        }
        if (parts.length >= 2) {
            return dayWindow(parts[0], parts[1]);
        }
        return null;
    }

    private int[] dayWindow(String first, String last) {
        try {
            int firstDay = Integer.parseInt(first);
            int lastDay = Integer.parseInt(last);
            if (firstDay < 1 || firstDay > 31 || lastDay < 1 || lastDay > 31) {
                return null;
            }
            return new int[]{firstDay, lastDay};
        } catch (NumberFormatException ignored) {
            return null;
        }
    }

    private static String required(JTextField field) {
        return field.getText().trim();
    }

    private void showError(String message, Exception exception) {
        JOptionPane.showMessageDialog(this, message + "\n" + exception.getMessage(),
                "Erro", JOptionPane.ERROR_MESSAGE);
    }

    private static JPanel createSectionPanel(String title) {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(title),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)));
        return panel;
    }

    private static GridBagConstraints formConstraints() {
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.insets = new Insets(5, 5, 5, 5);
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.anchor = GridBagConstraints.WEST;
        constraints.weightx = 1;
        return constraints;
    }

    private static int field(JPanel panel, GridBagConstraints constraints, int row,
                             String label, Component component) {
        constraints.gridx = 0;
        constraints.gridy = row;
        constraints.weightx = 0;
        panel.add(new JLabel(label.isEmpty() ? "" : label + ":"), constraints);
        constraints.gridx = 1;
        constraints.weightx = 1;
        panel.add(component, constraints);
        return row + 1;
    }
}
