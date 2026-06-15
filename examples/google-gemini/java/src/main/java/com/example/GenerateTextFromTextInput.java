package com.example;

import com.google.genai.Client;
import com.google.genai.types.GenerateContentResponse;

/**
 * Minimal Google Gemini API quickstart (Java).
 *
 * <p>Set your API key first:
 *
 * <pre>{@code
 * export GEMINI_API_KEY="your-api-key"
 * }</pre>
 *
 * <p>Then run:
 *
 * <pre>{@code
 * mvn compile exec:java
 * }</pre>
 */
public class GenerateTextFromTextInput {
  public static void main(String[] args) {
    // The client automatically picks up the API key from the
    // GEMINI_API_KEY (or GOOGLE_API_KEY) environment variable.
    Client client = new Client();

    GenerateContentResponse response =
        client.models.generateContent(
            "gemini-2.5-flash",
            "Explain how AI works in a few words",
            null);

    System.out.println(response.text());
  }
}
