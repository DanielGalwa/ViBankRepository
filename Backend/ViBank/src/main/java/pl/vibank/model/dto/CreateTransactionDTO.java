package pl.vibank.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateTransactionDTO {
    private String transactionRecipientName;
    private String title;
    private String recipientsAccountNumber;
    private String amount;
    private String senderAccountNumber;
}
