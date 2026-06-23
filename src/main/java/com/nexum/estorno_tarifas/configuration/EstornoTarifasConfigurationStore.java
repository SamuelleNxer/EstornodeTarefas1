package com.nexum.estorno_tarifas.configuration;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Properties;

public class EstornoTarifasConfigurationStore {
    public static final Path DEFAULT_PATH = Path.of(System.getProperty("user.dir"), "configuration.properties");

    private final Path path;

    public EstornoTarifasConfigurationStore() {
        this(DEFAULT_PATH);
    }

    EstornoTarifasConfigurationStore(Path path) {
        this.path = path;
    }

    public EstornoTarifasConfiguration load() throws IOException {
        EstornoTarifasConfiguration defaults = EstornoTarifasConfiguration.defaults();
        if (Files.notExists(path)) {
            save(defaults);
            return defaults;
        }

        Properties properties = new Properties();
        try (BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            properties.load(reader);
        }

        return new EstornoTarifasConfiguration(
                value(properties, "cooperativa", defaults.cooperativa()),
                bool(properties, "login.manual", defaults.loginManual()),
                value(properties, "usuario.sisbr", defaults.usuarioSisbr()),
                WindowsCredentialProtector.unprotect(value(properties, "senha.sisbr", defaults.senhaSisbr())),
                value(properties, "usuario.nxcoop", defaults.usuarioNxCoop()),
                WindowsCredentialProtector.unprotect(value(properties, "senha.nxcoop", defaults.senhaNxCoop())),
                value(properties, "nxcoop.api-url", defaults.nxCoopApiUrl()),
                bool(properties, "scheduler.habilitado", defaults.schedulerHabilitado()),
                integer(properties, "execucao.primeiro-dia-util", defaults.primeiroDiaUtil()),
                integer(properties, "execucao.ultimo-dia-util", defaults.ultimoDiaUtil()),
                value(properties, "planilha.caminho", defaults.caminhoPlanilha()),
                value(properties, "sisbr.executavel", defaults.executavelSisbr()),
                value(properties, "sisbr.modulo", defaults.moduloSisbr()),
                value(properties, "sisbr.menu", defaults.menuSisbr()),
                value(properties, "sisbr.submenu", defaults.submenuSisbr()),
                value(properties, "sisbr.rotina", defaults.rotinaSisbr()),
                value(properties, "lancamento.documento", defaults.documentoPadrao()),
                bool(properties, "lancamento.usar-data-atual", defaults.usarDataAtual()),
                bool(properties, "lancamento.marcar-estorno-tarifa", defaults.marcarEstornoTarifa()));
    }

    public void save(EstornoTarifasConfiguration configuration) throws IOException {
        Path parent = path.toAbsolutePath().getParent();
        if (parent != null) {
            Files.createDirectories(parent);
        }

        Path temporary = Files.createTempFile(parent, "configuration-", ".tmp");
        try (BufferedWriter writer = Files.newBufferedWriter(temporary, StandardCharsets.UTF_8)) {
            writer.write("# Configuracoes do estorno de tarifas - requisitos operacionais do PDD");
            writer.newLine();
            write(writer, "cooperativa", configuration.cooperativa());
            write(writer, "login.manual", configuration.loginManual());
            write(writer, "usuario.sisbr", configuration.usuarioSisbr());
            write(writer, "senha.sisbr", WindowsCredentialProtector.protect(configuration.senhaSisbr()));
            write(writer, "usuario.nxcoop", configuration.usuarioNxCoop());
            write(writer, "senha.nxcoop", WindowsCredentialProtector.protect(configuration.senhaNxCoop()));
            write(writer, "nxcoop.api-url", configuration.nxCoopApiUrl());
            write(writer, "scheduler.habilitado", configuration.schedulerHabilitado());
            write(writer, "execucao.primeiro-dia-util", configuration.primeiroDiaUtil());
            write(writer, "execucao.ultimo-dia-util", configuration.ultimoDiaUtil());
            write(writer, "planilha.caminho", configuration.caminhoPlanilha());
            write(writer, "sisbr.executavel", configuration.executavelSisbr());
            write(writer, "sisbr.modulo", configuration.moduloSisbr());
            write(writer, "sisbr.menu", configuration.menuSisbr());
            write(writer, "sisbr.submenu", configuration.submenuSisbr());
            write(writer, "sisbr.rotina", configuration.rotinaSisbr());
            write(writer, "lancamento.documento", configuration.documentoPadrao());
            write(writer, "lancamento.usar-data-atual", configuration.usarDataAtual());
            write(writer, "lancamento.marcar-estorno-tarifa", configuration.marcarEstornoTarifa());
        }

        try {
            Files.move(temporary, path, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
        } catch (IOException atomicMoveUnavailable) {
            Files.move(temporary, path, StandardCopyOption.REPLACE_EXISTING);
        }
    }

    public Path getPath() {
        return path;
    }

    private static String value(Properties properties, String key, String defaultValue) {
        return properties.getProperty(key, defaultValue).trim();
    }

    private static boolean bool(Properties properties, String key, boolean defaultValue) {
        return Boolean.parseBoolean(properties.getProperty(key, Boolean.toString(defaultValue)).trim());
    }

    private static int integer(Properties properties, String key, int defaultValue) {
        try {
            return Integer.parseInt(properties.getProperty(key, Integer.toString(defaultValue)).trim());
        } catch (NumberFormatException ignored) {
            return defaultValue;
        }
    }

    private static void write(BufferedWriter writer, String key, Object value) throws IOException {
        String text = value == null ? "" : value.toString();
        writer.write(key + "=" + escape(text));
        writer.newLine();
    }

    private static String escape(String value) {
        return value.replace("\\", "\\\\").replace("\n", "\\n").replace("\r", "");
    }
}
