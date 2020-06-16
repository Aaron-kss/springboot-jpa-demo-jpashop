package jpabook.jpashop.domain;

import jpabook.jpashop.domain.enums.DeliveryStatus;
import jpabook.jpashop.domain.enums.OrderStatus;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static javax.persistence.CascadeType.*;
import static javax.persistence.FetchType.*;

@Entity
@Table(name = "orders")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Order {

    @Id @GeneratedValue
    @Column(name = "order_id")
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @OneToMany(mappedBy = "order", cascade = ALL)
    @Builder.Default
    private List<OrderItem> orderItems = new ArrayList<>();

    @OneToOne(fetch = LAZY, cascade = ALL)
    @JoinColumn(name = "delivery_id")
    private Delivery delivery;

    private LocalDateTime orderDate;

    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus;

    //==연관 관계 메서드==
    public void setMember(Member member){
        this.member = member;
        member.getOrders().add(this);
    }

    public void addOrderItem(OrderItem orderItem){
        orderItem.setOrder(this);
        this.orderItems.add(orderItem);
    }

    public void setDelivery(Delivery delivery){
        this.delivery = delivery;
        delivery.setOrder(this);
    }

    //==생성 메서드==//

    /**
     * 주문생성
     * @param member
     * @param delivery
     * @param orderItems
     * @return
     */
    public static Order createOrder(Member member, Delivery delivery, OrderItem... orderItems){

        Order order = Order.builder()
                .orderStatus(OrderStatus.ORDER)
                .orderDate(LocalDateTime.now())
                .build();

        order.setMember(member);
        order.setDelivery(delivery);

        for(OrderItem orderItem: orderItems) {
            order.addOrderItem(orderItem);
        }

        return order;
    }

    /**
     * 주문 취소
     */
    public void cancel(){
        if(delivery.getDeliveryStatus() == DeliveryStatus.COMP){
            throw new IllegalStateException("이미 배송 완료된 상품은 취소가 불가능 합니다.");
        }
        this.orderStatus = OrderStatus.CANCEL;
        this.getOrderItems().forEach(OrderItem::cancel);
    }

    //==조회 로직==//
    /**
     * 전제 주문 가격 조회
     */

    public int getTotalPrice(){
        return this.orderItems.stream().mapToInt(OrderItem::getTotalPrice).sum();
    }
}
