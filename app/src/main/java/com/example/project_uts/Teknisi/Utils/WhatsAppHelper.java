package com.example.project_uts.Teknisi.Utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.Toast;
import java.net.URLEncoder;

public class WhatsAppHelper {
    private static final String GROUP_TEKNISI = "628978845390";

    // Untuk minta bantuan ke group teknisi
    public static void requestHelpToTeknisi(Context context,
                                            String komplainId,
                                            String judul,
                                            String pelapor,
                                            String status,
                                            String deskripsi) {
        String message = buildHelpMessage(komplainId, judul, pelapor, status, deskripsi);
        openWhatsApp(context, GROUP_TEKNISI, message);
    }

    // Untuk hubungi customer
    public static void contactCustomer(Context context,
                                       String customerPhone,
                                       String komplainId,
                                       String judul) {
        String message = buildCustomerMessage(komplainId, judul);
        openWhatsApp(context, customerPhone, message);
    }

    // Method utama untuk buka WhatsApp
    private static void openWhatsApp(Context context, String phone, String message) {
        try {
            // Pastikan nomor bersih
            String cleanPhone = cleanPhoneNumber(phone);
            String encodedMessage = URLEncoder.encode(message, "UTF-8");
            String url = "https://wa.me/" + cleanPhone + "?text=" + encodedMessage;

            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(url));
            context.startActivity(intent);

        } catch (Exception e) {
            Toast.makeText(context, "Gagal buka WhatsApp", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    // Bersihkan nomor telepon
    private static String cleanPhoneNumber(String phone) {
        if (phone == null) return "";
        // Hilangkan +, spasi, tanda minus
        return phone.replaceAll("[^0-9]", "");
    }

    // Format pesan untuk minta bantuan
    private static String buildHelpMessage(String id, String judul, String pelapor,
                                           String status, String deskripsi) {
        return "🚨 BUTUH BANTUAN TEKNISI 🚨\n\n" +
                "📋 ID: #" + id + "\n" +
                "📌 Judul: " + judul + "\n" +
                "👤 Pelapor: " + pelapor + "\n" +
                "🔄 Status: " + status + "\n" +
                "📝 Deskripsi: " + deskripsi + "\n\n" +
                "Mohon bantuan rekan teknisi!";
    }

    // Format pesan untuk customer
    private static String buildCustomerMessage(String id, String judul) {
        return "Halo, saya teknisi dari layanan perbaikan.\n\n" +
                "Mengenai komplain Anda:\n" +
                "📋 ID: #" + id + "\n" +
                "📌 Judul: " + judul + "\n\n" +
                "Apakah saya bisa membantu?";
    }
}