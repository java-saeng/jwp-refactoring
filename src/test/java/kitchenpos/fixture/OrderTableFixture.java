package kitchenpos.fixture;

import kitchenpos.order_table.domain.OrderTable;

public class OrderTableFixture {

  public static OrderTable createEmptySingleOrderTable() {
    return new OrderTable(
        5,
        true
    );
  }

  public static OrderTable createNotEmptySingleOrderTable() {
    return new OrderTable(
        5,
        false
    );
  }
}
