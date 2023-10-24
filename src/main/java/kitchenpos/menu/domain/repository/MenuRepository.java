package kitchenpos.menu.domain.repository;

import java.util.List;
import java.util.Optional;
import kitchenpos.menu.domain.Menu;

public interface MenuRepository {

  Menu save(Menu menu);

  Optional<Menu> findById(Long id);

  List<Menu> findAll();

  long countByIdIn(List<Long> ids);
}
