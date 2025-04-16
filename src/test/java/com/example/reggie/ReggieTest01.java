package com.example.reggie;

import com.example.reggie.entity.Category;
import com.example.reggie.entity.Employee;
import com.example.reggie.mapper.CategoryMapper;
import com.example.reggie.mapper.EmployeeMapper;
import com.example.reggie.service.CategoryService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@DisplayName("分页测试")
@SpringBootTest(classes = ReggieApplication.class)
public class ReggieTest01 {

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private CategoryMapper categoryMapper;

    @Autowired
    private EmployeeMapper employeeMapper;

    @BeforeAll
    public static void beforeAll() {
        System.out.println("+++++++++++++ 分页测试启动 +++++++++++++++");
    }

    @AfterAll
    public static void afterAll() {
        System.out.println("+++++++++++++ 分页测试结束 +++++++++++++++");
    }

    @BeforeEach
    public void beforeEach() {
        System.out.println("---------------------------- beforeEach -----------------------------");
    }

    @AfterEach
    public void afterEach() {
        System.out.println("----------------------------- afterEach ------------------------------");
    }


    @Test
    @DisplayName("菜品分类页面分页测试")
    public void page01() {
        List<Category> categories = categoryMapper.selectList(null);
        System.out.println(categories);
    }

    @Test
    @DisplayName("员工页面分页测试")
    public void page02() {
        List<Employee> employees = employeeMapper.selectList(null);
        employees.forEach(employee -> {
            System.out.println(employee.getName());
        });
    }
}
