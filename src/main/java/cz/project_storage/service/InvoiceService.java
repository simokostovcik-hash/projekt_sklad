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
        Font fontBold = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12);
        Font fontNormal = FontFactory.getFont(FontFactory.HELVETICA, 12);

        document.add(new Paragraph("INVOICE #" + order.getId(), fontTitle));
        document.add(new Paragraph(" "));

        document.add(new Paragraph("Customer: " + order.getUser().getUsername(), fontNormal));
        document.add(new Paragraph("Date: " + (order.getOrderDate() != null ? order.getOrderDate().toString() : "N/A"), fontNormal));
        document.add(new Paragraph(" "));

        PdfPTable table = new PdfPTable(3);
        table.setWidthPercentage(100);
        table.addCell(new Phrase("Product", fontBold));
        table.addCell(new Phrase("Quantity", fontBold));
        table.addCell(new Phrase("Price per unit", fontBold));

        for (OrderItem item : order.getItems()) {
            table.addCell(item.getCoffee() != null ? item.getCoffee().getName() : "Unknown");
            table.addCell(item.getQuantity() + "x");
            table.addCell(item.getCoffee() != null ? item.getCoffee().getPrice() + " CZK" : "0 CZK");
        }

        document.add(table);
        document.add(new Paragraph(" "));


        Paragraph total = new Paragraph("TOTAL PRICE: " + order.getTotalPrice() + " CZK", fontBold);
        total.setAlignment(Paragraph.ALIGN_RIGHT);
        document.add(total);

        document.close();
    }
}