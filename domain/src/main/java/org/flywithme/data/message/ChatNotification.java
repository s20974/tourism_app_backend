package org.flywithme.data.message;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ChatNotification {
    private Long id;
    private String senderId;
}
