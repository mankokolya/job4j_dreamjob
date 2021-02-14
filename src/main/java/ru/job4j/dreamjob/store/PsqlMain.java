package ru.job4j.dreamjob.store;

import ru.job4j.dreamjob.model.Post;

public class PsqlMain {
    public static void main(String[] args) {
        Store store = PsqlStore.instOf();
        store.save(new Post(0, "Java Job"));
        for (Post post : store.findAllPosts()) {
            System.out.println(post.getId() + " " + post.getName());
        }

        store.save(new Post(1, "Database Admin Job"));
        System.out.println(store.findById(1).getName());
    }
}
