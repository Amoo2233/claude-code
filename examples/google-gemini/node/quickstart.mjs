// Minimal Google Gemini API quickstart (Node.js).
//
// Set your API key first:
//
//     export GEMINI_API_KEY="your-api-key"
//
// Then run:
//
//     npm install && npm start

import { GoogleGenAI } from "@google/genai";

// The client automatically picks up the API key from the
// GEMINI_API_KEY (or GOOGLE_API_KEY) environment variable.
const ai = new GoogleGenAI({});

async function main() {
  const response = await ai.models.generateContent({
    model: "gemini-2.5-flash",
    contents: "Explain how AI works in a few words",
  });
  console.log(response.text);
}

await main();
