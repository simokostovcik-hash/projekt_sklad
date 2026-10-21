package cz.project_storage.service;

import cz.project_storage.model.Order;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class OrderEmailService {
    private final JavaMailSender sender;
    @Value("${spring.mail.username:}") private String from;
    public OrderEmailService(JavaMailSender sender) { this.sender = sender; }
    public boolean sendConfirmation(Order order) {
        if (order.getCustomerEmail() == null || order.getCustomerEmail().isBlank()
                || from.isBlank() || from.equals("tuj-email@gmail.com")) return false;
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(from);
        message.setTo(order.getCustomerEmail());
        message.setSubject("Kávový sklad – potvrzení objednávky #" + order.getId());
        message.setText("Dobrý den, " + order.getCustomerName() + ",\n\n"
                + "Vaše objednávka #" + order.getId() + " byla dokončena (TESTOVACÍ PLATBA – bez převodu peněz).\n"
                + "Částka: " + order.getTotalPrice() + " Kč\n\nDěkujeme za objednávku.");
        sender.send(message);
        return true;
    }
}
