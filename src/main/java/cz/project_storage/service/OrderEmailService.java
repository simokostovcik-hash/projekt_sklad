
package cz.project_storage.service;

import cz.project_storage.model.Order;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class OrderEmailService {

    private final JavaMailSender sender;
    private final InvoiceService invoiceService;

    @Value("${spring.mail.username:}")
    private String from;

    public OrderEmailService(
            JavaMailSender sender,
            InvoiceService invoiceService
    ) {
        this.sender = sender;
        this.invoiceService = invoiceService;
    }

    public boolean sendConfirmation(Order order) {

        if (order.getCustomerEmail() == null
                || order.getCustomerEmail().isBlank()) {
            throw new IllegalStateException(
                    "Objednávka nemá e-mail zákazníka."
            );
        }

        if (from == null || from.isBlank()) {
            throw new IllegalStateException(
                    "MAIL_USERNAME není nastavené."
            );
        }

        try {

            byte[] pdf = invoiceService.generateInvoiceBytes(order);

            MimeMessage message = sender.createMimeMessage();

            MimeMessageHelper helper = new MimeMessageHelper(
                    message,
                    true,
                    "UTF-8"
            );

            helper.setFrom(from);
            helper.setTo(order.getCustomerEmail());

            helper.setSubject(
                    "Kávový sklad – potvrzení objednávky #"
                            + order.getId()
            );

            String html = """
                    <!DOCTYPE html>
                    <html lang="cs">
                    <body style="
                        font-family: Arial, sans-serif;
                        background-color: #f7f4ef;
                        padding: 30px;
                    ">

                        <div style="
                            max-width: 600px;
                            margin: auto;
                            background: white;
                            padding: 30px;
                            border-radius: 16px;
                        ">

                            <h1 style="color: #382317;">
                                Kávový sklad
                            </h1>

                            <h2>Objednávka dokončena</h2>

                            <p>Dobrý den, %s,</p>

                            <p>
                                děkujeme za vaši objednávku.
                                Vaše testovací platba
                                byla úspěšně dokončena.
                            </p>

                            <p>
                                <strong>Číslo objednávky:</strong>
                                #%s
                            </p>

                            <p>
                                <strong>Celková částka:</strong>
                                %s Kč
                            </p>

                            <div style="
                                background: #fff3d6;
                                padding: 15px;
                                border-radius: 8px;
                                margin: 20px 0;
                            ">
                                DEMO – žádné skutečné peníze
                                se nepřevádějí.
                            </div>

                            <p>
                                V příloze tohoto e-mailu
                                najdete PDF doklad k objednávce.
                            </p>

                            <p>Děkujeme za objednávku!</p>

                            <p>
                                <strong>Tým Kávový sklad</strong>
                            </p>

                        </div>

                    </body>
                    </html>
                    """.formatted(
                    escapeHtml(order.getCustomerName()),
                    order.getId(),
                    order.getTotalPrice()
            );

            helper.setText(html, true);

            helper.addAttachment(
                    "faktura-" + order.getId() + ".pdf",
                    new ByteArrayResource(pdf),
                    "application/pdf"
            );

            sender.send(message);

            return true;

        } catch (MessagingException | IOException ex) {

            throw new IllegalStateException(
                    "Nepodařilo se připravit e-mail s PDF fakturou.",
                    ex
            );
        }
    }

    private String escapeHtml(String value) {

        if (value == null) {
            return "";
        }

        return value
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }
}
