package com.smooth.redis.dto;

import lombok.*;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 *
 * @author Cheng Yufei
 * @create 2025-10-24 10:16
 **/
@Getter
@Setter
@NoArgsConstructor
@ToString
@Accessors(chain = true)
public class User implements Serializable {


    private static final long serialVersionUID = 4177929567394707863L;


    private Integer id;
    private String username;
    private String type;
}
