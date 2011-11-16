package net.codjo.globs.util;
import org.crossbowlabs.globs.metamodel.GlobType;
import org.crossbowlabs.globs.model.Key;
import org.crossbowlabs.globs.utils.Strings;
/**
 *
 */
public class MadUtil {
    private MadUtil() {
    }


    public static String buildHandlerId(String prefix, Key key) {
        return prefix + Strings.capitalize(key.getGlobType().getName());
    }


    public static String buildHandlerId(String prefix, GlobType globType) {
        return prefix + Strings.capitalize(globType.getName());
    }
}
