package com.nexum.estorno_tarifas.ui.screens;

import com.nexum.estorno_tarifas.EstornoTarifasApp;
import com.nexum.estorno_tarifas.ui.outputstream.TextAreaOutputStream;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class EstornoTarifasMainScreen extends JFrame {
    private static JButton toggleButton;

    public EstornoTarifasMainScreen() {
        configureWindow();

        JTextArea logTextArea = createLogArea();
        redirectConsoleTo(logTextArea);

        add(createButtonPanel(), BorderLayout.NORTH);
        add(createScrollPane(logTextArea), BorderLayout.CENTER);
    }

    public static void showStoppedState() {
        if (toggleButton == null) {
            return;
        }
        toggleButton.setText("Iniciar Aplicacao");
        toggleButton.setBackground(new Color(46, 160, 67));
        toggleButton.setForeground(Color.WHITE);
        toggleButton.setEnabled(true);
    }

    private void configureWindow() {
        setTitle("Nxbot - Estorno de Tarifas");
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setLayout(new BorderLayout());
        setSize(900, 720);
        setMinimumSize(new java.awt.Dimension(640, 480));
        setLocationRelativeTo(null);
        getContentPane().setBackground(Color.BLACK);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent event) {
                EstornoTarifasApp.stopApplication();
                dispose();
            }
        });
    }

    private JTextArea createLogArea() {
        JTextArea textArea = new JTextArea(30, 80);
        textArea.setEditable(false);
        textArea.setBackground(Color.BLACK);
        textArea.setForeground(Color.WHITE);
        textArea.setCaretColor(Color.WHITE);
        textArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        textArea.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        return textArea;
    }

    private JScrollPane createScrollPane(JTextArea logTextArea) {
        JScrollPane scrollPane = new JScrollPane(logTextArea);
        scrollPane.getViewport().setBackground(Color.BLACK);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        return scrollPane;
    }

    private void redirectConsoleTo(JTextArea logTextArea) {
        PrintStream stream = new PrintStream(
                new TextAreaOutputStream(logTextArea), true, StandardCharsets.UTF_8);
        System.setOut(stream);
        System.setErr(stream);
    }

    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));

        toggleButton = new JButton("Iniciar Aplicacao");
        toggleButton.setBackground(new Color(46, 160, 67));
        toggleButton.setForeground(Color.WHITE);
        toggleButton.addActionListener(event -> toggleApplicationState());

        JButton settingsButton = new JButton("Configuracoes");
        settingsButton.addActionListener(event -> EstornoTarifasConfigurationScreen.open(this));

        panel.add(toggleButton);
        panel.add(settingsButton);
        return panel;
    }

    private void toggleApplicationState() {
        toggleButton.setEnabled(false);
        if (EstornoTarifasApp.isRunning()) {
            EstornoTarifasApp.stopApplication();
            showStoppedState();
            return;
        }

        toggleButton.setText("Parar Aplicacao");
        toggleButton.setBackground(new Color(207, 34, 46));
        toggleButton.setForeground(Color.WHITE);
        toggleButton.setEnabled(true);
        EstornoTarifasApp.startApplication();
    }

}
