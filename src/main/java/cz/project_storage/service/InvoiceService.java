package cz.project_storage.service;

import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfWriter;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Service;
import java.io.IOException;

@Service
public class InvoiceService {

    public void generateInvoice(HttpServletResponse response, String customerName, String coffeeName, int price) throws IOException {
        Document document = new Document(PageSize.A4);
        PdfWriter.getInstance(document, response.getOutputStream());

        document.open();

        Font fontTitle = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
        Font fontText = FontFactory.getFont(FontFactory.HELVETICA, 12);


        Paragraph title = new Paragraph("INVOICE - COFFEE STORAGE", fontTitle);
        title.setAlignment(Paragraph.ALIGN_CENTER);
        document.add(title);

        document.add(new Paragraph(" "));

        document.add(new Paragraph("Customer: " + customerName, fontText));
        document.add(new Paragraph("Product: " + coffeeName, fontText));
        document.add(new Paragraph("Price: " + price + " CZK", fontText));
        document.add(new Paragraph("Date: " + java.time.LocalDate.now(), fontText));

        document.add(new Paragraph(" "));
        document.add(new Paragraph("Thank you for your purchase!", fontText));

        document.close();
    }
}