"""Generate a video with Google's Veo model via the Gemini API.

Set your API key first:

    export GEMINI_API_KEY="your-api-key"

Then run:

    python generate_video.py

Note: video generation can take a few minutes, and Veo access may require a
paid tier / allowlisting on your API key.
"""

import time

from google import genai

# The client automatically picks up the API key from the
# GEMINI_API_KEY (or GOOGLE_API_KEY) environment variable.
client = genai.Client()

prompt = """Drone shot following a classic red convertible driven by a man along a winding coastal road at sunset, waves crashing against the rocks below.
The convertible accelerates fast and the engine roars loudly."""

operation = client.models.generate_videos(
    model="veo-3.1-generate-preview",
    prompt=prompt,
)

# Poll the operation status until the video is ready.
while not operation.done:
    print("Waiting for video generation to complete...")
    time.sleep(10)
    operation = client.operations.get(operation)

# Download the generated video.
generated_video = operation.response.generated_videos[0]
client.files.download(file=generated_video.video)
generated_video.video.save("realism_example.mp4")
print("Generated video saved to realism_example.mp4")
