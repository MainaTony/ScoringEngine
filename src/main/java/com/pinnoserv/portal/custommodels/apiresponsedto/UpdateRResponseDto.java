package com.pinnoserv.portal.custommodels.apiresponsedto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class UpdateRResponseDto {
    public String ResponseCode;
    public String ResponseMessage;
}
