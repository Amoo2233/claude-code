# Google Gemini API quickstart (Node.js)

A minimal example of calling the [Google Gemini API](https://ai.google.dev/)
with the official `@google/genai` JavaScript SDK.

## 1. Install

Requires Node.js 18+.

```bash
npm install
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
npm start
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
- The SDK can also target Vertex AI by passing `{ vertexai: true, project, location }`
  to the `GoogleGenAI` constructor. See the
  [SDK docs](https://googleapis.github.io/js-genai/).
