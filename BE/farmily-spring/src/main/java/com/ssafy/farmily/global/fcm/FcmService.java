package com.ssafy.farmily.global.fcm;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.io.IOException;

/**
 * FCM 푸시 알림 서비스
 */
@Slf4j
@Service
public class FcmService {

    @Value("${farmily.fcm.credentials-path}")
    private String credentialsPath;

    /**
     * Firebase 초기화
     */
    @PostConstruct
    public void initializeFirebase() {
        try {
            // classpath: 접두사 제거
            String path = credentialsPath.replace("classpath:", "");
            
            FileInputStream serviceAccount = new FileInputStream(path);

            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .build();

            if (FirebaseApp.getApps().isEmpty()) {
                FirebaseApp.initializeApp(options);
                log.info("Firebase initialized successfully");
            }
        } catch (IOException e) {
            log.error("Failed to initialize Firebase: {}", e.getMessage());
            // 개발 환경에서는 Firebase 없이도 실행 가능하도록 예외를 던지지 않음
        }
    }

    /**
     * FCM 푸시 알림 발송
     *
     * @param fcmToken 사용자 FCM 토큰
     * @param title    알림 제목
     * @param body     알림 내용
     */
    public void sendPushNotification(String fcmToken, String title, String body) {
        if (fcmToken == null || fcmToken.isBlank()) {
            log.warn("FCM token is null or empty, skipping notification");
            return;
        }

        if (FirebaseApp.getApps().isEmpty()) {
            log.warn("Firebase not initialized, skipping notification");
            return;
        }

        try {
            Message message = Message.builder()
                    .setToken(fcmToken)
                    .setNotification(Notification.builder()
                            .setTitle(title)
                            .setBody(body)
                            .build())
                    .build();

            String response = FirebaseMessaging.getInstance().send(message);
            log.info("Successfully sent FCM message: {}", response);
        } catch (Exception e) {
            log.error("Failed to send FCM message: {}", e.getMessage());
        }
    }
}
