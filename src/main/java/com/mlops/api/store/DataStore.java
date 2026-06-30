package com.mlops.api.store;

import com.mlops.api.models.EvaluationMetric;
import com.mlops.api.models.MachineLearningModel;
import com.mlops.api.models.MLWorkspace;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// in-memory store, using HashMaps instead of a database as required
public class DataStore {

    private static final DataStore INSTANCE = new DataStore();

    public static DataStore getInstance() {
        return INSTANCE;
    }

    private final Map<String, MLWorkspace> workspaces = new HashMap<>();
    private final Map<String, MachineLearningModel> models = new HashMap<>();
    // metrics stored per model id
    private final Map<String, List<EvaluationMetric>> metrics = new HashMap<>();

    private DataStore() {
        seedData();
    }

    private void seedData() {
        MLWorkspace ws1 = new MLWorkspace("WSVISION-01", "Computer Vision Lab", 500);
        workspaces.put(ws1.getId(), ws1);

        MachineLearningModel m1 = new MachineLearningModel(
                "MOD-8832", "TensorFlow", "DEPLOYED", 0.94, "WSVISION-01");
        MachineLearningModel m2 = new MachineLearningModel(
                "MOD-1103", "PyTorch", "TRAINING", 0.71, "WSVISION-01");
        MachineLearningModel m3 = new MachineLearningModel(
                "MOD-4471", "Scikit-Learn", "DEPRECATED", 0.68, "WSVISION-01");

        models.put(m1.getId(), m1);
        models.put(m2.getId(), m2);
        models.put(m3.getId(), m3);

        ws1.addModelId(m1.getId());
        ws1.addModelId(m2.getId());
        ws1.addModelId(m3.getId());

        // a couple of starter metrics for MOD-8832
        metrics.put("MOD-8832", new ArrayList<>());
        metrics.get("MOD-8832").add(
                new EvaluationMetric("eval-001", System.currentTimeMillis() - 86400000L, 0.91, "MOD-8832"));
    }

    public Map<String, MLWorkspace> getWorkspaces() { return workspaces; }

    public MLWorkspace getWorkspaceById(String id) { return workspaces.get(id); }

    public void putWorkspace(MLWorkspace ws) { workspaces.put(ws.getId(), ws); }

    public boolean deleteWorkspace(String id) { return workspaces.remove(id) != null; }

    public boolean workspaceExists(String id) { return workspaces.containsKey(id); }

    public Map<String, MachineLearningModel> getModels() { return models; }

    public MachineLearningModel getModelById(String id) { return models.get(id); }

    public void putModel(MachineLearningModel m) { models.put(m.getId(), m); }

    public boolean modelExists(String id) { return models.containsKey(id); }

    public List<EvaluationMetric> getMetricsForModel(String modelId) {
        return metrics.getOrDefault(modelId, new ArrayList<>());
    }

    public void addMetricForModel(String modelId, EvaluationMetric metric) {
        if (!metrics.containsKey(modelId)) {
            metrics.put(modelId, new ArrayList<>());
        }
        metrics.get(modelId).add(metric);
    }
}
