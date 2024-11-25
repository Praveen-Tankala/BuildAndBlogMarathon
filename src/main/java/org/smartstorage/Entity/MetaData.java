package org.smartstorage.Entity;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Accessors(chain = true)
public class MetaData {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer logId;

    private Integer size;


    private String description;


    private String uploadDate;

    private Integer status;

}
