package kitchenpos.menu.infrastructure.persistence;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import javax.sql.DataSource;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

@Repository
public class JdbcTemplateMenuGroupDao implements MenuGroupDao {

  private static final String TABLE_NAME = "menu_group";
  private static final String KEY_COLUMN_NAME = "id";

  private final NamedParameterJdbcTemplate jdbcTemplate;
  private final SimpleJdbcInsert jdbcInsert;

  public JdbcTemplateMenuGroupDao(final DataSource dataSource) {
    jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
    jdbcInsert = new SimpleJdbcInsert(dataSource)
        .withTableName(TABLE_NAME)
        .usingGeneratedKeyColumns(KEY_COLUMN_NAME)
    ;
  }

  @Override
  public MenuGroupEntity save(final MenuGroupEntity entity) {
    final SqlParameterSource parameters = new BeanPropertySqlParameterSource(entity);
    final Number key = jdbcInsert.executeAndReturnKey(parameters);
    return select(key.longValue());
  }

  @Override
  public Optional<MenuGroupEntity> findById(final Long id) {
    try {
      return Optional.of(select(id));
    } catch (final EmptyResultDataAccessException e) {
      return Optional.empty();
    }
  }

  @Override
  public List<MenuGroupEntity> findAll() {
    final String sql = "SELECT id, name FROM menu_group";
    return jdbcTemplate.query(sql, (resultSet, rowNumber) -> toEntity(resultSet));
  }

  @Override
  public boolean existsById(final Long id) {
    final String sql = "SELECT CASE WHEN COUNT(*) > 0 THEN TRUE ELSE FALSE END FROM menu_group WHERE id = (:id)";
    final SqlParameterSource parameters = new MapSqlParameterSource()
        .addValue("id", id);
    return jdbcTemplate.queryForObject(sql, parameters, Boolean.class);
  }

  private MenuGroupEntity select(final Long id) {
    final String sql = "SELECT id, name FROM menu_group WHERE id = (:id)";
    final SqlParameterSource parameters = new MapSqlParameterSource()
        .addValue("id", id);
    return jdbcTemplate.queryForObject(sql, parameters,
        (resultSet, rowNumber) -> toEntity(resultSet));
  }

  private MenuGroupEntity toEntity(final ResultSet resultSet) throws SQLException {
    final MenuGroupEntity entity = new MenuGroupEntity(
        resultSet.getLong("id"),
        resultSet.getString("name")
    );
    return entity;
  }
}
