import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Movies {

	public static void main(String[] args) {
		Scanner input = new Scanner(System.in);
		greeting(input);
		input.close();
	}

	public static void greeting(Scanner input) {
		System.out.print("Hello, Enter the movie name: ");
		String title = input.nextLine();
		System.out.print("Enter the country code: ");
		String country = input.nextLine();
		System.out.println("");

		OkHttpClient client = new OkHttpClient();
		// Creating a URL to search for user's inputs
		String url = "https://streaming-availability.p.rapidapi.com/search/title?title="
				+ URLEncoder.encode(title, StandardCharsets.UTF_8) + "&country="
				+ URLEncoder.encode(country, StandardCharsets.UTF_8) + "&show_type=all&output_language=en";

		Request request = new Request.Builder().url(url).get()
				.addHeader("X-RapidAPI-Key", "2cdabb2f15msh038c15a7c3e873bp118a71jsne410b9c1a971")
				.addHeader("X-RapidAPI-Host", "streaming-availability.p.rapidapi.com").build();

		try {
			Response response = client.newCall(request).execute();
			if (response.isSuccessful()) {
				String responseBody = response.body().string();

				// Parse the JSON response using Jackson ObjectMapper
				ObjectMapper objectMapper = new ObjectMapper();
				JsonNode jsonResponse = objectMapper.readTree(responseBody);

				// Extract movie details from the JSON response
				JsonNode resultsArray = jsonResponse.get("result");
				if (resultsArray != null && resultsArray.isArray()) {
					int movieCount = 0; // Initialize the counter
					for (JsonNode movieInfo : resultsArray) {
						if (movieCount >= 5) {
							break; // Exit the loop if we have 5 movies
						}

						String movieTitle = movieInfo.get("title").asText();

						// Extract streaming information
						JsonNode streamingInfo = movieInfo.get("streamingInfo");
						if (streamingInfo != null) {
							JsonNode usStreamingInfo = streamingInfo.get("us");
							if (usStreamingInfo != null && usStreamingInfo.isArray()) {
								for (JsonNode serviceInfo : usStreamingInfo) {
									String service = serviceInfo.get("service").asText();
									String streamingType = serviceInfo.get("streamingType").asText();
									String quality = serviceInfo.get("quality").asText();
									String link = serviceInfo.get("link").asText();
									
									System.out.println("Movie: " + movieTitle);
									System.out.println("Service: " + service);
									System.out.println("Streaming Type: " + streamingType);
									System.out.println("Quality: " + quality);
									System.out.println("Link: " + link);
									System.out.println("");

									// Extracting Price Info
									JsonNode priceInfo = serviceInfo.get("price");
									if (priceInfo != null) {
										String formatted = priceInfo.get("formatted").asText();
										System.out.println("Formatted: " + formatted);
									} else {
										System.out.println("Price information not available.");
									}

									
								}
							}
						}
						movieCount++;
					}
				}

			} else {
				System.out.println("Request was not sucessful. Response Code:" + response.code());
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
}
