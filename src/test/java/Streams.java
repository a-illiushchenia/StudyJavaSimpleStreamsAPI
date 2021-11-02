import com.illiushchenia.StudyJavaSimpleStreamsAPI.Department;
import com.illiushchenia.StudyJavaSimpleStreamsAPI.Employee;
import com.illiushchenia.StudyJavaSimpleStreamsAPI.Event;
import com.illiushchenia.StudyJavaSimpleStreamsAPI.Position;
import org.junit.jupiter.api.Test;

import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.*;

public class Streams {
    private List<Employee> emps = List.of(
            new Employee("Michael", "Smith",   243,  43, Position.CHEF),
            new Employee("Jane",    "Smith",   523,  40, Position.MANAGER),
            new Employee("Jury",    "Gagarin", 6423, 26, Position.MANAGER),
            new Employee("Jack",    "London",  5543, 53, Position.WORKER),
            new Employee("Eric",    "Jackson", 2534, 22, Position.WORKER),
            new Employee("Andrew",  "Bosh",    3456, 44, Position.WORKER),
            new Employee("Joe",     "Smith",   723,  30, Position.MANAGER),
            new Employee("Jack",    "Gagarin", 7423, 35, Position.MANAGER),
            new Employee("Jane",    "London",  7543, 42, Position.WORKER),
            new Employee("Mike",    "Jackson", 7534, 31, Position.WORKER),
            new Employee("Jack",    "Bosh",    7456, 54, Position.WORKER),
            new Employee("Mark",    "Smith",   123,  41, Position.MANAGER),
            new Employee("Jane",    "Gagarin", 1423, 28, Position.MANAGER),
            new Employee("Sam",     "London",  1543, 52, Position.WORKER),
            new Employee("Jack",    "Jackson", 1534, 27, Position.WORKER),
            new Employee("Eric",    "Bosh",    1456, 32, Position.WORKER)
    );

    private List<Department> deps = List.of(
            new Department(1, 0, "Head"),
            new Department(2, 1, "West"),
            new Department(3, 1, "East"),
            new Department(4, 2, "Germany"),
            new Department(5, 2, "France"),
            new Department(6, 3, "China"),
            new Department(7, 3, "Japan")
    );

    @Test
    public void creation() throws Exception{
        Stream<String> lines = Files.lines(Paths.get("some.txt"));
        Stream<Path> list = Files.list(Paths.get("./"));
        Stream<Path> walk = Files.walk(Paths.get("./"), 3);

        IntStream intStream = IntStream.of(1,2,3,4);
        DoubleStream doubleStream = DoubleStream.of(1.2, 3.4);
        IntStream range = IntStream.range(10,100);
        IntStream rangeClosed = IntStream.rangeClosed(10,100);

        int[] ints = {1, 2, 3, 4};
        IntStream stream = Arrays.stream(ints);

        Stream<String> stringStream = Stream.of("1", "2", "3");
        Stream<? extends Serializable> stream1 = Stream.of(1, "2", "3");

        Stream<String> build = Stream.<String>builder()
                .add("Mike")
                .add("Joe")
                .build();

        Stream<Employee> stream2 = emps.stream();
        Stream<Employee> employeeStream = emps.parallelStream();

        Stream<Event> generate = Stream.generate(() ->
                new Event(UUID.randomUUID(), LocalDateTime.now(), ""));

        Stream<Integer> iterate = Stream.iterate(1950, val -> val + 3);

        Stream<String> concat = Stream.concat(stringStream, build);
    }

    @Test
    public void terminate(){
        emps.stream().count();

        emps.stream().forEach(employee -> System.out.println(employee.getAge()));

        emps.stream().forEachOrdered(employee -> System.out.println(employee.getAge()));

        emps.stream().collect(Collectors.toList());
        emps.stream().collect(Collectors.toSet());
        emps.stream().toArray();
        Map<Integer, String> collect = emps.stream().collect(Collectors.toMap(
                Employee::getId,
                emp -> String.format("%s %s", emp.getLastName(), emp.getFirstName())));
        IntStream intStream = IntStream.of(100, 200, 300, 400);
        intStream.reduce(((left, right) -> left + right)).orElse(0);

        //Генерация дерева из плоского списка
        System.out.println(deps.stream().reduce(this::reducer));

        IntStream.of(100, 200, 300, 400).average();
        IntStream.of(100, 200, 300, 400).max();
        IntStream.of(100, 200, 300, 400).min();
        IntStream.of(100, 200, 300, 400).sum();
        IntStream.of(100, 200, 300, 400).summaryStatistics();

        emps.stream().max(Comparator.comparingInt(Employee::getAge));

        //в последовательном стриме оба метода вернут первый элемент
        //в параллельном стриме первый метод может вернуть любой из элементов стрима
        emps.stream().findAny();
        emps.stream().findFirst();

        //в коллекции нет ниодного объекта, который соответствует условию
        emps.stream().noneMatch(employee -> employee.getAge() > 60); //true
        //в коллекции присутствует хотя бы один объекта, который соответствует условию
        emps.stream().anyMatch(employee -> employee.getPosition() == Position.CHEF); //true
        //в объекты коллекции соответствуют условию
        emps.stream().allMatch(employee -> employee.getAge() > 18); //true
    }

    @Test
    public void transformation(){
        LongStream longStream = IntStream.of(100, 200, 300, 400).mapToLong(Long::valueOf);
        IntStream.of(100, 200, 300, 400).mapToObj(value ->
                new Event(UUID.randomUUID(), LocalDateTime.of(value, 12, 1, 12, 0), "")
        );

        IntStream.of(100, 200, 300, 400, 100, 200).distinct(); // 100, 200, 300, 400

        emps.stream().filter(employee -> employee.getPosition() != Position.CHEF);

        emps.stream().skip(3);
        emps.stream().limit(5);

        //сортирует элемены по возрасту
        emps.stream().sorted(Comparator.comparingInt(Employee::getAge))
                //преобразует внутренней состояние объекта, при этом не меняет его тип, в данном случае изменяет значение возраста
                .peek(emp -> emp.setAge(18))
                //преобразует элемент на входе в какой-то другой на выходе, в данном случае в строку
                .map(emp -> String.format("%s %s", emp.getLastName(), emp.getFirstName()));

        //возвращает элементы потока пока не будет нарушено условие, в данном случае, покуда в списке не попадется
        //первый элемент с Age <= 30
        emps.stream().takeWhile(employee -> employee.getAge() > 30).forEach(System.out::println);
        System.out.println("");
        //удаляет элементы потока пока не будет нарушено условие, в данном случае, покуда в списке не попадется
        //первый элемент с Age <= 30
        emps.stream().dropWhile(employee -> employee.getAge() > 30).forEach(System.out::println);

        System.out.println("");

        //возвращает большее количество элементов, чем в исходном стриме. В данном случае для каждого элемента исходного
        // стрима возвращается значение на 50 меньше, чем исходный элемент, плюс сам исходный элемент.
        IntStream.of(100, 200, 300, 400)
                .flatMap(value -> IntStream.of(value - 50, value))
                .forEach(System.out::println);
    }

    @Test
    public void real(){
        //вывод сотрудников, которые младше 30, но занимают руководящие должности
        Stream<Employee> empl = emps.stream()
                .filter(employee ->
                        employee.getAge() <= 30 && employee.getPosition() != Position.WORKER
                )
                .sorted(Comparator.comparing(Employee::getLastName));
        print(empl);

        //выбрать первых 4-х сотрудников старше 40 лет и отсортировать их по возрасту
        Stream<Employee> sorted = emps.stream()
                .filter(employee -> employee.getAge() > 40)
                .limit(4)
                .sorted(Comparator.comparing(Employee::getAge));
        print(sorted);

        //выбрать всех сотрудников старше 40 лет, отсортировать их по возраств в обратном порядке
        //и вывести первых 4-х
        Stream<Employee> sorted1 = emps.stream()
                .filter(employee -> employee.getAge() > 40)
                .sorted((o1, o2) -> o2.getAge() - o1.getAge())
                .limit(4);
        print(sorted1);

        //суммарная статистика по возрасту
        IntSummaryStatistics intSummaryStatistics = emps.stream()
                .mapToInt(Employee::getAge)
                .summaryStatistics();

        System.out.println(intSummaryStatistics);
    }

    private void print(Stream<Employee> stream) {
        stream
                .map(emp -> String.format(
                        "%4d | %-15s %-10s age %s %s",
                        emp.getId(),
                        emp.getLastName(),
                        emp.getFirstName(),
                        emp.getAge(),
                        emp.getPosition()
                ))
                .forEach(System.out::println);

        System.out.println();
    }

    public Department reducer(Department parent, Department child){
        if(child.getParent() == parent.getId()){
            parent.getChild().add(child);
        }else {
            parent.getChild().forEach(subParent -> reducer(subParent, child));
        }
        return parent;
    }
}

