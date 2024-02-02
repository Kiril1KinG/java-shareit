package ru.practicum.shareit.item.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Getter
@Setter
@Entity
@Table(name = "items", schema = "public")
public class ItemEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "pk_item")
    @Column(name = "id")
    private Integer id;

    @Column(name = "name")
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "is_available")
    private Boolean available;

    @Column(name = "owner_id")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id", table = "users")
    private Integer ownerId;

    @Column(name = "request_id")
    private String request;
}
