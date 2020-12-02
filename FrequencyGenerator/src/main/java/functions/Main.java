package functions;

import com.google.cloud.functions.HttpFunction;
import com.google.cloud.functions.HttpRequest;
import com.google.cloud.functions.HttpResponse;
import functions.helper.PostBodyParser;
import java.io.*;

public class Main implements HttpFunction {

    @Override
    public void service(HttpRequest request, HttpResponse response) {
        String url = PostBodyParser.getPostBody(request).get("url");
        if(url != null) {
            url = url.trim();
            try {
                StringBuilder json = new StringBuilder();

                FrequencyGenerator frequencyGenerator = new FrequencyGenerator(url);
                json.append("{").append("\"aggregatedFrequencies\"").append(":")
                        .append("\"").append(frequencyGenerator.getAggregatedFrequencies().trim()).append("\"")
                        .append(",")
                        .append("\"frequencies\"").append(":").append("\"")
                        .append(frequencyGenerator.getFrequencies()).append("\"").append("}");

                BufferedWriter out = response.getWriter();
                out.write(json.toString());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
