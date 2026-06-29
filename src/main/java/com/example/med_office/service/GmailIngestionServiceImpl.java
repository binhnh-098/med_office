package com.example.med_office.service;

import com.example.med_office.dto.CongVanDenCreateRequest;
import com.example.med_office.dto.IntegrationDTOs.ClientConfigInfo;
import com.example.med_office.dto.IntegrationDTOs.IntegrationChannelResponse;
import com.example.med_office.dto.IntegrationDTOs.IntegrationChannelConfigRequest;
import com.example.med_office.dto.IntegrationDTOs.IntegrationSyncLogResponse;
import com.example.med_office.entity.CongVanDen;
import com.example.med_office.entity.IntegrationChannel;
import com.example.med_office.entity.IntegrationSyncLog;
import com.example.med_office.entity.NhaCungCap;
import com.example.med_office.repository.CongVanDenRepository;
import com.example.med_office.repository.IntegrationChannelRepository;
import com.example.med_office.repository.IntegrationSyncLogRepository;
import com.example.med_office.repository.NhaCungCapRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMultipart;
import jakarta.mail.search.FlagTerm;
import jakarta.mail.search.SearchTerm;
import jakarta.mail.search.AndTerm;
import org.eclipse.angus.mail.gimap.GmailRawSearchTerm;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class GmailIngestionServiceImpl implements GmailIngestionService {

    private final IntegrationChannelRepository channelRepository;
    private final IntegrationSyncLogRepository logRepository;
    private final CongVanDenRepository congVanDenRepository;
    private final NhaCungCapRepository nhaCungCapRepository;
    private final CongVanDenService congVanDenService;

    public GmailIngestionServiceImpl(
            IntegrationChannelRepository channelRepository,
            IntegrationSyncLogRepository logRepository,
            CongVanDenRepository congVanDenRepository,
            NhaCungCapRepository nhaCungCapRepository,
            CongVanDenService congVanDenService) {
        this.channelRepository = channelRepository;
        this.logRepository = logRepository;
        this.congVanDenRepository = congVanDenRepository;
        this.nhaCungCapRepository = nhaCungCapRepository;
        this.congVanDenService = congVanDenService;

        // Auto-initialize standard mock channels if they don't exist
        initializeDefaultChannels();
    }

    private void initializeDefaultChannels() {
        // Zalo OA
        if (channelRepository.findByProviderId("zalo_oa").isEmpty()) {
            IntegrationChannel c = new IntegrationChannel();
            c.setProviderId("zalo_oa");
            c.setName("Zalo OA MedOffice");
            c.setStatus("CONNECTED");
            channelRepository.save(c);
        }
        // FB Messenger
        if (channelRepository.findByProviderId("fb_messenger").isEmpty()) {
            IntegrationChannel c = new IntegrationChannel();
            c.setProviderId("fb_messenger");
            c.setName("Fanpage Đa khoa MedOffice");
            c.setStatus("CONNECTED");
            channelRepository.save(c);
        }
        // ZKTeco
        if (channelRepository.findByProviderId("zkteco").isEmpty()) {
            IntegrationChannel c = new IntegrationChannel();
            c.setProviderId("zkteco");
            c.setName("Máy chấm công Cổng chính");
            c.setStatus("CONNECTED");
            channelRepository.save(c);
        }
        // Twilio
        if (channelRepository.findByProviderId("twilio").isEmpty()) {
            IntegrationChannel c = new IntegrationChannel();
            c.setProviderId("twilio");
            c.setName("Twilio Brandname OTP");
            c.setStatus("CONNECTED");
            channelRepository.save(c);
        }
        // Google Drive
        if (channelRepository.findByProviderId("gdrive").isEmpty()) {
            IntegrationChannel c = new IntegrationChannel();
            c.setProviderId("gdrive");
            c.setName("Google Drive Cloud Backup");
            c.setStatus("DISCONNECTED");
            channelRepository.save(c);
        }
        // Gmail
        if (channelRepository.findByProviderId("gmail").isEmpty()) {
            IntegrationChannel c = new IntegrationChannel();
            c.setProviderId("gmail");
            c.setName("Gmail CSKH");
            c.setStatus("DISCONNECTED");
            channelRepository.save(c);
        }
    }

    @Override
    public List<IntegrationChannelResponse> getChannels() {
        List<IntegrationChannel> list = channelRepository.findAll();
        List<IntegrationChannelResponse> resList = new ArrayList<>();
        for (IntegrationChannel channel : list) {
            resList.add(toChannelResponse(channel));
        }
        return resList;
    }

    @Override
    @Transactional
    public IntegrationChannelResponse toggleChannel(String id) {
        IntegrationChannel channel = channelRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Channel not found"));

        if (channel.getStatus().equals("CONNECTED")) {
            channel.setStatus("DISCONNECTED");
        } else {
            // If it's Gmail, verify we have configs before allowing connection
            if (channel.getProviderId().equals("gmail")) {
                if (channel.getEmail() == null || channel.getEmail().isBlank() ||
                    channel.getAppPassword() == null || channel.getAppPassword().isBlank()) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Vui lòng cấu hình Email và Mật khẩu ứng dụng trước khi kết nối.");
                }
                // Try testing the connection
                testIMAPConnection(channel.getEmail(), channel.getAppPassword());
            }
            channel.setStatus("CONNECTED");
        }

        channel = channelRepository.save(channel);

        // Log the action
        createSyncLog(
                channel.getName(),
                "Thay đổi trạng thái kết nối: " + (channel.getStatus().equals("CONNECTED") ? "BẬT" : "TẮT"),
                "SUCCESS",
                200,
                0,
                null
        );

        return toChannelResponse(channel);
    }

    @Override
    @Transactional
    public IntegrationChannelResponse configureChannel(String id, IntegrationChannelConfigRequest request) {
        IntegrationChannel channel = channelRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Channel not found"));

        channel.setName(request.getName());
        if (channel.getProviderId().equals("gmail")) {
            channel.setEmail(request.getEmail().trim());
            // Store password
            if (request.getAppPassword() != null && !request.getAppPassword().isBlank()) {
                channel.setAppPassword(request.getAppPassword().trim());
            }
            channel.setClientId(request.getClientId());
            channel.setClientSecret(request.getClientSecret());
        }

        channel = channelRepository.save(channel);
        return toChannelResponse(channel);
    }

    @Override
    public void syncChannel(String id) {
        IntegrationChannel channel = channelRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Channel not found"));

        if (!channel.getStatus().equals("CONNECTED")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Kênh kết nối chưa hoạt động.");
        }

        if (channel.getProviderId().equals("gmail")) {
            long startTime = System.currentTimeMillis();
            try {
                int count = pullEmails(channel.getEmail(), channel.getAppPassword());
                long duration = System.currentTimeMillis() - startTime;
                createSyncLog(
                        channel.getName(),
                        "Đồng bộ hộp thư: Đã nhận " + count + " email mới.",
                        "SUCCESS",
                        200,
                        (int) duration,
                        null
                );
            } catch (Exception e) {
                long duration = System.currentTimeMillis() - startTime;
                createSyncLog(
                        channel.getName(),
                        "Đồng bộ hộp thư thất bại",
                        "FAILED",
                        500,
                        (int) duration,
                        e.getMessage()
                );
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Đồng bộ Gmail thất bại: " + e.getMessage());
            }
        } else {
            // Mock sync for other services
            long startTime = System.currentTimeMillis();
            createSyncLog(
                    channel.getName(),
                    "Đồng bộ dữ liệu (Mô phỏng)",
                    "SUCCESS",
                    200,
                    150,
                    null
            );
        }
    }

    @Override
    public Page<IntegrationSyncLogResponse> getSyncLogs(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<IntegrationSyncLog> logs = logRepository.findAll(pageable);
        return logs.map(this::toSyncLogResponse);
    }

    @Override
    @Scheduled(fixedDelay = 300) // Runs every 30 seconds
    @Transactional
    public void triggerScheduledSync() {
        Optional<IntegrationChannel> gmailChannelOpt = channelRepository.findByProviderId("gmail");
        if (gmailChannelOpt.isPresent()) {
            IntegrationChannel channel = gmailChannelOpt.get();
            if (channel.getStatus().equals("CONNECTED") && channel.getEmail() != null && channel.getAppPassword() != null) {
                long startTime = System.currentTimeMillis();
                try {
                    int count = pullEmails(channel.getEmail(), channel.getAppPassword());
                    long duration = System.currentTimeMillis() - startTime;
                    createSyncLog(
                            channel.getName(),
                            "Đồng bộ tự động: Nhận " + count + " email mới.",
                            "SUCCESS",
                            200,
                            (int) duration,
                            null
                    );
                } catch (Exception e) {
                    long duration = System.currentTimeMillis() - startTime;
                    createSyncLog(
                            channel.getName(),
                            "Đồng bộ tự động thất bại",
                            "FAILED",
                            500,
                            (int) duration,
                            e.getMessage()
                    );
                }
            }
        }
    }

    private void testIMAPConnection(String email, String appPassword) {
        Properties properties = new Properties();
        properties.put("mail.store.protocol", "gimaps");
        properties.put("mail.gimaps.host", "imap.gmail.com");
        properties.put("mail.gimaps.port", "993");
        properties.put("mail.gimaps.ssl.enable", "true");
        properties.put("mail.gimaps.timeout", "6000");
        properties.put("mail.gimaps.connectiontimeout", "6000");
        properties.put("mail.debug", "true");

        try {
            System.out.println("[DEBUG-TEST] Connecting to Gmail IMAP server for testing: " + email);
            Session emailSession = Session.getInstance(properties);
            emailSession.setDebug(true);
            Store store = emailSession.getStore("gimaps");
            store.connect("imap.gmail.com", email, appPassword);
            System.out.println("[DEBUG-TEST] Connected to Gmail IMAP server successfully!");
            store.close();
        } catch (Exception e) {
            System.err.println("[DEBUG-TEST] Connection test failed: " + e.getMessage());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Kết nối tới Gmail thất bại: Vui lòng kiểm tra email và Mật khẩu ứng dụng. Chi tiết: " + e.getMessage());
        }
    }

    private int pullEmails(String email, String appPassword) throws Exception {
        Properties properties = new Properties();
        properties.put("mail.store.protocol", "gimaps");
        properties.put("mail.gimaps.host", "imap.gmail.com");
        properties.put("mail.gimaps.port", "993");
        properties.put("mail.gimaps.ssl.enable", "true");
        properties.put("mail.gimaps.timeout", "10000");
        properties.put("mail.gimaps.connectiontimeout", "10000");
        properties.put("mail.debug", "true");

        System.out.println("[DEBUG-SYNC] Starting email pull. Connecting to: " + email);
        Session emailSession = Session.getInstance(properties);
        emailSession.setDebug(true);
        Store store = emailSession.getStore("gimaps");
        store.connect("imap.gmail.com", email, appPassword);
        System.out.println("[DEBUG-SYNC] Connected to Gmail IMAP server successfully!");

        System.out.println("[DEBUG-SYNC] Opening INBOX folder...");
        Folder inbox = store.getFolder("INBOX");
        inbox.open(Folder.READ_WRITE);
        System.out.println("[DEBUG-SYNC] INBOX folder opened in READ_WRITE mode successfully!");

        System.out.println("[DEBUG-SYNC] Searching for messages in primary category...");
        SearchTerm combinedTerm = new GmailRawSearchTerm("category:primary");
        Message[] allMessages = inbox.search(combinedTerm);
        System.out.println("[DEBUG-SYNC] Search completed. Found " + allMessages.length + " total primary messages.");

        // Process only the last 30 most recent messages in the primary category to prevent performance issues
        int limit = 30;
        Message[] messages;
        if (allMessages.length > limit) {
            messages = Arrays.copyOfRange(allMessages, allMessages.length - limit, allMessages.length);
        } else {
            messages = allMessages;
        }
        System.out.println("[DEBUG-SYNC] Processing the last " + messages.length + " primary messages.");


        int count = 0;

        // Find or create default provider
        NhaCungCap gmailProvider = nhaCungCapRepository.findAll().stream()
                .filter(ncc -> ncc.getTenNhaCungCap().equalsIgnoreCase("Hộp thư điện tử (Gmail)"))
                .findFirst()
                .orElseGet(() -> {
                    NhaCungCap newNcc = new NhaCungCap();
                    newNcc.setMaNhaCungCap("GMAIL_PROVIDER");
                    newNcc.setTenNhaCungCap("Hộp thư điện tử (Gmail)");
                    newNcc.setTrangThai("ACTIVE");
                    newNcc.setNgayTao(LocalDateTime.now());
                    return nhaCungCapRepository.save(newNcc);
                });

        for (Message message : messages) {
            try {
                String subject = message.getSubject();
                if (subject == null || subject.isBlank()) {
                    subject = "Thư không có tiêu đề";
                }

                // Extract message ID as unique reference
                String messageId = "";
                String[] messageIdHeader = message.getHeader("Message-ID");
                if (messageIdHeader != null && messageIdHeader.length > 0) {
                    messageId = messageIdHeader[0].replaceAll("[<>]", "").trim();
                } else {
                    messageId = UUID.randomUUID().toString();
                }

                String soCongVan = "CV/GMAIL/" + messageId.substring(0, Math.min(messageId.length(), 20)).toUpperCase();

                // Avoid double import if soCongVan already exists
                if (congVanDenRepository.existsBySoCongVanIgnoreCase(soCongVan)) {
                    // Mark as read anyway so we don't process it again
                    message.setFlag(Flags.Flag.SEEN, true);
                    continue;
                }

                InternetAddress fromAddress = (InternetAddress) message.getFrom()[0];
                String senderEmail = fromAddress.getAddress();
                String senderName = fromAddress.getPersonal() != null ? fromAddress.getPersonal() : senderEmail;

                // Extract Body
                String content = getTextFromMessage(message);
                if (content.length() > 2000) {
                    content = content.substring(0, 1997) + "...";
                }

                // Create CongVanDen object
                CongVanDenCreateRequest req = new CongVanDenCreateRequest();
                req.setSoCongVan(soCongVan);
                req.setSoDen("GML-" + System.currentTimeMillis() % 100000);
                req.setTieuDe(subject);
                req.setNoiDungTomTat(content);
                req.setDonViGuiId(gmailProvider.getId());
                req.setNguoiKy(senderName);

                Date sentDate = message.getSentDate();
                LocalDate ngayVanBan = sentDate != null ?
                        sentDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate() : LocalDate.now();
                req.setNgayVanBan(ngayVanBan);
                req.setNgayNhan(LocalDate.now());

                req.setMucDoKhan("THUONG");
                req.setMucDoMat("THUONG");
                req.setNguonNhan("GMAIL");
                req.setDaDoc(false);
                req.setDaXuLy(false);
                req.setTrangThai("cho_phan_luong");
                req.setIsDeleted(false);

                String trichYeu = content;
                req.setTrichYeu(trichYeu);

                congVanDenService.create(req);
                count++;

                // Mark the mail as read (SEEN)
                message.setFlag(Flags.Flag.SEEN, true);
            } catch (Exception e) {
                // If parsing a specific message fails, print stack trace but continue other messages
                e.printStackTrace();
            }
        }

        inbox.close(true);
        store.close();

        return count;
    }

    private String cleanHtmlText(String html) {
        if (html == null) {
            return "";
        }
        // Remove style tags and their contents
        html = html.replaceAll("(?is)<style[^>]*>.*?</style>", "");
        // Remove script tags and their contents
        html = html.replaceAll("(?is)<script[^>]*>.*?</script>", "");
        // Strip remaining HTML tags
        String text = html.replaceAll("<[^>]*>", "");
        // Replace HTML entities
        text = text.replace("&nbsp;", " ")
                   .replace("&amp;", "&")
                   .replace("&lt;", "<")
                   .replace("&gt;", ">")
                   .replace("&quot;", "\"")
                   .replace("&#39;", "'");
        return text.trim();
    }

    private String getTextFromMessage(Message message) throws Exception {
        if (message.isMimeType("text/plain")) {
            return message.getContent().toString();
        } else if (message.isMimeType("text/html")) {
            String html = message.getContent().toString();
            return cleanHtmlText(html);
        } else if (message.isMimeType("multipart/*")) {
            MimeMultipart mimeMultipart = (MimeMultipart) message.getContent();
            return getTextFromMimeMultipart(mimeMultipart);
        }
        return "";
    }

    private String getTextFromMimeMultipart(MimeMultipart mimeMultipart) throws Exception {
        StringBuilder result = new StringBuilder();
        int count = mimeMultipart.getCount();
        for (int i = 0; i < count; i++) {
            BodyPart bodyPart = mimeMultipart.getBodyPart(i);
            if (bodyPart.isMimeType("text/plain")) {
                result.append(bodyPart.getContent());
                break; // prefer plain text
            } else if (bodyPart.isMimeType("text/html")) {
                String html = (String) bodyPart.getContent();
                result.append(cleanHtmlText(html));
            } else if (bodyPart.getContent() instanceof MimeMultipart) {
                result.append(getTextFromMimeMultipart((MimeMultipart) bodyPart.getContent()));
            }
        }
        return result.toString();
    }

    private void createSyncLog(String accountName, String action, String status, int responseCode, int executionTime, String errorMsg) {
        IntegrationSyncLog log = new IntegrationSyncLog();
        log.setAccountName(accountName);
        log.setAction(action);
        log.setStatus(status);
        log.setResponseCode(responseCode);
        log.setExecutionTime(executionTime);
        log.setErrorMessage(errorMsg);
        logRepository.save(log);
    }

    private IntegrationChannelResponse toChannelResponse(IntegrationChannel c) {
        IntegrationChannelResponse r = new IntegrationChannelResponse();
        r.setId(c.getId());
        r.setProviderId(c.getProviderId());
        r.setName(c.getName());
        r.setStatus(c.getStatus());
        r.setEmail(c.getEmail());

        if (c.getProviderId().equals("gmail") || c.getProviderId().equals("fb_messenger") || c.getProviderId().equals("zalo_oa")) {
            r.setWebhookUrl("https://portal.medoffice.vn/api/v1/integration/webhook/" + c.getProviderId() + "/" + c.getId().substring(0, 6));
            r.setWebhookSecret(c.getProviderId() + "_sec_" + c.getId().substring(0, 8));
        } else {
            r.setWebhookUrl("Không sử dụng Webhook");
            r.setWebhookSecret("N/A");
        }

        // Get last sync time from sync logs
        Pageable pageable = PageRequest.of(0, 1, Sort.by("createdAt").descending());
        // Simple mock search since logRepository doesn't query by name directly.
        // Let's retrieve from database
        List<IntegrationSyncLog> recent = logRepository.findAll(pageable).getContent();
        if (!recent.isEmpty() && recent.get(0).getAccountName().equals(c.getName())) {
            r.setLastSync(recent.get(0).getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
        } else {
            r.setLastSync(c.getUpdatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
        }

        ClientConfigInfo conf = new ClientConfigInfo();
        conf.setEmail(c.getEmail());
        conf.setClientId(c.getClientId());
        if (c.getClientSecret() != null && !c.getClientSecret().isBlank()) {
            conf.setClientSecret("••••••••••••••••");
        }
        r.setConfig(conf);

        return r;
    }

    private IntegrationSyncLogResponse toSyncLogResponse(IntegrationSyncLog l) {
        IntegrationSyncLogResponse r = new IntegrationSyncLogResponse();
        r.setId(l.getId());
        r.setAccountName(l.getAccountName());
        r.setAction(l.getAction());
        r.setStatus(l.getStatus());
        r.setResponseCode(l.getResponseCode());
        r.setExecutionTime(l.getExecutionTime());
        r.setErrorMessage(l.getErrorMessage());
        r.setCreatedAt(l.getCreatedAt());
        return r;
    }
}
