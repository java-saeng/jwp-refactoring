package kitchenpos.product.api;

import java.net.URI;
import java.util.List;
import kitchenpos.product.application.ProductService;
import kitchenpos.product.domain.Product;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ProductRestController {

  private final ProductService productService;

  public ProductRestController(final ProductService productService) {
    this.productService = productService;
  }

  @PostMapping("/api/products")
  public ResponseEntity<Product> create(@RequestBody final Product product) {
    final Product created = productService.create(product);
    final URI uri = URI.create("/api/products/" + created.getId());
    return ResponseEntity.created(uri)
        .body(created)
        ;
  }

  @GetMapping("/api/products")
  public ResponseEntity<List<Product>> list() {
    return ResponseEntity.ok()
        .body(productService.list())
        ;
  }
}
