package fpt.aptech.projectbe.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Base64;

@Service
public class GeminiService {

    private static final String API_KEY = "AIzaSyBtgaBzoFuDXIzsMQFyJ9HEifMPHK3VH38";
    private static final String GEMINI_API_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent?key=" + API_KEY;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public String generateDescriptionFromImage(MultipartFile image, String name) throws IOException, InterruptedException {
        String base64Image = Base64.getEncoder().encodeToString(image.getBytes());

        String payload = """
        {
          "contents": [
            {
              "parts": [
                {
                  "text": "Viết mô tả hấp dẫn chi tiết cho sản phẩm thời trang có tên: %s. Mô tả nên bao gồm chất liệu, kiểu dáng, màu sắc, phong cách và các đặc điểm nổi bật. Mô tả cần hấp dẫn, chuyên nghiệp và phù hợp để hiển thị trên website bán hàng."
                },
                {
                  "inline_data": {
                    "mime_type": "%s",
                    "data": "%s"
                  }
                }
              ]
            }
          ]
        }
        """.formatted(name, image.getContentType(), base64Image);

        return sendRequestToGemini(payload);
    }
    
    public String generateCustomDescriptionFromImage(MultipartFile image, String prompt) throws IOException, InterruptedException {
        String base64Image = Base64.getEncoder().encodeToString(image.getBytes());

        String payload = """
        {
          "contents": [
            {
              "parts": [
                {
                  "text": "%s"
                },
                {
                  "inline_data": {
                    "mime_type": "%s",
                    "data": "%s"
                  }
                }
              ]
            }
          ]
        }
        """.formatted(prompt, image.getContentType(), base64Image);

        return sendRequestToGemini(payload);
    }
    
    private String sendRequestToGemini(String payload) throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(GEMINI_API_URL))
            .header("Content-Type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(payload))
            .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        
        if (response.statusCode() != 200) {
            throw new IOException("Error from Gemini API: " + response.body());
        }

        return extractDescription(response.body());
    }

    private String extractDescription(String responseBody) {
        try {
            JsonNode rootNode = objectMapper.readTree(responseBody);
            JsonNode candidates = rootNode.path("candidates");
            
            if (candidates.isArray() && candidates.size() > 0) {
                JsonNode content = candidates.get(0).path("content");
                JsonNode parts = content.path("parts");
                
                if (parts.isArray() && parts.size() > 0) {
                    return parts.get(0).path("text").asText();
                }
            }
            
            // Fallback for error cases
            JsonNode error = rootNode.path("error");
            if (!error.isMissingNode()) {
                return "Error: " + error.path("message").asText();
            }
            
            return "Không tạo được mô tả. Vui lòng thử lại.";
        } catch (Exception e) {
            return "Lỗi khi xử lý phản hồi: " + e.getMessage();
        }
    }
}
