package com.nexum.estorno_tarifas;

import com.nexum.estorno_tarifas.directories.Directories;
import com.nexum.estorno_tarifas.directories.SisbrLauncher;
import com.nexum.estorno_tarifas.ui.screens.EstornoTarifasMainScreen;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.swing.SwingUtilities;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class EstornoTarifasApp {
    private static final AtomicBoolean RUNNING = new AtomicBoolean(false);

    private static volatile ConfigurableApplicationContext context;
    private static volatile ExecutorService executor;

    private EstornoTarifasApp() {
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new EstornoTarifasMainScreen().setVisible(true));
    }

    public static boolean isRunning() {
        return RUNNING.get();
    }

    public static synchronized void startApplication() {
        if (!RUNNING.compareAndSet(false, true)) {
            return;
        }

        executor = Executors.newSingleThreadExecutor(runnable -> {
            Thread thread = new Thread(runnable, "estorno-tarifas");
            thread.setDaemon(true);
            return thread;
        });

        executor.submit(() -> {
            try {
                Directories.reload();
                SisbrLauncher.open(Directories.SISBR_EXECUTABLE_DIRECTORY);

                SpringApplication application = new SpringApplication(EstornoTarifasApp.class);
                application.setWebApplicationType(WebApplicationType.NONE);
                context = application.run();
                System.out.println("Aplicacao de estorno de tarifas iniciada.");
            } catch (Exception exception) {
                RUNNING.set(false);
                System.err.println("Nao foi possivel iniciar a aplicacao: " + exception.getMessage());
                SwingUtilities.invokeLater(EstornoTarifasMainScreen::showStoppedState);
            }
        });
    }

    public static synchronized void stopApplication() {
        if (!RUNNING.compareAndSet(true, false)) {
            return;
        }

        ConfigurableApplicationContext currentContext = context;
        context = null;
        if (currentContext != null) {
            currentContext.close();
        }

        ExecutorService currentExecutor = executor;
        executor = null;
        if (currentExecutor != null) {
            currentExecutor.shutdownNow();
        }
        System.out.println("Aplicacao de estorno de tarifas parada.");
    }
}
