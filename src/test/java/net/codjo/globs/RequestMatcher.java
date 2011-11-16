package net.codjo.globs;
import net.codjo.mad.client.request.Request;
import net.codjo.test.common.XmlUtil;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
/**
 *
 */
class RequestMatcher extends BaseMatcher<Request[]> {
    private String[] requestIds;
    private String[] xml;


    RequestMatcher(String[] requestIds, String[] xml) {
        this.requestIds = requestIds;
        this.xml = xml;
    }


    public boolean matches(Object o) {
        if (!(o instanceof Request[])) {
            return false;
        }
        Request[] requests = (Request[])o;
        if (requests.length != requestIds.length) {
            return false;
        }
        for (int i = 0; i < requestIds.length; i++) {
            if (!requestIds[i].equals(requests[i].getHandlerId())) {
                return false;
            }
            XmlUtil.assertEquivalent(xml[i], requests[i].toXml());
        }
        return true;
    }


    public void describeTo(Description description) {
    }
}
