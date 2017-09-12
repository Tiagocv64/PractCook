package com.example.tcver.practcook;

public class DatabaseClasses {

    // Isto é um pouco estranho mas tem que ser assim
    // VER: https://firebase.google.com/docs/database/android/read-and-write

    static class User {
        private String bio;
        private String email;
        private String foto;
        private Object friends;
        private String id;
        private int level;
        private String name;
        private Object receitas;

        public User() {
        }

        // Isto é assim simplesmente porque tem que ser. Copia
        public User(String bio, String email, String foto, Object friends, String id, int level, String name, Object receitas) {
            this.bio = bio;
            this.email = email;
            this.foto = foto;
            this.friends = friends;
            this.id = id;
            this.level = level;
            this.name = name;
            this.receitas = receitas;

        }

        public String getBio(){
            return bio;
        }

        public String getEmail() {
            return email;
        }

        public String getFoto() {
            return foto;
        }

        public Object getFriends() {
            return friends;
        }

        public String getID() {
            return id;
        }

        public int getLevel() {
            return level;
        }

        public String getName() {
            return name;
        }

        public Object getReceitas() {
            return receitas;
        }
    }
}
