package jpabook.jpashop.repository;

import jpabook.jpashop.domain.enums.OrderStatus;
import lombok.Getter;

@Getter
public class OrderSearch {
    private String memberName;
    private OrderStatus orderStatus;
}
