package de.adorsys.aspsp.xs2a.spi.domain;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * Created by aro on 27.11.17.
 */

@Data
@ApiModel(description = "Exception")
@JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL)

public class Exception {
	 @ApiModelProperty(value = "transaction status", example = "Rejected")
	 private TransactionStatus transaction_status;
	 @ApiModelProperty(value = "TPP message")
	 private TPPMessageInformation tpp_message;
}
