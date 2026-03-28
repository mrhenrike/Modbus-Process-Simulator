/*
 * Persistência de workspace para o Modbus Slave Simulator (fork ModbusPal).
 * Author: André Henrique (LinkedIn/X: @mrhenrike)
 */

package modbuspal.main;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

/**
 * Armazena o caminho absoluto do último ficheiro de projeto (.xmpp) aberto ou gravado.
 *
 * <p>Utiliza o mesmo nó de preferências que {@link modbuspal.toolkit.XFileChooser}
 * ({@value #PREF_NODE}) para manter coerência com o restante da aplicação.</p>
 */
public final class WorkspacePreferences {

    private static final Logger LOGGER = Logger.getLogger(WorkspacePreferences.class.getName());

    /** Nó de preferências alinhado a {@code XFileChooser.MODBUSPAL_REG_PATH}. */
    public static final String PREF_NODE = "modbuspal";

    private static final String KEY_LAST_PROJECT = "last_project_path";
    private static final String KEY_LANGUAGE_TAG = "language_tag";

    private WorkspacePreferences() {
    }

    /**
     * Obtém o caminho absoluto do último projeto guardado nas preferências.
     *
     * @return caminho absoluto ou {@code null} se não existir valor válido
     */
    public static String getLastProjectPath() {
        Preferences prefs = Preferences.userRoot().node(PREF_NODE);
        String path = prefs.get(KEY_LAST_PROJECT, "");
        if (path == null) {
            return null;
        }
        String trimmed = path.trim();
        if (trimmed.isEmpty()) {
            return null;
        }
        return trimmed;
    }

    /**
     * Grava o caminho absoluto do ficheiro de projeto para reabrir na próxima sessão.
     *
     * @param projectFile ficheiro de projeto; se {@code null}, não faz nada
     */
    public static void setLastProjectPath(File projectFile) {
        if (projectFile == null) {
            return;
        }
        try {
            Preferences prefs = Preferences.userRoot().node(PREF_NODE);
            prefs.put(KEY_LAST_PROJECT, projectFile.getAbsolutePath());
            prefs.flush();
        } catch (BackingStoreException ex) {
            LOGGER.log(Level.WARNING, "Falha ao gravar preferência last_project_path", ex);
        }
    }

    /**
     * Remove o último projeto memorizado (ex.: após "Clear project").
     */
    public static void clearLastProjectPath() {
        try {
            Preferences prefs = Preferences.userRoot().node(PREF_NODE);
            prefs.remove(KEY_LAST_PROJECT);
            prefs.flush();
        } catch (BackingStoreException ex) {
            LOGGER.log(Level.WARNING, "Falha ao limpar preferência last_project_path", ex);
        }
    }

    public static String getLanguageTag() {
        Preferences prefs = Preferences.userRoot().node(PREF_NODE);
        String languageTag = prefs.get(KEY_LANGUAGE_TAG, "");
        if (languageTag == null) {
            return "";
        }
        return languageTag.trim();
    }

    public static void setLanguageTag(String languageTag) {
        if (languageTag == null || languageTag.trim().isEmpty()) {
            return;
        }
        try {
            Preferences prefs = Preferences.userRoot().node(PREF_NODE);
            prefs.put(KEY_LANGUAGE_TAG, languageTag.trim());
            prefs.flush();
        } catch (BackingStoreException ex) {
            LOGGER.log(Level.WARNING, "Falha ao gravar preferência language_tag", ex);
        }
    }
}
