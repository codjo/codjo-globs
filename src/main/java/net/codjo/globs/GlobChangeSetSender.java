package net.codjo.globs;
import net.codjo.globs.util.MadFieldConverter;
import net.codjo.globs.util.MadUtil;
import net.codjo.mad.client.plugin.MadConnectionOperations;
import net.codjo.mad.client.request.FieldsList;
import net.codjo.mad.client.request.Request;
import net.codjo.mad.client.request.RequestException;
import net.codjo.mad.client.request.Result;
import net.codjo.mad.gui.request.RequestSubmiter;
import net.codjo.mad.gui.request.factory.DeleteFactory;
import net.codjo.mad.gui.request.factory.InsertFactory;
import net.codjo.mad.gui.request.factory.UpdateFactory;
import net.codjo.mad.gui.request.util.MultiRequestsHelper;
import java.util.LinkedHashMap;
import java.util.Map;
import org.crossbowlabs.globs.metamodel.Field;
import org.crossbowlabs.globs.metamodel.GlobModel;
import org.crossbowlabs.globs.model.ChangeSet;
import org.crossbowlabs.globs.model.ChangeSetVisitor;
import org.crossbowlabs.globs.model.FieldValues;
import org.crossbowlabs.globs.model.Key;
import org.crossbowlabs.globs.model.utils.ChangeSetSequencer;
/**
 *
 */
public class GlobChangeSetSender {
    private MadFieldConverter converter;
    private final ChangeSet changeSet;
    private final GlobModel model;
    private MadConnectionOperations operations;


    public GlobChangeSetSender(ChangeSet changeSet,
                               GlobModel model,
                               String encryptionKey,
                               MadConnectionOperations operations) {
        this.changeSet = changeSet;
        this.model = model;
        this.operations = operations;
        converter = new MadFieldConverter(encryptionKey);
    }


    public void run() throws RequestException {
        try {
            MultiRequestsHelper helper = new MultiRequestsHelper(operations);
            fillRequest(changeSet, helper);
            helper.sendRequest();
        }
        catch (Exception e) {
            throw new RequestException(e.getMessage());
        }
    }


    private void fillRequest(ChangeSet currentSet, MultiRequestsHelper helper) throws Exception {
        RequestBuilder requestBuilder = new RequestBuilder(helper);
        ChangeSetSequencer.process(currentSet, model, requestBuilder);
    }


    private FieldsList buildPrimaryKey(Key key) {
        final FieldsList primaryKey = new FieldsList();
        key.safeApply(new FieldValues.Functor() {
            public void process(Field field, Object value) throws Exception {
                primaryKey.addField(field.getName(), converter.toString(field, value));
            }
        });
        return primaryKey;
    }


    private Map buildFields(FieldValues key, FieldValues values) {
        final Map<String, String> fields = new LinkedHashMap<String, String>();
        FieldFiller fieldFiller = new FieldFiller(fields);
        if (key != null) {
            key.safeApply(fieldFiller);
        }
        if (values != null) {
            values.safeApply(fieldFiller);
        }
        return fields;
    }


    private class RequestBuilder implements ChangeSetVisitor {
        private final MultiRequestsHelper requestHelper;


        private RequestBuilder(MultiRequestsHelper helper) {
            requestHelper = helper;
        }


        public void visitCreation(Key key, FieldValues values) throws Exception {
            requestHelper.addSubmiter(new CreateRequestSubmitter(key, values));
        }


        public void visitUpdate(Key key, FieldValues values) throws Exception {
            requestHelper.addSubmiter(new UpdateRequestSubmitter(key, values));
        }


        public void visitDeletion(Key key, FieldValues values) throws Exception {
            requestHelper.addSubmiter(new DeleteRequestSubmitter(key));
        }
    }

    private abstract class AbstractRequestSubmitter implements RequestSubmiter {
        private final Key key;
        private final FieldValues values;


        private AbstractRequestSubmitter(Key key, FieldValues values) {
            this.key = key;
            this.values = values;
        }


        public void setResult(Result result) {
        }


        protected Key getKey() {
            return key;
        }


        protected FieldValues getValues() {
            return values;
        }
    }

    private class DeleteRequestSubmitter extends AbstractRequestSubmitter {
        private DeleteRequestSubmitter(Key key) {
            super(key, null);
        }


        public Request buildRequest() {
            DeleteFactory deleteFactory = new DeleteFactory(MadUtil.buildHandlerId("delete", getKey()));
            deleteFactory.init(buildPrimaryKey(getKey()));
            return deleteFactory.buildRequest(null);
        }
    }

    private class CreateRequestSubmitter extends AbstractRequestSubmitter {
        private CreateRequestSubmitter(Key key, FieldValues values) {
            super(key, values);
        }


        public Request buildRequest() {
            InsertFactory insertFactory = new InsertFactory(MadUtil.buildHandlerId("new", getKey()));
            insertFactory.init(buildPrimaryKey(getKey()));
            return insertFactory.buildRequest(buildFields(getKey(), getValues()));
        }
    }

    private class UpdateRequestSubmitter extends AbstractRequestSubmitter {
        private UpdateRequestSubmitter(Key key, FieldValues values) {
            super(key, values);
        }


        public Request buildRequest() {
            UpdateFactory updateFactory = new UpdateFactory(MadUtil.buildHandlerId("update", getKey()));
            updateFactory.init(buildPrimaryKey(getKey()));
            return updateFactory.buildRequest(buildFields(null, getValues()));
        }
    }

    private class FieldFiller implements FieldValues.Functor {
        private final Map<String, String> fields;


        private FieldFiller(Map<String, String> fields) {
            this.fields = fields;
        }


        public void process(Field field, Object value) throws Exception {
            fields.put(field.getName(), converter.toString(field, value));
        }
    }
}
