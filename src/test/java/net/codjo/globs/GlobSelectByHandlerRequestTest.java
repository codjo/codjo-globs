package net.codjo.globs;
import net.codjo.mad.client.request.MadServerFixture;
import net.codjo.mad.client.request.Result;
import java.util.HashMap;
import java.util.Map;
import org.crossbowlabs.globs.model.GlobRepository;
import org.crossbowlabs.globs.model.GlobRepositoryBuilder;
import org.crossbowlabs.globs.xml.XmlComparisonMode;
import org.junit.Test;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.verify;
/**
 *
 */
public class GlobSelectByHandlerRequestTest extends AbstractGlobRequestTestCase {

    @Test
    public void test_standardRequest() throws Exception {
        Result result = MadServerFixture
              .createResult(new String[]{"id", "bankName", "bankCode", "number", "date"},
                            new String[][]{
                                  {"1", "Société Générale", "SG01234", "123456789", "2007-08-02"},
                                  {"2", "Crédit Agricole", "CA01234", "null", "2007-08-02"},
                                  {"3", "Crédit Mutuel", "CM01234", "498475245", "2007-08-02"},
                            });

        stubResultManager(result);

        GlobRepository repository = GlobRepositoryBuilder.createEmpty();

        Map<String, String> parameters = new HashMap<String, String>();
        parameters.put("param", "val");

        new GlobSelectByHandlerRequest(GlobChangeSetSenderTest.BASIC_KEY)
              .add(Account.TYPE, "selectAccountByEmployeeId", parameters)
              .execute(repository, operations);

        verify(operations).sendRequests(argThat(new RequestMatcher(
              new String[]{"selectAccountByEmployeeId",
              }, new String[]{
              "<select request_id='1'>"
              + "    <id>selectAccountByEmployeeId</id>"
              + "    <attributes>"
              + "      <name>id</name>"
              + "      <name>bankName</name>"
              + "      <name>bankCode</name>"
              + "      <name>number</name>"
              + "      <name>date</name>"
              + "    </attributes>"
              + "    <selector>"
              + "      <field name='param'>val</field>"
              + "    </selector>"
              + "    <page num='1' rows='2147483647'/>"
              + "  </select>"
        })));

        checker.assertEquals(repository,
                             "<account id='1' bankName='Société Générale' bankCode='SG01234' number='123456789' date='2007/08/02'/>"
                             + "<account id='2' bankName='Crédit Agricole' bankCode='CA01234' date='2007/08/02'/>"
                             + "<account id='3' bankName='Crédit Mutuel' bankCode='CM01234' number='498475245' date='2007/08/02'/>",
                             XmlComparisonMode.ALL_ATTRIBUTES);
    }
}
