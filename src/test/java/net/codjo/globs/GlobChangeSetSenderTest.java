package net.codjo.globs;
import net.codjo.mad.client.plugin.MadConnectionOperations;
import net.codjo.mad.client.request.Request;
import net.codjo.mad.client.request.RequestIdManager;
import net.codjo.mad.client.request.ResultManager;
import java.io.StringReader;
import org.crossbowlabs.globs.metamodel.GlobModel;
import org.crossbowlabs.globs.metamodel.GlobType;
import org.crossbowlabs.globs.metamodel.Link;
import org.crossbowlabs.globs.metamodel.annotations.Key;
import org.crossbowlabs.globs.metamodel.annotations.Target;
import org.crossbowlabs.globs.metamodel.fields.DateField;
import org.crossbowlabs.globs.metamodel.fields.IntegerField;
import org.crossbowlabs.globs.metamodel.fields.StringField;
import org.crossbowlabs.globs.metamodel.utils.GlobModelBuilder;
import org.crossbowlabs.globs.metamodel.utils.GlobTypeLoader;
import org.crossbowlabs.globs.model.ChangeSet;
import org.crossbowlabs.globs.model.KeyBuilder;
import org.crossbowlabs.globs.model.delta.MutableChangeSet;
import org.crossbowlabs.globs.xml.XmlChangeSetParser;
import org.junit.Before;
import org.junit.Test;
import static org.mockito.Mockito.anyObject;
import static org.mockito.Mockito.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.stub;
import static org.mockito.Mockito.verify;

public class GlobChangeSetSenderTest {
    public static final String BASIC_KEY = "bouh";
    private MadConnectionOperations operations;

    @SuppressWarnings({"ALL"})
    public static class ObjectWithCompositeKey {
        public static GlobType TYPE;

        @Key
        public static IntegerField ID1;

        @Key
        public static IntegerField ID2;

        public static StringField NAME;
        public static DateField DATE;


        static {
            GlobTypeLoader.init(ObjectWithCompositeKey.class);
        }
    }

    @SuppressWarnings({"ALL"})
    public static class LinkedToObjectWithCompositeKey {
        public static GlobType TYPE;

        @Key
        public static IntegerField ID;

        @Target(ObjectWithCompositeKey.class)
        public static IntegerField LINK1;

        @Target(ObjectWithCompositeKey.class)
        public static IntegerField LINK2;

        public static Link LINK;


        static {
            GlobTypeLoader.init(LinkedToObjectWithCompositeKey.class)
                  .defineLink(LinkedToObjectWithCompositeKey.LINK)
                  .add(LinkedToObjectWithCompositeKey.LINK1, ObjectWithCompositeKey.ID1)
                  .add(LinkedToObjectWithCompositeKey.LINK2, ObjectWithCompositeKey.ID2);
        }
    }


    @Test
    public void test_standardCase() throws Exception {
        ChangeSet changeSet = XmlChangeSetParser.parse(Model.MODEL, new StringReader(
              "<changes>"
              + "  <create type='linkedToObjectWithCompositeKey' id='0' link1='1' link2='2'/>"
              + "  <update type='linkedToObjectWithCompositeKey' id='2' link1='2' link2='3'/>"
              + "  <delete type='objectWithCompositeKey' id1='3' id2='4'/>"
              + "  <delete type='linkedToObjectWithCompositeKey' id='1' link1='3' link2='4'/>"
              + "  <update type='objectWithCompositeKey' id1='2' id2='3' name='newName' date='2007/08/02'/>"
              + "  <create type='objectWithCompositeKey' id1='1' id2='2'/>"
              + "</changes>"));

        new GlobChangeSetSender(changeSet, Model.MODEL, BASIC_KEY, operations).run();

        verify(operations).sendRequests(argThat(new RequestMatcher(
              new String[]{"newObjectWithCompositeKey",
                           "newLinkedToObjectWithCompositeKey",
                           "updateObjectWithCompositeKey",
                           "updateLinkedToObjectWithCompositeKey",
                           "deleteLinkedToObjectWithCompositeKey",
                           "deleteObjectWithCompositeKey",
              }, new String[]{
              "<insert request_id='1'>"
              + "    <id>newObjectWithCompositeKey</id>"
              + "    <row>"
              + "      <field name='id1'>1</field>"
              + "      <field name='id2'>2</field>"
              + "    </row>"
              + "  </insert>",
              "  <insert request_id='2'>"
              + "    <id>newLinkedToObjectWithCompositeKey</id>"
              + "    <row>"
              + "      <field name='id'>0</field>"
              + "      <field name='link2'>2</field>"
              + "      <field name='link1'>1</field>"
              + "    </row>"
              + "  </insert>",
              "  <update request_id='3'>"
              + "    <id>updateObjectWithCompositeKey</id>"
              + "    <primarykey>"
              + "      <field name='id1'>2</field>"
              + "      <field name='id2'>3</field>"
              + "    </primarykey>"
              + "    <row>"
              + "      <field name='name'>newName</field>"
              + "      <field name='date'>2007-08-02</field>"
              + "    </row>"
              + "  </update>",
              "  <update request_id='4'>"
              + "    <id>updateLinkedToObjectWithCompositeKey</id>"
              + "    <primarykey>"
              + "      <field name='id'>2</field>"
              + "    </primarykey>"
              + "    <row>"
              + "      <field name='link2'>3</field>"
              + "      <field name='link1'>2</field>"
              + "    </row>"
              + "  </update>",
              "  <delete request_id='5'>"
              + "    <id>deleteLinkedToObjectWithCompositeKey</id>"
              + "    <primarykey>"
              + "      <field name='id'>1</field>"
              + "    </primarykey>"
              + "  </delete>",
              "  <delete request_id='6'>"
              + "    <id>deleteObjectWithCompositeKey</id>"
              + "    <primarykey>"
              + "      <field name='id1'>3</field>"
              + "      <field name='id2'>4</field>"
              + "    </primarykey>"
              + "  </delete>"
        })));
    }


    @Test
    public void test_encryptedFields() throws Exception {
        MutableChangeSet changeSet = (MutableChangeSet)XmlChangeSetParser.parse(Model.MODEL, new StringReader(
              "<changes>"
              + "  <create type='objectWithEncryptedFields' id='0' date='2007/07/07'/>"
              + "  <delete type='objectWithEncryptedFields' id='2'/>"
              + "</changes>"));
        changeSet.processUpdate(KeyBuilder.newKey(ObjectWithEncryptedFields.TYPE, 1),
                                ObjectWithEncryptedFields.NAME, "obj1");
        changeSet.processUpdate(KeyBuilder.newKey(ObjectWithEncryptedFields.TYPE, 1),
                                ObjectWithEncryptedFields.DATE, null);

        new GlobChangeSetSender(changeSet, Model.MODEL, BASIC_KEY, operations).run();

        verify(operations).sendRequests(argThat(new RequestMatcher(
              new String[]{"newObjectWithEncryptedFields",
                           "updateObjectWithEncryptedFields",
                           "deleteObjectWithEncryptedFields",
              }, new String[]{
              "  <insert request_id='1'>"
              + "    <id>"
              + "      newObjectWithEncryptedFields"
              + "    </id>"
              + "    <row>"
              + "      <field name='id'>0</field>"
              + "      <field name='date'>vpE0JmtkU4JAtqZhQlPKew==</field>"
              + "    </row>"
              + "  </insert>",
              "  <update request_id='2'>"
              + "    <id>"
              + "      updateObjectWithEncryptedFields"
              + "    </id>"
              + "    <primarykey>"
              + "      <field name='id'>1</field>"
              + "    </primarykey>"
              + "    <row>"
              + "      <field name='date'>ByZ3V8pyONs=</field>"
              + "      <field name='name'>8smvx6Ul2XY=</field>"
              + "    </row>"
              + "  </update>",
              "  <delete request_id='3'>"
              + "    <id>"
              + "      deleteObjectWithEncryptedFields"
              + "    </id>"
              + "    <primarykey>"
              + "      <field name='id'>2</field>"
              + "    </primarykey>"
              + "  </delete>"
        })));
    }


    @Before
    public void setUp() throws Exception {
        operations = mock(MadConnectionOperations.class);
        ResultManager resultManager = mock(ResultManager.class);
        stub(operations.sendRequests((Request[])anyObject())).toReturn(resultManager);
        RequestIdManager.getInstance().reset();
    }


    private static class Model {
        static final GlobModel MODEL = GlobModelBuilder.init(ObjectWithCompositeKey.TYPE,
                                                             LinkedToObjectWithCompositeKey.TYPE,
                                                             ObjectWithEncryptedFields.TYPE).get();


        private Model() {
        }
    }
}
