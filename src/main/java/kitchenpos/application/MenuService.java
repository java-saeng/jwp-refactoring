package kitchenpos.application;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import kitchenpos.dao.MenuDao;
import kitchenpos.dao.MenuGroupDao;
import kitchenpos.dao.MenuProductDao;
import kitchenpos.dao.ProductDao;
import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuProduct;
import kitchenpos.domain.Product;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MenuService {

  private final MenuDao menuDao;
  private final MenuGroupDao menuGroupDao;
  private final MenuProductDao menuProductDao;
  private final ProductDao productDao;

  public MenuService(
      final MenuDao menuDao,
      final MenuGroupDao menuGroupDao,
      final MenuProductDao menuProductDao,
      final ProductDao productDao
  ) {
    this.menuDao = menuDao;
    this.menuGroupDao = menuGroupDao;
    this.menuProductDao = menuProductDao;
    this.productDao = productDao;
  }

  @Transactional
  public Menu create(final Menu menu) {
    final BigDecimal price = menu.getPrice();

    validatePrice(price);

    if (!menuGroupDao.existsById(menu.getMenuGroupId())) {
      throw new IllegalArgumentException();
    }

    final List<MenuProduct> menuProducts = menu.getMenuProducts();

    BigDecimal sum = sumMenuProducts(menuProducts);

    validatePriceGreaterThanSum(price, sum);

    final Menu savedMenu = menuDao.save(menu);

    saveMenuProducts(menuProducts, savedMenu);

    return savedMenu;
  }

  private static void validatePrice(final BigDecimal price) {
    if (Objects.isNull(price) || price.compareTo(BigDecimal.ZERO) < 0) {
      throw new IllegalArgumentException();
    }
  }

  private static void validatePriceGreaterThanSum(final BigDecimal price, final BigDecimal sum) {
    if (price.compareTo(sum) > 0) {
      throw new IllegalArgumentException();
    }
  }

  private void saveMenuProducts(
      final List<MenuProduct> menuProducts,
      final Menu savedMenu
  ) {
    final List<MenuProduct> savedMenuProducts = new ArrayList<>();

    for (final MenuProduct menuProduct : menuProducts) {
      menuProduct.setMenuId(savedMenu.getId());
      savedMenuProducts.add(menuProductDao.save(menuProduct));
    }

    savedMenu.setMenuProducts(savedMenuProducts);
  }

  private BigDecimal sumMenuProducts(final List<MenuProduct> menuProducts) {
    BigDecimal sum = BigDecimal.ZERO;

    for (final MenuProduct menuProduct : menuProducts) {
      final Product product = productDao.findById(menuProduct.getProductId())
          .orElseThrow(IllegalArgumentException::new);
      sum = sum.add(product.getPrice().multiply(BigDecimal.valueOf(menuProduct.getQuantity())));
    }

    return sum;
  }

  public List<Menu> list() {
    final List<Menu> menus = menuDao.findAll();

    for (final Menu menu : menus) {
      menu.setMenuProducts(menuProductDao.findAllByMenuId(menu.getId()));
    }

    return menus;
  }
}
