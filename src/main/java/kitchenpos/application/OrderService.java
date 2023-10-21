package kitchenpos.application;

import java.time.LocalDateTime;
import java.util.List;
import kitchenpos.domain.repository.MenuRepository;
import kitchenpos.domain.Order;
import kitchenpos.domain.OrderLineItems;
import kitchenpos.domain.repository.OrderRepository;
import kitchenpos.domain.OrderStatus;
import kitchenpos.domain.OrderTable;
import kitchenpos.domain.repository.OrderTableRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrderService {

  private final MenuRepository menuRepository;
  private final OrderRepository orderRepository;
  private final OrderTableRepository orderTableRepository;

  public OrderService(
      final MenuRepository menuRepository,
      final OrderRepository orderRepository,
      final OrderTableRepository orderTableRepository
  ) {
    this.menuRepository = menuRepository;
    this.orderRepository = orderRepository;
    this.orderTableRepository = orderTableRepository;
  }

  @Transactional
  public Order create(final Order order) {
    final OrderLineItems orderLineItems = new OrderLineItems(order.getOrderLineItems());
    orderLineItems.validateSameMenuSize(menuRepository.countByIdIn(orderLineItems.getMenuIds()));

    final OrderTable orderTable = orderTableRepository.findById(order.getOrderTable().getId())
        .orElseThrow(IllegalArgumentException::new);

    orderTable.validateEmpty();

    final Order saveOrder = new Order(
        orderTable,
        OrderStatus.COOKING,
        LocalDateTime.now(),
        order.getOrderLineItems()
    );

    return orderRepository.save(saveOrder);
  }

  public List<Order> list() {
    return orderRepository.findAll();
  }

  @Transactional
  public Order changeOrderStatus(final Long orderId, final Order order) {
    final Order savedOrder = orderRepository.findById(orderId)
        .orElseThrow(IllegalArgumentException::new);

    savedOrder.changeStatus(order.getOrderStatus());

    return orderRepository.save(savedOrder);
  }
}
