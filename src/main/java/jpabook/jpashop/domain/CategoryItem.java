package jpabook.jpashop.domain;


import jpabook.jpashop.domain.Item.Item;

import javax.persistence.*;

@Entity
public class CategoryItem {

    @Id
    @GeneratedValue
    @Column(name = "CATEGORYITEM_ID")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CATEOGRY_ID")
    private Category category;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ITEM_ID")
    private Item item;


}
