package de.adorsys.psd2.model;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import de.adorsys.psd2.model.MessageCode405LTB;
import de.adorsys.psd2.model.TppMessageCategory;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.springframework.validation.annotation.Validated;
import javax.validation.Valid;
import javax.validation.constraints.*;

/**
 * TppMessage405LTB
 */
@Validated
@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.SpringCodegen", date = "2020-11-12T17:35:11.808068+02:00[Europe/Kiev]")

public class TppMessage405LTB   {
  @JsonProperty("category")
  private TppMessageCategory category = null;

  @JsonProperty("code")
  private MessageCode405LTB code = null;

  @JsonProperty("path")
  private String path = null;

  @JsonProperty("text")
  private String text = null;

  public TppMessage405LTB category(TppMessageCategory category) {
    this.category = category;
    return this;
  }

  /**
   * Get category
   * @return category
  **/
  @ApiModelProperty(required = true, value = "")
  @NotNull

  @Valid


  @JsonProperty("category")
  public TppMessageCategory getCategory() {
    return category;
  }

  public void setCategory(TppMessageCategory category) {
    this.category = category;
  }

  public TppMessage405LTB code(MessageCode405LTB code) {
    this.code = code;
    return this;
  }

  /**
   * Get code
   * @return code
  **/
  @ApiModelProperty(required = true, value = "")
  @NotNull

  @Valid


  @JsonProperty("code")
  public MessageCode405LTB getCode() {
    return code;
  }

  public void setCode(MessageCode405LTB code) {
    this.code = code;
  }

  public TppMessage405LTB path(String path) {
    this.path = path;
    return this;
  }

  /**
   * Get path
   * @return path
  **/
  @ApiModelProperty(value = "")



  @JsonProperty("path")
  public String getPath() {
    return path;
  }

  public void setPath(String path) {
    this.path = path;
  }

  public TppMessage405LTB text(String text) {
    this.text = text;
    return this;
  }

  /**
   * Get text
   * @return text
  **/
  @ApiModelProperty(value = "")

@Size(max=500)

  @JsonProperty("text")
  public String getText() {
    return text;
  }

  public void setText(String text) {
    this.text = text;
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
}    TppMessage405LTB tppMessage405LTB = (TppMessage405LTB) o;
    return Objects.equals(this.category, tppMessage405LTB.category) &&
    Objects.equals(this.code, tppMessage405LTB.code) &&
    Objects.equals(this.path, tppMessage405LTB.path) &&
    Objects.equals(this.text, tppMessage405LTB.text);
  }

  @Override
  public int hashCode() {
    return Objects.hash(category, code, path, text);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class TppMessage405LTB {\n");

    sb.append("    category: ").append(toIndentedString(category)).append("\n");
    sb.append("    code: ").append(toIndentedString(code)).append("\n");
    sb.append("    path: ").append(toIndentedString(path)).append("\n");
    sb.append("    text: ").append(toIndentedString(text)).append("\n");
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

