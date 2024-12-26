package com.fancy.exam_words1;

public class Words {
    private int id;
    private String english;
    private String chinese;
    private int type;  // 0 默认未查询, 1 完全陌生, 2 似曾相识, 3 熟悉

    // 默认构造方法
    public Words() {
    }

    // 带参数的构造方法
    public Words(int id, String english, String chinese, int type) {
        this.id = id;
        this.english = english;
        this.chinese = chinese;
        this.type = type;
    }

    // Getter 和 Setter 方法

    // 获取 id
    public int getId() {
        return id;
    }

    // 设置 id
    public void setId(int id) {
        this.id = id;
    }

    // 获取 english
    public String getEnglish() {
        return english;
    }

    // 设置 english
    public void setEnglish(String english) {
        this.english = english;
    }

    // 获取 chinese
    public String getChinese() {
        return chinese;
    }

    // 设置 chinese
    public void setChinese(String chinese) {
        this.chinese = chinese;
    }

    // 获取 type
    public int getType() {
        return type;
    }

    // 设置 type
    public void setType(int type) {
        this.type = type;
    }

    // toString 方法（可选，用于打印对象时的友好显示）
    @Override
    public String toString() {
        return "Words{" +
                "id=" + id +
                ", english='" + english + '\'' +
                ", chinese='" + chinese + '\'' +
                ", type=" + type +
                '}';
    }
}

