package com.ghostwriter.message;

import java.util.List;

public interface MessageParser {
    List<Message> parse(String content);
}
