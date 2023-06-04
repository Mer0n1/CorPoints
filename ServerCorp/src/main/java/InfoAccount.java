public class InfoAccount {
    public final String nickname;
    public final String password;
    public int score;
    public Group group;

    InfoAccount(String nickname, String password, int score) {
        this.nickname = nickname;
        this.score = score;
        this.password = password;
        group = null;
    }
}
