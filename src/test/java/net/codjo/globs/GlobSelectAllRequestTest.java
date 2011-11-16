package net.codjo.globs;
import net.codjo.mad.client.request.MadServerFixture;
import net.codjo.mad.client.request.RequestException;
import net.codjo.mad.client.request.Result;
import org.crossbowlabs.globs.metamodel.GlobType;
import org.crossbowlabs.globs.model.GlobRepository;
import org.crossbowlabs.globs.model.GlobRepositoryBuilder;
import org.crossbowlabs.globs.xml.XmlComparisonMode;
import org.junit.Test;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.verify;
/**
 *
 */
public class GlobSelectAllRequestTest extends AbstractGlobRequestTestCase {

    @Test
    public void test_standardCase() throws Exception {
        Result result = MadServerFixture
              .createResult(new String[]{"id", "bankName", "bankCode", "number", "date"},
                            new String[][]{
                                  {"1", "Société Générale", "SG01234", "123456789", "2007-08-02"},
                                  {"2", "Crédit Agricole", "CA01234", "null", "2007-08-02"},
                                  {"3", "Crédit Mutuel", "CM01234", "498475245", "2007-08-02"},
                            });
        sendRequest(result,
                    Account.TYPE,
                    "selectAllAccount",
                    "  <select request_id='1'>"
                    + "    <id>selectAllAccount</id>"
                    + "    <attributes>"
                    + "      <name>id</name>"
                    + "      <name>bankName</name>"
                    + "      <name>bankCode</name>"
                    + "      <name>number</name>"
                    + "      <name>date</name>"
                    + "    </attributes>"
                    + "    <page num='1' rows='2147483647'/>"
                    + "  </select>",
                    "<account id='1' bankName='Société Générale' bankCode='SG01234' number='123456789' date='2007/08/02'/>"
                    + "<account id='2' bankName='Crédit Agricole' bankCode='CA01234' date='2007/08/02'/>"
                    + "<account id='3' bankName='Crédit Mutuel' bankCode='CM01234' number='498475245' date='2007/08/02'/>"
        );
    }


    @Test
    public void test_noResult() throws Exception {
        Result result = MadServerFixture
              .createResult(new String[]{"id", "bankName", "bankCode", "number", "date"},
                            new String[0][0]);

        sendRequest(result,
                    Account.TYPE,
                    "selectAllAccount",
                    "  <select request_id='1'>"
                    + "    <id>selectAllAccount</id>"
                    + "    <attributes>"
                    + "      <name>id</name>"
                    + "      <name>bankName</name>"
                    + "      <name>bankCode</name>"
                    + "      <name>number</name>"
                    + "      <name>date</name>"
                    + "    </attributes>"
                    + "    <page num='1' rows='2147483647'/>"
                    + "  </select>",
                    "");
    }


    // "Impossible de charger les globs de type 'Account'. Le champ 'unknown' n'existe pas sur le glob."
    @Test(expected = RequestException.class)
    public void test_fieldUnknownInGlobThrowsAnException() throws Exception {
        Result result = MadServerFixture
              .createResult(new String[]{"id", "unknown", "bankCode", "number", "date"},
                            new String[][]{
                                  {"1", "Société Générale", "SG01234", "123456789"},
                            });
        sendRequest(result, Account.TYPE, null, null, null);
    }


    @Test
    public void test_objectWithEncryptedFields() throws Exception {
        Result result = MadServerFixture
              .createResult(new String[]{"id", "name", "date"},
                            new String[][]{
                                  {"0", "/sw3UZoOiqw=", "vpE0JmtkU4JAtqZhQlPKew=="},
                                  {"1", "8smvx6Ul2XY=", "QWS/40wJ35hGLJQHbiNMnA=="},
                                  {"2", "ByZ3V8pyONs=", "ByZ3V8pyONs="},
                            });

        sendRequest(result,
                    ObjectWithEncryptedFields.TYPE,
                    "selectAllObjectWithEncryptedFields",
                    "  <select request_id='1'>"
                    + "    <id>selectAllObjectWithEncryptedFields</id>"
                    + "    <attributes>"
                    + "      <name>id</name>"
                    + "      <name>name</name>"
                    + "      <name>date</name>"
                    + "    </attributes>"
                    + "    <page num='1' rows='2147483647'/>"
                    + "  </select>",
                    "<objectWithEncryptedFields id='0' name='' date='2007/07/07'/>"
                    + "<objectWithEncryptedFields id='1' name='obj1' date='2007/05/07'/>"
                    + "<objectWithEncryptedFields id='2'/>");
    }


    @Test
    public void test_objectWithEncryptedNull() throws Exception {

        Result result = MadServerFixture
              .createResult(new String[]{"id", "name"},
                            new String[][]{
                                  {"0", "null"},
                                  {"1", "8smvx6Ul2XY="}
                            });

        sendRequest(result,
                    ObjectWithEncryptedFields.TYPE,
                    "selectAllObjectWithEncryptedFields",
                    "  <select request_id='1'>"
                    + "    <id>selectAllObjectWithEncryptedFields</id>"
                    + "    <attributes>"
                    + "      <name>id</name>"
                    + "      <name>name</name>"
                    + "      <name>date</name>"
                    + "    </attributes>"
                    + "    <page num='1' rows='2147483647'/>"
                    + "  </select>",
                    "<objectWithEncryptedFields id='0'/>"
                    + "<objectWithEncryptedFields id='1' name='obj1'/>");
    }


    @Test
    public void test_specific() throws Exception {
        Result result = MadServerFixture
              .createResult(new String[]{"id", "bankName", "bankCode", "number", "date"},
                            new String[][]{
                                  {"1", "Société Générale", "SG01234", "123456789", "2007-08-02"},
                                  {"2", "Crédit Agricole", "CA01234", "null", "2007-08-02"},
                                  {"3", "Crédit Mutuel", "CM01234", "498475245", "2007-08-02"},
                            });

        sendRequest(result,
                    Account.TYPE,
                    "selectAllAccount",
                    "  <select request_id='1'>"
                    + "    <id>selectAllAccount</id>"
                    + "    <attributes>"
                    + "      <name>id</name>"
                    + "      <name>bankName</name>"
                    + "      <name>bankCode</name>"
                    + "      <name>number</name>"
                    + "      <name>date</name>"
                    + "    </attributes>"
                    + "    <page num='1' rows='2147483647'/>"
                    + "  </select>",
                    "<account id='1' bankName='Société Générale' bankCode='SG01234' number='123456789' date='2007/08/02'/>"
                    + "<account id='2' bankName='Crédit Agricole' bankCode='CA01234' date='2007/08/02'/>"
                    + "<account id='3' bankName='Crédit Mutuel' bankCode='CM01234' number='498475245' date='2007/08/02'/>");
    }


    private void sendRequest(Result result,
                             GlobType type,
                             String expectedHandlerId,
                             String expectedRequestXml,
                             String expectedRepositoryContents)
          throws RequestException {
        stubResultManager(result);

        GlobRepository repository = send(type);

        verify(operations).sendRequests(argThat(new RequestMatcher(
              new String[]{expectedHandlerId},
              new String[]{expectedRequestXml
        })));

        checker.assertEquals(repository, expectedRepositoryContents, XmlComparisonMode.ALL_ATTRIBUTES);
    }


    private GlobRepository send(GlobType globTypes) throws RequestException {
        GlobSelectAllRequest request = new GlobSelectAllRequest(GlobChangeSetSenderTest.BASIC_KEY,
                                                                globTypes);
        GlobRepository repository = GlobRepositoryBuilder.createEmpty();
        request.execute(repository, operations);
        return repository;
    }
}
