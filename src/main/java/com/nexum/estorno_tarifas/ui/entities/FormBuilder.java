package com.nexum.estorno_tarifas.ui.entities;

import com.nexum.estorno_tarifas.directories.SisbrLauncher;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Window;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Properties;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

public class FormBuilder extends JDialog {
    public static final String PATH_SISBR2 = "sisbr.executavel";

    private final Path propertiesPath;
    private final List<Field> fields;

    public FormBuilder(Window owner, String title, Path propertiesPath, List<Field> fields) {
        super(owner, title, ModalityType.APPLICATION_MODAL);
        this.propertiesPath = Objects.requireNonNull(propertiesPath, "propertiesPath");
        this.fields = new ArrayList<>(Objects.requireNonNull(fields, "fields"));
        initUI();
        loadProperties();
    }

    private void initUI() {
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));
        add(new JScrollPane(createFormPanel()), BorderLayout.CENTER);
        add(createActionsPanel(), BorderLayout.SOUTH);
        pack();
        setLocationRelativeTo(getOwner());
    }

    private JPanel createFormPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(12, 16, 12, 16));

        GridBagConstraints constraints = new GridBagConstraints();
        constraints.insets = new Insets(5, 5, 5, 5);
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.anchor = GridBagConstraints.WEST;

        int row = 0;
        for (Field field : fields) {
            constraints.gridx = 0;
            constraints.gridy = row;
            constraints.weightx = 0;
            panel.add(new JLabel(field.label() + ":"), constraints);

            constraints.gridx = 1;
            constraints.weightx = 1;
            panel.add(field.createComponent(), constraints);
            row++;
        }
        return panel;
    }

    private JPanel createActionsPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 8));
        panel.add(createSaveButton());

        JButton close = new JButton("Fechar");
        close.addActionListener(event -> dispose());
        panel.add(close);
        return panel;
    }

    private JButton createSaveButton() {
        JButton save = new JButton("Salvar");
        save.addActionListener(event -> saveProperties());
        getRootPane().setDefaultButton(save);
        return save;
    }

    private void saveProperties() {
        Properties props = readProperties();
        for (Field field : fields) {
            String key = storageKey(field);
            String value = fixMojibakeIfNeeded(field.getValue());
            if (PATH_SISBR2.equals(key)) {
                value = SisbrLauncher.normalizeExecutablePath(value);
            }
            props.setProperty(key, value);
        }

        try {
            Path parent = propertiesPath.toAbsolutePath().getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }
            try (BufferedWriter writer = Files.newBufferedWriter(propertiesPath, StandardCharsets.UTF_8)) {
                props.store(writer, "Configuracoes");
            }
            JOptionPane.showMessageDialog(this, "Configuracoes salvas com sucesso.",
                    "Configuracoes", JOptionPane.INFORMATION_MESSAGE);
            dispose();
        } catch (IOException exception) {
            showError("Nao foi possivel salvar as configuracoes.", exception);
        }
    }

    private void loadProperties() {
        Properties props = readProperties();
        for (Field field : fields) {
            String key = storageKey(field);
            String value = props.getProperty(key, "");
            if (PATH_SISBR2.equals(key)) {
                value = SisbrLauncher.normalizeExecutablePath(value);
            }
            field.setValue(value != null ? fixMojibakeIfNeeded(value) : "");
        }
    }

    private Properties readProperties() {
        Properties props = new Properties();
        if (Files.notExists(propertiesPath)) {
            return props;
        }

        try (BufferedReader reader = Files.newBufferedReader(propertiesPath, StandardCharsets.UTF_8)) {
            props.load(reader);
        } catch (IOException exception) {
            showError("Nao foi possivel carregar as configuracoes.", exception);
        }
        return props;
    }

    private String storageKey(Field field) {
        if (field.storageKey() != null && !field.storageKey().isBlank()) {
            return field.storageKey();
        }
        return normalizeKey(field.label());
    }

    private String normalizeKey(String value) {
        String normalized = Normalizer.normalize(value, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .toLowerCase()
                .replaceAll("[^a-z0-9]+", ".")
                .replaceAll("^\\.|\\.$", "");
        return normalized;
    }

    private String fixMojibakeIfNeeded(String value) {
        if (value == null || value.isBlank()) {
            return "";
        }
        if (!value.contains("Ã") && !value.contains("�")) {
            return value;
        }
        byte[] bytes = value.getBytes(StandardCharsets.ISO_8859_1);
        return new String(bytes, StandardCharsets.UTF_8);
    }

    private void showError(String message, Exception exception) {
        JOptionPane.showMessageDialog(this, message + "\n" + exception.getMessage(),
                "Erro", JOptionPane.ERROR_MESSAGE);
    }

    public record Field(String label, String storageKey, Component component) {
        public static Field text(String label, String storageKey) {
            return new Field(label, storageKey, new JTextField(24));
        }

        public static Field password(String label, String storageKey) {
            return new Field(label, storageKey, new JPasswordField(24));
        }

        public static Field checkbox(String label, String storageKey) {
            return new Field(label, storageKey, new JCheckBox());
        }

        public static Field combo(String label, String storageKey, String... options) {
            return new Field(label, storageKey, new JComboBox<>(options));
        }

        Component createComponent() {
            return component;
        }

        String getValue() {
            if (component instanceof JPasswordField passwordField) {
                return new String(passwordField.getPassword());
            }
            if (component instanceof JTextField textField) {
                return textField.getText();
            }
            if (component instanceof JCheckBox checkbox) {
                return Boolean.toString(checkbox.isSelected());
            }
            if (component instanceof JComboBox<?> comboBox) {
                Object selected = comboBox.getSelectedItem();
                return selected == null ? "" : selected.toString();
            }
            return "";
        }

        void setValue(String value) {
            if (component instanceof JPasswordField passwordField) {
                passwordField.setText(value);
            } else if (component instanceof JTextField textField) {
                textField.setText(value);
            } else if (component instanceof JCheckBox checkbox) {
                checkbox.setSelected(Boolean.parseBoolean(value));
            } else if (component instanceof JComboBox<?> comboBox) {
                comboBox.setSelectedItem(value);
            }
        }
    }
}
