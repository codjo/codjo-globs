package net.codjo.globs;
import org.junit.Before;
import org.crossbowlabs.globs.model.GlobChecker;
import org.mockito.Mockito;
import org.mockito.Matchers;
import net.codjo.mad.client.plugin.MadConnectionOperations;
import net.codjo.mad.client.request.Result;
import net.codjo.mad.client.request.RequestException;
import net.codjo.mad.client.request.ResultManager;
import net.codjo.mad.client.request.Request;
import net.codjo.mad.client.request.RequestIdManager;
/**
 *
 */
public abstract class AbstractGlobRequestTestCase {
    protected GlobChecker checker = new GlobChecker();
    protected MadConnectionOperations operations;


    @Before
    public void setUp() throws Exception {
        operations = Mockito.mock(MadConnectionOperations.class);
        RequestIdManager.getInstance().reset();
    }


    protected void stubResultManager(Result result) throws RequestException {
        ResultManager resultManager = Mockito.mock(ResultManager.class);
        Mockito.stub(resultManager.getResult(Matchers.anyString())).toReturn(result);
        Mockito.stub(operations.sendRequests((Request[])Matchers.anyObject())).toReturn(resultManager);
    }
}
