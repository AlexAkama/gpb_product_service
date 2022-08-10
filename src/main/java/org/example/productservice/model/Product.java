package org.example.productservice.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Product extends AbstractEntity {

    private String name;
    private String description;
    private Integer kilocalories;

}
