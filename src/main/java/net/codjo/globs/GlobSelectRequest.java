package net.codjo.globs;
import net.codjo.mad.client.request.RequestException;
import net.codjo.mad.client.plugin.MadConnectionOperations;
import org.crossbowlabs.globs.model.GlobRepository;
/**
 *
 */
public interface GlobSelectRequest {
    void execute(GlobRepository repository, MadConnectionOperations operations) throws RequestException;
}
