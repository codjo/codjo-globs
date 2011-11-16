package net.codjo.globs;
import net.codjo.globs.util.MadFieldConverter;
import net.codjo.mad.client.plugin.MadConnectionOperations;
import net.codjo.mad.client.request.FieldsList;
import net.codjo.mad.client.request.Request;
import net.codjo.mad.client.request.RequestException;
import net.codjo.mad.client.request.Result;
import net.codjo.mad.client.request.ResultManager;
import net.codjo.mad.client.request.Row;
import net.codjo.mad.client.request.SelectRequest;
import net.codjo.mad.gui.request.factory.SelectFactory;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import org.crossbowlabs.globs.metamodel.Field;
import org.crossbowlabs.globs.metamodel.GlobType;
import org.crossbowlabs.globs.model.Glob;
import org.crossbowlabs.globs.model.GlobList;
import org.crossbowlabs.globs.model.GlobRepository;
import org.crossbowlabs.globs.model.utils.GlobBuilder;
import org.crossbowlabs.globs.utils.Strings;
/**
 *
 */
public class GlobSelectByHandlerRequest implements GlobSelectRequest {
    private MadFieldConverter converter;
    private Map<GlobType, Request> typeToRequest;


    public GlobSelectByHandlerRequest(String encryptionKey) {
        typeToRequest = new HashMap<GlobType, Request>();
        converter = new MadFieldConverter(encryptionKey);
    }


    public GlobSelectByHandlerRequest add(GlobType type, String handlerId, Map<String, String> parameters) {
        Request request = createRequest(type, handlerId, parameters);
        typeToRequest.put(type, request);
        return this;
    }


    public void execute(GlobRepository repository, MadConnectionOperations operations)
          throws RequestException {
        GlobList list = new GlobList();
        for (Map.Entry<GlobType, Request> entry : typeToRequest.entrySet()) {
            Result result = sendRequest(entry.getValue(), operations);
            list.addAll(createGlobs(entry.getKey(), result));
        }
        GlobType[] globTypes = typeToRequest.keySet().toArray(new GlobType[typeToRequest.keySet().size()]);
        repository.reset(list, globTypes);
    }


    private GlobList createGlobs(GlobType globType, Result result) throws RequestException {
        if (result.getRowCount() == 0) {
            return GlobList.EMPTY;
        }
        GlobList globs = new GlobList();
        for (Object row : result.getRows()) {
            globs.add(createGlob(globType, (Row)row));
        }
        return globs;
    }


    private Glob createGlob(GlobType globType, Row row) throws RequestException {
        GlobBuilder builder = GlobBuilder.init(globType);
        for (Object field : row.getFields()) {
            net.codjo.mad.client.request.Field madField = (net.codjo.mad.client.request.Field)field;
            Field globField = globType.findField(madField.getName());
            if (globField == null) {
                throw new RequestException(
                      "Impossible de charger les globs de type '" + Strings.capitalize(globType.getName())
                      + "'. Le champ '" + madField.getName()
                      + "' n'existe pas sur le glob.");
            }
            builder.setObject(globField, converter.toObject(globField, madField.getValue()));
        }
        return builder.get();
    }


    private Result sendRequest(Request request, MadConnectionOperations operations) throws RequestException {
        ResultManager resultManager = operations.sendRequests(new Request[]{request});
        if (resultManager.getErrorResult() != null) {
            throw new RequestException(resultManager.getErrorResult());
        }
        return resultManager.getResult(request.getRequestId());
    }


    private Request createRequest(GlobType globType, String handlerId, Map<String, String> parameters) {
        boolean hasParameters = parameters != null && parameters.size() > 0;
        SelectFactory selectFactory = new SelectFactory(handlerId, hasParameters);
        if (hasParameters) {
            FieldsList selector = new FieldsList(parameters);
            selectFactory.init(selector);
        }
        SelectRequest request = (SelectRequest)selectFactory.buildRequest(buildFields(globType));
        request.setPage(1, Integer.MAX_VALUE);
        return request;
    }


    private Map buildFields(GlobType globType) {
        Map<String, String> fields = new LinkedHashMap<String, String>();
        for (Field field : globType.getFields()) {
            fields.put(field.getName(), field.getName());
        }
        return fields;
    }
}
