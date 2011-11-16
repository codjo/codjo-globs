package net.codjo.globs;
import net.codjo.globs.util.MadUtil;
import net.codjo.mad.client.plugin.MadConnectionOperations;
import net.codjo.mad.client.request.RequestException;
import org.crossbowlabs.globs.metamodel.GlobType;
import org.crossbowlabs.globs.model.GlobRepository;
/**
 *
 */
public class GlobSelectAllRequest implements GlobSelectRequest {
    private final GlobType[] globTypes;
    private final String encryptionKey;


    public GlobSelectAllRequest(String encryptionKey,
                                GlobType... globTypes) {
        this.globTypes = globTypes;
        this.encryptionKey = encryptionKey;
    }


    public void execute(GlobRepository repository, MadConnectionOperations operations) throws RequestException {
        GlobSelectByHandlerRequest request = new GlobSelectByHandlerRequest(encryptionKey);
        for (GlobType type : globTypes) {
            request.add(type, MadUtil.buildHandlerId("selectAll", type), null);
        }
        request.execute(repository, operations);
    }
}
