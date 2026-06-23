package com.nexum.estorno_tarifas.directories;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

public final class SisbrLauncher {
    private SisbrLauncher() {
    }

    public static Process open(String executableDirectory) throws IOException {
        Path executable = resolveExecutable(executableDirectory);

        System.out.println("Abrindo SISBR: " + executable);
        return new ProcessBuilder(executable.toString())
                .directory(executable.getParent().toFile())
                .start();
    }

    static Path resolveExecutable(String executableDirectory) throws IOException {
        String configuredPath = stripQuotes(executableDirectory);
        if (configuredPath.isBlank()) {
            throw new IOException(
                    "Caminho do SISBR nao configurado. Abra Configuracoes e selecione o executavel.");
        }

        String lowerPath = configuredPath.toLowerCase();
        if (lowerPath.startsWith("http://") || lowerPath.startsWith("https://")) {
            throw new IOException("Informe o caminho do arquivo do SISBR no computador, nao uma URL da internet.");
        }

        Path executable = toPath(configuredPath).toAbsolutePath().normalize();
        if (Files.isDirectory(executable)) {
            executable = findExecutableIn(executable);
        }
        if (!Files.isRegularFile(executable)) {
            throw new IOException("Executavel do SISBR nao encontrado: " + executable);
        }
        if (!executable.getFileName().toString().toLowerCase().endsWith(".exe")) {
            throw new IOException("O caminho configurado para o SISBR deve apontar para um arquivo .exe.");
        }
        return executable;
    }

    public static String normalizeExecutablePath(String executableDirectory) {
        String text = stripQuotes(executableDirectory);
        if (text.toLowerCase().startsWith("file:")) {
            return text;
        }
        text = text.replace('/', '\\');
        text = text.replaceAll("\\\\{2,}", "\\\\");
        if (text.matches("^[A-Za-z]:[^\\\\].*")) {
            text = text.substring(0, 2) + "\\" + text.substring(2);
        }
        return text;
    }

    private static String stripQuotes(String executableDirectory) {
        if (executableDirectory == null) {
            return "";
        }
        String text = executableDirectory.trim();
        while ((text.startsWith("\"") && text.endsWith("\""))
                || (text.startsWith("'") && text.endsWith("'"))) {
            text = text.substring(1, text.length() - 1).trim();
        }
        return text;
    }

    private static Path toPath(String configuredPath) throws IOException {
        if (!configuredPath.toLowerCase().startsWith("file:")) {
            try {
                return Path.of(normalizeExecutablePath(configuredPath));
            } catch (InvalidPathException exception) {
                throw new IOException("Caminho invalido para o SISBR: " + configuredPath, exception);
            }
        }
        try {
            return Path.of(new URI(configuredPath));
        } catch (IllegalArgumentException | URISyntaxException exception) {
            throw new IOException("URL de arquivo invalida para o SISBR: " + configuredPath, exception);
        }
    }

    private static Path findExecutableIn(Path directory) throws IOException {
        List<Path> executables;
        try (Stream<Path> files = Files.list(directory)) {
            executables = files
                    .filter(Files::isRegularFile)
                    .filter(path -> path.getFileName().toString().toLowerCase().endsWith(".exe"))
                    .sorted(Comparator.comparing(path -> path.getFileName().toString().toLowerCase()))
                    .toList();
        }

        List<Path> sisbrExecutables = executables.stream()
                .filter(path -> path.getFileName().toString().toLowerCase().contains("sisbr"))
                .toList();
        if (!sisbrExecutables.isEmpty()) {
            return sisbrExecutables.get(0);
        }
        if (executables.size() == 1) {
            return executables.get(0);
        }
        if (executables.isEmpty()) {
            throw new IOException("A pasta configurada nao contem um executavel .exe: " + directory);
        }
        throw new IOException("A pasta configurada contem varios executaveis. Selecione o arquivo .exe do SISBR.");
    }
}
