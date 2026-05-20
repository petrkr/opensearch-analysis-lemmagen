// TODO: Bring back tests for keyword_repeat and keyword_marker after
// https://github.com/elastic/elasticsearch/issues/27527 is resolved
//
package org.opensearch.index.analysis;

import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.email.UAX29URLEmailTokenizer;

import org.opensearch.Version;
import org.opensearch.cluster.metadata.IndexMetadata;
import org.opensearch.common.settings.Settings;
import org.opensearch.env.Environment;
import org.opensearch.plugin.analysis.lemmagen.AnalysisLemmagenPlugin;
import org.opensearch.test.OpenSearchTestCase;
import org.opensearch.test.OpenSearchTokenStreamTestCase;

import java.io.IOException;
import java.io.StringReader;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.apache.lucene.tests.analysis.BaseTokenStreamTestCase.assertTokenStreamContents;
import static org.hamcrest.Matchers.instanceOf;

public class LemmagenAnalysisTests extends OpenSearchTokenStreamTestCase {

  public void testLemmagenTokenFilter() throws IOException {
    OpenSearchTestCase.TestAnalysis analysis = createAnalysis();

    String source = "Děkuji, že jsi přišel.";
    String[] expected = { "Děkovat", "že", "být", "přijít" };
    String[] filters = { "lemmagen_lexicon", "lemmagen_lexicon_with_ext", "lemmagen_lexicon_path" };

    for (String filter : filters) {
      TokenFilterFactory tokenFilter = analysis.tokenFilter.get(filter);
      assertThat(tokenFilter, instanceOf(LemmagenFilterFactory.class));

      Tokenizer tokenizer = new UAX29URLEmailTokenizer();
      tokenizer.setReader(new StringReader(source));

      assertTokenStreamContents(tokenFilter.create(tokenizer), expected);
    }
  }

  public OpenSearchTestCase.TestAnalysis createAnalysis() throws IOException {
    InputStream lexicon = LemmagenAnalysisTests.class.getResourceAsStream("/org/opensearch/index/analysis/cs.lem");

    Path home = createTempDir();
    Path config = home.resolve("config" + "/" + LemmagenFilterFactory.DEFAULT_DIRECTORY);
    Files.createDirectories(config);
    Files.copy(lexicon, config.resolve("cs.lem"));

    String path = "/org/opensearch/index/analysis/lemmagen.json";

    Settings settings = Settings.builder().loadFromStream(path, getClass().getResourceAsStream(path), false)
        .put(IndexMetadata.SETTING_VERSION_CREATED, Version.CURRENT).put(Environment.PATH_HOME_SETTING.getKey(), home)
        .build();

    return AnalysisTestsHelper.createTestAnalysisFromSettings(settings, new AnalysisLemmagenPlugin());
  }

}
