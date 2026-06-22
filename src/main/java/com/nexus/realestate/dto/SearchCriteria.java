package com.nexus.realestate.dto;

import com.nexus.realestate.enums.PropertyType;
import com.nexus.realestate.enums.ListingStatus;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class SearchCriteria {
    private BigDecimal budgetMin;
    private BigDecimal budgetMax;
    private String areaName;
    private PropertyType propertyType;
    private BigDecimal sqmMin;
    private Integer roomsMin;
    private ListingStatus listingStatus;
}