package vn.iotstar.coolenglish.invoice;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType0Font;

/**
 * Xuất {@link InvoiceDocument} ra PDF (Apache PDFBox + font Unicode).
 */
public class InvoicePdfRenderer {

    private static final DateTimeFormatter DT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public void render(InvoiceDocument doc, OutputStream out) throws IOException {
        byte[] fontData = loadFontBytes();
        if (fontData == null || fontData.length < 1_000) {
            throw new IOException(
                    "Khong doc duoc font Unicode (fonts/NotoSans-Regular.ttf). Kiem tra file trong src/main/resources/fonts va build lai WAR.");
        }
        try (PDDocument pdf = new PDDocument();
                ByteArrayInputStream fontStream = new ByteArrayInputStream(fontData)) {
            // embedSubset=false: nhung full font, tranh loi glyph khi subset + tieng Viet
            PDFont font = PDType0Font.load(pdf, fontStream, false);
            PDPage page = new PDPage(PDRectangle.A4);
            pdf.addPage(page);
            float margin = 48;
            float y = page.getMediaBox().getHeight() - margin;
            float width = page.getMediaBox().getWidth() - 2 * margin;
            float fontSize = 9f;
            float lead = 12f;

            try (PDPageContentStream cs = new PDPageContentStream(pdf, page)) {
                y = drawCentered(cs, font, 14f, page.getMediaBox().getWidth() / 2f, y, doc.getHeader().getDocumentTitle());
                y -= lead * 1.5f;
                y = drawCentered(cs, font, fontSize, page.getMediaBox().getWidth() / 2f, y,
                        "Số HĐ: " + doc.getInvoiceNumber());
                y -= lead * 2f;

                InvoiceHeaderInfo h = doc.getHeader();
                y = drawLeftBlock(cs, font, fontSize, lead, margin, y, width, new String[] {
                        "Đơn vị: " + safe(h.getIssuerName()),
                        "Địa chỉ: " + safe(h.getIssuerAddress()),
                        "MST: " + safe(h.getIssuerTaxCode()) + "    Hotline: " + safe(h.getIssuerHotline()),
                        "Ngày lập: " + (h.getIssueDate() != null ? h.getIssueDate().toString() : "—")
                });

                y -= lead;
                cs.setLineWidth(0.5f);
                cs.moveTo(margin, y);
                cs.lineTo(margin + width, y);
                cs.stroke();
                y -= lead * 1.2f;

                BuyerInfo b = doc.getBuyer();
                y = drawLeftBlock(cs, font, fontSize, lead, margin, y, width, new String[] {
                        "NGƯỜI MUA / HỌC VIÊN",
                        "Họ tên: " + safe(b.getFullName()),
                        "Email: " + safe(b.getEmail()),
                        "Điện thoại: " + safe(b.getPhone()),
                        "Mã HV: " + safe(b.getStudentCode())
                });

                y -= lead;
                ClassEnrollmentSection c = doc.getClassSection();
                y = drawLeftBlock(cs, font, fontSize, lead, margin, y, width, new String[] {
                        "KHÓA HỌC & LỚP",
                        "Lớp: " + safe(c.getClassName()) + " (" + safe(c.getClassId()) + ")",
                        "Khóa: " + safe(c.getCourseName()) + " - Cấp độ: " + safe(c.getCourseLevel()),
                        "Thời gian lớp: "
                                + (c.getClassStartDate() != null ? c.getClassStartDate().toString() : "—")
                                + " -> "
                                + (c.getClassEndDate() != null ? c.getClassEndDate().toString() : "—"),
                        "Phòng: " + safe(c.getRoomLabel()),
                        "Lịch: " + safe(c.getScheduleSummary())
                });

                y -= lead * 1.2f;
                y = drawLineItemsTable(cs, font, fontSize, margin, y, width, doc.getLineItems());

                InvoiceTotals t = doc.getTotals();
                y -= lead * 1.5f;
                y = drawLeftBlock(cs, font, fontSize, lead, margin, y, width, new String[] {
                        "Tạm tính (trước VAT): " + formatMoney(t.getSubtotalExVat()),
                        "VAT " + (int) t.getVatRatePercent() + "%: " + formatMoney(t.getVatAmount()),
                        "Giảm giá: " + formatMoney(t.getDiscountAmount()),
                        "TỔNG THANH TOÁN: " + formatMoney(t.getGrandTotal())
                });

                PaymentTransactionSection p = doc.getPaymentSection();
                y -= lead;
                y = drawLeftBlock(cs, font, fontSize, lead, margin, y, width, new String[] {
                        "THANH TOÁN",
                        "Phương thức: " + safe(p.getPaymentMethodLabel()),
                        "Mã giao dịch: " + safe(p.getTransactionRef()),
                        "Thời điểm: " + (p.getPaymentTimestamp() != null ? DT.format(p.getPaymentTimestamp()) : "—"),
                        "TT thanh toán: " + safe(p.getPaymentStatusLabel()) + "  |  TT HĐ: "
                                + safe(p.getInvoiceStatusLabel())
                });

                y -= lead;
                for (String note : doc.getFootnotes()) {
                    y -= lead * 0.8f;
                    y = drawWrappedLine(cs, font, 8f, margin, y, width, "* " + note);
                }
            }
            pdf.save(out);
        }
    }

    private static byte[] loadFontBytes() throws IOException {
        byte[] fromResource = readStreamFully(InvoicePdfRenderer.class.getResourceAsStream("/fonts/NotoSans-Regular.ttf"));
        if (fromResource != null && fromResource.length >= 1_000) {
            return fromResource;
        }
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        if (cl != null) {
            byte[] fromCtx = readStreamFully(cl.getResourceAsStream("fonts/NotoSans-Regular.ttf"));
            if (fromCtx != null && fromCtx.length >= 1_000) {
                return fromCtx;
            }
            fromCtx = readStreamFully(cl.getResourceAsStream("/fonts/NotoSans-Regular.ttf"));
            if (fromCtx != null && fromCtx.length >= 1_000) {
                return fromCtx;
            }
        }
        return null;
    }

    private static byte[] readStreamFully(InputStream in) throws IOException {
        if (in == null) {
            return null;
        }
        try (InputStream stream = in) {
            return stream.readAllBytes();
        }
    }

    /**
     * PDFBox + NotoSans-Regular thieu mot so glyph Unicode (vi du U+2192). Thay bang ASCII de tranh loi khi xuat PDF.
     */
    private static String normalizeGlyphsForPdf(String text) {
        if (text == null || text.isEmpty()) {
            return text == null ? "" : text;
        }
        return text.replace("\u2192", "->")
                .replace("\u2190", "<-")
                .replace("\u2194", "<->")
                .replace('\u2014', '-')
                .replace('\u2013', '-');
    }

    private static float drawCentered(PDPageContentStream cs, PDFont font, float size, float cx, float y, String text)
            throws IOException {
        text = normalizeGlyphsForPdf(text);
        float tw = font.getStringWidth(text) / 1000f * size;
        cs.beginText();
        cs.setFont(font, size);
        cs.newLineAtOffset(cx - tw / 2f, y);
        cs.showText(text);
        cs.endText();
        return y - size - 4;
    }

    private static float drawLeftBlock(PDPageContentStream cs, PDFont font, float size, float lead, float x, float y,
            float maxW, String[] lines) throws IOException {
        for (String line : lines) {
            y = drawWrappedLine(cs, font, size, x, y, maxW, line);
            y -= lead * 0.3f;
        }
        return y;
    }

    /**
     * Bảng chi tiết khoản phí: viền, cột, căn lề (mô tả có thể xuống dòng).
     */
    private static float drawLineItemsTable(PDPageContentStream cs, PDFont font, float fontSize, float margin, float y,
            float tableWidth, List<InvoiceLineItem> items) throws IOException {
        final float pad = 5f;
        final float wCode = 52f;
        final float wQty = 34f;
        final float wUnit = 86f;
        final float wTotal = 86f;
        final float wDesc = tableWidth - wCode - wQty - wUnit - wTotal;
        final float x0 = margin;
        final float x1 = x0 + wCode;
        final float x2 = x1 + wDesc;
        final float x3 = x2 + wQty;
        final float x4 = x3 + wUnit;
        final float x5 = x0 + tableWidth;

        final float lineHeight = fontSize + 3f;
        final float headerRowH = fontSize + 2 * pad + 4f;

        List<Float> rowHeights = new ArrayList<>();
        for (InvoiceLineItem item : items) {
            String desc = normalizeGlyphsForPdf(item.getDescription());
            int descLines = wrapToLines(font, fontSize, desc, wDesc - 2 * pad).size();
            float contentH = Math.max(lineHeight, descLines * lineHeight);
            rowHeights.add(contentH + 2 * pad);
        }

        float totalH = headerRowH;
        for (float h : rowHeights) {
            totalH += h;
        }

        float tableTop = y;
        float tableBottom = tableTop - totalH;

        cs.setLineWidth(0.5f);
        cs.addRect(x0, tableBottom, tableWidth, totalH);
        cs.stroke();

        for (float xv : new float[] { x1, x2, x3, x4, x5 }) {
            cs.moveTo(xv, tableBottom);
            cs.lineTo(xv, tableTop);
            cs.stroke();
        }

        float yLine = tableTop;
        cs.moveTo(x0, yLine);
        cs.lineTo(x5, yLine);
        cs.stroke();
        yLine -= headerRowH;
        cs.moveTo(x0, yLine);
        cs.lineTo(x5, yLine);
        cs.stroke();
        for (float h : rowHeights) {
            yLine -= h;
            cs.moveTo(x0, yLine);
            cs.lineTo(x5, yLine);
            cs.stroke();
        }

        float headerBaseline = tableTop - pad - fontSize * 0.85f;
        drawTextLeft(cs, font, fontSize, x0 + pad, headerBaseline, "Mã");
        drawTextLeft(cs, font, fontSize, x1 + pad, headerBaseline, "Mô tả dịch vụ");
        drawTextCenteredInCell(cs, font, fontSize, x2, wQty, headerBaseline, "SL");
        drawTextRightInCell(cs, font, fontSize, x3, wUnit, headerBaseline, "Đơn giá");
        drawTextRightInCell(cs, font, fontSize, x4, wTotal, headerBaseline, "Thành tiền");

        float rowTop = tableTop - headerRowH;
        for (int i = 0; i < items.size(); i++) {
            InvoiceLineItem item = items.get(i);
            float rowH = rowHeights.get(i);
            float baselineTop = rowTop - pad - fontSize * 0.85f;

            drawTextLeft(cs, font, fontSize, x0 + pad, baselineTop, normalizeGlyphsForPdf(item.getLineCode()));

            List<String> descLinesList = wrapToLines(font, fontSize, normalizeGlyphsForPdf(item.getDescription()),
                    wDesc - 2 * pad);
            float descY = baselineTop;
            for (String dl : descLinesList) {
                drawTextLeft(cs, font, fontSize, x1 + pad, descY, dl);
                descY -= lineHeight;
            }

            drawTextCenteredInCell(cs, font, fontSize, x2, wQty, baselineTop, String.valueOf(item.getQuantity()));
            drawTextRightInCell(cs, font, fontSize, x3, wUnit, baselineTop, formatMoney(item.getUnitPrice()));
            drawTextRightInCell(cs, font, fontSize, x4, wTotal, baselineTop, formatMoney(item.getLineTotal()));

            rowTop -= rowH;
        }

        return tableBottom - 10f;
    }

    private static List<String> wrapToLines(PDFont font, float size, String text, float maxW) throws IOException {
        if (text == null || text.isEmpty()) {
            return List.of("");
        }
        List<String> lines = new ArrayList<>();
        StringBuilder cur = new StringBuilder();
        float w = 0;
        for (int i = 0; i < text.length(); i++) {
            char ch = text.charAt(i);
            String s = String.valueOf(ch);
            float cw = font.getStringWidth(s) / 1000f * size;
            if (w + cw > maxW && cur.length() > 0) {
                lines.add(cur.toString());
                cur.setLength(0);
                w = 0;
            }
            cur.append(ch);
            w += cw;
        }
        if (cur.length() > 0) {
            lines.add(cur.toString());
        }
        return lines;
    }

    private static void drawTextLeft(PDPageContentStream cs, PDFont font, float size, float x, float baselineY, String text)
            throws IOException {
        flushLine(cs, font, size, x, baselineY, normalizeGlyphsForPdf(text));
    }

    private static void drawTextCenteredInCell(PDPageContentStream cs, PDFont font, float size, float cellLeft,
            float cellWidth, float baselineY, String text) throws IOException {
        text = normalizeGlyphsForPdf(text);
        float tw = font.getStringWidth(text) / 1000f * size;
        float x = cellLeft + (cellWidth - tw) / 2f;
        flushLine(cs, font, size, x, baselineY, text);
    }

    private static void drawTextRightInCell(PDPageContentStream cs, PDFont font, float size, float cellLeft,
            float cellWidth, float baselineY, String text) throws IOException {
        text = normalizeGlyphsForPdf(text);
        float tw = font.getStringWidth(text) / 1000f * size;
        float x = cellLeft + cellWidth - padRight(cellWidth) - tw;
        flushLine(cs, font, size, x, baselineY, text);
    }

    private static float padRight(float cellWidth) {
        return Math.min(5f, cellWidth * 0.08f);
    }

    /**
     * Ngắt dòng đơn giản theo chiều rộng (ước lượng glyph).
     */
    private static float drawWrappedLine(PDPageContentStream cs, PDFont font, float size, float x, float y, float maxW,
            String text) throws IOException {
        String safeText = normalizeGlyphsForPdf(text == null ? "" : text);
        StringBuilder line = new StringBuilder();
        float lineW = 0;
        for (int i = 0; i < safeText.length(); i++) {
            char ch = safeText.charAt(i);
            String s = String.valueOf(ch);
            float w = font.getStringWidth(s) / 1000f * size;
            if (lineW + w > maxW && line.length() > 0) {
                flushLine(cs, font, size, x, y, line.toString());
                y -= size + 2;
                line.setLength(0);
                lineW = 0;
            }
            line.append(ch);
            lineW += w;
        }
        if (line.length() > 0) {
            flushLine(cs, font, size, x, y, line.toString());
            y -= size + 2;
        }
        return y;
    }

    private static void flushLine(PDPageContentStream cs, PDFont font, float size, float x, float y, String text)
            throws IOException {
        cs.beginText();
        cs.setFont(font, size);
        cs.newLineAtOffset(x, y);
        cs.showText(text);
        cs.endText();
    }

    private static String safe(String s) {
        return s == null ? "—" : s;
    }

    private static String formatMoney(double v) {
        return String.format(java.util.Locale.forLanguageTag("vi-VN"), "%,.0f d", v);
    }
}
