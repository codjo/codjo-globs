package net.codjo.globs;
import org.crossbowlabs.globs.metamodel.GlobType;
import org.crossbowlabs.globs.metamodel.annotations.Encrypted;
import org.crossbowlabs.globs.metamodel.annotations.Key;
import org.crossbowlabs.globs.metamodel.fields.DateField;
import org.crossbowlabs.globs.metamodel.fields.IntegerField;
import org.crossbowlabs.globs.metamodel.fields.StringField;
import org.crossbowlabs.globs.metamodel.utils.GlobTypeLoader;
/**
 *
 */
@SuppressWarnings({"ALL"})
public class ObjectWithEncryptedFields {

    public static GlobType TYPE;

    @Key
    public static IntegerField ID;

    @Encrypted
    public static StringField NAME;

    @Encrypted
    public static DateField DATE;


    static {
        GlobTypeLoader.init(ObjectWithEncryptedFields.class);
    }
}
