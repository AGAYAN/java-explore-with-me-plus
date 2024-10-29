package ru.practicum;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class EndPointHitDto {

  @NotBlank(message = "app should not be blank.")
  @Size(max = 255, message = "app should not exceed max length.")
  private String app;

  @NotBlank(message = "uri should not be blank.")
  @Size(max = 255, message = "uri should not exceed max length.")
  private String uri;

  @NotBlank(message = "ip should not be blank.")
  @Size(max = 39, message = "ip should not exceed max length.")
  private String ip;

  @NotNull(message = "requestTime should not be null.")
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
  @PastOrPresent(message = "requestTime should not be in the future.")
  @JsonProperty("timestamp")
  private LocalDateTime requestTime;
}
