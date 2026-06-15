"""Minimal Google Gemini API quickstart.

Set your API key first:

    export GEMINI_API_KEY="your-api-key"

Then run:

    python quickstart.py
"""

from google import genai

# The client automatically picks up the API key from the
# GEMINI_API_KEY (or GOOGLE_API_KEY) environment variable.
client = genai.Client()

response = client.models.generate_content(
    model="gemini-2.5-flash",
    contents="Explain how AI works in a few words",
)

print(response.text)
