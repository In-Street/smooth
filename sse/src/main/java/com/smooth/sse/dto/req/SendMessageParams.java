package com.smooth.sse.dto.req;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * <p>
 * 消息
 * </p>
 *
 * @author Shawn Lee
 * @since 2025-09-07
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class SendMessageParams implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 接收人
     */
    @NotNull(message = "接收人不能为空")
    private Long receiverId;

    @NotBlank(message = "消息不能为空")
    private String content;

}
