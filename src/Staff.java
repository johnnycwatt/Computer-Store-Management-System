public class Staff {
    private  String username;
    private String password;

    public Staff(String username, String password){
        this.username = username;
        this.password = password;
    }


    //Getters
    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }



    @Override
    public String toString() {
        return "Username: " + username + ", Password: " + password;
    }
}
