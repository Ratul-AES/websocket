package com.example.WebSocketServer.function;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.List;

@Data
@AllArgsConstructor
@Component
public class Conversion {
    public Integer byteToDecimal(byte hex){
        String s = String.valueOf(hex);
        return Integer.parseInt(s,16);
    }

    public byte twoByteToOneByte(byte one, byte two){
        return (byte) ((one << 4) | two);
    }

    public Integer twoByteToOneInteger(byte one, byte two){
        return ((one << 4) | two);
    }

    public byte fourByteToOneByte(byte one, byte two, byte three, byte four){
        byte a = (byte)((one << 4) | two);
        byte b = (byte)((three << 4) | four);
        return (byte) ((a << 4) | b);
    }

    public Integer fourByteToOneInteger(byte one, byte two, byte three, byte four){
        return (one << 12)| (two << 8) | (three << 4) | four;
    }
    public Integer eightByteToOneInteger(byte one, byte two, byte three, byte four, byte five, byte six, byte seven, byte eight){
        return (one << 28)| (two << 24) | (three << 20) | (four << 16) | (five << 12) | (six << 8) | (seven << 4) | eight;
    }

    public String byteArrayToString(byte[] bytes){
        String byteString = new String();
        for(int i = 0; i < bytes.length; i++){
            byteString += Integer.toHexString(bytes[i] & 0xFF).toUpperCase();
        }
        return byteString;
    }
    public byte[] oneByteToTwoByte(int one){
        byte[] bytes = new byte[2];
        bytes[0] = (byte) (one >> 4);
        bytes[1] = (byte) (one & 0xF);

        return bytes;
    }
    public byte[] oneByteToFourByte(int one){
        byte[] bytes = new byte[4];
        bytes[0] = (byte) (one >> 12);
        bytes[1] = (byte) ((one >> 8) & 0xF);
        bytes[2] = (byte) ((one >> 4) & 0xF);
        bytes[3] = (byte) (one & 0xF);

        return bytes;
    }
    public byte[] oneByteToEightByte(int one){
        byte[] bytes = new byte[8];
        bytes[0] = (byte) (one >> 28);
        bytes[1] = (byte) ((one >> 24) & 0xF);
        bytes[2] = (byte) ((one >> 20) & 0xF);
        bytes[3] = (byte) ((one >> 16) & 0xF);
        bytes[4] = (byte) ((one >> 12) & 0xF);
        bytes[5] = (byte) ((one >> 8) & 0xF);
        bytes[6] = (byte) ((one >> 4) & 0xF);
        bytes[7] = (byte) (one & 0xF);

        return bytes;
    }
    public byte[] oneLongToFourByte(Long one){
        byte[] bytes = new byte[4];
        bytes[0] = (byte) (one >> 12);
        bytes[1] = (byte) ((one >> 8) & 0xF);
        bytes[2] = (byte) ((one >> 4) & 0xF);
        bytes[3] = (byte) (one & 0xF);

        return bytes;
    }
    public String byteToImage(byte[] imageBytes, String imageName) throws IOException {
        try {
            ByteArrayInputStream bis = new ByteArrayInputStream(imageBytes);
            BufferedImage bImage2 = ImageIO.read(bis);
            ImageIO.write(bImage2, "jpg", new File(imageName) );

            return "Image create successfully";
        }catch (Exception e){
            return "Image not created, reason - "+e.getMessage();
        }

    }
    /*public byte twoStringOneByte(String a, String b){
        byte c = (byte) Integer.parseInt(a, 16);
        return (byte) ((a << 4) | b);
    }*/
}
