package jpabook.jpashop.domain;

import jpabook.jpashop.domain.item.Item;
import lombok.*;

import javax.persistence.*;

import static javax.persistence.FetchType.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class OrderItem {

    @Id @GeneratedValue
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "item_id")
    private Item item;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "order_id")
    @Setter
    private Order order;

    private int orderPrice;

    private int count;

    public static OrderItem createOrderItem(Item item, int orderPrice, int count){
        OrderItem orderItem = OrderItem.builder()
                .item(item)
                .orderPrice(orderPrice)
                .count(count)
                .build();

        item.removeStock(count);

        return orderItem;
    }

    public void cancel() {
        this.getItem().addStock(this.count);
    }

    /**
     * 주문 상품 전체 가격 조회
     * @return
     */
    public int getTotalPrice() {
        return this.getItem().getPrice() * this.count;
    }
}