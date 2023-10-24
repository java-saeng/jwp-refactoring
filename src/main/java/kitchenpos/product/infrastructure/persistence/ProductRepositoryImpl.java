package kitchenpos.product.infrastructure.persistence;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import kitchenpos.product.domain.Product;
import kitchenpos.product.domain.repository.ProductRepository;
import org.springframework.stereotype.Repository;

@Repository
public class ProductRepositoryImpl implements ProductRepository {

  private final ProductDao productDao;

  public ProductRepositoryImpl(final ProductDao productDao) {
    this.productDao = productDao;
  }

  @Override
  public Product save(final Product product) {

    final ProductEntity entity = productDao.save(
        new ProductEntity(
            product.getName(),
            product.getPrice()
        )
    );

    return ProductMapper.mapToProduct(entity);
  }

  @Override
  public Optional<Product> findById(final Long id) {
    return productDao.findById(id)
        .map(ProductMapper::mapToProduct);
  }

  @Override
  public List<Product> findAll() {
    return productDao.findAll()
        .stream()
        .map(ProductMapper::mapToProduct)
        .collect(Collectors.toList());
  }
}
