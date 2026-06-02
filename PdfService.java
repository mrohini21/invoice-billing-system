package com.company.invoice.service;

import com.company.invoice.model.*;
import com.company.invoice.repository.InvoiceItemRepository;
import com.company.invoice.repository.PaymentRepository;

import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.io.source.ByteArrayOutputStream;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.*;
import com.itextpdf.layout.*;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.*;
import com.itextpdf.layout.properties.*;

import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class PdfService {

    private final InvoiceItemRepository itemRepository;
    private final PaymentRepository paymentRepository;

    public PdfService(InvoiceItemRepository itemRepository,
                      PaymentRepository paymentRepository) {
        this.itemRepository = itemRepository;
        this.paymentRepository = paymentRepository;
    }

    public byte[] generateInvoicePdf(Invoice invoice) {

        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            PdfWriter writer = new PdfWriter(out);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);

            PdfFont bold = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
            PdfFont normal = PdfFontFactory.createFont(StandardFonts.HELVETICA);

            // ================= HEADER =================
            Paragraph title = new Paragraph("INVOICE")
                    .setFont(bold)
                    .setFontSize(22)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setFontColor(ColorConstants.BLUE);

            document.add(title);
            document.add(new Paragraph("\n"));

            // ================= CLIENT INFO BOX =================
            Table clientTable = new Table(2).useAllAvailableWidth();

            clientTable.addCell(getCell("Client Name:", bold));
            clientTable.addCell(getCell(invoice.getClient().getName(), normal));

            clientTable.addCell(getCell("Email:", bold));
            clientTable.addCell(getCell(invoice.getClient().getEmail(), normal));

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

            clientTable.addCell(getCell("Issue Date:", bold));
            clientTable.addCell(getCell(invoice.getIssueDate().format(formatter), normal));

            clientTable.setBorder(new SolidBorder(ColorConstants.GRAY, 1));

            document.add(clientTable);
            document.add(new Paragraph("\n"));

            // ================= ITEMS TABLE =================
            Paragraph itemHeader = new Paragraph("ITEM DETAILS")
                    .setFont(bold)
                    .setFontSize(14)
                    .setUnderline();

            document.add(itemHeader);

            Table itemTable = new Table(4).useAllAvailableWidth();

            String[] headers = {"Item", "Qty", "Price", "Total"};

            for (String h : headers) {
                itemTable.addHeaderCell(new Cell()
                        .add(new Paragraph(h).setFont(bold))
                        .setBackgroundColor(ColorConstants.LIGHT_GRAY));
            }

            List<InvoiceItem> items =
                    itemRepository.findByInvoiceId(invoice.getId());

            for (InvoiceItem item : items) {
                itemTable.addCell(item.getItemName());
                itemTable.addCell(String.valueOf(item.getQuantity()));
                itemTable.addCell(item.getPrice().toString());
                itemTable.addCell(item.getTotal().toString());
            }

            document.add(itemTable);
            document.add(new Paragraph("\n"));

            // ================= TOTAL SECTION =================
            Paragraph total = new Paragraph("TOTAL: ₹" + invoice.getTotalAmount())
                    .setFont(bold)
                    .setFontSize(14)
                    .setTextAlignment(TextAlignment.RIGHT)
                    .setFontColor(ColorConstants.BLACK);

            Paragraph paid = new Paragraph("Paid: ₹" + invoice.getPaidAmount())
                    .setTextAlignment(TextAlignment.RIGHT);

            Paragraph balance = new Paragraph("Balance: ₹" + invoice.getBalanceAmount())
                    .setTextAlignment(TextAlignment.RIGHT);

            document.add(total);
            document.add(paid);
            document.add(balance);

            document.add(new Paragraph("\n"));

            // ================= PAYMENT HISTORY =================
            Paragraph payHeader = new Paragraph("PAYMENT HISTORY")
                    .setFont(bold)
                    .setFontSize(14)
                    .setUnderline();

            document.add(payHeader);

            List<Payment> payments =
                    paymentRepository.findByInvoiceId(invoice.getId());

            if (payments.isEmpty()) {
                document.add(new Paragraph("No payments found."));
            } else {

                Table paymentTable = new Table(3).useAllAvailableWidth();

                paymentTable.addHeaderCell(headerCell("Date", bold));
                paymentTable.addHeaderCell(headerCell("Amount", bold));
                paymentTable.addHeaderCell(headerCell("Method", bold));

                for (Payment p : payments) {
                    paymentTable.addCell(p.getPaymentDate() != null
                            ? p.getPaymentDate().toString()
                            : "-");

                    paymentTable.addCell(p.getAmount().toString());
                    paymentTable.addCell(p.getPaymentMethod().toString());
                }

                document.add(paymentTable);
            }

            document.close();
            return out.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("PDF generation failed", e);
        }
    }

    // ================= HELPER METHODS =================

    private Cell getCell(String text, PdfFont font) {
        return new Cell()
                .add(new Paragraph(text).setFont(font))
                .setBorder(Border.NO_BORDER)
                .setPadding(5);
    }

    private Cell headerCell(String text, PdfFont font) {
        return new Cell()
                .add(new Paragraph(text).setFont(font))
                .setBackgroundColor(ColorConstants.LIGHT_GRAY)
                .setPadding(5);
    }
}