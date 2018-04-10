package cn.neu.arcty.thunchat.Entity;

import java.io.Serializable;

/**
 * Created by arcty on 16-12-9.
 * 聊天信息基类,包括姓名，内容和时间戳
 */

public class ChatEntity implements Serializable {
    private String name;
    private String word;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }
}
