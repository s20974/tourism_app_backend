package org.flywithme.data.message;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Data
@Builder
@AllArgsConstructor
public class UserChatsDto {

    Long userId;

    String recipientName;

    String recipientSurname;

    String recipientMainPhoto;

    String content;

    Date timestamp;
}
