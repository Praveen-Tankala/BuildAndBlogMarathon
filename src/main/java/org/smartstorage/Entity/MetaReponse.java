package org.smartstorage.Entity;

import com.google.cloud.storage.Blob;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;


@AllArgsConstructor
@NoArgsConstructor
@Data
@Accessors(chain = true)
public class MetaReponse {


    private Integer logId;

    private Long size;

    private String name;

    private String description;

    private String uploadDate;

    private Integer status;

    private String link;

    private Blob blob;
}
