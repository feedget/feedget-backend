package kr.co.mashup.feedgetapi.web.dto;

import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;

/**
 * Created by ethan.kim on 2018. 3. 15..
 */
public class DeviceDto {

//    private String deviceId;

    // cloud messaging token - gcm, fcm...
//    private String cloudMsgRegToken;

    // Install ID
//    private String installId;

    // Manufacturer Device Model Name
//    private String modelName;

    // Manufacturer Device Serial Number
//    private String serialNo;

    // OS Version Name
//    private String osVersionCode;

    // Application Version Code
//    private Integer appVersionCode;

    @Data
    public static class UpdateCloudMsgRegId {

        // cloud messaging token - gcm, fcm...
        @NotBlank
        private String cloudMsgRegToken;
    }
}
