package modbuspal.main;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import modbuspal.link.ModbusSerialLink;

/**
 * Checks runtime/project dependencies and can repair project-local jars.
 *
 * Author: André Henrique (LinkedIn/X: @mrhenrike)
 */
public class DependencyCheckDialog extends JDialog {

    private static final long serialVersionUID = 1L;

    private static final String JYTHON_FILE = "jython-standalone.jar";
    private static final String JYTHON_URL =
            "https://repo1.maven.org/maven2/org/python/jython-standalone/2.7.0/jython-standalone-2.7.0.jar";

    private static final String JFREECHART_FILE = "jfreechart.jar";
    private static final String JFREECHART_URL =
            "https://repo1.maven.org/maven2/org/jfree/jfreechart/1.0.15/jfreechart-1.0.15.jar";

    private static final String JCOMMON_FILE = "jcommon.jar";
    private static final String JCOMMON_URL =
            "https://repo1.maven.org/maven2/org/jfree/jcommon/1.0.23/jcommon-1.0.23.jar";

    private static final String RXTX_FILE = "rxtx.jar";
    private static final String RXTX_URL =
            "https://repo1.maven.org/maven2/dev/prokop/rxtx/rxtx/2.2.2/rxtx-2.2.2.jar";

    private final JTextArea reportArea = new JTextArea();

    public DependencyCheckDialog(JDialog owner) {
        super(owner, LanguageManager.tr("deps.title"), true);
        initializeUi();
    }

    public DependencyCheckDialog(java.awt.Frame owner) {
        super(owner, LanguageManager.tr("deps.title"), true);
        initializeUi();
    }

    private void initializeUi() {
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(780, 480));

        reportArea.setEditable(false);
        reportArea.setLineWrap(false);
        add(new JScrollPane(reportArea), BorderLayout.CENTER);

        JButton recheckButton = new JButton(LanguageManager.tr("deps.recheck"));
        JButton fixButton = new JButton(LanguageManager.tr("deps.fix"));
        JButton closeButton = new JButton(LanguageManager.tr("deps.close"));

        recheckButton.addActionListener(e -> refreshReport());
        fixButton.addActionListener(e -> {
            fixProjectDependencies();
            refreshReport();
        });
        closeButton.addActionListener(e -> setVisible(false));

        java.awt.Panel bottomPanel = new java.awt.Panel(new FlowLayout(FlowLayout.RIGHT));
        bottomPanel.add(recheckButton);
        bottomPanel.add(fixButton);
        bottomPanel.add(closeButton);
        add(bottomPanel, BorderLayout.SOUTH);

        pack();
        setLocationRelativeTo(getOwner());
        refreshReport();
    }

    public final void refreshReport() {
        StringBuilder sb = new StringBuilder();
        sb.append(LanguageManager.tr("deps.report.header")).append("\n\n");
        appendOsSection(sb);
        appendClasspathSection(sb);
        appendProjectLibSection(sb);
        appendSerialSection(sb);
        reportArea.setText(sb.toString());
        reportArea.setCaretPosition(0);
    }

    private void appendOsSection(StringBuilder sb) {
        sb.append("[").append(LanguageManager.tr("deps.section.os")).append("]\n");
        sb.append("os.name=").append(System.getProperty("os.name")).append("\n");
        sb.append("os.arch=").append(System.getProperty("os.arch")).append("\n");
        sb.append("java.version=").append(System.getProperty("java.version")).append("\n");
        sb.append("java.home=").append(System.getProperty("java.home")).append("\n\n");
    }

    private void appendClasspathSection(StringBuilder sb) {
        sb.append("[").append(LanguageManager.tr("deps.section.classpath")).append("]\n");
        appendClassStatus(sb, "org.python.util.PythonInterpreter", "Jython");
        appendClassStatus(sb, "org.jfree.chart.JFreeChart", "JFreeChart");
        appendClassStatus(sb, "gnu.io.CommPortIdentifier", "RXTX");
        sb.append("\n");
    }

    private void appendClassStatus(StringBuilder sb, String className, String label) {
        boolean available = isClassAvailable(className);
        sb.append(label).append(": ").append(available ? "OK" : "MISSING");
        sb.append(" (").append(className).append(")\n");
    }

    private void appendProjectLibSection(StringBuilder sb) {
        sb.append("[").append(LanguageManager.tr("deps.section.project_libs")).append("]\n");
        File libDir = new File("lib");
        sb.append("lib dir: ").append(libDir.getAbsolutePath()).append("\n");
        appendJarStatus(sb, libDir, JYTHON_FILE);
        appendJarStatus(sb, libDir, JFREECHART_FILE);
        appendJarStatus(sb, libDir, JCOMMON_FILE);
        appendJarStatus(sb, libDir, RXTX_FILE);
        sb.append("\n");
    }

    private void appendJarStatus(StringBuilder sb, File libDir, String jarName) {
        File jar = new File(libDir, jarName);
        sb.append(jarName).append(": ");
        if (jar.isFile()) {
            sb.append("OK (").append(jar.length()).append(" bytes)");
        } else {
            sb.append("MISSING");
        }
        sb.append("\n");
    }

    private void appendSerialSection(StringBuilder sb) {
        sb.append("[").append(LanguageManager.tr("deps.section.serial")).append("]\n");
        sb.append("java.ext.dirs present: ")
                .append(ModbusSerialLink.isLegacyExtDirsAvailable() ? "YES" : "NO")
                .append("\n");
        if (!ModbusSerialLink.isLegacyExtDirsAvailable()) {
            sb.append(LanguageManager.tr("deps.serial.note")).append("\n");
        }
        sb.append("\n");
    }

    private void fixProjectDependencies() {
        File libDir = new File("lib");
        if (!libDir.exists()) {
            libDir.mkdirs();
        }
        StringBuilder actions = new StringBuilder();
        actions.append("[").append(LanguageManager.tr("deps.section.actions")).append("]\n");
        ensureJar(libDir, JYTHON_FILE, JYTHON_URL, actions);
        ensureJar(libDir, JFREECHART_FILE, JFREECHART_URL, actions);
        ensureJar(libDir, JCOMMON_FILE, JCOMMON_URL, actions);
        ensureJar(libDir, RXTX_FILE, RXTX_URL, actions);
        actions.append("\n");
        String merged = reportArea.getText() + "\n" + actions.toString();
        reportArea.setText(new String(merged.getBytes(StandardCharsets.UTF_8), StandardCharsets.UTF_8));
    }

    private void ensureJar(File libDir, String jarFileName, String sourceUrl, StringBuilder actions) {
        File target = new File(libDir, jarFileName);
        if (target.isFile() && target.length() > 0) {
            actions.append("SKIP ").append(jarFileName).append(" (already present)\n");
            return;
        }
        actions.append("DOWNLOAD ").append(jarFileName).append(" ... ");
        try (InputStream in = new URL(sourceUrl).openStream();
             FileOutputStream out = new FileOutputStream(target)) {
            byte[] buffer = new byte[8192];
            int read;
            while ((read = in.read(buffer)) >= 0) {
                out.write(buffer, 0, read);
            }
            actions.append("OK\n");
        } catch (Exception ex) {
            actions.append("FAIL (").append(ex.getClass().getSimpleName()).append(": ")
                    .append(ex.getLocalizedMessage()).append(")\n");
        }
    }

    private boolean isClassAvailable(String className) {
        try {
            ClassLoader cl = ClassLoader.getSystemClassLoader();
            Class.forName(className, false, cl);
            return true;
        } catch (Throwable ex) {
            return false;
        }
    }
}
