package com.example.WebSocketServer.services;

import com.example.WebSocketServer.function.Conversion;
import com.example.WebSocketServer.image.CreateImage;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Arrays;

@Data
@NoArgsConstructor
@Service
public class WebSocketService {
    @Autowired
    private Conversion conversion;
    @Autowired
    private CreateImage createImage;

    public void webSocketRequest(byte[] bytes){
        /*********** Retrieve Packet Data Start  *********************/

        //Packet Start
        Integer startOfFrameByte = conversion.twoByteToOneInteger(bytes[0], bytes[1]);
        System.out.println("Received Packet");
        System.out.println("-------------------------------------------------------------");

        //Packet Length
        Integer packetLength = conversion.fourByteToOneInteger(bytes[2],bytes[3],bytes[4],bytes[5]);
        System.out.println("Packet Length: "+packetLength);

        //End of Frame
        Integer endOfFrameByte = conversion.twoByteToOneInteger(bytes[(packetLength*2) - 2], bytes[(packetLength*2) - 1]);
        System.out.println("End of Packet: "+endOfFrameByte);

        //Message ID
        Integer messageId = conversion.fourByteToOneInteger(bytes[6],bytes[7],bytes[8],bytes[9]);
        System.out.println("Message ID: "+messageId);

        //Source ID
        Integer sourceId = conversion.fourByteToOneInteger(bytes[10],bytes[11],bytes[12],bytes[13]);
        System.out.println("Source ID: "+sourceId);

        //Destination ID
        Integer destinationId = conversion.fourByteToOneInteger(bytes[14],bytes[15],bytes[16],bytes[17]);
        System.out.println("Destination ID: "+destinationId);

        //Payload Metadata
        Integer metaData = conversion.eightByteToOneInteger(bytes[18],bytes[19], bytes[20],bytes[21], bytes[22],bytes[23], bytes[24],bytes[25]);
        System.out.println("Payload Metadata: "+metaData);

        byte[] payloadBytes = Arrays.copyOfRange(bytes, 26, bytes.length-2);
        String payloadString = conversion.byteArrayToString(payloadBytes);
        System.out.println(payloadString);

        System.out.println("-------------------------------------------------------------");
        System.out.println();

        /*********** Retrieve Packet Data End  *********************/

        if (messageId == 5) {
            createImage.imageDataBegin(sourceId);
        }
        if (messageId == 7) {
            createImage.imageDataStore(sourceId, payloadBytes);
        }
        //Message id: 9 for image data end Packet
        else if (messageId == 9) {
            try {
                createImage.imageDataEnd(sourceId, metaData);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        else{

        }
    }

}
