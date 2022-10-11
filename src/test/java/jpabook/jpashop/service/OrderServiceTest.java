package jpabook.jpashop.service;

import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Member;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderStatus;
import jpabook.jpashop.domain.item.Book;
import jpabook.jpashop.domain.item.Item;
import jpabook.jpashop.exception.NotEnoughStockException;
import jpabook.jpashop.repository.OrderRepository;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
public class OrderServiceTest {

    @Autowired
    OrderService orderService;
    @Autowired
    OrderRepository orderRepository;
    @Autowired
    EntityManager em;

    @Test
    public void 상품주문() throws Exception {
        //given
        Member member = createMember();
        Book book = createBook();

        //when
        Long orderId = orderService.order(member.getId(), book.getId(), 5);

        //then
        Order order = orderRepository.findOne(orderId);

        Assertions.assertEquals( OrderStatus.ORDER, order.getStatus());
        Assertions.assertEquals( 5, order.getOrderItems().get(0).getCount());
        Assertions.assertEquals( 30000 * 5, order.getTotalPrice());
        Assertions.assertEquals( 10 - 5, book.getStockQuantity());
    }



    @Test
    public void 주문취소() throws Exception {
        //given
        Member member = createMember();
        Book book = createBook();

        int orderCount = 2;
        // 주문 생성
        Long orderId = orderService.order(member.getId(), book.getId(), orderCount);

        //when
        // 주문 취소
        orderService.cancelOrder(orderId);

        //then
        Assertions.assertEquals(OrderStatus.CANCEL, orderRepository.findOne(orderId).getStatus());
        Assertions.assertEquals(10, book.getStockQuantity());
    }
    @Test(expected = NotEnoughStockException.class)
    public void 상품주문_재고수량초과() throws Exception {
        //given
        Member member = createMember();
        Book book = createBook();

        //when
        orderService.order(member.getId(), book.getId(), 15);

        //then
        fail("재고 수량 초과시 예외가 발생해야함.");
    }

    private Member createMember() {
        Member member = new Member();
        member.setName("회원1");
        member.setAddress(new Address("서울", "오금로", "123-123"));
        em.persist(member);
        return member;
    }

    private Book createBook() {
        Book book = new Book();
        book.setName("JPA");
        book.setPrice(30000);
        book.setStockQuantity(10);
        em.persist(book);
        return book;
    }
}