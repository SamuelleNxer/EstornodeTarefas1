package com.nexum.estorno_tarifas.ui.outputstream;

import java.io.IOException;
import java.io.OutputStream;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

public class TextAreaOutputStream extends OutputStream {
    private final JTextArea textArea;
    private final StringBuilder buffer = new StringBuilder();

    public TextAreaOutputStream(JTextArea textArea) {
        this.textArea = textArea;
    }

    @Override
    public synchronized void write(int value) throws IOException {
        char character = (char) value;
        buffer.append(character);
        if (character == '\n') {
            flush();
        }
    }

    @Override
    public synchronized void flush() {
        if (buffer.length() == 0) {
            return;
        }

        String text = buffer.toString();
        buffer.setLength(0);
        SwingUtilities.invokeLater(() -> {
            textArea.append(text);
            textArea.setCaretPosition(textArea.getDocument().getLength());
        });
    }
}
