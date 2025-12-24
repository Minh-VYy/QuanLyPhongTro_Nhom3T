package com.example.QuanLyPhongTro_App.ui.shared;

import android.widget.TextView;
import com.example.QuanLyPhongTro_App.data.ChatThread;
import com.example.QuanLyPhongTro_App.data.ChatMessage;
import com.example.QuanLyPhongTro_App.data.MockData;
import java.util.List;

/**
 * Helper class để debug và xem MockData Chat
 * Có thể dùng trong log hoặc UI
 */
public class ChatDebugHelper {

    /**
     * In ra tất cả tenant threads để console
     */
    public static String debugTenantThreads(String tenantEmail) {
        StringBuilder sb = new StringBuilder();
        sb.append("=== Tenant ").append(tenantEmail).append(" Threads ===\n");

        List<ChatThread> threads = MockData.getChatThreadsForTenant(tenantEmail);
        sb.append("Total threads: ").append(threads.size()).append("\n");

        for (ChatThread thread : threads) {
            sb.append("\nThread ID: ").append(thread.getThreadId()).append("\n");
            sb.append("Landlord: ").append(thread.getLandlordName()).append(" (ID: ").append(thread.getLandlordId()).append(")\n");
            sb.append("Messages: ").append(thread.getMessages().size()).append("\n");

            for (ChatMessage msg : thread.getMessages()) {
                sb.append("  - [").append(msg.getSenderName()).append("]: ").append(msg.getContent()).append("\n");
            }
        }

        return sb.toString();
    }

    /**
     * In ra tất cả landlord threads
     */
    public static String debugLandlordThreads(String landlordEmail) {
        StringBuilder sb = new StringBuilder();
        sb.append("=== Landlord ").append(landlordEmail).append(" Threads ===\n");

        List<ChatThread> threads = MockData.getChatThreadsForLandlord(landlordEmail);
        sb.append("Total threads: ").append(threads.size()).append("\n");

        for (ChatThread thread : threads) {
            sb.append("\nThread ID: ").append(thread.getThreadId()).append("\n");
            sb.append("Tenant: ").append(thread.getTenantName()).append(" (ID: ").append(thread.getTenantId()).append(")\n");
            sb.append("Messages: ").append(thread.getMessages().size()).append("\n");

            for (ChatMessage msg : thread.getMessages()) {
                sb.append("  - [").append(msg.getSenderName()).append("]: ").append(msg.getContent()).append("\n");
            }
        }

        return sb.toString();
    }

    /**
     * In chi tiết một thread
     */
    public static String debugThread(String threadId) {
        StringBuilder sb = new StringBuilder();
        ChatThread thread = MockData.getChatThreadById(threadId);

        if (thread == null) {
            return "Thread not found: " + threadId;
        }

        sb.append("=== Thread Details ===\n");
        sb.append("Thread ID: ").append(thread.getThreadId()).append("\n");
        sb.append("Tenant: ").append(thread.getTenantName()).append(" (ID: ").append(thread.getTenantId()).append(")\n");
        sb.append("Landlord: ").append(thread.getLandlordName()).append(" (ID: ").append(thread.getLandlordId()).append(")\n");
        sb.append("Total Messages: ").append(thread.getMessages().size()).append("\n\n");

        for (int i = 0; i < thread.getMessages().size(); i++) {
            ChatMessage msg = thread.getMessages().get(i);
            sb.append("[").append(i + 1).append("] ");
            sb.append(msg.getSenderName()).append(": ").append(msg.getContent()).append("\n");
            sb.append("    From: ").append(msg.isFromLandlord() ? "Landlord" : "Tenant").append("\n");
            sb.append("    Time: ").append(msg.getTimestamp()).append("\n");
            sb.append("    Read: ").append(msg.isRead()).append("\n\n");
        }

        return sb.toString();
    }

    /**
     * Display debug info in TextView (for UI)
     */
    public static void displayTenantDebug(TextView tv, String tenantEmail) {
        if (tv != null) {
            tv.setText(debugTenantThreads(tenantEmail));
        }
    }

    /**
     * Display debug info in TextView (for UI)
     */
    public static void displayLandlordDebug(TextView tv, String landlordEmail) {
        if (tv != null) {
            tv.setText(debugLandlordThreads(landlordEmail));
        }
    }

    /**
     * Display thread details in TextView
     */
    public static void displayThreadDebug(TextView tv, String threadId) {
        if (tv != null) {
            tv.setText(debugThread(threadId));
        }
    }

    /**
     * Test sendChatMessage
     */
    public static String testSendMessage(String threadId, String senderEmail, String content) {
        boolean success = MockData.sendChatMessage(threadId, senderEmail, content);
        if (success) {
            return "✅ Message sent successfully!\n" + debugThread(threadId);
        } else {
            return "❌ Failed to send message";
        }
    }

    /**
     * Log all threads count
     */
    public static String debugAllThreads() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== All Threads in MockData ===\n");

        // Test each account by email keys (use public helper)
        List<String> allEmails = MockData.getAllUserEmails();
        for (String email : allEmails) {
            MockData.User u = MockData.getUserByEmail(email);
            if (u != null && "tenant".equals(u.userType)) {
                List<ChatThread> threads = MockData.getChatThreadsForTenant(email);
                sb.append("Tenant ").append(email).append(": ").append(threads.size()).append(" threads\n");
            }
        }

        for (String email : allEmails) {
            MockData.User u = MockData.getUserByEmail(email);
            if (u != null && "landlord".equals(u.userType)) {
                List<ChatThread> threads = MockData.getChatThreadsForLandlord(email);
                sb.append("Landlord ").append(email).append(": ").append(threads.size()).append(" threads\n");
            }
        }

        return sb.toString();
    }
}
