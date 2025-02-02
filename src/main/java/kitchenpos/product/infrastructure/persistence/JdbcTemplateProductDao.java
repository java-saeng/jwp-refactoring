package kitchenpos.product.infrastructure.persistence;

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
public class JdbcTemplateProductDao implements ProductDao {

  private static final String TABLE_NAME = "product";
  private static final String KEY_COLUMN_NAME = "id";

  private final NamedParameterJdbcTemplate jdbcTemplate;
  private final SimpleJdbcInsert jdbcInsert;

  public JdbcTemplateProductDao(final DataSource dataSource) {
    jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
    jdbcInsert = new SimpleJdbcInsert(dataSource)
        .withTableName(TABLE_NAME)
        .usingGeneratedKeyColumns(KEY_COLUMN_NAME)
    ;
  }

  @Override
  public ProductEntity save(final ProductEntity entity) {
    final SqlParameterSource parameters = new BeanPropertySqlParameterSource(entity);
    final Number key = jdbcInsert.executeAndReturnKey(parameters);
    return select(key.longValue());
  }

  @Override
  public Optional<ProductEntity> findById(final Long id) {
    try {
      return Optional.of(select(id));
    } catch (final EmptyResultDataAccessException e) {
      return Optional.empty();
    }
  }

  @Override
  public List<ProductEntity> findAll() {
    final String sql = "SELECT id, name, price FROM product";
    return jdbcTemplate.query(sql, (resultSet, rowNumber) -> toEntity(resultSet));
  }

  private ProductEntity select(final Long id) {
    final String sql = "SELECT id, name, price FROM product WHERE id = (:id)";
    final SqlParameterSource parameters = new MapSqlParameterSource()
        .addValue("id", id);
    return jdbcTemplate.queryForObject(sql, parameters,
        (resultSet, rowNumber) -> toEntity(resultSet));
  }

  private ProductEntity toEntity(final ResultSet resultSet) throws SQLException {
    return new ProductEntity(
        resultSet.getLong(KEY_COLUMN_NAME),
        resultSet.getString("name"),
        resultSet.getBigDecimal("price")
    );
  }
}
