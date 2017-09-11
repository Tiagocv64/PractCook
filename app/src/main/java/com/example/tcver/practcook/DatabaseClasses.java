package com.example.tcver.practcook;

public class DatabaseClasses {

    // Isto é um pouco estranho mas tem que ser assim
// VER: https://firebase.google.com/docs/database/android/read-and-write

    static class User {
        private String Bio;
        private String Email;
        private String Foto;
        private Object Friends;
        private String ID;
        private int Level;
        private String Name;
        private Object Receitas;

        public User() {
        }

        // Isto é assim simplesmente porque tem que ser. Copia
        public User(String Bio, String Email, String Foto, Object Friends, String ID, int Level, String Name, Object Receitas) {
            this.Bio = Bio;
            this.Email = Email;
            this.Foto = Foto;
            this.Friends = Friends;
            this.ID = ID;
            this.Level = Level;
            this.Name = Name;
            this.Receitas = Receitas;

        }

        public String getBio(){
            return Bio;
        }

        public String getEmail() {
            return Email;
        }

        public String getFoto() {
            return Foto;
        }

        public Object getFriends() {
            return Friends;
        }

        public String getID() {
            return ID;
        }

        public int getLevel() {
            return Level;
        }

        public String getName() {
            return Name;
        }

        public Object getReceitas() {
            return Receitas;
        }
    }
}
