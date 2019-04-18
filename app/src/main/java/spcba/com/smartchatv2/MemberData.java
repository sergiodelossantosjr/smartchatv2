package spcba.com.smartchatv2;

/**
 * Created by serge.delossantos on 11/25/2018.
 */

class MemberData {
    private String name;
    private String color;
    private String url;

    public MemberData(String name, String color, String url) {
        this.name = name;
        this.color = color;
        this.url = url;
    }

    public MemberData() {
    }

    public String getName() {
        return name;
    }

    public String getColor() {
        return color;
    }

    public String getAvatar(){ return url; }

    @Override
    public String toString() {
        return "MemberData{" +
                "name='" + name + '\'' +
                ", color='" + color + '\'' +
                ", url='" + url + '\'' +
                '}';
    }
}
