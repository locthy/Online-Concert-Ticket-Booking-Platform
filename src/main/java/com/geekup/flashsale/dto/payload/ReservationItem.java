package com.geekup.flashsale.dto.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReservationItem {

    private Long categoryId;

    private Integer quantity;

    private Long concertId;
}