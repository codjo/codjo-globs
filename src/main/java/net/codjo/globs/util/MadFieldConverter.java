package net.codjo.globs.util;
import net.codjo.crypto.common.StringEncrypter;
import net.codjo.crypto.common.StringEncrypterException;
import java.text.SimpleDateFormat;
import org.crossbowlabs.globs.metamodel.Field;
import org.crossbowlabs.globs.metamodel.annotations.Encrypted;
import org.crossbowlabs.globs.xml.FieldConverter;
/**
 *
 */
public class MadFieldConverter extends FieldConverter {

    private StringEncrypter encrypter;
    private static final String UNABLE_TO_DECRYPT = "<unable to decrypt>";


    public MadFieldConverter(String encryptionKey) {
        encrypter = new StringEncrypter(encryptionKey);
        setDateFormat(new SimpleDateFormat("yyyy-MM-dd"));
    }


    @Override
    public String toString(Field field, Object value) {
        String valueToTransfer = value == null ? "null" : super.toString(field, value);
        if (field.hasAnnotation(Encrypted.class)) {
            valueToTransfer = encrypter.encrypt(valueToTransfer);
        }
        return valueToTransfer;
    }


    @Override
    public Object toObject(Field field, String stringValue) {
        if (stringValue == null) {
            return null;
        }
        if ("null".equals(stringValue)) {
            return null;
        }

        String valueToTransfer = stringValue;
        if (field.hasAnnotation(Encrypted.class)) {
            try {
                valueToTransfer = encrypter.decrypt(stringValue);
            }
            catch (StringEncrypterException exc) {
                valueToTransfer = UNABLE_TO_DECRYPT;
            }
        }

        if ("null".equals(valueToTransfer)) {
            return null;
        }

        return super.toObject(field, valueToTransfer);
    }
}
