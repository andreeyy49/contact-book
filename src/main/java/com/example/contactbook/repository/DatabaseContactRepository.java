package com.example.contactbook.repository;

import com.example.contactbook.Contact;
import com.example.contactbook.exception.ContactNotFoundException;
import com.example.contactbook.repository.mapper.ContactRowMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.jdbc.core.ArgumentPreparedStatementSetter;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
@Slf4j
public class DatabaseContactRepository implements ContactRepository {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<Contact> findAll() {
        log.debug("Calling DatabaseContactRepository->findAll");

        String sql = "SELECT * FROM contact";

        return jdbcTemplate.query(sql, new ContactRowMapper());
    }

    @Override
    public Optional<Contact> findById(Long id) {
        log.debug("Calling DatabaseContactRepository->findById with ID: {}", id);

        String sql = "SELECT * FROM contact WHERE id = ?";

        Contact contact = DataAccessUtils.singleResult(
                jdbcTemplate.query(
                        sql,
                        new ArgumentPreparedStatementSetter(new Object[]{id}),
                        new ContactRowMapper()
                )
        );

        return Optional.ofNullable(contact);
    }

    @Override
    public Contact save(Contact contact) {
        log.debug("Calling DatabaseContactRepository->save with contact: {}", contact);

        contact.setId(System.currentTimeMillis());
        String sql = "INSERT INTO contact (firstName, lastName, email, phone, id) values(?, ?, ?, ?, ?)";
        jdbcTemplate.update(
                sql,
                contact.getFirstName(),
                contact.getLastName(),
                contact.getEmail(),
                contact.getPhone(),
                contact.getId()
        );

        return contact;
    }

    @Override
    public Contact update(Contact contact) {
        log.debug("Calling DatabaseContactRepository->update with contact: {}", contact);

        Contact existContact = findById(contact.getId()).orElse(null);

        if (existContact != null) {
            String sql = "UPDATE contact SET firstName = ?, lastName = ?, email = ?, phone = ? WHERE id = ?";
            jdbcTemplate.update(
                    sql,
                    contact.getFirstName(),
                    contact.getLastName(),
                    contact.getEmail(),
                    contact.getPhone(),
                    contact.getId()
            );

            return contact;
        }

        log.warn("Contact with ID: {} not found!", contact.getId());
        return null;
    }

    @Override
    public void deleteById(Long id) {
        log.debug("Calling DatabaseContactRepository->delete with ID: {}", id);

        String sql = "DELETE FROM contact WHERE id = ?";
        jdbcTemplate.update(sql, id);
    }

    @Override
    public void batchInsert(List<Contact> contacts) {
        log.debug("Calling DatabaseTaskRepository->batchInsert");

        String sql = "INSERT INTO contact (firstName, lastName, email, phone, id) values(?, ?, ?, ?, ?)";

        jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                Contact contact = contacts.get(i);
                ps.setString(1, contact.getFirstName());
                ps.setString(2, contact.getLastName());
                ps.setString(3, contact.getEmail());
                ps.setString(4, contact.getPhone());
                ps.setLong(5, contact.getId());
            }

            @Override
            public int getBatchSize() {
                return contacts.size();
            }
        });
    }
}
