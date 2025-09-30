# 📧 Email Setup Guide for Travel Plan Assistant

## 🚀 Free Email Service Options

### 1. Gmail SMTP (Recommended - Completely Free)

#### **Setup Steps:**

1. **Enable 2-Factor Authentication**
   - Go to [Google Account Security](https://myaccount.google.com/security)
   - Enable 2-Step Verification

2. **Generate App Password**
   - Go to [Google Account Security](https://myaccount.google.com/security)
   - Click "App passwords"
   - Select "Mail" and "Other (custom name)"
   - Enter "Travel Plan Assistant" as the name
   - Copy the generated 16-character password

3. **Update application.properties**
   ```properties
   # Replace with your actual Gmail address
   spring.mail.username=your-email@gmail.com
   # Replace with the app password you generated
   spring.mail.password=your-16-character-app-password
   ```

#### **Gmail Limits:**
- **Personal Gmail**: 500 emails/day
- **Google Workspace**: 2,000 emails/day
- **Cost**: Completely free

---

### 2. SendGrid (Free Tier - 100 emails/day)

#### **Setup Steps:**

1. **Create SendGrid Account**
   - Go to [SendGrid](https://sendgrid.com/)
   - Sign up for free account
   - Verify your email

2. **Generate API Key**
   - Go to Settings → API Keys
   - Create API Key with "Mail Send" permissions
   - Copy the API key

3. **Add SendGrid Dependency to pom.xml**
   ```xml
   <dependency>
       <groupId>com.sendgrid</groupId>
       <artifactId>sendgrid-java</artifactId>
       <version>4.9.3</version>
   </dependency>
   ```

4. **Update application.properties**
   ```properties
   # SendGrid Configuration
   sendgrid.api.key=your-sendgrid-api-key
   sendgrid.from.email=your-verified-email@domain.com
   sendgrid.from.name=Travel Plan Assistant
   ```

#### **SendGrid Limits:**
- **Free Tier**: 100 emails/day, 40,000 emails/month
- **Cost**: Free for first 100 emails/day

---

### 3. Mailgun (Free Tier - 5,000 emails/month for 3 months)

#### **Setup Steps:**

1. **Create Mailgun Account**
   - Go to [Mailgun](https://www.mailgun.com/)
   - Sign up for free account
   - Verify your domain or use sandbox domain

2. **Get API Credentials**
   - Go to API Keys section
   - Copy your API key and domain

3. **Add Mailgun Dependency to pom.xml**
   ```xml
   <dependency>
       <groupId>com.mailgun</groupId>
       <artifactId>mailgun-java</artifactId>
       <version>3.0.6</version>
   </dependency>
   ```

4. **Update application.properties**
   ```properties
   # Mailgun Configuration
   mailgun.api.key=your-mailgun-api-key
   mailgun.domain=your-mailgun-domain
   mailgun.from.email=noreply@your-domain.com
   ```

#### **Mailgun Limits:**
- **Free Tier**: 5,000 emails/month for 3 months
- **Cost**: Free for first 3 months

---

## 🔧 Configuration Examples

### Gmail SMTP Configuration (Recommended)
```properties
# Email Configuration (Gmail SMTP - Free)
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=your-email@gmail.com
spring.mail.password=your-app-password
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true
spring.mail.properties.mail.smtp.ssl.trust=smtp.gmail.com

# Notification Configuration
app.notification.email.enabled=true
app.notification.email.from-name=Travel Plan Assistant
```

### Testing Your Configuration
```bash
# Test email endpoint
curl -X POST "http://localhost:8080/api/notifications/test" \
  -d "toEmail=test@example.com"

# Send custom notification
curl -X POST "http://localhost:8080/api/notifications/send" \
  -d "toEmail=user@example.com" \
  -d "subject=Test Subject" \
  -d "content=Test content"
```

---

## 📋 Email Templates Included

The NotificationService includes beautiful HTML email templates for:

1. **Travel Plan Start Reminder** 🚀
   - Sent when a plan starts
   - Includes plan details, dates, members

2. **Travel Plan Completion** 🎉
   - Sent when a plan is completed
   - Congratulatory message with trip summary

3. **Application Status Updates** 📧
   - Sent when applications are accepted/refused
   - Color-coded based on status

4. **Invitation Notifications** 🎯
   - Sent when users are invited to plans
   - Includes plan details and inviter info

5. **Custom Notifications** ✉️
   - For any custom email needs
   - Supports HTML content

---

## 🚨 Troubleshooting

### Common Issues:

1. **"Authentication failed"**
   - Make sure you're using App Password, not your regular Gmail password
   - Ensure 2-Factor Authentication is enabled

2. **"Connection refused"**
   - Check your internet connection
   - Verify Gmail SMTP settings (smtp.gmail.com:587)

3. **"Invalid credentials"**
   - Double-check your email and app password
   - Make sure there are no extra spaces

4. **Emails not received**
   - Check spam folder
   - Verify recipient email address
   - Check Gmail daily sending limits

### Testing Commands:
```bash
# Check if email service is working
curl -X POST "http://localhost:8080/api/notifications/test?toEmail=your-email@gmail.com"

# Check application logs for email errors
tail -f logs/application.log | grep -i mail
```

---

## 💡 Best Practices

1. **Use App Passwords** - Never use your main Gmail password
2. **Monitor Limits** - Keep track of daily email limits
3. **Error Handling** - The service includes comprehensive error handling
4. **HTML Templates** - All emails use beautiful, responsive HTML templates
5. **Logging** - All email operations are logged for debugging

---

## 🎯 Recommendation

**For your project, I recommend Gmail SMTP** because:
- ✅ Completely free
- ✅ High reliability (Google infrastructure)
- ✅ Easy setup
- ✅ 500 emails/day is sufficient for most applications
- ✅ No API key management needed
- ✅ Works immediately after setup

The NotificationService is already configured and ready to use with Gmail SMTP!
