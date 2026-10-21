
package cz.project_storage.service;

import com.lowagie.text.*;
import com.lowagie.text.pdf.*;
import cz.project_storage.model.Order;
import cz.project_storage.model.OrderItem;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Service
public class InvoiceService {

    // Údaje dodavatele
    @Value("${invoice.supplier.name:}")
    private String supplierName;

    @Value("${invoice.supplier.address:}")
    private String supplierAddress;

    @Value("${invoice.supplier.company-id:}")
    private String supplierId;

    @Value("${invoice.supplier.vat-id:}")
    private String supplierVatId;

    @Value("${invoice.supplier.email:}")
    private String supplierEmail;

    @Value("${invoice.bank-account:}")
    private String bankAccount;

    @Value("${invoice.payment-method:Not specified}")
    private String paymentMethod;

    @Value("${invoice.due-days:14}")
    private int dueDays;

    private static final DateTimeFormatter DATE =
            DateTimeFormatter.ofPattern("dd.MM.yyyy");

    private static final Font TITLE =
            FontFactory.getFont(FontFactory.HELVETICA_BOLD, 19);

    private static final Font SECTION =
            FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11);

    private static final Font NORMAL =
            FontFactory.getFont(FontFactory.HELVETICA, 10);

    private static final Font SMALL =
            FontFactory.getFont(FontFactory.HELVETICA, 8);



    private String text(String value) {
        return value == null || value.isBlank()
                ? "Not specified"
                : value;
    }

    private String money(BigDecimal value) {
        return value.setScale(2, RoundingMode.HALF_UP)
                .toPlainString() + " CZK";
    }

    private void line(
            Document document,
            String label,
            String value
    ) throws DocumentException {

        document.add(
                new Paragraph(
                        label + ": " + text(value),
                        NORMAL
                )
        );
    }

    private PdfPCell cell(
            String text,
            boolean header,
            int alignment
    ) {

        PdfPCell cell = new PdfPCell(
                new Phrase(
                        text,
                        header ? SECTION : NORMAL
                )
        );

        cell.setPadding(7);
        cell.setHorizontalAlignment(alignment);

        if (header) {
            cell.setBackgroundColor(
                    new java.awt.Color(240, 237, 231)
            );
        }

        return cell;
    }



    public byte[] generateInvoiceBytes(Order order)
            throws IOException {

        ByteArrayOutputStream output =
                new ByteArrayOutputStream();

        writeInvoice(output, order);

        return output.toByteArray();
    }



    public void generateInvoice(
            HttpServletResponse response,
            Order order
    ) throws IOException {

        response.setContentType("application/pdf");

        response.setHeader(
                "Content-Disposition",
                "attachment; filename=\"invoice-"
                        + order.getId() + ".pdf\""
        );

        writeInvoice(
                response.getOutputStream(),
                order
        );
    }



    private void writeInvoice(
            OutputStream output,
            Order order
    ) throws IOException {

        Document document = new Document(
                PageSize.A4,
                40,
                40,
                45,
                45
        );

        try {

            PdfWriter.getInstance(
                    document,
                    output
            );

            document.open();

            // NADPIS
            document.add(
                    new Paragraph(
                            "INVOICE / FAKTURA",
                            TITLE
                    )
            );

            document.add(
                    new Paragraph(
                            "Invoice number: "
                                    + order.getId(),
                            SECTION
                    )
            );

            document.add(new Paragraph(" "));


            LocalDate issued =
                    order.getOrderDate() == null
                            ? LocalDate.now()
                            : order.getOrderDate().toLocalDate();

            line(
                    document,
                    "Issue date",
                    issued.format(DATE)
            );

            line(
                    document,
                    "Order date",
                    order.getOrderDate() == null
                            ? null
                            : order.getOrderDate()
                            .toLocalDate()
                            .format(DATE)
            );

            line(
                    document,
                    "Due date",
                    issued.plusDays(
                            Math.max(0, dueDays)
                    ).format(DATE)
            );

            line(
                    document,
                    "Variable symbol",
                    String.valueOf(order.getId())
            );

            document.add(new Paragraph(" "));



            PdfPTable parties = new PdfPTable(2);

            parties.setWidthPercentage(100);
            parties.setSpacingAfter(16);

            String supplier =
                    "SUPPLIER / DODAVATEL\n"
                            + text(supplierName)
                            + "\n"
                            + text(supplierAddress)
                            + "\nCompany ID (ICO): "
                            + text(supplierId)
                            + "\nVAT ID (DIC): "
                            + text(supplierVatId)
                            + "\nEmail: "
                            + text(supplierEmail);

            String customer =
                    "CUSTOMER / ODBERATEL\n"
                            + text(order.getCustomerName())
                            + "\n"
                            + text(order.getDeliveryAddress())
                            + "\n"
                            + text(order.getDeliveryPostalCode())
                            + " "
                            + text(order.getDeliveryCity())
                            + "\nEmail: "
                            + text(order.getCustomerEmail())
                            + "\nPhone: "
                            + text(order.getCustomerPhone());

            PdfPCell left = new PdfPCell(
                    new Phrase(supplier, NORMAL)
            );

            PdfPCell right = new PdfPCell(
                    new Phrase(customer, NORMAL)
            );

            left.setPadding(10);
            right.setPadding(10);

            parties.addCell(left);
            parties.addCell(right);

            document.add(parties);



            PdfPTable items = new PdfPTable(
                    new float[]{
                            4.4f,
                            1.2f,
                            2.1f,
                            2.3f
                    }
            );

            items.setWidthPercentage(100);
            items.setHeaderRows(1);

            items.addCell(
                    cell(
                            "Product",
                            true,
                            Element.ALIGN_LEFT
                    )
            );

            items.addCell(
                    cell(
                            "Qty",
                            true,
                            Element.ALIGN_RIGHT
                    )
            );

            items.addCell(
                    cell(
                            "Unit price",
                            true,
                            Element.ALIGN_RIGHT
                    )
            );

            items.addCell(
                    cell(
                            "Line total",
                            true,
                            Element.ALIGN_RIGHT
                    )
            );

            BigDecimal calculated =
                    BigDecimal.ZERO;

            if (order.getItems() != null) {

                for (OrderItem item : order.getItems()) {

                    int qty =
                            item.getQuantity() == null
                                    ? 0
                                    : item.getQuantity();

                    BigDecimal unit =
                            BigDecimal.valueOf(
                                    item.getDisplayUnitPrice() == null
                                            ? 0
                                            : item.getDisplayUnitPrice()
                            );

                    BigDecimal lineTotal =
                            unit.multiply(
                                    BigDecimal.valueOf(qty)
                            );

                    calculated =
                            calculated.add(lineTotal);

                    items.addCell(
                            cell(
                                    item.getDisplayCoffeeName(),
                                    false,
                                    Element.ALIGN_LEFT
                            )
                    );

                    items.addCell(
                            cell(
                                    String.valueOf(qty),
                                    false,
                                    Element.ALIGN_RIGHT
                            )
                    );

                    items.addCell(
                            cell(
                                    money(unit),
                                    false,
                                    Element.ALIGN_RIGHT
                            )
                    );

                    items.addCell(
                            cell(
                                    money(lineTotal),
                                    false,
                                    Element.ALIGN_RIGHT
                            )
                    );
                }
            }

            document.add(items);

            document.add(new Paragraph(" "));



            BigDecimal total =
                    order.getTotalPrice() == null
                            ? calculated
                            : BigDecimal.valueOf(
                            order.getTotalPrice()
                    );

            Paragraph totalLine = new Paragraph(
                    "TOTAL TO PAY: " + money(total),
                    TITLE
            );

            totalLine.setAlignment(
                    Element.ALIGN_RIGHT
            );

            document.add(totalLine);

            document.add(new Paragraph(" "));



            line(
                    document,
                    "Payment method",
                    paymentMethod
            );

            line(
                    document,
                    "Bank account",
                    bankAccount
            );

            line(
                    document,
                    "Order reference",
                    String.valueOf(order.getId())
            );

            document.add(new Paragraph(" "));



            document.add(
                    new Paragraph(
                            "Tax/VAT breakdown is not shown: "
                                    + "configure the seller's tax status "
                                    + "and tax data before issuing "
                                    + "a tax invoice.",
                            SMALL
                    )
            );



            if (supplierName.isBlank()
                    || supplierAddress.isBlank()
                    || supplierId.isBlank()) {

                document.add(
                        new Paragraph(
                                "DRAFT - Supplier details are incomplete. "
                                        + "Not ready for official issuance.",
                                SECTION
                        )
                );
            }

        } catch (DocumentException ex) {

            throw new IOException(
                    "Failed to generate invoice PDF",
                    ex
            );

        } finally {

            if (document.isOpen()) {
                document.close();
            }
        }
    }



    public void exportOrder(
            Order order,
            HttpServletResponse response
    ) throws IOException {

        generateInvoice(response, order);
    }
}
