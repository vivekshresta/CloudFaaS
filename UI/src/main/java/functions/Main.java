package functions;

import com.google.cloud.functions.HttpFunction;
import com.google.cloud.functions.HttpRequest;
import com.google.cloud.functions.HttpResponse;
import functions.cache.Cache;
import functions.cache.UIContent;
import org.json.simple.JSONObject;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Map;
import java.util.Optional;

public class Main implements HttpFunction {
    private String frequencyGeneratorURL = "https://us-central1-vivekshresta-bandaru.cloudfunctions.net/frequency-function";
    private String histogramGeneratorURL = "https://us-central1-vivekshresta-bandaru.cloudfunctions.net/histogram_function";
    private Cache cache = new Cache();

    @Override
    public void service(HttpRequest request, HttpResponse response) throws IOException {
        response.appendHeader("Content-Type", "text/html; charset=UTF-8");
        BufferedWriter out = response.getWriter();

        Optional<String> url = request.getFirstQueryParameter("url");
        if(url.isEmpty())
            out.write(getForm());
        else
            out.write(getFinalPage(url.get().trim()));
    }

    private String getForm() {
        return "<form action = /ui-function/histogram method=\"POST\">\n" +
                "        <label for=\"url\">Enter the URL:</label>\n" +
                "        <input type=\"text\" name=\"url\" id=\"url\" size=\"15\">\n" +
                "        <input type=\"submit\" name=\"submit\" id=\"submit\"/>\n" +
                "</form>";
    }

    private String getFinalPage(String url) {
        StringBuilder result = new StringBuilder(getForm()).append("\n");

        try {
            long initialTime = System.currentTimeMillis();
            UIContent uiContent = getUIContent(url);
            result.append("<body>\n").append("<img src=\"").append(uiContent.getImageURL()).append("\" alt=\"Histogram image\">\n")
                    .append("<br>")
                    .append("\t<div>The time taken to generate the frequency distribution and histogram: ").append(System.currentTimeMillis() - initialTime).append(" ms</div>\n")
                    .append("<br>")
                    .append("\t<div><b>Frequency distribution for sentence lengths:</b> (Format - Sentence length: Number of occurrences)\" </div>\n")
                    .append("\t<div>").append(uiContent.getAggregatedFrequencies()).append("</div>\n")
                    .append("</body>");
        } catch (IOException e) {
            e.printStackTrace();
        }

        return result.toString();
    }

    private UIContent getUIContent(String url) throws IOException {
        UIContent uiContent = cache.getUIContent(url);
        if(uiContent == null) {
            JSONObject json = new JSONObject();
            json.put("url", url);
            Map<String, String> frequencies = Client.getData(frequencyGeneratorURL, json);

            json.put("frequencies", frequencies.get("frequencies"));
            String imageURL = Client.getData(histogramGeneratorURL, json).get("histogramURL").trim();
            String aggregatedFrequencies = frequencies.get("aggregatedFrequencies");

            cache.addUIContent(url, imageURL, aggregatedFrequencies);
            uiContent = new UIContent(imageURL, aggregatedFrequencies);
        }

        return uiContent;
    }
}
