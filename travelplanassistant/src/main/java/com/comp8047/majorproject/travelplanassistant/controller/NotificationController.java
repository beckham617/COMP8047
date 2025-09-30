package com.comp8047.majorproject.travelplanassistant.controller;

import com.comp8047.majorproject.travelplanassistant.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Controller for notification management
 */
@RestController
@RequestMapping("/notifications")
@CrossOrigin(origins = "http://localhost:3000")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    /**
     * Send custom notification email
     */
    @PostMapping("/send")
    public ResponseEntity<Map<String, String>> sendCustomNotification(
            @RequestParam String toEmail,
            @RequestParam String subject,
            @RequestParam String content) {
        
        try {
            notificationService.sendCustomNotification(toEmail, subject, content);
            Map<String, String> response = new HashMap<>();
            response.put("message", "Notification sent successfully");
            response.put("to", toEmail);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Failed to send notification: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Test email configuration
     */
    @PostMapping("/test")
    public ResponseEntity<Map<String, String>> testEmail(@RequestParam String toEmail) {
        try {
            String testSubject = "Test Email from Travel Plan Assistant";
            String testContent = """
                <!DOCTYPE html>
                <html>
                <head>
                    <meta charset="UTF-8">
                    <style>
                        body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }
                        .container { max-width: 600px; margin: 0 auto; padding: 20px; }
                        .header { background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); color: white; padding: 30px; text-align: center; border-radius: 10px 10px 0 0; }
                        .content { background: #f9f9f9; padding: 30px; border-radius: 0 0 10px 10px; }
                    </style>
                </head>
                <body>
                    <div class="container">
                        <div class="header">
                            <h1>✅ Email Test Successful!</h1>
                        </div>
                        <div class="content">
                            <p>This is a test email to verify that your email configuration is working correctly.</p>
                            <p>If you received this email, your notification system is properly configured! 🎉</p>
                        </div>
                    </div>
                </body>
                </html>
                """;
            
            notificationService.sendCustomNotification(toEmail, testSubject, testContent);
            
            Map<String, String> response = new HashMap<>();
            response.put("message", "Test email sent successfully");
            response.put("to", toEmail);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Failed to send test email: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
}
