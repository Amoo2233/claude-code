// Minimal Google Gemini API quickstart (C# / .NET).
//
// Set your API key first:
//
//     export GEMINI_API_KEY="your-api-key"
//
// Then run:
//
//     dotnet run

using System;
using System.Threading.Tasks;
using Google.GenAI;

public class GenerateContentSimpleText
{
    public static async Task Main()
    {
        // The client automatically picks up the API key from the
        // GEMINI_API_KEY (or GOOGLE_API_KEY) environment variable.
        var client = new Client();

        var response = await client.Models.GenerateContentAsync(
            model: "gemini-2.5-flash",
            contents: "Explain how AI works in a few words"
        );

        Console.WriteLine(response.Candidates[0].Content.Parts[0].Text);
    }
}
