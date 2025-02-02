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
public class JdbcTemplateMenuProductDao implements MenuProductDao {

  private static final String TABLE_NAME = "menu_product";
  private static final String KEY_COLUMN_NAME = "seq";

  private final NamedParameterJdbcTemplate jdbcTemplate;
  private final SimpleJdbcInsert jdbcInsert;

  public JdbcTemplateMenuProductDao(final DataSource dataSource) {
    jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
    jdbcInsert = new SimpleJdbcInsert(dataSource)
        .withTableName(TABLE_NAME)
        .usingGeneratedKeyColumns(KEY_COLUMN_NAME)
    ;
  }

  @Override
  public MenuProductEntity save(final MenuProductEntity entity) {
    final SqlParameterSource parameters = new BeanPropertySqlParameterSource(entity);
    final Number key = jdbcInsert.executeAndReturnKey(parameters);
    return select(key.longValue());
  }

  @Override
  public Optional<MenuProductEntity> findById(final Long id) {
    try {
      return Optional.of(select(id));
    } catch (final EmptyResultDataAccessException e) {
      return Optional.empty();
    }
  }

  @Override
  public List<MenuProductEntity> findAll() {
    final String sql = "SELECT seq, menu_id, product_id, quantity FROM menu_product";
    return jdbcTemplate.query(sql, (resultSet, rowNumber) -> toEntity(resultSet));
  }

  @Override
  public List<MenuProductEntity> findAllByMenuId(final Long menuId) {
    final String sql = "SELECT seq, menu_id, product_id, quantity FROM menu_product WHERE menu_id = (:menuId)";
    final SqlParameterSource parameters = new MapSqlParameterSource()
        .addValue("menuId", menuId);
    return jdbcTemplate.query(sql, parameters, (resultSet, rowNumber) -> toEntity(resultSet));
  }

  private MenuProductEntity select(final Long id) {
    final String sql = "SELECT seq, menu_id, product_id, quantity FROM menu_product WHERE seq = (:seq)";
    final SqlParameterSource parameters = new MapSqlParameterSource()
        .addValue("seq", id);
    return jdbcTemplate.queryForObject(sql, parameters,
        (resultSet, rowNumber) -> toEntity(resultSet));
  }

  private MenuProductEntity toEntity(final ResultSet resultSet) throws SQLException {
    return new MenuProductEntity(
        resultSet.getLong(KEY_COLUMN_NAME),
        resultSet.getLong("menu_id"),
        resultSet.getLong("product_id"),
        resultSet.getLong("quantity")
    );
  }
}
