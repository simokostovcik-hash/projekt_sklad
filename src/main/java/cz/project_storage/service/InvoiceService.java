package cz.project_storage.service;

import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import cz.project_storage.model.Order;
import cz.project_storage.model.OrderItem;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Service;
import java.io.IOException;

@Service
public class InvoiceService {

    public void generateInvoice(HttpServletResponse response, Order order) throws IOException {
        Document document = new Document(PageSize.A4);
        PdfWriter.getInstance(document, response.getOutputStream());

        document.open();

        Font fontTitle = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 20);
        Font fontSubtitle = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14);
        Font fontBold = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10);
        Font fontNormal = FontFactory.getFont(FontFactory.HELVETICA, 10);

        document.add(new Paragraph("INVOICE #" + order.getId(), fontTitle));
        document.add(new Paragraph("Date: " + (order.getOrderDate() != null ? order.getOrderDate().toString() : "N/A"), fontNormal));
        document.add(new Paragraph(" "));

        PdfPTable addressTable = new PdfPTable(2);
        addressTable.setWidthPercentage(100);
        addressTable.getDefaultCell().setBorder(Rectangle.NO_BORDER);

        Paragraph customerInfo = new Paragraph();
        customerInfo.add(new Chunk("SHIPPING ADDRESS:\n", fontBold));
        customerInfo.add(new Chunk((order.getFullName() != null ? order.getFullName() : "N/A") + "\n", fontNormal));
        customerInfo.add(new Chunk((order.getAddress() != null ? order.getAddress() : "N/A") + "\n", fontNormal));
        customerInfo.add(new Chunk((order.getZipCode() != null ? order.getZipCode() : "") + " " + (order.getCity() != null ? order.getCity() : "") + "\n", fontNormal));
        customerInfo.add(new Chunk("Email: " + (order.getEmail() != null ? order.getEmail() : "N/A"), fontNormal));
        addressTable.addCell(customerInfo);

        boolean hasCompanyData = order.getCompanyName() != null && !order.getCompanyName().isEmpty();

        if (order.isCompany() || hasCompanyData) {
            Paragraph companyInfo = new Paragraph();
            companyInfo.add(new Chunk("BILLING DETAILS (COMPANY):\n", fontBold));
            companyInfo.add(new Chunk((order.getCompanyName() != null ? order.getCompanyName() : "N/A") + "\n", fontNormal));
            companyInfo.add(new Chunk("IČO: " + (order.getIc() != null ? order.getIc() : "N/A") + "\n", fontNormal));

            if (order.getDic() != null && !order.getDic().isEmpty()) {
                companyInfo.add(new Chunk("DIČ: " + order.getDic(), fontNormal));
            }
            addressTable.addCell(companyInfo);
        } else {
            addressTable.addCell("");
        }

        document.add(addressTable);
        document.add(new Paragraph(" "));
        document.add(new Paragraph("Payment Method: " + (order.getPaymentMethod() != null ? order.getPaymentMethod() : "N/A"), fontNormal));
        document.add(new Paragraph(" "));

        PdfPTable table = new PdfPTable(3);
        table.setWidthPercentage(100);
        table.addCell(new Phrase("Product", fontBold));
        table.addCell(new Phrase("Quantity", fontBold));
        table.addCell(new Phrase("Price total", fontBold));

        if (order.getItems() != null) {
            for (OrderItem item : order.getItems()) {
                table.addCell(new Phrase(item.getCoffee() != null ? item.getCoffee().getName() : "Unknown", fontNormal));
                table.addCell(new Phrase(item.getQuantity() + "x", fontNormal));
                double itemTotal = (item.getCoffee() != null ? item.getCoffee().getPrice() : 0) * item.getQuantity();
                table.addCell(new Phrase(itemTotal + " CZK", fontNormal));
            }
        }

        document.add(table);
        document.add(new Paragraph(" "));

        Paragraph total = new Paragraph("TOTAL PRICE: " + order.getTotalPrice() + " CZK", fontSubtitle);
        total.setAlignment(Paragraph.ALIGN_RIGHT);
        document.add(total);

        document.close();
    }

    public void exportOrder(Order order, HttpServletResponse response) throws IOException {
        generateInvoice(response, order);
    }
}