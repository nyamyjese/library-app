package com.hei.school.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "library")
public class Library {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false)
    private String name;
    private String address;
    private String phone;

    @OneToMany(mappedBy = "library" , cascade = CascadeType.ALL)
    private List<Book> books = new ArrayList<>();
}
