// Minimal Google Gemini API quickstart (Go).
//
// Set your API key first:
//
//	export GEMINI_API_KEY="your-api-key"
//
// Then run:
//
//	go run .
package main

import (
	"context"
	"fmt"
	"log"

	"google.golang.org/genai"
)

func main() {
	ctx := context.Background()

	// The client automatically picks up the API key from the
	// GEMINI_API_KEY (or GOOGLE_API_KEY) environment variable.
	client, err := genai.NewClient(ctx, nil)
	if err != nil {
		log.Fatal(err)
	}

	result, err := client.Models.GenerateContent(
		ctx,
		"gemini-2.5-flash",
		genai.Text("Explain how AI works in a few words"),
		nil,
	)
	if err != nil {
		log.Fatal(err)
	}
	fmt.Println(result.Text())
}
