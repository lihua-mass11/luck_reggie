import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.reggie.ReggieApplication;
import com.example.reggie.entity.Category;
import com.example.reggie.entity.Employee;
import com.example.reggie.service.CategoryService;
import com.example.reggie.service.EmployeeService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = ReggieApplication.class)
public class Test1 {

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private CategoryService categoryService;

    @Test
    public void test() throws Exception {
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Employee::getUsername,"admin");
        System.out.println("对象:" + queryWrapper);

        // 获取第一个数据 .getOne查询唯一数据
        Employee emp = employeeService.getOne(queryWrapper);
        System.out.println(emp);
    }

    @Test
    public void testPage() throws Exception {
        //System.out.println(categoryService);
        Page<Category> page = new Page<>(1,2);
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.orderByDesc(Category::getSort);
        categoryService.page(page, queryWrapper);

        page.getRecords().stream().
                forEach(System.out::println);
    }
}
