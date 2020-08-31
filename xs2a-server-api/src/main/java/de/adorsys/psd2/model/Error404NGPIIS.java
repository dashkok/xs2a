package de.adorsys.psd2.model;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import de.adorsys.psd2.model.TppMessage404PIIS;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.springframework.validation.annotation.Validated;
import javax.validation.Valid;
import javax.validation.constraints.*;

/**
 * NextGenPSD2 specific definition of reporting error information in case of a HTTP error code 404.
 */
@ApiModel(description = "NextGenPSD2 specific definition of reporting error information in case of a HTTP error code 404. ")
@Validated
@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.SpringCodegen", date = "2020-08-31T12:29:06.642536+03:00[Europe/Kiev]")

public class Error404NGPIIS   {
  @JsonProperty("tppMessages")
  @Valid
  private List<TppMessage404PIIS> tppMessages = null;

  @JsonProperty("_links")
  private Map _links = null;

  public Error404NGPIIS tppMessages(List<TppMessage404PIIS> tppMessages) {
    this.tppMessages = tppMessages;
    return this;
  }

  public Error404NGPIIS addTppMessagesItem(TppMessage404PIIS tppMessagesItem) {
    if (this.tppMessages == null) {
      this.tppMessages = new ArrayList<>();
    }
    this.tppMessages.add(tppMessagesItem);
    return this;
  }

  /**
   * Get tppMessages
   * @return tppMessages
  **/
  @ApiModelProperty(value = "")

  @Valid


  @JsonProperty("tppMessages")
  public List<TppMessage404PIIS> getTppMessages() {
    return tppMessages;
  }

  public void setTppMessages(List<TppMessage404PIIS> tppMessages) {
    this.tppMessages = tppMessages;
  }

  public Error404NGPIIS _links(Map _links) {
    this._links = _links;
    return this;
  }

  /**
   * Get _links
   * @return _links
  **/
  @ApiModelProperty(value = "")

  @Valid


  @JsonProperty("_links")
  public Map getLinks() {
    return _links;
  }

  public void setLinks(Map _links) {
    this._links = _links;
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
}    Error404NGPIIS error404NGPIIS = (Error404NGPIIS) o;
    return Objects.equals(this.tppMessages, error404NGPIIS.tppMessages) &&
    Objects.equals(this._links, error404NGPIIS._links);
  }

  @Override
  public int hashCode() {
    return Objects.hash(tppMessages, _links);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class Error404NGPIIS {\n");

    sb.append("    tppMessages: ").append(toIndentedString(tppMessages)).append("\n");
    sb.append("    _links: ").append(toIndentedString(_links)).append("\n");
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

