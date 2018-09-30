package com.computeams.models;

import lombok.Data;

import javax.persistence.*;
import java.sql.Blob;

@Entity
@Table(name = "resources", uniqueConstraints = @UniqueConstraint(columnNames = "id"))
@Data
public class Resource {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    private String name;

    @Lob
    private Blob content;
    private String contentType;
    private String siteUrl;
}
