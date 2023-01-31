package jpabook.jpashop.domain;


import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Table(name = "ORDERS") //'실제 DB의 테이블명은 ORDERS'이기 때문.
                      //왜 '테이블명을 ORDER'로 안 했냐면, '자바 예약어 Order'가 이미 존재하기 때문에,
                      //따라서, 아래 'Order 객체'를 그 '실제 DB의 테이블 ORDERS'와 매핑시켜줘야 한다!
@Entity
public class Order {


    @Id
    @GeneratedValue
    @Column(name = "ORDER_ID") //'테이블 ORDER의 PK 컬럼명'은 'ORDER_ID'이기 떄문에,
                               //여기 자바 객체에서도 반드시 '컬럼 id'가 아니라, '컬럼 ORDER_ID'와 매핑시켜야함!
                               //대소문자는 상관없음. "member_id"로 해도 상관없음.
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY) //'@ManyToOne', '@OneToOne'은 기본설정이 '즉시로딩 EAGER'이므로,
                                       //반드시 '지연로딩 LAZY'로 설정 바꿔줘야 한다!
    @JoinColumn(name = "MEMBER_ID") //'주인이 아닌 테이블 MEMBER의 PK 컬럼인 MEMBER_ID'
                                    //= '주인 테이블(현재 테이블) ORDERS의 FK 컬럼인 MEMBER_ID'
    private Member member; //'클래스 Member의 필드 id'를 '참조한 필드'


    //'주인 객체'는 '저쪽 객체인 OrderItem 객체'임
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    //'주인이 아닌 객체(1. 여기 'Order 객체')'가 '주인 객체(N. 저기 'OrderItem 객체')'를
    //'종속시켜 관리하기' 때문에, '주인이 아닌 객체(여기 'Order 객체')'의 내부 필드에
    //'cascade'를 작성하는 것이다!
    private List<OrderItem> orderItems = new ArrayList<>();


    //'주인 객체'는 '현재 객체인 Order 객체'임
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    //- '@ManyToOne', '@OneToOne'은 기본설정이 '즉시로딩 EAGER'이므로, 반드시 '지연로딩 LAZY'로 설정 바꿔줘야 한다!
    //- 'cascade = CascadeType.PERSIST':
    //'1:1 양방향 매핑'에서는 꼭 반드시 '주인이 아닌 객체('Delivery 객체')의 내부 필드'에 'cascade'를 쓰는 것은 아니고,
    //(여기서는 'Order 객체 = 주인 객체', 'Delivery 객체 = 주인이 아닌 객체')
    //'더 상위 객체(하위 객체를 종속시켜 관리하는 객체)'에 Cascade를 붙이는 것이다!
    //여기 'Order 객체와 Delivery 객체 간의 관계에서,
    //'주문 Order'가 발생해야, 그에 이어져서 '배송 Delivery'라는 사건이 발생하므로,
    //'Order 객체'가 'Delivery 객체'를 종속하고 있고, 그에 따라, 'Order 객체 내부의 필드'에 'Cascade를 붙은 것'이다!
    @JoinColumn(name = "DELIEVERY_ID") //'주인이 아닌 테이블 DELIEVERY의 PK 컬럼인 DELIVERY_ID'
                                       //= '주인 테이블(현재 테이블) ORDERS의 FK 컬럼인 DELIVERY_ID'
    private Delivery delivery; //'클래스 Delivery의 필드 id'를 '참조한 필드'

    private LocalDateTime orderDate; //

    @Enumerated(EnumType.STRING)
    private OrderStatus status;


//=============================================================================================================


    //[ '엔티티 설계시 주의점'강. 22:50~ ]. 더 확인하기!
    //< 연관관계 편의 메소드1 > : '양방향 매핑'일 때 사용하는 것!
    //'주인인 Order 객체(N)'와 '주인이 아닌 Member 객체(1)' 연관관계에서 '주인인 Order 객체'의 입장
    public void changeMember(Member m){

        //여기 'if문 로직'은 '들어온 매개변수 m'과는 무관함
        if(this.member != null){
                this.member.getOrders().remove(this);
        }
        this.member = m;

        m.getOrders().add(this);

    }


//=============================================================================================================


//    //< 연관관계 편의 메소드2 >
//    //'Order 객체(1)'와 'OrderItem 객체(N)' 연관관계에서 '주인이 아닌 Order 객체'의 입장
//    public void addOrderItem(OrderItem oi){
//
//        orderItems.add(oi); //
//        oi.setOrder(this); //'OrderItem 객체의 속성'으로 'Order 객체 this..이거 더 확인하기!'를 넣어줌.
//    }


//=============================================================================================================


//    //< 연관관계 편의 메소드3 >
//    //'주인인 Order 객체(1)'와 '주인이 아닌 Delivery 객체(1)' 연관관계에서 '주인인 Order 객체'의 입장
//    public void changeDelivery(Delivery d){
//
//        if(this.delivery != null){
//            this.delivery.getOrder().remove(this);
//        }
//        this.delivery = d;
//
//        d.getOrder().add(this);
//
//    }


//=============================================================================================================


    //[ '주문, 주문상품 엔티티 개발'강. 02:10~ ]
    //< 주문 생성 메소드 >
    //'주문된 상품 을 생성하려면', '주문한 회원 정보 Member', '주문된 상품의 배송 정보 Delivery',
    //'주문한 상품들 OrderItem...'의 정보가 모두 필요하다!
    public static Order createOrder(Member member, Delivery delivery, OrderItem... orderItems){ //'... 문법'!

        Order order = new Order(); //'엔티티 Order'를 사용하기 위해, 여기서 'Order 객체를 생성해줌'.
        order.setMember(member); //'현재 Order 객체에 (주문한)특정 회원 정보(속성)을 추가함'.
        order.setDelivery(delivery); //'현재 Order 객체에 (주문된 상품의)배송 정보(속성)을 추가함'.







        return null;
    }




//=============================================================================================================


//    //< 연관관계 편의 메소드2 >
//    //'Order 객체(1)'와 'OrderItem 객체(N)' 연관관계에서 '주인이 아닌 Order 객체'의 입장
//    public void addOrderItem(OrderItems oi){
//
//        if(oderItems != null){
//            this.orderItems.getOrder
//        }
//
//
//
//    }





//
//    //< 연관관계 편의 메소드2 >
//    public void addOrderItem(OrderItem oi){
//
//        orderItems.add(oi); //
//        oi.setOrder(this); //'OrderItem 객체의 속성'으로 'Order 객체 this..이거 더 확인하기!'를 넣어줌.
//    }
//
//
//    //< 연관관계 편의 메소드3 >
//    public void setDelivery(Delivery d){
//
//        this.delivery = d;
//        d.setOrder(this);
//    }





}

