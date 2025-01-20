package org.example;

import org.example.interfaces.Input;
import org.example.interfaces.Service;
import org.example.model.Clothes;
import org.example.model.Customer;
import org.example.model.Order;
import org.example.services.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class App implements CommandLineRunner {

	@Autowired
	private Input input;

	@Autowired
	private Service<Clothes> clothingService;

	@Autowired
	private Service<Customer> customerService;

	@Autowired
	private OrderService orderService;


	@Override
	public void run(String... args) throws Exception {
		System.out.println("------ Магазин верхней одежды с базой данных ------");
		boolean repeat = true;

		do {
			System.out.println("Список задач: ");
			System.out.println("0. Выход");
			System.out.println("1. Добавить одежду");
			System.out.println("2. Список одежды");
			System.out.println("3. Редактировать одежду");
			System.out.println("4. Удалить одежду");
			System.out.println("5. Добавить клиента");
			System.out.println("6. Список клиентов");
			System.out.println("7. Редактировать клиента");
			System.out.println("8. Удалить клиента");
			System.out.println("9. Оформить заказ");
			System.out.println("10.	Список заказов");
			System.out.println("11. Доход магазина за указанный день, месяц, год");

			System.out.print("Введите номер задачи: ");
			int task = Integer.parseInt(input.getString());

			switch (task) {
				case 0:
					repeat = false;
					break;
				case 1:
					if (clothingService.add()) {
						System.out.println("Одежда добавлена.");
					} else {
						System.out.println("Одежду добавить не удалось.");
					}
					break;
				case 2:
					clothingService.print();
					break;
				case 3:
					System.out.print("Введите ID одежды для редактирования: ");
					Long id = Long.parseLong(input.getString());
					Clothes clothesToEdit = clothingService.findById(id); // Метод для поиска одежды по ID
					if (clothesToEdit != null) {
						if (clothingService.edit(clothesToEdit)) {
							System.out.println("Одежда отредактирована.");
						} else {
							System.out.println("Одежду отредактировать не удалось.");
						}
					} else {
						System.out.println("Одежда с таким ID не найдена.");
					}
					break;

				case 4:
					clothingService.print();
					System.out.print("Введите ID одежды для удаления: ");
					Long idToRemove = Long.parseLong(input.getString());
					if (clothingService.remove(idToRemove)) {
						System.out.println("Одежда успешно удалена.");
					} else {
						System.out.println("Не удалось удалить одежду. Убедитесь, что ID корректен.");
					}
					break;
				case 5:
					if (customerService.add()) {
						System.out.println("Клиент добавлен.");
					} else {
						System.out.println("Клиента добавить не удалось.");
					}
					break;
				case 6:
					customerService.print();
					break;
				case 7:
					System.out.print("Введите ID клиента для редактирования: ");
					Long customerId = Long.parseLong(input.getString());
					Customer customerToEdit = customerService.findById(customerId);
					if (customerToEdit != null) {
						if (customerService.edit(customerToEdit)) {
							System.out.println("Клиент отредактирован.");
						} else {
							System.out.println("Клиента отредактировать не удалось.");
						}
					} else {
						System.out.println("Клиент с таким ID не найден.");
					}
					break;
				case 8:
					customerService.print();
					System.out.print("Введите ID клиента для удаления: ");
					Long customerIdToRemove = Long.parseLong(input.getString());
					if (customerService.remove(customerIdToRemove)) {
						System.out.println("Клиент успешно удален.");
					} else {
						System.out.println("Не удалось удалить клиента. Убедитесь, что ID корректен.");
					}
					break;
				case 9: // Оформление заказа
					customerService.print();
					System.out.print("Введите ID клиента для оформления заказа: ");
					Long customerIdForOrder = Long.parseLong(input.getString());
					Customer customerForOrder = customerService.findById(customerIdForOrder);

					if (customerForOrder != null) {
						clothingService.print();
						System.out.print("Введите ID товара для покупки: ");
						Long clothesId = Long.parseLong(input.getString());


						Order order = new Order();


						if (orderService.placeOrder(customerIdForOrder, order, clothesId)) {
							System.out.println("Заказ успешно оформлен для клиента: " + customerForOrder.getFirstName() + " " + customerForOrder.getLastName());

						} else {
							System.out.println("Ошибка: Заказ оформить не удалось. Проверьте наличие товара и баланс клиента.");
						}
					} else {
						System.out.println("Ошибка: Клиент с таким ID не найден.");
					}
					break;

				case 10:
					System.out.println("Введите 0, чтобы увидеть все заказы, или введите ID клиента, чтобы увидеть его заказы:");
					Long selectedCustomerId = Long.parseLong(input.getString());

					if (selectedCustomerId == 0) {

						orderService.printAllOrders();
					} else {

						orderService.printOrdersByCustomer(selectedCustomerId);
					}
					break;

				case 11:
					System.out.println("Выберите период для вычисления дохода:");
					System.out.println("1. День");
					System.out.println("2. Месяц");
					System.out.println("3. Год");
					System.out.print("Введите номер периода: ");
					int period = Integer.parseInt(input.getString());

					switch (period) {
						case 1:
							System.out.print("Введите дату (yyyy-MM-dd): ");
							String dayInput = input.getString();
							try {
								java.time.LocalDate day = java.time.LocalDate.parse(dayInput);
								double dailyIncome = orderService.calculateIncomeForDay(day);
								System.out.printf("Доход за %s: %.2f\n", day, dailyIncome);
							} catch (Exception e) {
								System.out.println("Ошибка: Неверный формат даты. Используйте yyyy-MM-dd.");
							}
							break;

						case 2:
							System.out.print("Введите год (yyyy): ");
							int year = Integer.parseInt(input.getString());
							System.out.print("Введите месяц (1-12): ");
							int month = Integer.parseInt(input.getString());
							try {
								double monthlyIncome = orderService.calculateIncomeForMonth(year, month);
								System.out.printf("Доход за %d-%02d: %.2f\n", year, month, monthlyIncome);
							} catch (Exception e) {
								System.out.println("Ошибка: Неверные данные. Проверьте год и месяц.");
							}
							break;

						case 3:
							System.out.print("Введите год (yyyy): ");
							int yearForIncome = Integer.parseInt(input.getString());
							try {
								double yearlyIncome = orderService.calculateIncomeForYear(yearForIncome);
								System.out.printf("Доход за %d год: %.2f\n", yearForIncome, yearlyIncome);
							} catch (Exception e) {
								System.out.println("Ошибка: Неверный год.");
							}
							break;

						default:
							System.out.println("Ошибка: Неверный выбор периода.");
					}
					break;








				default:
					System.out.println("Выбрана задача не из списка.");
			}
		} while (repeat);

		System.out.println("До свидания!");
	}

	public static void main(String[] args) {
		SpringApplication.run(App.class, args);
	}
}
