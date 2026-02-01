package com.ironlady;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;

public class ChatServlet extends HttpServlet {

    // 🔐 OpenAI API Key ( do not expose in public repos)
	//For demo i have inserted the PASTE_YOUR_API_KEY_HERE in place of API key
    private static final String OPENAI_API_KEY = "PASTE_YOUR_API_KEY_HERE";

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // CORS + response config
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        // Read request body
        BufferedReader reader = request.getReader();
        StringBuilder body = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            body.append(line);
        }

        JSONObject input = new JSONObject(body.toString());
        String userMessage = input.getString("message");

        // Get AI or fallback response
        String reply = callOpenAI(userMessage);

        // Send JSON response
        JSONObject output = new JSONObject();
        output.put("reply", reply);
        response.getWriter().write(output.toString());
    }

    // ===================== AI CALL =====================

    private String callOpenAI(String userMessage) throws IOException {

        URL url = new URL("https://api.openai.com/v1/chat/completions");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        conn.setRequestMethod("POST");
        conn.setRequestProperty("Authorization", "Bearer " + OPENAI_API_KEY);
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setDoOutput(true);

        // Build request JSON
        JSONObject requestBody = new JSONObject();
        requestBody.put("model", "gpt-3.5-turbo");

        JSONArray messages = new JSONArray();
        messages.put(new JSONObject()
                .put("role", "system")
                .put("content",
                        "You are an AI assistant for Iron Lady. Help users understand programs, career growth, and learning paths."));
        messages.put(new JSONObject()
                .put("role", "user")
                .put("content", userMessage));

        requestBody.put("messages", messages);

        // Send request
        OutputStream os = conn.getOutputStream();
        os.write(requestBody.toString().getBytes());
        os.flush();
        os.close();

        int status = conn.getResponseCode();

        // If OpenAI fails (quota, billing, etc.), use fallback
        if (status < 200 || status >= 300) {
            return getFallbackResponse(userMessage);
        }

        // Read OpenAI response
        BufferedReader br = new BufferedReader(
                new InputStreamReader(conn.getInputStream()));
        StringBuilder responseText = new StringBuilder();
        String resLine;
        while ((resLine = br.readLine()) != null) {
            responseText.append(resLine);
        }
        br.close();

        JSONObject jsonResponse = new JSONObject(responseText.toString());
        return jsonResponse
                .getJSONArray("choices")
                .getJSONObject(0)
                .getJSONObject("message")
                .getString("content");
    }

    // ===================== FALLBACK =====================

    private String getFallbackResponse(String userMessage) {

        String msg = userMessage.toLowerCase();

        // Greetings
        if (msg.contains("hi") || msg.contains("hello") || msg.contains("hlo")) {
            return "Hello! 👋 I’m the Iron Lady AI Assistant. You can ask me about programs, career guidance, or support for working professionals.";
        }

        // Working professional (handle spelling variations)
        if (msg.contains("working")) {
            return "Iron Lady offers flexible, mentor-led programs specially designed for working professionals to grow leadership skills and confidence without disrupting work life.";
        }

        // Career-related
        if (msg.contains("career") || msg.contains("job") || msg.contains("growth")) {
            return "Iron Lady helps women gain career clarity, confidence, and direction through structured learning and mentorship programs.";
        }

        // Programs
        if (msg.contains("program") || msg.contains("course")) {
            return "Iron Lady provides career-focused programs covering leadership, personal branding, and professional development.";
        }

        // Default
        return "Iron Lady is a learning and growth platform that empowers women through career-focused programs, mentorship, and a strong support community.";
    }

}
