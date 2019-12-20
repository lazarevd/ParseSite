package ru.laz.sender.senders.telegram;

public class TelegramDTO {
    int chat_id;
    String text;

    public TelegramDTO(int chat_id, String text) {
        this.chat_id = chat_id;
        this.text = text;
    }

    public TelegramDTO() {}

    public int getChat_id() {
        return chat_id;
    }

    public void setChat_id(int chat_id) {
        this.chat_id = chat_id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
