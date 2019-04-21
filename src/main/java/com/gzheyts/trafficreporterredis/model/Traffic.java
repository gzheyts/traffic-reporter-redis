package com.gzheyts.trafficreporterredis.model;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;

import java.net.URI;
import java.util.List;

@Data
@Builder
@ToString
public class Traffic {
    List<URI> links;
}
