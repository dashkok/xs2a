package de.adorsys.psd2.model;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import de.adorsys.psd2.model.AccountReference;
import de.adorsys.psd2.model.PaymentInitiationBulkElementJson;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import org.springframework.validation.annotation.Validated;
import javax.validation.Valid;
import javax.validation.constraints.*;

/**
 * Generic Body for a bulk payment initation via JSON.  paymentInformationId is contained in code but commented since it is n.a.  and not all ASPSP are able to support this field now. In a later version the field will be mandatory.
 */
@ApiModel(description = "Generic Body for a bulk payment initation via JSON.  paymentInformationId is contained in code but commented since it is n.a.  and not all ASPSP are able to support this field now. In a later version the field will be mandatory. ")
@Validated
@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.SpringCodegen", date = "2020-08-31T12:29:06.642536+03:00[Europe/Kiev]")

public class BulkPaymentInitiationJson   {
  @JsonProperty("batchBookingPreferred")
  private Boolean batchBookingPreferred = null;

  @JsonProperty("debtorAccount")
  private AccountReference debtorAccount = null;

  @JsonProperty("requestedExecutionDate")
  private LocalDate requestedExecutionDate = null;

  @JsonProperty("requestedExecutionTime")
  private OffsetDateTime requestedExecutionTime = null;

  @JsonProperty("payments")
  @Valid
  private List<PaymentInitiationBulkElementJson> payments = new ArrayList<>();

  public BulkPaymentInitiationJson batchBookingPreferred(Boolean batchBookingPreferred) {
    this.batchBookingPreferred = batchBookingPreferred;
    return this;
  }

  /**
   * Get batchBookingPreferred
   * @return batchBookingPreferred
  **/
  @ApiModelProperty(value = "")



  @JsonProperty("batchBookingPreferred")
  public Boolean getBatchBookingPreferred() {
    return batchBookingPreferred;
  }

  public void setBatchBookingPreferred(Boolean batchBookingPreferred) {
    this.batchBookingPreferred = batchBookingPreferred;
  }

  public BulkPaymentInitiationJson debtorAccount(AccountReference debtorAccount) {
    this.debtorAccount = debtorAccount;
    return this;
  }

  /**
   * Get debtorAccount
   * @return debtorAccount
  **/
  @ApiModelProperty(required = true, value = "")
  @NotNull

  @Valid


  @JsonProperty("debtorAccount")
  public AccountReference getDebtorAccount() {
    return debtorAccount;
  }

  public void setDebtorAccount(AccountReference debtorAccount) {
    this.debtorAccount = debtorAccount;
  }

  public BulkPaymentInitiationJson requestedExecutionDate(LocalDate requestedExecutionDate) {
    this.requestedExecutionDate = requestedExecutionDate;
    return this;
  }

  /**
   * Get requestedExecutionDate
   * @return requestedExecutionDate
  **/
  @ApiModelProperty(value = "")

  @Valid


  @JsonProperty("requestedExecutionDate")
  public LocalDate getRequestedExecutionDate() {
    return requestedExecutionDate;
  }

  public void setRequestedExecutionDate(LocalDate requestedExecutionDate) {
    this.requestedExecutionDate = requestedExecutionDate;
  }

  public BulkPaymentInitiationJson requestedExecutionTime(OffsetDateTime requestedExecutionTime) {
    this.requestedExecutionTime = requestedExecutionTime;
    return this;
  }

  /**
   * Get requestedExecutionTime
   * @return requestedExecutionTime
  **/
  @ApiModelProperty(value = "")

  @Valid


  @JsonProperty("requestedExecutionTime")
  public OffsetDateTime getRequestedExecutionTime() {
    return requestedExecutionTime;
  }

  public void setRequestedExecutionTime(OffsetDateTime requestedExecutionTime) {
    this.requestedExecutionTime = requestedExecutionTime;
  }

  public BulkPaymentInitiationJson payments(List<PaymentInitiationBulkElementJson> payments) {
    this.payments = payments;
    return this;
  }

  public BulkPaymentInitiationJson addPaymentsItem(PaymentInitiationBulkElementJson paymentsItem) {
    this.payments.add(paymentsItem);
    return this;
  }

  /**
   * A list of generic JSON bodies payment initations for bulk payments via JSON.  Note: Some fields from single payments do not occcur in a bulk payment element
   * @return payments
  **/
  @ApiModelProperty(required = true, value = "A list of generic JSON bodies payment initations for bulk payments via JSON.  Note: Some fields from single payments do not occcur in a bulk payment element ")
  @NotNull

  @Valid


  @JsonProperty("payments")
  public List<PaymentInitiationBulkElementJson> getPayments() {
    return payments;
  }

  public void setPayments(List<PaymentInitiationBulkElementJson> payments) {
    this.payments = payments;
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
}    BulkPaymentInitiationJson bulkPaymentInitiationJson = (BulkPaymentInitiationJson) o;
    return Objects.equals(this.batchBookingPreferred, bulkPaymentInitiationJson.batchBookingPreferred) &&
    Objects.equals(this.debtorAccount, bulkPaymentInitiationJson.debtorAccount) &&
    Objects.equals(this.requestedExecutionDate, bulkPaymentInitiationJson.requestedExecutionDate) &&
    Objects.equals(this.requestedExecutionTime, bulkPaymentInitiationJson.requestedExecutionTime) &&
    Objects.equals(this.payments, bulkPaymentInitiationJson.payments);
  }

  @Override
  public int hashCode() {
    return Objects.hash(batchBookingPreferred, debtorAccount, requestedExecutionDate, requestedExecutionTime, payments);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class BulkPaymentInitiationJson {\n");

    sb.append("    batchBookingPreferred: ").append(toIndentedString(batchBookingPreferred)).append("\n");
    sb.append("    debtorAccount: ").append(toIndentedString(debtorAccount)).append("\n");
    sb.append("    requestedExecutionDate: ").append(toIndentedString(requestedExecutionDate)).append("\n");
    sb.append("    requestedExecutionTime: ").append(toIndentedString(requestedExecutionTime)).append("\n");
    sb.append("    payments: ").append(toIndentedString(payments)).append("\n");
    sb.append("}");
    return sb.toString();
  }

  /**
   * Convert the given object to string with each line indented by 4 spaces
   * (except the first line).
   */
  private String toIndentedString(Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }
}

