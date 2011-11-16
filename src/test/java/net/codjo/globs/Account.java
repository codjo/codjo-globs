package net.codjo.globs;
import org.crossbowlabs.globs.metamodel.GlobType;
import org.crossbowlabs.globs.metamodel.annotations.Key;
import org.crossbowlabs.globs.metamodel.fields.DateField;
import org.crossbowlabs.globs.metamodel.fields.IntegerField;
import org.crossbowlabs.globs.metamodel.fields.StringField;
import org.crossbowlabs.globs.metamodel.utils.GlobTypeLoader;
/**
 *
 */
@SuppressWarnings({"ALL"})
public class Account {
    public static GlobType TYPE;

    @Key
    public static IntegerField ID;

    public static StringField BANK_NAME;
    public static StringField BANK_CODE;
    public static IntegerField NUMBER;
    public static DateField DATE;


    static {
        GlobTypeLoader.init(Account.class);
    }
}
