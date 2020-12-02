package functions;

import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.sentdetect.SentenceModel;
import opennlp.tools.tokenize.SimpleTokenizer;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FrequencyGenerator {
    private Map<Integer, Integer> aggregatedFrequencies;
    private List<Integer> frequencies;

    public FrequencyGenerator(String url) throws IOException {
        aggregatedFrequencies = new HashMap<>();
        frequencies = new ArrayList<>();

        url = url.trim();
        String content = Client.getData(url);
        String[] sentences = getSentences(content);

        SimpleTokenizer tokenizer = SimpleTokenizer.INSTANCE;
        for(String sentence : sentences) {
            int numOfWords = tokenizer.tokenize(sentence.trim()).length;
            aggregatedFrequencies.compute(numOfWords, (k, v) -> v == null ? 1 : v + 1);
            frequencies.add(numOfWords);
        }
    }

    private String[] getSentences(String content) throws IOException {
        SentenceDetectorME sentenceDetectorME = new SentenceDetectorME(new SentenceModel(new FileInputStream("en-sent.bin")));
        return sentenceDetectorME.sentDetect(content);
    }

    public String getAggregatedFrequencies() {
        StringBuilder result = new StringBuilder();

        for(Integer frequency : aggregatedFrequencies.keySet())
            result.append(frequency).append(":").append(aggregatedFrequencies.get(frequency)).append(", ");

        return result.substring(0, result.length() - 2);
    }

    public String getFrequencies() {
        return frequencies.toString();
    }
}
