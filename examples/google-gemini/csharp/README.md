# Google Gemini API quickstart (C# / .NET)

A minimal example of calling the [Google Gemini API](https://ai.google.dev/)
with the official `Google.GenAI` .NET SDK.

## 1. Install

Requires the [.NET SDK](https://dotnet.microsoft.com/download) 8.0+. The
`Google.GenAI` package is restored automatically from `GoogleGeminiQuickstart.csproj`:

```bash
dotnet restore
```

## 2. Set your API key

Get a key from [Google AI Studio](https://aistudio.google.com/app/apikey),
then make it available to the SDK:

```bash
export GEMINI_API_KEY="your-api-key"
```

The client reads the key from the `GEMINI_API_KEY` (or `GOOGLE_API_KEY`)
environment variable automatically.

## 3. Run

```bash
dotnet run
```

Expected output is a short, model-generated sentence, for example:

```
AI learns patterns from data to make predictions.
```

## Notes

- The example uses the `gemini-2.5-flash` model. Swap in any model your key has
  access to (e.g. `gemini-2.5-pro`); see the
  [models documentation](https://ai.google.dev/gemini-api/docs/models) for the
  current list.
- The SDK targets `net8.0` and `netstandard2.0`; this sample uses `net8.0`.
- See the [SDK docs](https://github.com/googleapis/dotnet-genai) for more,
  including Vertex AI configuration.
