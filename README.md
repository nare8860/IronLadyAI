# IronLadyAI
AI-based Customer Interaction Solution (Task 1)

## Overview
An AI-powered customer interaction assistant designed for Iron Lady to guide users about programs, career growth, and suitability for working professionals.

## Features
- Conversational chat UI
- AI-powered responses using OpenAI
- Intent-based fallback responses
- Reliable and demo-safe design

## Tech Stack
- Java Servlets
- Apache Tomcat 9
- HTML, CSS, JavaScript
- JSON
- OpenAI API

## How It Works
1. User enters a query in the chat UI
2. Frontend sends request to `/chat`
3. Backend servlet processes request
4. OpenAI generates response (or fallback is used)
5. JSON response is displayed to the user

## How to Run
1. Import project into Eclipse
2. Add `json` JAR to `WEB-INF/lib`
3. Configure Apache Tomcat 9
4. Start Tomcat
5. Open:
   http://localhost:8080/IronLadyAI/test.html

## Note
For demo reliability, fallback logic is implemented when API quota is unavailable.
