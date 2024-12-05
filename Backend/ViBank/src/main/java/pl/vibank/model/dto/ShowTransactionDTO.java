package pl.vibank.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@Builder
public class ShowTransactionDTO {
    private String transactionRecipientName;
    private String title;
    private String recipientsAccountNumber;
    private String amount;
    private String senderAccountNumber;
    private boolean isProfit;
    private LocalDate date;
}
