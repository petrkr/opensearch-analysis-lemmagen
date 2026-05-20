package org.opensearch.plugin.analysis.lemmagen;

import org.opensearch.index.analysis.LemmagenFilterFactory;
import org.opensearch.index.analysis.TokenFilterFactory;
import org.opensearch.indices.analysis.AnalysisModule.AnalysisProvider;
import org.opensearch.plugins.AnalysisPlugin;
import org.opensearch.plugins.Plugin;

import static org.opensearch.plugins.AnalysisPlugin.requiresAnalysisSettings;

import java.util.Map;

import static java.util.Collections.singletonMap;

public class AnalysisLemmagenPlugin extends Plugin implements AnalysisPlugin {
  @Override
  public Map<String, AnalysisProvider<TokenFilterFactory>> getTokenFilters() {
    return singletonMap("lemmagen", requiresAnalysisSettings(
        (indexSettings, env, name, settings) -> new LemmagenFilterFactory(indexSettings, env, name, settings)));
  }
}
