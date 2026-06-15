# Google Gemini API quickstart

A minimal example of calling the [Google Gemini API](https://ai.google.dev/)
with the official `google-genai` Python SDK.

> Prefer another language? See the [Node.js](./node/), [Go](./go/),
> [Java](./java/), or [C#](./csharp/) versions.

## 1. Install

```bash
# (optional) create and activate a virtual environment
python -m venv .venv
source .venv/bin/activate    # Windows: .venv\Scripts\activate

pip install -r requirements.txt
```

## 2. Set your API key

Get a key from [Google AI Studio](https://aistudio.google.com/app/apikey),
then make it available to the SDK:

```bash
export GEMINI_API_KEY="your-api-key"
```

The client reads the key from the `GEMINI_API_KEY` (or `GOOGLE_API_KEY`)
environment variable automatically. You can also copy `.env.example` to `.env`
and load it with your tooling of choice.

## 3. Run

```bash
python quickstart.py
```

Expected output is a short, model-generated sentence, for example:

```
AI learns patterns from data to make predictions.
```

## Bonus: video generation with Veo

[`generate_video.py`](./generate_video.py) shows how to generate a video with
Google's Veo model, polling the long-running operation until the clip is ready
and saving it to `realism_example.mp4`:

```bash
python generate_video.py
```

Video generation takes a few minutes, and Veo access may require a paid tier or
allowlisting on your API key.

## Notes

- The example uses the `gemini-2.5-flash` model. Swap in any model your key has
  access to (e.g. `gemini-2.5-pro`); see the
  [models documentation](https://ai.google.dev/gemini-api/docs/models) for the
  current list.
- The SDK can also target Vertex AI by setting
  `GOOGLE_GENAI_USE_VERTEXAI=true` along with the relevant project/location
  variables. See the [SDK docs](https://googleapis.github.io/python-genai/).
