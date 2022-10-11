package jpabook.jpashop.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter
public class Member {

    @Id @GeneratedValue
    @Column(name="member_id")
    private Long id;

    private String name;

    @Embedded // 내장 타입 선언
    private Address address;

    @OneToMany(mappedBy = "member") // 연관관계에서 상대 member에 의해 mapping된 거울일 뿐이라고 선언
    private List<Order> orders = new ArrayList<>();
}
