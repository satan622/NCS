package com.monad.noisecontrolsystem.Model;

/**
 * Created by temp on 2017. 5. 23..
 */

public class ChatData {
    private String message;
    private int room_number;
    private int to_room_number;

    public ChatData() { }

    public ChatData(String message, int room_number, int to_room_number) {
        this.message = message;
        this.room_number = room_number;
        this.to_room_number = to_room_number;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getRoom_number() {
        return room_number;
    }

    public void setRoom_number(int room_number) {
        this.room_number = room_number;
    }

    public int getTo_room_number() {
        return to_room_number;
    }

    public void setTo_room_number(int to_room_number) {
        this.to_room_number = to_room_number;
    }
}
