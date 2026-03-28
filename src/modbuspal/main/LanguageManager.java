package modbuspal.main;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Centralized language manager for localized UI strings.
 *
 * Author: André Henrique (LinkedIn/X: @mrhenrike)
 */
public final class LanguageManager {

    private static final Logger LOGGER = Logger.getLogger(LanguageManager.class.getName());
    private static final String BUNDLE_BASE = "modbuspal.i18n.messages";

    public static final Locale LOCALE_PT_BR = Locale.forLanguageTag("pt-BR");
    public static final Locale LOCALE_EN_US = Locale.forLanguageTag("en-US");
    public static final Locale LOCALE_ES_ES = Locale.forLanguageTag("es-ES");

    private static Locale currentLocale = LOCALE_PT_BR;
    private static ResourceBundle bundle = ResourceBundle.getBundle(BUNDLE_BASE, currentLocale);

    private LanguageManager() {
    }

    public static void initializeFromPreferences() {
        String configuredTag = WorkspacePreferences.getLanguageTag();
        if (configuredTag == null || configuredTag.isEmpty()) {
            setLocale(LOCALE_PT_BR, false);
            return;
        }
        setLocale(Locale.forLanguageTag(configuredTag), false);
    }

    public static Locale getLocale() {
        return currentLocale;
    }

    public static void setLocale(Locale locale) {
        setLocale(locale, true);
    }

    private static void setLocale(Locale locale, boolean persist) {
        Locale normalized = normalize(locale);
        currentLocale = normalized;
        Locale.setDefault(normalized);
        bundle = ResourceBundle.getBundle(BUNDLE_BASE, normalized);
        if (persist) {
            WorkspacePreferences.setLanguageTag(normalized.toLanguageTag());
        }
        LOGGER.log(Level.INFO, "Language set to {0}", normalized.toLanguageTag());
    }

    public static Locale normalize(Locale locale) {
        if (locale == null) {
            return LOCALE_PT_BR;
        }
        String tag = locale.toLanguageTag().toLowerCase(Locale.ROOT);
        if (tag.startsWith("pt")) {
            return LOCALE_PT_BR;
        }
        if (tag.startsWith("es")) {
            return LOCALE_ES_ES;
        }
        return LOCALE_EN_US;
    }

    public static String tr(String key) {
        try {
            return bundle.getString(key);
        } catch (Exception ex) {
            LOGGER.log(Level.WARNING, "Missing i18n key: {0}", key);
            return "!!" + key + "!!";
        }
    }

    public static String tr(String key, Object... args) {
        String pattern = tr(key);
        return MessageFormat.format(pattern, args);
    }
}
