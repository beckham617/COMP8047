package com.comp8047.majorproject.travelplanassistant.repository;

import com.comp8047.majorproject.travelplanassistant.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository for Notification entity operations
 */
@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    /**
     * Find notifications by recipient email
     */
    List<Notification> findByRecipientEmailOrderByCreatedAtDesc(String recipientEmail);

    /**
     * Find notifications by recipient user ID
     */
    List<Notification> findByRecipientUserIdOrderByCreatedAtDesc(Long recipientUserId);

    /**
     * Find notifications by sender user ID
     */
    List<Notification> findBySenderUserIdOrderByCreatedAtDesc(Long senderUserId);

    /**
     * Find notifications by travel plan ID
     */
    List<Notification> findByTravelPlanIdOrderByCreatedAtDesc(Long travelPlanId);

    /**
     * Find notifications by status
     */
    List<Notification> findByStatusOrderByCreatedAtDesc(Notification.NotificationStatus status);

    /**
     * Find notifications by type
     */
    List<Notification> findByNotificationTypeOrderByCreatedAtDesc(Notification.NotificationType notificationType);

    /**
     * Find failed notifications that can be retried
     */
    @Query("SELECT n FROM Notification n WHERE n.status = 'FAILED' AND n.retryCount < n.maxRetries ORDER BY n.createdAt ASC")
    List<Notification> findFailedNotificationsForRetry();

    /**
     * Find pending notifications
     */
    List<Notification> findByStatusOrderByCreatedAtAsc(Notification.NotificationStatus status);

    /**
     * Find notifications created within a date range
     */
    @Query("SELECT n FROM Notification n WHERE n.createdAt BETWEEN :startDate AND :endDate ORDER BY n.createdAt DESC")
    List<Notification> findByCreatedAtBetween(@Param("startDate") LocalDateTime startDate, 
                                            @Param("endDate") LocalDateTime endDate);

    /**
     * Find notifications by recipient email and type
     */
    List<Notification> findByRecipientEmailAndNotificationTypeOrderByCreatedAtDesc(
            String recipientEmail, Notification.NotificationType notificationType);

    /**
     * Find notifications by recipient user ID and type
     */
    List<Notification> findByRecipientUserIdAndNotificationTypeOrderByCreatedAtDesc(
            Long recipientUserId, Notification.NotificationType notificationType);

    /**
     * Count notifications by status
     */
    long countByStatus(Notification.NotificationStatus status);

    /**
     * Count notifications by type
     */
    long countByNotificationType(Notification.NotificationType notificationType);

    /**
     * Count notifications by recipient email
     */
    long countByRecipientEmail(String recipientEmail);

    /**
     * Find notifications with error messages
     */
    @Query("SELECT n FROM Notification n WHERE n.errorMessage IS NOT NULL ORDER BY n.createdAt DESC")
    List<Notification> findNotificationsWithErrors();

    /**
     * Find recent notifications (last 30 days)
     */
    @Query("SELECT n FROM Notification n WHERE n.createdAt >= :since ORDER BY n.createdAt DESC")
    List<Notification> findRecentNotifications(@Param("since") LocalDateTime since);

    /**
     * Find notifications by multiple statuses
     */
    @Query("SELECT n FROM Notification n WHERE n.status IN :statuses ORDER BY n.createdAt DESC")
    List<Notification> findByStatusInOrderByCreatedAtDesc(@Param("statuses") List<Notification.NotificationStatus> statuses);

    /**
     * Delete old notifications (older than specified date)
     */
    @Query("DELETE FROM Notification n WHERE n.createdAt < :cutoffDate")
    void deleteOldNotifications(@Param("cutoffDate") LocalDateTime cutoffDate);

    /**
     * Find notifications for a specific travel plan and type
     */
    List<Notification> findByTravelPlanIdAndNotificationTypeOrderByCreatedAtDesc(
            Long travelPlanId, Notification.NotificationType notificationType);

    /**
     * Find notifications sent to a specific user for a specific travel plan
     */
    List<Notification> findByRecipientUserIdAndTravelPlanIdOrderByCreatedAtDesc(
            Long recipientUserId, Long travelPlanId);
}
