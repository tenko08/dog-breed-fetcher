package dogapi;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * BreedFetcher implementation that relies on the dog.ceo API.
 * Note that all failures get reported as BreedNotFoundException
 * exceptions to align with the requirements of the BreedFetcher interface.
 */
public class DogApiBreedFetcher implements BreedFetcher {
    private final OkHttpClient client = new OkHttpClient();

    /**
     * Fetch the list of sub breeds for the given breed from the dog.ceo API.
     * @param breed the breed to fetch sub breeds for
     * @return list of sub breeds for the given breed
     * @throws BreedNotFoundException if the breed does not exist (or if the API call fails for any reason)
     */
    @Override
    public List<String> getSubBreeds(String breed) {
        if (breed == null) {
            throw new BreedNotFoundException("null");
        }
        
        try {
            String url = "https://dog.ceo/api/breed/" + breed + "/list";
            
            Request request = new Request.Builder()
                    .url(url)
                    .build();
            
            try (Response response = client.newCall(request).execute()) {
                if (!response.isSuccessful()) {
                    throw new BreedNotFoundException(breed);
                }
                
                var responseBodyObj = response.body();
                if (responseBodyObj == null) {
                    throw new BreedNotFoundException(breed);
                }
                String responseBody = responseBodyObj.string();
                if (responseBody.isEmpty()) {
                    throw new BreedNotFoundException(breed);
                }
                JSONObject jsonResponse = new JSONObject(responseBody);
                
                if (!"success".equals(jsonResponse.getString("status"))) {
                    throw new BreedNotFoundException(breed);
                }
                
                JSONArray subBreedsArray = jsonResponse.getJSONArray("message");
                List<String> subBreeds = new ArrayList<>();
                
                for (int i = 0; i < subBreedsArray.length(); i++) {
                    subBreeds.add(subBreedsArray.getString(i));
                }
                
                return subBreeds;
            }
        } catch (IOException e) {
            throw new BreedNotFoundException(breed);
        }
    }
}