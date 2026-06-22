package com.nexum.estorno_tarifas.ui.screens;

import com.nexum.estorno_tarifas.configuration.EstornoTarifasConfiguration;
import com.nexum.estorno_tarifas.configuration.EstornoTarifasConfigurationStore;
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
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileNameExtensionFilter;

public class EstornoTarifasConfigurationScreen extends JDialog {
    private final EstornoTarifasConfigurationStore store = new EstornoTarifasConfigurationStore();
    private final JTextField cooperativa = new JTextField(20);
    private final JCheckBox loginManual = new JCheckBox("Realizar login manualmente");
    private final JTextField usuarioSisbr = new JTextField(20);
    private final JPasswordField senhaSisbr = new JPasswordField(20);
    private final JCheckBox schedulerHabilitado = new JCheckBox("Executar de forma agendada");
    private final JSpinner primeiroDiaUtil = new JSpinner(new SpinnerNumberModel(1, 1, 31, 1));
    private final JSpinner ultimoDiaUtil = new JSpinner(new SpinnerNumberModel(5, 1, 31, 1));
    private final JTextField caminhoPlanilha = new JTextField(30);
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
        add(new JScrollPane(createForm()), BorderLayout.CENTER);
        add(createActions(), BorderLayout.SOUTH);
        loadConfiguration();
        pack();
        setMinimumSize(new Dimension(650, 620));
        setSize(700, 680);
        setLocationRelativeTo(owner);
    }

    private JPanel createForm() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(12, 16, 12, 16));
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.insets = new Insets(5, 5, 5, 5);
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.anchor = GridBagConstraints.WEST;
        constraints.weightx = 1;

        int row = 0;
        row = section(panel, constraints, row, "Identificacao e acesso");
        row = field(panel, constraints, row, "Cooperativa", cooperativa);
        row = field(panel, constraints, row, "Login", loginManual);
        row = field(panel, constraints, row, "Usuario SISBR", usuarioSisbr);
        row = field(panel, constraints, row, "Senha SISBR", senhaSisbr);

        row = section(panel, constraints, row, "Execucao mensal");
        row = field(panel, constraints, row, "Agendamento", schedulerHabilitado);
        row = field(panel, constraints, row, "Primeiro dia util", primeiroDiaUtil);
        row = field(panel, constraints, row, "Ultimo dia util", ultimoDiaUtil);

        row = section(panel, constraints, row, "Base de dados");
        JPanel filePanel = new JPanel(new BorderLayout(6, 0));
        filePanel.add(caminhoPlanilha, BorderLayout.CENTER);
        JButton browse = new JButton("Selecionar...");
        browse.addActionListener(event -> selectSpreadsheet());
        filePanel.add(browse, BorderLayout.EAST);
        row = field(panel, constraints, row, "Planilha XLSX", filePanel);

        row = section(panel, constraints, row, "Navegacao no SISBR");
        row = field(panel, constraints, row, "Modulo", moduloSisbr);
        row = field(panel, constraints, row, "Menu", menuSisbr);
        row = field(panel, constraints, row, "Submenu", submenuSisbr);
        row = field(panel, constraints, row, "Rotina", rotinaSisbr);

        row = section(panel, constraints, row, "Lancamento");
        row = field(panel, constraints, row, "Documento padrao", documentoPadrao);
        row = field(panel, constraints, row, "Data", usarDataAtual);
        field(panel, constraints, row, "Tipo", marcarEstornoTarifa);
        return panel;
    }

    private JPanel createActions() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 8));
        JButton restore = new JButton("Restaurar PDD");
        restore.addActionListener(event -> populate(EstornoTarifasConfiguration.defaults()));
        JButton cancel = new JButton("Cancelar");
        cancel.addActionListener(event -> dispose());
        JButton save = new JButton("Salvar");
        save.addActionListener(event -> saveConfiguration());
        panel.add(restore);
        panel.add(cancel);
        panel.add(save);
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
        loginManual.setSelected(configuration.loginManual());
        usuarioSisbr.setText(configuration.usuarioSisbr());
        senhaSisbr.setText(configuration.senhaSisbr());
        schedulerHabilitado.setSelected(configuration.schedulerHabilitado());
        primeiroDiaUtil.setValue(configuration.primeiroDiaUtil());
        ultimoDiaUtil.setValue(configuration.ultimoDiaUtil());
        caminhoPlanilha.setText(configuration.caminhoPlanilha());
        moduloSisbr.setText(configuration.moduloSisbr());
        menuSisbr.setText(configuration.menuSisbr());
        submenuSisbr.setText(configuration.submenuSisbr());
        rotinaSisbr.setText(configuration.rotinaSisbr());
        documentoPadrao.setText(configuration.documentoPadrao());
        usarDataAtual.setSelected(configuration.usarDataAtual());
        marcarEstornoTarifa.setSelected(configuration.marcarEstornoTarifa());
        updateCredentialState();
        loginManual.addActionListener(event -> updateCredentialState());
    }

    private void saveConfiguration() {
        int firstDay = (Integer) primeiroDiaUtil.getValue();
        int lastDay = (Integer) ultimoDiaUtil.getValue();
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
                required(cooperativa), loginManual.isSelected(), usuarioSisbr.getText().trim(),
                new String(senhaSisbr.getPassword()), schedulerHabilitado.isSelected(), firstDay, lastDay,
                caminhoPlanilha.getText().trim(), required(moduloSisbr), menuSisbr.getText().trim(),
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

    private void updateCredentialState() {
        boolean automaticLogin = !loginManual.isSelected();
        usuarioSisbr.setEnabled(automaticLogin);
        senhaSisbr.setEnabled(automaticLogin);
    }

    private static String required(JTextField field) {
        return field.getText().trim();
    }

    private void showError(String message, Exception exception) {
        JOptionPane.showMessageDialog(this, message + "\n" + exception.getMessage(),
                "Erro", JOptionPane.ERROR_MESSAGE);
    }

    private static int section(JPanel panel, GridBagConstraints constraints, int row, String title) {
        JLabel label = new JLabel(title);
        label.setFont(label.getFont().deriveFont(java.awt.Font.BOLD));
        constraints.gridx = 0;
        constraints.gridy = row;
        constraints.gridwidth = 2;
        constraints.insets = new Insets(row == 0 ? 2 : 14, 5, 4, 5);
        panel.add(label, constraints);
        constraints.gridwidth = 1;
        constraints.insets = new Insets(5, 5, 5, 5);
        return row + 1;
    }

    private static int field(JPanel panel, GridBagConstraints constraints, int row,
                             String label, Component component) {
        constraints.gridx = 0;
        constraints.gridy = row;
        constraints.weightx = 0;
        panel.add(new JLabel(label + ":"), constraints);
        constraints.gridx = 1;
        constraints.weightx = 1;
        panel.add(component, constraints);
        return row + 1;
    }
}
