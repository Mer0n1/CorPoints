public class InfoAccount {
    public String nickname;
    public int score;
    public Group group;

    InfoAccount(String nickname, int score) {
        this.nickname = nickname;
        this.score = score;
        group = null;

        if (this.nickname == null)
            this.nickname = "null";
    }
    InfoAccount() {
        nickname = "null";
        score = 0;
    }
}
