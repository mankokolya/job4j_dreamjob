package ru.job4j.dreamjob.store;

import org.apache.commons.dbcp2.BasicDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.job4j.dreamjob.model.Candidate;
import ru.job4j.dreamjob.model.Post;

import java.io.BufferedReader;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Properties;

public class PsqlStore implements Store {
    private final Logger logger = LoggerFactory.getLogger(PsqlStore.class);
    private final BasicDataSource pool = new BasicDataSource();

    private PsqlStore() {
        Properties cfg = new Properties();
        try (BufferedReader io = new BufferedReader(new FileReader("db.properties"))) {
            cfg.load(io);
        } catch (Exception e) {
            logger.info("Problems with reading properties file");
            throw new IllegalStateException(e);
        }
        try {
            Class.forName(cfg.getProperty("jdbc.driver"));
        } catch (Exception e) {
            logger.info("Problems with downloading jdbc driver");
            throw new IllegalStateException(e);
        }

        pool.setDriverClassName(cfg.getProperty("jdbc.driver"));
        pool.setUrl(cfg.getProperty("jdbc.url"));
        pool.setUsername(cfg.getProperty("jdbc.username"));
        pool.setPassword(cfg.getProperty("jdbc.password"));
        pool.setMinIdle(5);
        pool.setMaxIdle(10);
        pool.setMaxOpenPreparedStatements(100);
    }

    private static final class Lazy {
        private static final Store INST = new PsqlStore();
    }

    public static Store instOf() {
        return Lazy.INST;
    }

    @Override
    public Collection<Post> findAllPosts() {
        List<Post> posts = new ArrayList<>();
        try (Connection cn = pool.getConnection();
             PreparedStatement ps = cn.prepareStatement("SELECT * FROM post")) {
            try (ResultSet it = ps.executeQuery()) {
                while (it.next()) {
                    posts.add(new Post(it.getInt("id"), it.getString("name")));
                }
            }
        } catch (Exception e) {
            logger.info("Problems with finding all posts", e);
        }
        return posts;
    }

    @Override
    public Collection<Candidate> findAllCandidates() {
        List<Candidate> candidates = new ArrayList<>();
        try (Connection cn = pool.getConnection();
             PreparedStatement ps = cn.prepareStatement("SELECT * FROM candidate")) {
            try (ResultSet it = ps.executeQuery()) {
                while (it.next()) {
                    candidates.add(new Candidate(it.getInt("id"), it.getString("name")));
                }
            }
        } catch (Exception e) {
            logger.info("Problems with finding all candidates", e);
        }
        return candidates;
    }

    @Override
    public void save(Post post) {
        if (post.getId() == 0) {
            create(post);
        } else {
            update(post);
        }
    }

    @Override
    public void save(Candidate candidate) {
        if (candidate.getId() == 0) {
            create(candidate);
        } else {
            update(candidate);
        }

    }

    private void update(Candidate candidate) {
        try (Connection connection = pool.getConnection();
             PreparedStatement ps = connection.prepareStatement("UPDATE candidate SET name = (?) where id=(?)")) {
            ps.setString(1, candidate.getName());
            ps.setInt(2, candidate.getId());
            ps.execute();
        } catch (Exception e) {
            logger.info("Problems with updating candidate", e);
        }
    }

    private Candidate create(Candidate candidate) {
        try (Connection cn = pool.getConnection();
             PreparedStatement ps = cn.prepareStatement("INSERT INTO candidate(name) VALUES (?)",
                     PreparedStatement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, candidate.getName());
            ps.execute();
            try (ResultSet resultSet = ps.getGeneratedKeys()) {
                if (resultSet.next()) {
                    candidate.setId(resultSet.getInt(1));
                }
            }
        } catch (Exception e) {
            logger.info("Problems with creating new candidate", e);
        }
        return candidate;
    }

    private void update(Post post) {
        try (Connection con = pool.getConnection();
             PreparedStatement ps = con.prepareStatement("update post set name = (?) where id=(?)")) {
            ps.setString(1, post.getName());
            ps.setInt(2, post.getId());
            ps.execute();
        } catch (Exception e) {
            logger.info("Problems with updating post", e);
        }
    }

    private Post create(Post post) {
        try (Connection cn = pool.getConnection();
             PreparedStatement ps = cn.prepareStatement(
                     "INSERT INTO post(name) VALUES (?)", PreparedStatement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, post.getName());
            ps.execute();
            try (ResultSet id = ps.getGeneratedKeys()) {
                if (id.next()) {
                    post.setId(id.getInt(1));
                }
            }
        } catch (Exception e) {
            logger.info("Problems with creating new post", e);
        }
        return post;
    }

    @Override
    public Post findPostById(int id) {
        Post post = null;
        try (Connection con = pool.getConnection();
             PreparedStatement ps = con.prepareStatement("select * from post where id=(?)")) {
            ps.setInt(1, id);
            try (ResultSet resultSet = ps.executeQuery()) {
                if (resultSet.next()) {
                    post = new Post(resultSet.getInt(1), resultSet.getString(2));
                }
            }
        } catch (Exception e) {
            logger.info("Problems with finding post by id", e);
        }
        return post;
    }

    @Override
    public Candidate findCandidateById(int id) {
        Candidate candidate = null;
        try (Connection connection = pool.getConnection();
             PreparedStatement ps = connection.prepareStatement("select * from candidate where id=(?)")) {
            ps.setInt(1, id);
            try (ResultSet resultSet = ps.executeQuery()) {
                if (resultSet.next()) {
                    candidate = new Candidate(resultSet.getInt(1), resultSet.getString(2));
                }
            }
        } catch (Exception e) {
            logger.info("Problems with finding candidate by id", e);
        }
        return candidate;
    }
}
