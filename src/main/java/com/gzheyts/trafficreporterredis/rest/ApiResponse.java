package com.gzheyts.trafficreporterredis.rest;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.http.HttpStatus;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "domains",
        "status"
})
@AllArgsConstructor
@Data
public class ApiResponse {
    @JsonIgnore
    private HttpStatus httpStatus;

    @JsonProperty("status")
    private String errorMessage;

    private List<String> domains;

    public static ApiResponse of(HttpStatus httpStatus, String errorMessage) {
        return new ApiResponse(httpStatus, errorMessage, null);
    }

    public static ApiResponse ok() {
        return new ApiResponse(HttpStatus.CREATED, "ok", null);
    }

    public static ApiResponse domains(List<String> domains) {
        return new ApiResponse(HttpStatus.OK, "ok", domains);
    }
}
