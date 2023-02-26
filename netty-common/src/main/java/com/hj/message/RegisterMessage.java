package com.hj.message;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegisterMessage extends Message {

    String clientId;

    @Override
    public int getMessageType() {
        return 0;
    }
}
