package com.comp8047.majorproject.travelplanassistant.service;

import com.comp8047.majorproject.travelplanassistant.entity.TravelPlan;
import com.comp8047.majorproject.travelplanassistant.entity.User;
import com.comp8047.majorproject.travelplanassistant.entity.UserPlanStatus;
import com.comp8047.majorproject.travelplanassistant.entity.Notification;
import com.comp8047.majorproject.travelplanassistant.repository.UserPlanStatusRepository;
import com.comp8047.majorproject.travelplanassistant.repository.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.io.UnsupportedEncodingException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for sending email notifications and reminders
 */
@Service
public class NotificationService {

    @Autowired
    private JavaMailSender mailSender;


    @Autowired
    private UserPlanStatusRepository userPlanStatusRepository;
    
    @Autowired
    private NotificationRepository notificationRepository;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Value("${app.notification.email.enabled:true}")
    private boolean emailNotificationsEnabled;

    @Value("${app.notification.email.from-name:Travel Plan Assistant}")
    private String fromName;

    /**
     * Send travel plan start reminder
     */
    public void sendTravelPlanStartReminder(TravelPlan travelPlan) {
        if (!emailNotificationsEnabled) {
            return;
        }

        List<User> members = getActiveMembers(travelPlan);
        
        for (User member : members) {
            String subject = "Travel Plan Starting Soon: " + travelPlan.getTitle();
            String content = buildTravelPlanStartEmailContent(travelPlan, member);
            
            // Create notification record
            Notification notification = new Notification(
                member.getEmail(),
                member.getFirstName() + " " + member.getLastName(),
                subject,
                content,
                Notification.NotificationType.TRAVEL_PLAN_START_REMINDER,
                travelPlan.getId(),
                null, // System notification
                member.getId()
            );
            
            try {
                sendHtmlEmail(member.getEmail(), subject, content);
                notification.markAsSent();
                System.out.println("Start reminder sent to: " + member.getEmail());
            } catch (Exception e) {
                notification.markAsFailed(e.getMessage());
                System.err.println("Failed to send start reminder to " + member.getEmail() + ": " + e.getMessage());
            } finally {
                notificationRepository.save(notification);
            }
        }
    }

    /**
     * Send travel plan completion reminder
     */
    public void sendTravelPlanCompletionReminder(TravelPlan travelPlan) {
        if (!emailNotificationsEnabled) {
            return;
        }

        try {
            List<User> members = getActiveMembers(travelPlan);
            
            for (User member : members) {
                String subject = "Travel Plan Completed: " + travelPlan.getTitle();
                String content = buildTravelPlanCompletionEmailContent(travelPlan, member);
                
                // Create notification record
                Notification notification = new Notification(
                    member.getEmail(),
                    member.getFirstName() + " " + member.getLastName(),
                    subject,
                    content,
                    Notification.NotificationType.TRAVEL_PLAN_COMPLETION,
                    travelPlan.getId(),
                    null, // System notification
                    member.getId()
                );
                
                try {
                    sendHtmlEmail(member.getEmail(), subject, content);
                    notification.markAsSent();
                    System.out.println("Completion reminder sent to: " + member.getEmail());
                } catch (Exception e) {
                    notification.markAsFailed(e.getMessage());
                    System.err.println("Failed to send completion reminder to " + member.getEmail() + ": " + e.getMessage());
                } finally {
                    notificationRepository.save(notification);
                }
            }
        } catch (Exception e) {
            System.err.println("Failed to send completion reminder: " + e.getMessage());
        }
    }

    /**
     * Send application status notification
     */
    public void sendApplicationStatusNotification(User user, TravelPlan travelPlan, UserPlanStatus.Status status) {
        if (!emailNotificationsEnabled) {
            return;
        }

        String subject = "Application Update: " + travelPlan.getTitle();
        String content = buildApplicationStatusEmailContent(user, travelPlan, status);
        
        // Create notification record
        Notification notification = new Notification(
            user.getEmail(),
            user.getFirstName() + " " + user.getLastName(),
            subject,
            content,
            Notification.NotificationType.APPLICATION_STATUS_UPDATE,
            travelPlan.getId(),
            null, // System notification
            user.getId()
        );
        
        try {
            sendHtmlEmail(user.getEmail(), subject, content);
            notification.markAsSent();
            System.out.println("Application status notification sent to: " + user.getEmail());
        } catch (Exception e) {
            notification.markAsFailed(e.getMessage());
            System.err.println("Failed to send application status notification to " + user.getEmail() + ": " + e.getMessage());
        } finally {
            notificationRepository.save(notification);
        }
    }

    /**
     * Send invitation notification
     */
    public void sendInvitationNotification(User user, TravelPlan travelPlan, User inviter) {
        if (!emailNotificationsEnabled) {
            return;
        }

        String subject = "You're Invited to Join: " + travelPlan.getTitle();
        String content = buildInvitationEmailContent(user, travelPlan, inviter);
        
        // Create notification record
        Notification notification = new Notification(
            user.getEmail(),
            user.getFirstName() + " " + user.getLastName(),
            subject,
            content,
            Notification.NotificationType.INVITATION_NOTIFICATION,
            travelPlan.getId(),
            inviter.getId(),
            user.getId()
        );
        
        try {
            sendHtmlEmail(user.getEmail(), subject, content);
            notification.markAsSent();
            System.out.println("Invitation notification sent to: " + user.getEmail());
        } catch (Exception e) {
            notification.markAsFailed(e.getMessage());
            System.err.println("Failed to send invitation notification to " + user.getEmail() + ": " + e.getMessage());
        } finally {
            notificationRepository.save(notification);
        }
    }

    /**
     * Send custom notification
     */
    public void sendCustomNotification(String toEmail, String subject, String content) {
        if (!emailNotificationsEnabled || !StringUtils.hasText(toEmail)) {
            return;
        }

        // Create notification record
        Notification notification = new Notification(
            toEmail,
            null, // No recipient name for custom notifications
            subject,
            content,
            Notification.NotificationType.CUSTOM_NOTIFICATION,
            null, // No travel plan for custom notifications
            null, // No sender for custom notifications
            null  // No recipient user ID for custom notifications
        );
        
        try {
            sendHtmlEmail(toEmail, subject, content);
            notification.markAsSent();
            System.out.println("Custom notification sent to: " + toEmail);
        } catch (Exception e) {
            notification.markAsFailed(e.getMessage());
            System.err.println("Failed to send custom notification to " + toEmail + ": " + e.getMessage());
        } finally {
            notificationRepository.save(notification);
        }
    }


    /**
     * Send HTML email
     */
    void sendHtmlEmail(String to, String subject, String htmlContent) throws MessagingException, UnsupportedEncodingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
        
        helper.setFrom(fromEmail, fromName);
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(htmlContent, true);
        
        mailSender.send(message);
    }

    /**
     * Get active members of a travel plan
     */
    List<User> getActiveMembers(TravelPlan travelPlan) {
        return userPlanStatusRepository.findActiveMembers(travelPlan)
                .stream()
                .map(UserPlanStatus::getUser)
                .collect(Collectors.toList());
    }

    /**
     * Build HTML content for travel plan start email
     */
    String buildTravelPlanStartEmailContent(TravelPlan travelPlan, User user) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy 'at' HH:mm");
        
        return String.format("""
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <style>
                    body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }
                    .container { max-width: 600px; margin: 0 auto; padding: 20px; }
                    .header { background: linear-gradient(135deg, #667eea 0%%, #764ba2 100%%); color: white; padding: 30px; text-align: center; border-radius: 10px 10px 0 0; }
                    .content { background: #f9f9f9; padding: 30px; border-radius: 0 0 10px 10px; }
                    .plan-details { background: white; padding: 20px; border-radius: 8px; margin: 20px 0; box-shadow: 0 2px 4px rgba(0,0,0,0.1); }
                    .button { display: inline-block; background: #667eea; color: white; padding: 12px 24px; text-decoration: none; border-radius: 5px; margin: 10px 0; }
                    .footer { text-align: center; margin-top: 30px; color: #666; font-size: 14px; }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <h1>🚀 Your Travel Plan is Starting!</h1>
                    </div>
                    <div class="content">
                        <p>Hi %s,</p>
                        <p>Great news! Your travel plan is about to begin. Here are the details:</p>
                        
                        <div class="plan-details">
                            <h3>📋 %s</h3>
                            <p><strong>📍 Destination:</strong> %s, %s</p>
                            <p><strong>📅 Start Date:</strong> %s</p>
                            <p><strong>📅 End Date:</strong> %s</p>
                            <p><strong>👥 Members:</strong> %d</p>
                            <p><strong>📝 Description:</strong> %s</p>
                        </div>
                        
                        <p>Have a wonderful trip! Safe travels! 🌍✈️</p>
                        
                        <div class="footer">
                            <p>This is an automated message from Travel Plan Assistant</p>
                        </div>
                    </div>
                </div>
            </body>
            </html>
            """,
            user.getFirstName(),
            travelPlan.getTitle(),
            travelPlan.getDestinationCity(),
            travelPlan.getDestinationCountry(),
            travelPlan.getStartDate().format(formatter),
            travelPlan.getEndDate().format(formatter),
            userPlanStatusRepository.countActiveMembers(travelPlan),
            travelPlan.getDescription()
        );
    }

    /**
     * Build HTML content for travel plan completion email
     */
    String buildTravelPlanCompletionEmailContent(TravelPlan travelPlan, User user) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy 'at' HH:mm");
        
        return String.format("""
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <style>
                    body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }
                    .container { max-width: 600px; margin: 0 auto; padding: 20px; }
                    .header { background: linear-gradient(135deg, #4CAF50 0%%, #45a049 100%%); color: white; padding: 30px; text-align: center; border-radius: 10px 10px 0 0; }
                    .content { background: #f9f9f9; padding: 30px; border-radius: 0 0 10px 10px; }
                    .plan-details { background: white; padding: 20px; border-radius: 8px; margin: 20px 0; box-shadow: 0 2px 4px rgba(0,0,0,0.1); }
                    .footer { text-align: center; margin-top: 30px; color: #666; font-size: 14px; }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <h1>🎉 Travel Plan Completed!</h1>
                    </div>
                    <div class="content">
                        <p>Hi %s,</p>
                        <p>Congratulations! Your travel plan has been completed successfully.</p>
                        
                        <div class="plan-details">
                            <h3>📋 %s</h3>
                            <p><strong>📍 Destination:</strong> %s, %s</p>
                            <p><strong>📅 Completed:</strong> %s</p>
                            <p><strong>👥 Members:</strong> %d</p>
                        </div>
                        
                        <p>We hope you had an amazing time! Share your memories and plan your next adventure! 🌟</p>
                        
                        <div class="footer">
                            <p>This is an automated message from Travel Plan Assistant</p>
                        </div>
                    </div>
                </div>
            </body>
            </html>
            """,
            user.getFirstName(),
            travelPlan.getTitle(),
            travelPlan.getDestinationCity(),
            travelPlan.getDestinationCountry(),
            LocalDateTime.now().format(formatter),
            userPlanStatusRepository.countActiveMembers(travelPlan)
        );
    }

    /**
     * Build HTML content for application status email
     */
    String buildApplicationStatusEmailContent(User user, TravelPlan travelPlan, UserPlanStatus.Status status) {
        String statusMessage = switch (status) {
            case APPLIED_ACCEPTED -> "Congratulations! Your application has been accepted!";
            case APPLIED_REFUSED -> "Unfortunately, your application was not accepted this time.";
            default -> "Your application status has been updated.";
        };

        String statusColor = switch (status) {
            case APPLIED_ACCEPTED -> "#4CAF50";
            case APPLIED_REFUSED -> "#f44336";
            default -> "#2196F3";
        };

        return String.format("""
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <style>
                    body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }
                    .container { max-width: 600px; margin: 0 auto; padding: 20px; }
                    .header { background: %s; color: white; padding: 30px; text-align: center; border-radius: 10px 10px 0 0; }
                    .content { background: #f9f9f9; padding: 30px; border-radius: 0 0 10px 10px; }
                    .plan-details { background: white; padding: 20px; border-radius: 8px; margin: 20px 0; box-shadow: 0 2px 4px rgba(0,0,0,0.1); }
                    .footer { text-align: center; margin-top: 30px; color: #666; font-size: 14px; }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <h1>📧 Application Update</h1>
                    </div>
                    <div class="content">
                        <p>Hi %s,</p>
                        <p>%s</p>
                        
                        <div class="plan-details">
                            <h3>📋 %s</h3>
                            <p><strong>📍 Destination:</strong> %s, %s</p>
                            <p><strong>📅 Start Date:</strong> %s</p>
                        </div>
                        
                        <div class="footer">
                            <p>This is an automated message from Travel Plan Assistant</p>
                        </div>
                    </div>
                </div>
            </body>
            </html>
            """,
            statusColor,
            user.getFirstName(),
            statusMessage,
            travelPlan.getTitle(),
            travelPlan.getDestinationCity(),
            travelPlan.getDestinationCountry(),
            travelPlan.getStartDate().format(DateTimeFormatter.ofPattern("MMM dd, yyyy"))
        );
    }

    /**
     * Build HTML content for invitation email
     */
    String buildInvitationEmailContent(User user, TravelPlan travelPlan, User inviter) {
        return String.format("""
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <style>
                    body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }
                    .container { max-width: 600px; margin: 0 auto; padding: 20px; }
                    .header { background: linear-gradient(135deg, #FF9800 0%%, #F57C00 100%%); color: white; padding: 30px; text-align: center; border-radius: 10px 10px 0 0; }
                    .content { background: #f9f9f9; padding: 30px; border-radius: 0 0 10px 10px; }
                    .plan-details { background: white; padding: 20px; border-radius: 8px; margin: 20px 0; box-shadow: 0 2px 4px rgba(0,0,0,0.1); }
                    .button { display: inline-block; background: #FF9800; color: white; padding: 12px 24px; text-decoration: none; border-radius: 5px; margin: 10px 0; }
                    .footer { text-align: center; margin-top: 30px; color: #666; font-size: 14px; }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <h1>🎯 You're Invited!</h1>
                    </div>
                    <div class="content">
                        <p>Hi %s,</p>
                        <p><strong>%s</strong> has invited you to join their travel plan!</p>
                        
                        <div class="plan-details">
                            <h3>📋 %s</h3>
                            <p><strong>📍 Destination:</strong> %s, %s</p>
                            <p><strong>📅 Start Date:</strong> %s</p>
                            <p><strong>📅 End Date:</strong> %s</p>
                            <p><strong>👥 Current Members:</strong> %d</p>
                            <p><strong>📝 Description:</strong> %s</p>
                        </div>
                        
                        <p><a href="http://localhost:5173">Log in</a> to your account to accept or decline this invitation.</p>
                        
                        <div class="footer">
                            <p>This is an automated message from Let's Go Travel Plan Assistant</p>
                        </div>
                    </div>
                </div>
            </body>
            </html>
            """,
            user.getFirstName(),
            inviter.getFirstName() + " " + inviter.getLastName(),
            travelPlan.getTitle(),
            travelPlan.getDestinationCity(),
            travelPlan.getDestinationCountry(),
            travelPlan.getStartDate().format(DateTimeFormatter.ofPattern("MMM dd, yyyy")),
            travelPlan.getEndDate().format(DateTimeFormatter.ofPattern("MMM dd, yyyy")),
            userPlanStatusRepository.countActiveMembers(travelPlan),
            travelPlan.getDescription()
        );
    }
}
